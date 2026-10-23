package tech.qiantong.qontology.module.app.service.appUnstructTask.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.common.utils.FileReader;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.AppUnstructTaskPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.AppUnstructTaskRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTask.vo.AppUnstructTaskSaveReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.AppUnstructTaskDocRelSaveReqVO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTask.AppUnstructTaskDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskDocRel.AppUnstructTaskDocRelDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import tech.qiantong.qontology.module.app.dal.mapper.appUnstructTask.AppUnstructTaskMapper;
import tech.qiantong.qontology.module.app.extEnum.AppLogStatusEnum;
import tech.qiantong.qontology.module.app.extEnum.AppReleaseStatus;
import tech.qiantong.qontology.module.app.extEnum.AppTaskStatus;
import tech.qiantong.qontology.module.app.extEnum.AppTaskTypeEnum;
import tech.qiantong.qontology.module.app.service.appTaskLog.IAppTaskLogService;
import tech.qiantong.qontology.module.app.service.appUnstructTask.IAppUnstructTaskService;
import tech.qiantong.qontology.module.app.service.appUnstructTaskDocRel.IAppUnstructTaskDocRelService;
import tech.qiantong.qontology.module.app.service.appUnstructTaskText.IAppUnstructTaskTextService;
import tech.qiantong.qontology.module.app.service.extraction.AppExtractionModelService;
import tech.qiantong.qontology.module.app.service.neo4j.AppNeo4jService;
import tech.qiantong.qontology.module.basic.api.dto.file.BasicFileRespDTO;
import tech.qiantong.qontology.module.basic.api.service.file.IBasicFileApiService;
import tech.qiantong.qontology.redis.service.IRedisService;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 非结构化抽取任务Service业务层处理
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Slf4j
@Service
public class AppUnstructTaskServiceImpl extends ServiceImpl<AppUnstructTaskMapper, AppUnstructTaskDO>
        implements IAppUnstructTaskService {
    private static final String QUEUE_KEY = "app_unstruck";

    @Resource
    private AppUnstructTaskMapper appUnstructTaskMapper;
    @Resource
    private IAppUnstructTaskDocRelService appUnstructTaskDocRelService;
    @Resource
    private IAppUnstructTaskTextService appUnstructTaskTextService;
    @Resource
    private IAppTaskLogService appTaskLogService;
    @Resource
    private IBasicFileApiService basicFileApiService;
    @Resource
    private AppNeo4jService appNeo4jService;
    @Resource
    private IRedisService redisService;
    @Resource
    private AppExtractionModelService extractionModelService;

    @Value("${dromara.x-file-storage.local-plus[0].storage-path:}")
    private String storagePath;

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "app-unstruct-task-thread");
        thread.setDaemon(true);
        return thread;
    });

    /**
     * 执行任务抽取，先放入Redis队列中
     *
     * @param createReqVO 抽取任务
     * @return 入队结果
     */
    @Override
    public AjaxResult executeExtraction(AppUnstructTaskSaveReqVO createReqVO) {
        Long taskId = createReqVO.getId();
        AppUnstructTaskDO unstructTaskDO = requireTask(taskId);
        if (AppReleaseStatus.PUBLISHED.getStatus().equals(unstructTaskDO.getPublishStatus())) {
            throw new ServiceException("已发布状态不能重新执行抽取");
        }
        if (AppTaskStatus.INPROGRESS.getValue().equals(unstructTaskDO.getStatus())) {
            throw new ServiceException("执行中不能重新执行抽取");
        }
        if (AppTaskStatus.QUEUE.getValue().equals(unstructTaskDO.getStatus())) {
            throw new ServiceException("队列中不能重新执行抽取");
        }
        if (appUnstructTaskDocRelService.findByTaskId(taskId).isEmpty()) {
            return AjaxResult.error("当前任务没有关联需要抽取的文件");
        }

        unstructTaskDO.setStatus(AppTaskStatus.QUEUE.getValue());
        appUnstructTaskMapper.updateAppUnstructTask(unstructTaskDO);
        redisService.leftPush(QUEUE_KEY, String.valueOf(taskId));
        CompletableFuture.runAsync(this::consumeQueue, executor);
        return AjaxResult.success("操作成功，已放入队列中，当前队列排队数：" + redisService.getListSize(QUEUE_KEY));
    }

    /**
     * 消费队列中的非结构化抽取任务
     */
    @Override
    public void consumeQueue() {
        while (true) {
            String taskId = redisService.leftPop(QUEUE_KEY);
            if (StringUtils.isBlank(taskId)) {
                log.info("队列中没有等待抽取的任务");
                return;
            }
            executeTask(Long.valueOf(taskId));
        }
    }

    /** 获得非结构化抽取任务分页列表。 */
    @Override
    public PageResult<AppUnstructTaskDO> getAppUnstructTaskPage(AppUnstructTaskPageReqVO pageReqVO) {
        return appUnstructTaskMapper.selectPage(pageReqVO);
    }

    /** 创建非结构化抽取任务并保存文件关联。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppUnstructTask(AppUnstructTaskSaveReqVO createReqVO) {
        AppUnstructTaskDO unstructTaskDO = BeanUtils.toBean(createReqVO, AppUnstructTaskDO.class);
        unstructTaskDO.setStatus(AppTaskStatus.UNEXECUTED.getValue());
        unstructTaskDO.setPublishStatus(AppReleaseStatus.UNPUBLISHED.getStatus());
        unstructTaskDO.setValidFlag(true);
        unstructTaskDO.setDelFlag(false);
        appUnstructTaskMapper.insert(unstructTaskDO);
        saveUnstructDocRel(createReqVO, unstructTaskDO.getId(), getDocIds(createReqVO));
        return unstructTaskDO.getId();
    }

    /** 更新非结构化抽取任务并重建文件关联。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAppUnstructTask(AppUnstructTaskSaveReqVO updateReqVO) {
        requireTask(updateReqVO.getId());
        AppUnstructTaskDO updateObj = BeanUtils.toBean(updateReqVO, AppUnstructTaskDO.class);
        updateObj.setStatus(AppTaskStatus.UNEXECUTED.getValue());
        appUnstructTaskDocRelService.deletePhysicallyByTaskId(updateReqVO.getId());
        saveUnstructDocRel(updateReqVO, updateReqVO.getId(), getDocIds(updateReqVO));
        return appUnstructTaskMapper.updateById(updateObj);
    }

    /** 删除非结构化抽取任务及其关联数据。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int removeAppUnstructTask(Collection<Long> idList) {
        for (Long taskId : idList) {
            appUnstructTaskDocRelService.removeByTaskId(taskId);
            appUnstructTaskTextService.removeByTaskId(taskId);
            appNeo4jService.deleteUnstructExtraction(taskId);
        }
        return appUnstructTaskMapper.deleteByIds(idList);
    }

    /** 获得非结构化抽取任务详情。 */
    @Override
    public AppUnstructTaskDO getAppUnstructTaskById(Long id) {
        return appUnstructTaskMapper.selectById(id);
    }

    /** 导入非结构化抽取任务数据。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String importAppUnstructTask(List<AppUnstructTaskRespVO> importExcelList,
                                        boolean isUpdateSupport, String operName) {
        if (importExcelList == null || importExcelList.isEmpty()) {
            throw new ServiceException("导入数据不能为空！");
        }
        int successNum = 0;
        for (AppUnstructTaskRespVO respVO : importExcelList) {
            AppUnstructTaskDO taskDO = BeanUtils.toBean(respVO, AppUnstructTaskDO.class);
            AppUnstructTaskDO existing = respVO.getId() == null
                    ? null : appUnstructTaskMapper.selectById(respVO.getId());
            if (existing != null) {
                if (!isUpdateSupport) {
                    throw new ServiceException("数据已存在，ID为 " + respVO.getId());
                }
                taskDO.setUpdateBy(operName);
                appUnstructTaskMapper.updateById(taskDO);
            } else {
                taskDO.setCreateBy(operName);
                appUnstructTaskMapper.insert(taskDO);
            }
            successNum++;
        }
        return "恭喜您，数据已全部导入成功！共 " + successNum + " 条。";
    }

    /** 发布非结构化抽取任务并同步发布 Neo4j 图谱数据。 */
    @Override
    public AjaxResult releaseByTaskId(AppUnstructTaskDO taskDO) {
        appNeo4jService.releaseUnstructExtraction(taskDO.getId());
        taskDO.setPublishStatus(AppReleaseStatus.PUBLISHED.getStatus());
        taskDO.setPublishTime(new Date());
        appUnstructTaskMapper.updateById(taskDO);
        return AjaxResult.success();
    }

    /** 取消发布非结构化抽取任务并同步取消发布 Neo4j 图谱数据。 */
    @Override
    public AjaxResult cancelReleaseByTaskId(AppUnstructTaskDO taskDO) {
        appNeo4jService.cancelReleaseUnstructExtraction(taskDO.getId());
        taskDO.setPublishStatus(AppReleaseStatus.UNPUBLISHED.getStatus());
        appUnstructTaskMapper.updateById(taskDO);
        return AjaxResult.success();
    }

    /** 执行单个非结构化抽取任务并维护通用抽取日志。 */
    private void executeTask(Long taskId) {
        AppUnstructTaskDO unstructTaskDO = requireTask(taskId);
        Long logId = appTaskLogService.startInvoke(
                unstructTaskDO.getWorkspaceId(), taskId, unstructTaskDO.getName(), AppTaskTypeEnum.UNSTRUCTURED,
                unstructTaskDO.getCreateBy(), unstructTaskDO.getCreatorId());
        try {
            unstructTaskDO.setStatus(AppTaskStatus.INPROGRESS.getValue());
            appUnstructTaskMapper.updateAppUnstructTask(unstructTaskDO);
            appTaskLogService.recordStep(logId, "非结构化抽取任务开始");
            List<AppUnstructTaskDocRelDO> docRelDOList = appUnstructTaskDocRelService.findByTaskId(taskId);
            execTaskByModel(unstructTaskDO, docRelDOList, logId);
            unstructTaskDO.setStatus(AppTaskStatus.EXECUTED.getValue());
            appUnstructTaskMapper.updateAppUnstructTask(unstructTaskDO);
            appTaskLogService.endInvoke(logId, AppLogStatusEnum.SUCCESS, "");
        } catch (Exception exception) {
            unstructTaskDO.setStatus(AppTaskStatus.ERROR.getValue());
            appUnstructTaskMapper.updateAppUnstructTask(unstructTaskDO);
            appTaskLogService.recordStep(logId, exception.getMessage());
            appTaskLogService.endInvoke(logId, AppLogStatusEnum.FAIL, exception.getMessage());
            log.error("非结构化抽取失败，taskId={}", taskId, exception);
        }
    }

    /**
     * 调用大模型执行抽取任务
     *
     * @param unstructTaskDO 抽取任务
     * @param docRelDOList 任务文件关联列表
     * @param logId 执行日志编号
     */
    private void execTaskByModel(AppUnstructTaskDO unstructTaskDO,
                                 List<AppUnstructTaskDocRelDO> docRelDOList,
                                 Long logId) throws Exception {
        String ttl = FileReader.safeReadFile(resolveFilePath(unstructTaskDO.getTtlFilePath()));
        if (StringUtils.isBlank(ttl)) {
            throw new ServiceException("TTL文件为空，无法执行抽取");
        }
        List<Long> docIdList = docRelDOList.stream()
                .map(AppUnstructTaskDocRelDO::getDocId)
                .collect(Collectors.toList());
        Map<Long, BasicFileRespDTO> documentMap = basicFileApiService.getFileListByIds(docIdList).stream()
                .collect(Collectors.toMap(BasicFileRespDTO::getId, document -> document));

        appNeo4jService.deleteUnstructExtraction(unstructTaskDO.getId());
        appUnstructTaskTextService.removeByTaskId(unstructTaskDO.getId());
        for (AppUnstructTaskDocRelDO docRelDO : docRelDOList) {
            BasicFileRespDTO document = documentMap.get(docRelDO.getDocId());
            if (document == null) {
                throw new ServiceException("关联文件不存在：" + docRelDO.getDocName());
            }
            String fileText = FileReader.safeReadFile(resolveFilePath(document.getFilePath()));
            if (StringUtils.isBlank(fileText)) {
                appTaskLogService.recordStep(logId, "抽取文件为空，抽取完成");
                continue;
            }

            List<String> segments = FileReader.splitText2(fileText, 3000, "\n\n");
            for (String segment : segments) {
                if (StringUtils.isBlank(segment)) {
                    continue;
                }
                int paragraphIndex = segments.indexOf(segment) + 1;
                int fileIndex = docRelDOList.indexOf(docRelDO) + 1;
                String step = "开始抽取第" + fileIndex + "个文件的第" + paragraphIndex + "个分段";
                appTaskLogService.recordStep(logId, step);

                JSONObject result = extractionModelService.extract(ttl, segment);
                log.info("非结构化抽取标准结果：{}", result.toJSONString());
                Long textId = saveTaskText(
                        unstructTaskDO, docRelDO.getDocId(), paragraphIndex, segment);

                // 按文件分段保存非结构化抽取实体和关系。
                appNeo4jService.saveUnstructExtraction(
                        unstructTaskDO.getId(), docRelDO.getDocId(), paragraphIndex, textId, result);
                appTaskLogService.recordStep(logId, step + "，调用大模型抽取完成");
            }
        }
    }

    /** 保存抽取片段及其来源信息。 */
    private Long saveTaskText(AppUnstructTaskDO taskDO, Long docId, Integer paragraphIndex,
                              String text) {
        AppUnstructTaskTextDO taskTextDO = new AppUnstructTaskTextDO();
        taskTextDO.setWorkspaceId(taskDO.getWorkspaceId());
        taskTextDO.setTaskId(taskDO.getId());
        taskTextDO.setDocId(docId);
        taskTextDO.setParagraphIndex(paragraphIndex);
        taskTextDO.setText(text);
        taskTextDO.setValidFlag(true);
        taskTextDO.setDelFlag(false);
        taskTextDO.setCreateBy(taskDO.getUpdateBy());
        taskTextDO.setCreatorId(taskDO.getUpdaterId());
        appUnstructTaskTextService.save(taskTextDO);
        return taskTextDO.getId();
    }

    /** 保存任务与文件管理文件的关联。 */
    private void saveUnstructDocRel(AppUnstructTaskSaveReqVO reqVO, Long taskId, List<Long> docIds) {
        if (docIds.isEmpty()) {
            throw new ServiceException("请选择需要抽取的文件");
        }
        List<BasicFileRespDTO> documents = basicFileApiService.getFileListByIds(docIds);
        if (documents.size() != docIds.size()) {
            throw new ServiceException("部分关联文件不存在或已删除");
        }
        for (BasicFileRespDTO document : documents) {
            AppUnstructTaskDocRelSaveReqVO docRelReqVO = new AppUnstructTaskDocRelSaveReqVO();
            docRelReqVO.setWorkspaceId(reqVO.getWorkspaceId());
            docRelReqVO.setTaskId(taskId);
            docRelReqVO.setDocId(document.getId());
            docRelReqVO.setDocName(document.getFileName());
            docRelReqVO.setValidFlag(true);
            docRelReqVO.setCreateBy(reqVO.getCreateBy());
            docRelReqVO.setCreatorId(reqVO.getCreatorId());
            docRelReqVO.setUpdateBy(reqVO.getUpdateBy());
            docRelReqVO.setUpdaterId(reqVO.getUpdaterId());
            appUnstructTaskDocRelService.createAppUnstructTaskDocRel(docRelReqVO);
        }
    }

    /** 从qKnow同名params.docIds参数读取文件编号。 */
    private List<Long> getDocIds(AppUnstructTaskSaveReqVO reqVO) {
        Object value = reqVO.getParams().get("docIds");
        if (value == null || StringUtils.isBlank(String.valueOf(value))) {
            return Collections.emptyList();
        }
        return Arrays.stream(String.valueOf(value).split(","))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    /** 校验并返回非结构化抽取任务。 */
    private AppUnstructTaskDO requireTask(Long id) {
        AppUnstructTaskDO taskDO = appUnstructTaskMapper.selectById(id);
        if (taskDO == null) {
            throw new ServiceException("非结构化抽取任务不存在");
        }
        return taskDO;
    }

    /** 将上传地址或文件管理路径转换为本地存储绝对路径。 */
    private String resolveFilePath(String path) {
        if (StringUtils.isBlank(path)) {
            throw new ServiceException("文件存储地址为空");
        }
        if (StringUtils.isBlank(storagePath)) {
            throw new ServiceException("本地文件存储路径未配置");
        }
        String normalizedPath = path.trim().replace('\\', '/');
        if (normalizedPath.startsWith("http://") || normalizedPath.startsWith("https://")) {
            normalizedPath = URI.create(normalizedPath).getPath();
        }
        int profileIndex = normalizedPath.indexOf("/profile/");
        if (profileIndex >= 0) {
            normalizedPath = normalizedPath.substring(profileIndex + "/profile/".length());
        }
        Path storageRoot = new File(storagePath).toPath().toAbsolutePath().normalize();
        File file = new File(normalizedPath);
        if (file.isAbsolute()) {
            Path absolutePath = file.toPath().toAbsolutePath().normalize();
            if (absolutePath.startsWith(storageRoot)) {
                return absolutePath.toString();
            }
        }
        while (normalizedPath.startsWith("/")) {
            normalizedPath = normalizedPath.substring(1);
        }
        Path resolvedPath = storageRoot.resolve(normalizedPath).normalize();
        if (!resolvedPath.startsWith(storageRoot)) {
            throw new ServiceException("文件存储地址不合法");
        }
        return resolvedPath.toString();
    }
}
