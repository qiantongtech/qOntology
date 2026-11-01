package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.file.util.FileUploadUtil;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.OmAiTaskCreateReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.OmAiTaskPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmDataRuleSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.constraint.vo.OmModelAxiomSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAdoptLog.OmAiAdoptLogDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute.OmAiAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataEvidence.OmAiDataEvidenceDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmDataRuleDO;
import tech.qiantong.qontology.module.om.dal.dataobject.constraint.OmModelAxiomDO;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.object.OmObjectTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.omtarget.OmTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.relation.OmObjectTypeRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumData.OmAiEnumDataDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumType.OmAiEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObject.OmAiObjectDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObjectTarget.OmAiObjectTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelation.OmAiRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelationTarget.OmAiRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTarget.OmAiTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTerm.OmAiTermDO;
import tech.qiantong.qontology.module.basic.dal.mapper.dataType.BasicDataTypeMapper;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAdoptLog.OmAiAdoptLogMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAttribute.OmAiAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAxiom.OmAiAxiomMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataEvidence.OmAiDataEvidenceMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataRule.OmAiDataRuleMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumData.OmAiEnumDataMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumType.OmAiEnumTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEvidence.OmAiEvidenceMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiFile.OmAiFileMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiObject.OmAiObjectMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiObjectTarget.OmAiObjectTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiRelation.OmAiRelationMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiRelationTarget.OmAiRelationTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiStage.OmAiStageMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTarget.OmAiTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTask.OmAiTaskMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTerm.OmAiTermMapper;
import tech.qiantong.qontology.module.om.dal.mapper.dataType.OmDataTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmDataRuleMapper;
import tech.qiantong.qontology.module.om.dal.mapper.constraint.OmModelAxiomMapper;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.object.OmObjectTypeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.omtarget.OmTargetMapper;
import tech.qiantong.qontology.module.om.dal.mapper.relation.OmObjectTypeRelationMapper;
import tech.qiantong.qontology.module.om.dal.mapper.term.OmTermMapper;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;
import tech.qiantong.qontology.module.om.enums.AcceptStatusEnum;
import tech.qiantong.qontology.module.om.enums.TargetPriorityEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiCandidateTypeEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiFileParseStatusEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiTaskStatusEnum;
import tech.qiantong.qontology.module.om.service.enumeration.IOmEnumDataService;
import tech.qiantong.qontology.module.om.service.enumeration.IOmEnumTypeService;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeAttributeService;
import tech.qiantong.qontology.module.om.service.object.IOmObjectTypeService;
import tech.qiantong.qontology.module.om.service.omtarget.IOmTargetService;
import tech.qiantong.qontology.module.om.service.relation.IOmObjectTypeRelationService;
import tech.qiantong.qontology.module.om.service.term.IOmTermService;
import tech.qiantong.qontology.module.om.service.constraint.IOmDataRuleService;
import tech.qiantong.qontology.module.om.service.constraint.IOmModelAxiomService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

/** AI 辅助建模任务、候选数据和采纳流程服务实现。 */
@Service
public class OmAiModelingServiceImpl implements IOmAiModelingService {

    private static final Set<String> SUPPORTED_FILE_EXTENSIONS = Set.of(
            "doc", "docx", "pdf", "txt", "png", "jpg", "jpeg", "xls", "xlsx");

    @Resource
    private OmAiTaskMapper taskMapper;
    @Resource
    private OmAiFileMapper fileMapper;
    @Resource
    private OmAiStageMapper stageMapper;
    @Resource
    private OmAiEvidenceMapper evidenceMapper;
    @Resource
    private OmAiModelingAsyncProcessor asyncProcessor;
    @Resource(name = "threadPoolTaskExecutor")
    private TaskExecutor taskExecutor;
    @Resource
    private OmAiTargetMapper aiTargetMapper;
    @Resource
    private OmAiTermMapper aiTermMapper;
    @Resource
    private OmAiEnumTypeMapper aiEnumTypeMapper;
    @Resource
    private OmAiEnumDataMapper aiEnumDataMapper;
    @Resource
    private OmAiObjectMapper aiObjectMapper;
    @Resource
    private OmAiAttributeMapper aiAttributeMapper;
    @Resource
    private OmAiRelationMapper aiRelationMapper;
    @Resource
    private OmAiAxiomMapper aiAxiomMapper;
    @Resource
    private OmAiDataRuleMapper aiDataRuleMapper;
    @Resource
    private OmAiObjectTargetMapper objectTargetMapper;
    @Resource
    private OmAiRelationTargetMapper relationTargetMapper;
    @Resource
    private OmAiDataEvidenceMapper dataEvidenceMapper;
    @Resource
    private OmAiAdoptLogMapper adoptLogMapper;
    @Resource
    private OmDataTypeMapper dataTypeMapper;
    @Resource
    private BasicDataTypeMapper basicDataTypeMapper;
    @Resource
    private OmEnumTypeMapper formalEnumTypeMapper;
    @Resource
    private BasicEnumTypeMapper basicEnumTypeMapper;
    @Resource
    private OmTargetMapper formalTargetMapper;
    @Resource
    private OmTermMapper formalTermMapper;
    @Resource
    private OmObjectTypeMapper formalObjectMapper;
    @Resource
    private OmObjectTypeAttributeMapper formalAttributeMapper;
    @Resource
    private OmObjectTypeRelationMapper formalRelationMapper;
    @Resource
    private OmModelAxiomMapper formalAxiomMapper;
    @Resource
    private OmDataRuleMapper formalDataRuleMapper;
    @Resource
    private IOmTargetService targetService;
    @Resource
    private IOmTermService termService;
    @Resource
    private IOmEnumTypeService enumTypeService;
    @Resource
    private IOmEnumDataService enumDataService;
    @Resource
    private IOmObjectTypeService objectTypeService;
    @Resource
    private IOmObjectTypeAttributeService attributeService;
    @Resource
    private IOmObjectTypeRelationService relationService;
    @Resource
    private IOmModelAxiomService modelAxiomService;
    @Resource
    private IOmDataRuleService dataRuleService;

    /** 查询指定版本的 AI 建模任务分页列表。 */
    @Override
    public PageResult<OmAiTaskDO> page(OmAiTaskPageReqVO request) {
        return taskMapper.selectPage(request);
    }

    /** 查询 AI 建模任务详情。 */
    @Override
    public OmAiTaskDO get(Long id) {
        return getRequiredTask(id);
    }

    /** 第一步：创建任务并保存上传文件，不在当前请求中启动解析。 */
    @Override
    public Long create(OmAiTaskCreateReqVO request, Long workspaceId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new ServiceException("请至少上传一个文件");
        }
        OmAiTaskDO task = OmAiTaskDO.builder()
                .taskName(request.getTaskName())
                .description(request.getDescription())
                .workspaceId(workspaceId)
                .versionId(request.getVersionId())
                .status(OmAiTaskStatusEnum.WAITING.getCode())
                .currentStage(null)
                .candidateCount(0)
                .startedTime(null)
                .delFlag(false)
                .build();
        taskMapper.insert(task);
        try {
            for (MultipartFile file : files) {
                saveSourceFile(task.getId(), file);
            }
        } catch (RuntimeException exception) {
            task.setStatus(OmAiTaskStatusEnum.FAILED.getCode());
            task.setFailureReason(limitMessage(exception.getMessage()));
            taskMapper.updateById(task);
            throw exception;
        }
        // 提前创建阶段记录，第二步流式接口启动后按实际执行顺序推送。
        asyncProcessor.initializeStages(task);
        return task.getId();
    }

    /** 第二步：启动后台解析；重复连接流式接口时不会重复提交任务。 */
    @Override
    public synchronized void start(Long id) {
        OmAiTaskDO task = getRequiredTask(id);
        if (OmAiTaskStatusEnum.RUNNING.getCode().equals(task.getStatus())
                || OmAiTaskStatusEnum.SUCCESS.getCode().equals(task.getStatus())
                || OmAiTaskStatusEnum.FAILED.getCode().equals(task.getStatus())) {
            return;
        }
        if (!asyncProcessor.reserve(id)) {
            return;
        }
        task.setStatus(OmAiTaskStatusEnum.RUNNING.getCode());
        task.setCurrentStage("FILE_PARSE");
        task.setStartedTime(new Date());
        task.setFailureReason(null);
        taskMapper.updateById(task);
        try {
            taskExecutor.execute(() -> asyncProcessor.process(id));
        } catch (RuntimeException exception) {
            asyncProcessor.release(id);
            task.setStatus(OmAiTaskStatusEnum.FAILED.getCode());
            task.setFailureReason("后台解析任务提交失败：" + exception.getMessage());
            task.setFinishedTime(new Date());
            taskMapper.updateById(task);
            throw exception;
        }
    }

    /** 保存原始文件；结构化解析由异步处理器执行。 */
    private void saveSourceFile(Long taskId, MultipartFile file) {
        try {
            String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
            if (!SUPPORTED_FILE_EXTENSIONS.contains(extension)) {
                throw new ServiceException("不支持的文件格式：" + extension);
            }
            byte[] fileBytes = file.getBytes();
            MultipartFile stableFile = new ByteArrayMultipartFile(
                    file.getName(), file.getOriginalFilename(), file.getContentType(), fileBytes);
            FileInfo fileInfo = FileUploadUtil.upload(stableFile, "ai-modeling/");
            OmAiFileDO fileData = OmAiFileDO.builder()
                    .taskId(taskId).fileName(file.getOriginalFilename()).fileUrl(fileInfo.getUrl())
                    .fileExt(extension).fileSize((long) fileBytes.length)
                    .contentHash(DigestUtils.sha256Hex(fileBytes))
                    .parseStatus(OmAiFileParseStatusEnum.WAITING.getCode())
                    .blockCount(0).delFlag(false).build();
            fileMapper.insert(fileData);
        } catch (Exception exception) {
            if (exception instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new ServiceException("文件解析失败：" + exception.getMessage());
        }
    }

    /** 删除任务记录。 */
    @Override
    public int delete(Collection<Long> ids) {
        return taskMapper.deleteBatchIds(ids);
    }

    /** 清空旧结果并对任务执行全量重新解析。 */
    @Override
    public synchronized void reparse(Long id) {
        OmAiTaskDO task = getRequiredTask(id);
        if (OmAiTaskStatusEnum.RUNNING.getCode().equals(task.getStatus()) && asyncProcessor.isRunning(id)) {
            throw new ServiceException("任务正在解析");
        }
        if (OmAiTaskStatusEnum.RUNNING.getCode().equals(task.getStatus())) {
            task.setStatus(OmAiTaskStatusEnum.FAILED.getCode());
            task.setFailureReason("应用进程已中断，上一次解析未正常结束，现已重新启动解析");
            task.setFinishedTime(new Date());
            taskMapper.updateById(task);
        }
        long fileCount = fileMapper.selectCount(new QueryWrapper<OmAiFileDO>().eq("task_id", id));
        if (fileCount == 0) {
            throw new ServiceException("任务没有原始文件，无法重新解析，请删除任务后重新上传文件");
        }
        clearTaskResults(id);
        task.setStatus(OmAiTaskStatusEnum.RUNNING.getCode());
        task.setCurrentStage("FILE_PARSE");
        task.setStartedTime(new Date());
        task.setCandidateCount(0);
        task.setFinishedTime(null);
        task.setFailureReason(null);
        taskMapper.updateById(task);
        asyncProcessor.initializeStages(task);
        if (!asyncProcessor.reserve(id)) {
            throw new ServiceException("任务正在解析");
        }
        try {
            taskExecutor.execute(() -> asyncProcessor.process(id));
        } catch (RuntimeException exception) {
            asyncProcessor.release(id);
            task.setStatus(OmAiTaskStatusEnum.FAILED.getCode());
            task.setFailureReason("后台解析任务提交失败：" + exception.getMessage());
            task.setFinishedTime(new Date());
            taskMapper.updateById(task);
            throw new ServiceException("后台解析任务提交失败，请稍后重试");
        }
    }

    /**
     * 基于内存字节提供可重复读取的上传文件，避免解析器关闭临时上传流后文件无法再次上传。
     */
    private static final class ByteArrayMultipartFile implements MultipartFile {

        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        private ByteArrayMultipartFile(
                String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content.clone();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }

    /** 清除任务的候选数据、关联关系、证据关联、采纳日志和阶段记录。 */
    private void clearTaskResults(Long taskId) {
        objectTargetMapper.delete(new QueryWrapper<OmAiObjectTargetDO>().eq("task_id", taskId));
        relationTargetMapper.delete(new QueryWrapper<OmAiRelationTargetDO>().eq("task_id", taskId));
        dataEvidenceMapper.delete(new QueryWrapper<OmAiDataEvidenceDO>().eq("task_id", taskId));
        adoptLogMapper.delete(new QueryWrapper<OmAiAdoptLogDO>().eq("task_id", taskId));
        aiDataRuleMapper.delete(new QueryWrapper<OmAiDataRuleDO>().eq("task_id", taskId));
        aiAxiomMapper.delete(new QueryWrapper<OmAiAxiomDO>().eq("task_id", taskId));
        aiRelationMapper.delete(new QueryWrapper<OmAiRelationDO>().eq("task_id", taskId));
        aiAttributeMapper.delete(new QueryWrapper<OmAiAttributeDO>().eq("task_id", taskId));
        aiObjectMapper.delete(new QueryWrapper<OmAiObjectDO>().eq("task_id", taskId));
        aiEnumDataMapper.delete(new QueryWrapper<OmAiEnumDataDO>().eq("task_id", taskId));
        aiEnumTypeMapper.delete(new QueryWrapper<OmAiEnumTypeDO>().eq("task_id", taskId));
        aiTermMapper.delete(new QueryWrapper<OmAiTermDO>().eq("task_id", taskId));
        aiTargetMapper.delete(new QueryWrapper<OmAiTargetDO>().eq("task_id", taskId));
        evidenceMapper.delete(new QueryWrapper<OmAiEvidenceDO>().eq("task_id", taskId));
        fileMapper.resetParseStatus(OmAiFileDO.builder()
                .taskId(taskId)
                .parseStatus(OmAiFileParseStatusEnum.WAITING.getCode())
                .build());
        stageMapper.delete(new QueryWrapper<OmAiStageDO>().eq("task_id", taskId));
    }

    /** 查询任务上传文件。 */
    @Override
    public List<OmAiFileDO> files(Long taskId) {
        return fileMapper.selectList(new QueryWrapper<OmAiFileDO>()
                .eq("task_id", taskId).orderByAsc("id"));
    }

    /** 查询任务解析过程时间线。 */
    @Override
    public List<OmAiStageDO> stages(Long taskId) {
        return stageMapper.selectList(new QueryWrapper<OmAiStageDO>()
                .eq("task_id", taskId).orderByAsc("sequence_no"));
    }

    @Override
    public List<Map<String, Object>> candidateTargets(String type, Long id) {
        if ("object".equals(type)) return objectTargetMapper.selectTargets(id);
        if ("relation".equals(type)) return relationTargetMapper.selectTargets(id);
        throw new ServiceException("仅对象类型和对象关系支持关联建模目标");
    }

    @Override
    public void saveCandidateTargets(String type, Long id, List<Long> targetIds) {
        if (!("object".equals(type) || "relation".equals(type))) throw new ServiceException("仅对象类型和对象关系支持关联建模目标");
        if (targetIds == null || targetIds.isEmpty()) throw new ServiceException("请至少关联一个建模目标");
        Map<String, Object> candidate = getCandidate("object".equals(type)
                ? OmAiCandidateTypeEnum.OBJECT : OmAiCandidateTypeEnum.RELATION, id);
        Long taskId = longValue(candidate, "task_id");
        List<Long> distinctTargetIds = targetIds.stream().distinct().toList();
        Integer count = aiTargetMapper.countTaskTargets(taskId, distinctTargetIds);
        if (count == null || count != distinctTargetIds.size()) throw new ServiceException("关联建模目标不存在或不属于当前任务");
        if ("object".equals(type)) {
            objectTargetMapper.delete(new QueryWrapper<OmAiObjectTargetDO>().eq("object_candidate_id", id));
            objectTargetMapper.insertBatch(taskId, id, distinctTargetIds);
        } else {
            relationTargetMapper.delete(new QueryWrapper<OmAiRelationTargetDO>().eq("relation_candidate_id", id));
            relationTargetMapper.insertBatch(taskId, id, distinctTargetIds);
        }
        aiTargetMapper.refreshRelatedElementCounts(taskId);
    }

    @Override
    public List<Map<String, Object>> attributeBusinessTypes(Long taskId, String source) {
        OmAiTaskDO task = getRequiredTask(taskId);
        if ("PROJECT".equals(source)) {
            return dataTypeMapper.selectAiOptions(task.getVersionId());
        }
        if (!"GLOBAL".equals(source)) throw new ServiceException("类型来源只能为当前项目或全局");
        return basicDataTypeMapper.selectAiOptions();
    }

    @Override
    public List<Map<String, Object>> attributeEnumOptions(Long taskId, String source) {
        OmAiTaskDO task = getRequiredTask(taskId);
        if ("PROJECT".equals(source)) {
            return formalEnumTypeMapper.selectAiOptions(task.getVersionId());
        }
        if (!"GLOBAL".equals(source)) throw new ServiceException("枚举来源只能为当前项目或全局");
        return basicEnumTypeMapper.selectAiOptions();
    }

    /** 查询候选数据的原始文件来源和精确位置。 */
    @Override
    public List<Map<String, Object>> evidence(String type, Long id) {
        OmAiCandidateTypeEnum.fromCode(type);
        return dataEvidenceMapper.selectEvidence(type, id);
    }

    /** 查询一键采纳前置条件。 */
    @Override
    public Map<String, Object> adoptionRequirements(Long taskId) {
        getRequiredTask(taskId);
        return Map.of("attributesToConfirm", List.of(), "canAdoptAll", true);
    }

    /** 采纳单条候选数据；关联候选必须已提前采纳。 */
    @Override
    public int adopt(String type, Long id) {
        OmAiCandidateTypeEnum candidateType = OmAiCandidateTypeEnum.fromCode(type);
        Map<String, Object> candidate = getCandidate(candidateType, id);
        if (OmAiAdoptStatusEnum.ADOPTED.getCode().equals(integerValue(candidate, "adopt_status"))) {
            return 1;
        }
        try {
            Long formalId = adoptCandidate(candidateType, candidate);
            updateCandidateAdoptStatus(candidateType, id, OmAiAdoptStatusEnum.ADOPTED, formalId, null);
            insertAdoptLog(candidate, candidateType, formalId, OmAiAdoptStatusEnum.ADOPTED, "采纳成功");
            return 1;
        } catch (Exception exception) {
            String message = limitMessage(exception.getMessage());
            updateCandidateAdoptStatus(candidateType, id, OmAiAdoptStatusEnum.FAILED, null, message);
            insertAdoptLog(candidate, candidateType, null, OmAiAdoptStatusEnum.FAILED, message);
            if (exception instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new ServiceException(message);
        }
    }

    /** 按候选类型创建正式数据。 */
    private Long adoptCandidate(OmAiCandidateTypeEnum type, Map<String, Object> row) {
        OmAiTaskDO task = getRequiredTask(longValue(row, "task_id"));
        return switch (type) {
            case TARGET -> adoptTarget(task, row);
            case TERM -> adoptTerm(task, row);
            case ENUM_TYPE -> adoptEnumType(task, row);
            case ENUM_DATA -> throw new ServiceException("枚举值需随枚举类型一起采纳");
            case OBJECT -> adoptObject(task, row);
            case ATTRIBUTE -> adoptAttribute(task, row);
            case RELATION -> adoptRelation(task, row);
            case AXIOM -> adoptAxiom(task, row);
            case DATA_RULE -> adoptDataRule(task, row);
        };
    }

    /** 采纳建模目标。 */
    private Long adoptTarget(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "question", "目标问题");
        requireText(row, "question_source", "问题来源");
        requireText(row, "business_scenarios", "业务场景");
        requireText(row, "accept_criterion", "验收标准");
        requireNotExists(formalTargetMapper.selectCount(new QueryWrapper<OmTargetDO>()
                        .eq("version_id", task.getVersionId())
                        .eq("question", stringValue(row, "question"))
                        .eq("del_flag", 0)),
                "建模目标已存在，不能重复采纳");
        OmTargetSaveReqVO request = new OmTargetSaveReqVO();
        request.setVersionId(task.getVersionId());
        request.setQuestion(stringValue(row, "question"));
        request.setQuestionSource(stringValue(row, "question_source"));
        request.setBusinessScenarios(stringValue(row, "business_scenarios"));
        request.setPriority(toPriority(integerValue(row, "priority")));
        request.setAcceptCriterion(stringValue(row, "accept_criterion"));
        request.setAcceptStatus(AcceptStatusEnum.WAITING);
        return targetService.createOmTarget(request);
    }

    /** 采纳术语。 */
    private Long adoptTerm(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "std_name", "名称");
        requireText(row, "en_name", "英文名称");
        requireText(row, "alias", "别名");
        requireText(row, "abbr", "缩写");
        requireText(row, "source", "来源");
        requireNotExists(formalTermMapper.selectCount(new QueryWrapper<OmTermDO>()
                        .eq("workspace_id", task.getWorkspaceId())
                        .eq("version_id", task.getVersionId())
                        .eq("std_name", stringValue(row, "std_name"))
                        .eq("del_flag", 0)),
                "术语名称已存在，不能重复采纳");
        OmTermSaveReqVO request = new OmTermSaveReqVO();
        request.setWorkspaceId(task.getWorkspaceId());
        request.setVersionId(task.getVersionId());
        request.setStdName(stringValue(row, "std_name"));
        request.setEnName(emptyIfNull(row, "en_name"));
        request.setAlias(emptyIfNull(row, "alias"));
        request.setAbbr(emptyIfNull(row, "abbr"));
        request.setSource(emptyIfNull(row, "source"));
        request.setDescription(stringValue(row, "description"));
        return termService.createOmTerm(request);
    }

    /** 采纳枚举类型及其全部未采纳枚举值。 */
    private Long adoptEnumType(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "enum_name", "枚举名称");
        requireText(row, "enum_type", "枚举类型");
        requireNotExists(formalEnumTypeMapper.selectCount(new QueryWrapper<OmEnumTypeDO>()
                        .eq("workspace_id", task.getWorkspaceId())
                        .eq("version_id", task.getVersionId())
                        .eq("enum_name", stringValue(row, "enum_name"))
                        .eq("enum_type", stringValue(row, "enum_type"))
                        .eq("del_flag", 0)),
                "枚举名称和枚举类型已存在，不能重复采纳");
        OmEnumTypeSaveReqVO request = new OmEnumTypeSaveReqVO();
        request.setWorkspaceId(task.getWorkspaceId());
        request.setVersionId(task.getVersionId());
        request.setEnumName(stringValue(row, "enum_name"));
        request.setEnumType(stringValue(row, "enum_type"));
        request.setStatus(Optional.ofNullable(stringValue(row, "status")).orElse("0"));
        request.setRemark(stringValue(row, "remark"));
        Long formalTypeId = enumTypeService.createOmEnumType(request);
        List<Map<String, Object>> children = aiEnumDataMapper.selectPendingByEnumType(
                longValue(row, "id"), OmAiAdoptStatusEnum.ADOPTED.getCode());
        for (Map<String, Object> child : children) {
            OmEnumDataSaveReqVO childRequest = new OmEnumDataSaveReqVO();
            childRequest.setWorkspaceId(task.getWorkspaceId());
            childRequest.setVersionId(task.getVersionId());
            childRequest.setEnumSort(longValue(child, "enum_sort"));
            childRequest.setEnumLabel(stringValue(child, "enum_label"));
            childRequest.setEnumValue(stringValue(child, "enum_value"));
            childRequest.setEnumType(stringValue(row, "enum_type"));
            childRequest.setStatus(Optional.ofNullable(stringValue(child, "status")).orElse("0"));
            childRequest.setRemark(stringValue(child, "remark"));
            Long formalChildId = enumDataService.createOmEnumData(childRequest);
            updateCandidateAdoptStatus(OmAiCandidateTypeEnum.ENUM_DATA, longValue(child, "id"),
                    OmAiAdoptStatusEnum.ADOPTED, formalChildId, null);
        }
        return formalTypeId;
    }

    /** 采纳对象类型；父对象和关联建模目标必须已采纳。 */
    private Long adoptObject(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "name", "对象类型名称");
        requireNotExists(formalObjectMapper.selectCount(new QueryWrapper<OmObjectTypeDO>()
                        .eq("workspace_id", task.getWorkspaceId())
                        .eq("version_id", task.getVersionId())
                        .eq("name", stringValue(row, "name"))
                        .eq("del_flag", 0)),
                "对象类型名称已存在，不能重复采纳");
        Long parentCandidateId = nullableLongValue(row, "parent_candidate_id");
        Long parentId = 0L;
        if (parentCandidateId != null) {
            Map<String, Object> parent = requireAdopted(
                    OmAiCandidateTypeEnum.OBJECT, parentCandidateId, "上级对象类型");
            parentId = longValue(parent, "adopted_id");
        }
        List<Long> targetIds = requireLinkedTargetsAdopted(
                objectTargetMapper.selectTargetIds(longValue(row, "id")));
        if (targetIds.isEmpty()) {
            throw new ServiceException("对象类型至少需要一个建模目标");
        }
        OmObjectTypeSaveReqVO request = new OmObjectTypeSaveReqVO();
        request.setWorkspaceId(task.getWorkspaceId());
        request.setVersionId(task.getVersionId());
        request.setName(stringValue(row, "name"));
        request.setDescription(stringValue(row, "description"));
        request.setParentId(parentId);
        request.setOrderNum(Optional.ofNullable(nullableLongValue(row, "order_num")).orElse(0L));
        request.setTargetIds(targetIds);
        return objectTypeService.createOmObjectType(request);
    }

    /** 采纳对象属性；所属对象类型必须已采纳。 */
    private Long adoptAttribute(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "name", "属性名称");
        requireText(row, "code", "属性代码");
        Long objectCandidateId = longValue(row, "object_candidate_id");
        Map<String, Object> objectCandidate = requireAdopted(
                OmAiCandidateTypeEnum.OBJECT, objectCandidateId, "所属对象类型");
        Long objectTypeId = longValue(objectCandidate, "adopted_id");
        requireNotExists(formalAttributeMapper.selectCount(new QueryWrapper<OmObjectTypeAttributeDO>()
                        .eq("workspace_id", task.getWorkspaceId())
                        .eq("version_id", task.getVersionId())
                        .eq("object_type_id", objectTypeId)
                        .eq("name", stringValue(row, "name"))
                        .eq("del_flag", 0)),
                "当前对象类型下属性名称已存在，不能重复采纳");
        OmObjectTypeAttributeSaveReqVO request = new OmObjectTypeAttributeSaveReqVO();
        request.setWorkspaceId(task.getWorkspaceId());
        request.setVersionId(task.getVersionId());
        request.setObjectTypeId(objectTypeId);
        request.setName(stringValue(row, "name"));
        request.setCode(stringValue(row, "code"));
        request.setDescription(stringValue(row, "description"));
        request.setTypeCategory(integerValue(row, "type_category"));
        request.setBasicTypeCode(nullableIntegerValue(row, "basic_type_code"));
        request.setBusinessTypeId(nullableLongValue(row, "business_type_id"));
        request.setBusinessTypeSource(stringValue(row, "business_type_source"));
        request.setUnit(stringValue(row, "unit"));
        request.setDecimalPlaces(nullableIntegerValue(row, "decimal_places"));
        request.setFormat(stringValue(row, "format"));
        request.setEnumType(stringValue(row, "enum_type"));
        request.setEnumSource(stringValue(row, "enum_source"));
        return attributeService.createOmObjectTypeAttribute(request);
    }

    /** 采纳对象类型关系；两端对象和关联建模目标必须已采纳。 */
    private Long adoptRelation(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "name", "关系名称");
        Long startCandidateId = longValue(row, "start_object_candidate_id");
        Long endCandidateId = longValue(row, "end_object_candidate_id");
        Map<String, Object> startObject = requireAdopted(
                OmAiCandidateTypeEnum.OBJECT, startCandidateId, "起点对象类型");
        Map<String, Object> endObject = requireAdopted(
                OmAiCandidateTypeEnum.OBJECT, endCandidateId, "终点对象类型");
        Long startTypeId = longValue(startObject, "adopted_id");
        Long endTypeId = longValue(endObject, "adopted_id");
        requireNotExists(formalRelationMapper.selectCount(new QueryWrapper<OmObjectTypeRelationDO>()
                        .eq("workspace_id", task.getWorkspaceId())
                        .eq("version_id", task.getVersionId())
                        .eq("start_type_id", startTypeId)
                        .eq("name", stringValue(row, "name"))
                        .eq("end_type_id", endTypeId)
                        .eq("del_flag", 0)),
                "相同起点类型、关系名称和终点类型的关系已存在，不能重复采纳");
        List<Long> targetIds = requireLinkedTargetsAdopted(
                relationTargetMapper.selectTargetIds(longValue(row, "id")));
        if (targetIds.isEmpty()) {
            throw new ServiceException("对象关系至少需要一个建模目标");
        }
        OmObjectTypeRelationSaveReqVO request = new OmObjectTypeRelationSaveReqVO();
        request.setWorkspaceId(task.getWorkspaceId());
        request.setVersionId(task.getVersionId());
        request.setName(stringValue(row, "name"));
        request.setDescription(stringValue(row, "description"));
        request.setStartTypeId(startTypeId);
        request.setEndTypeId(endTypeId);
        request.setReversibleFlag(integerValue(row, "reversible_flag"));
        request.setReverseName(stringValue(row, "reverse_name"));
        request.setTargetIds(targetIds);
        return relationService.createOmObjectTypeRelation(request);
    }

    /** 采纳模型公理；公理引用的对象和属性候选必须已经采纳。 */
    private Long adoptAxiom(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "name", "模型公理名称");
        requireText(row, "type", "公理类型");
        requireNotExists(formalAxiomMapper.selectCount(new QueryWrapper<OmModelAxiomDO>()
                        .eq("version_id", task.getVersionId())
                        .eq("name", stringValue(row, "name"))
                        .eq("del_flag", 0)),
                "当前版本已存在同名模型公理，不能重复采纳");
        OmModelAxiomSaveReqVO request = new OmModelAxiomSaveReqVO();
        request.setVersionId(task.getVersionId()); request.setName(stringValue(row, "name"));
        request.setDescription(stringValue(row, "description")); request.setType(stringValue(row, "type"));
        request.setLevel(Optional.ofNullable(stringValue(row, "level")).orElse("INFO"));
        request.setObjectTypeId(adoptedDependencyId(OmAiCandidateTypeEnum.OBJECT, row, "object_candidate_id", "对象类型"));
        request.setOtherTypeId(adoptedDependencyId(OmAiCandidateTypeEnum.OBJECT, row, "other_object_candidate_id", "另一对象类型"));
        request.setDomainId(adoptedDependencyId(OmAiCandidateTypeEnum.OBJECT, row, "domain_candidate_id", "Domain对象类型"));
        request.setRangeId(adoptedDependencyId(OmAiCandidateTypeEnum.OBJECT, row, "range_candidate_id", "Range对象类型"));
        request.setAttributeId(adoptedDependencyId(OmAiCandidateTypeEnum.ATTRIBUTE, row, "attribute_candidate_id", "对象属性"));
        request.setRelationName(stringValue(row, "relation_name")); request.setTypeCategory(stringValue(row, "type_category"));
        request.setBasicTypeCode(stringValue(row, "basic_type_code")); request.setBusinessTypeId(nullableLongValue(row, "business_type_id"));
        request.setBusinessTypeSource(stringValue(row, "business_type_source")); request.setUnit(stringValue(row, "unit"));
        request.setDecimalPlaces(nullableLongValue(row, "decimal_places")); request.setFormat(stringValue(row, "format"));
        request.setEnumType(stringValue(row, "enum_type")); request.setEnumSource(stringValue(row, "enum_source"));
        validateAxiomDependencies(request);
        return modelAxiomService.createOmModelAxiom(request);
    }

    /** 按正式模型公理页面规则校验类型专属必填项。 */
    private void validateAxiomDependencies(OmModelAxiomSaveReqVO request) {
        if ("DOMAIN_RANGE".equals(request.getType())) {
            if (request.getRelationName() == null || request.getRelationName().isBlank()
                    || request.getDomainId() == null || request.getRangeId() == null) {
                throw new ServiceException("Domain/Range公理的关系、Domain和Range必须完整，请先修改候选数据");
            }
            return;
        }
        if ("PROPERTY_RANGE".equals(request.getType())) {
            if (request.getObjectTypeId() == null || request.getAttributeId() == null
                    || request.getTypeCategory() == null || request.getTypeCategory().isBlank()) {
                throw new ServiceException("属性范围公理的对象类型、属性和数据类型必须完整，请先修改候选数据");
            }
            return;
        }
        if (request.getObjectTypeId() == null || request.getOtherTypeId() == null) {
            throw new ServiceException("该模型公理的两个对象类型必须完整，请先修改候选数据");
        }
    }

    /** 采纳数据规则，并把条件中的候选编号转换为正式对象及属性编号。 */
    private Long adoptDataRule(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "name", "数据规则名称"); requireText(row, "type", "规则类型");
        requireText(row, "conditions", "失败条件"); requireText(row, "failure_message", "失败提示");
        requireNotExists(formalDataRuleMapper.selectCount(new QueryWrapper<OmDataRuleDO>()
                        .eq("version_id", task.getVersionId())
                        .eq("name", stringValue(row, "name"))
                        .eq("del_flag", 0)),
                "当前版本已存在同名数据规则，不能重复采纳");
        JSONArray candidates = JSON.parseArray(stringValue(row, "conditions"));
        if (candidates == null || candidates.isEmpty()) throw new ServiceException("数据规则至少需要一个失败条件");
        JSONArray conditions = new JSONArray();
        for (Object value : candidates) {
            JSONObject candidate = (JSONObject) value;
            Long objectCandidateId = candidate.getLong("objectCandidateId");
            Long attributeCandidateId = candidate.getLong("attributeCandidateId");
            if (objectCandidateId == null || attributeCandidateId == null) throw new ServiceException("规则条件的对象类型或属性未抽取到，请先修改补充");
            JSONObject condition = new JSONObject();
            condition.put("objectTypeId", longValue(requireAdopted(OmAiCandidateTypeEnum.OBJECT, objectCandidateId, "规则对象类型"), "adopted_id"));
            condition.put("attributeId", longValue(requireAdopted(OmAiCandidateTypeEnum.ATTRIBUTE, attributeCandidateId, "规则对象属性"), "adopted_id"));
            condition.put("operator", candidate.getString("operator")); condition.put("value", candidate.get("value")); conditions.add(condition);
        }
        validateDataRuleConditions(stringValue(row, "type"), conditions);
        OmDataRuleSaveReqVO request = new OmDataRuleSaveReqVO(); request.setVersionId(task.getVersionId());
        request.setName(stringValue(row, "name")); request.setDescription(stringValue(row, "description"));
        request.setType(stringValue(row, "type")); request.setLevel(Optional.ofNullable(stringValue(row, "level")).orElse("INFO"));
        request.setConditionRelation(Optional.ofNullable(stringValue(row, "condition_relation")).orElse("OR"));
        request.setConditions(conditions.toJSONString()); request.setFailureMessage(stringValue(row, "failure_message"));
        return dataRuleService.createOmDataRule(request);
    }

    /** 按正式数据规则页面规则校验条件结构和类型专属值。 */
    private void validateDataRuleConditions(String type, JSONArray conditions) {
        for (Object value : conditions) {
            JSONObject condition = (JSONObject) value;
            if ("RANGE".equals(type)) {
                String operator = condition.getString("operator");
                if (!Set.of("LT", "LE", "EQ", "NE", "GE", "GT").contains(operator)) {
                    throw new ServiceException("取值范围规则的比较符无效，请先修改候选数据");
                }
                try {
                    new java.math.BigDecimal(String.valueOf(condition.get("value")));
                } catch (Exception exception) {
                    throw new ServiceException("取值范围规则的条件值必须为数字，请先修改候选数据");
                }
            } else if (("ENUM".equals(type) || "FORMAT".equals(type))
                    && (condition.get("value") == null || String.valueOf(condition.get("value")).isBlank())) {
                throw new ServiceException("枚举或格式规则的条件值不能为空，请先修改候选数据");
            }
        }
    }

    /** 读取可空候选依赖；存在引用时要求对应候选已采纳。 */
    private Long adoptedDependencyId(OmAiCandidateTypeEnum type, Map<String, Object> row, String key, String label) {
        Long candidateId = nullableLongValue(row, key);
        return candidateId == null ? null : longValue(requireAdopted(type, candidateId, label), "adopted_id");
    }

    /** 校验关联表中的建模目标均已采纳，并返回正式目标编号。 */
    private List<Long> requireLinkedTargetsAdopted(List<Long> candidateTargetIds) {
        List<Long> targetIds = new ArrayList<>();
        for (Long targetCandidateId : candidateTargetIds) {
            Map<String, Object> target = requireAdopted(
                    OmAiCandidateTypeEnum.TARGET, targetCandidateId, "关联建模目标");
            targetIds.add(longValue(target, "adopted_id"));
        }
        return targetIds;
    }

    /** 校验指定依赖候选已采纳。 */
    private Map<String, Object> requireAdopted(OmAiCandidateTypeEnum type, Long id, String dependencyName) {
        Map<String, Object> dependency = getCandidate(type, id);
        if (!OmAiAdoptStatusEnum.ADOPTED.getCode().equals(integerValue(dependency, "adopt_status"))
                || nullableLongValue(dependency, "adopted_id") == null) {
            throw new ServiceException(dependencyName + "尚未采纳，请先采纳关联数据后再操作");
        }
        return dependency;
    }

    /** 校验正式表中不存在满足唯一业务键的数据。 */
    private void requireNotExists(Long count, String message) {
        if (count != null && count > 0) {
            throw new ServiceException(message);
        }
    }

    /** 按依赖顺序采纳任务全部候选数据。 */
    @Override
    public Map<String, Integer> adoptAll(Long taskId) {
        int success = 0;
        int failed = 0;
        List<OmAiCandidateTypeEnum> order = List.of(OmAiCandidateTypeEnum.TARGET, OmAiCandidateTypeEnum.TERM,
                OmAiCandidateTypeEnum.ENUM_TYPE, OmAiCandidateTypeEnum.OBJECT,
                OmAiCandidateTypeEnum.ATTRIBUTE, OmAiCandidateTypeEnum.RELATION,
                OmAiCandidateTypeEnum.AXIOM, OmAiCandidateTypeEnum.DATA_RULE);
        for (OmAiCandidateTypeEnum type : order) {
            for (Long candidateId : selectPendingCandidateIds(type, taskId)) {
                try {
                    adopt(type.getCode(), candidateId);
                    success++;
                } catch (RuntimeException exception) {
                    failed++;
                }
            }
        }
        return Map.of("success", success, "failed", failed);
    }

    /** 查询候选数据，不存在时抛出业务异常。 */
    private Map<String, Object> getCandidate(OmAiCandidateTypeEnum type, Long id) {
        Object candidate = switch (type) {
            case TARGET -> aiTargetMapper.selectById(id);
            case TERM -> aiTermMapper.selectById(id);
            case ENUM_TYPE -> aiEnumTypeMapper.selectById(id);
            case ENUM_DATA -> aiEnumDataMapper.selectById(id);
            case OBJECT -> aiObjectMapper.selectById(id);
            case ATTRIBUTE -> aiAttributeMapper.selectById(id);
            case RELATION -> aiRelationMapper.selectById(id);
            case AXIOM -> aiAxiomMapper.selectById(id);
            case DATA_RULE -> aiDataRuleMapper.selectById(id);
        };
        if (candidate == null) {
            throw new ServiceException("候选数据不存在");
        }
        return JSON.parseObject(JSON.toJSONString(candidate), Map.class);
    }

    /** 写入候选数据采纳日志。 */
    private void insertAdoptLog(Map<String, Object> row, OmAiCandidateTypeEnum type, Long formalId,
                                OmAiAdoptStatusEnum status, String message) {
        OmAiAdoptLogDO log = new OmAiAdoptLogDO();
        log.setTaskId(longValue(row, "task_id"));
        log.setDataType(type.getCode());
        log.setDataId(longValue(row, "id"));
        log.setFormalId(formalId);
        log.setStatus(status.getCode());
        log.setMessage(message);
        adoptLogMapper.insert(log);
    }

    /** 查询指定候选类型下尚未采纳的数据编号。 */
    private List<Long> selectPendingCandidateIds(OmAiCandidateTypeEnum type, Long taskId) {
        Integer adoptedStatus = OmAiAdoptStatusEnum.ADOPTED.getCode();
        return switch (type) {
            case TARGET -> aiTargetMapper.selectList(new QueryWrapper<OmAiTargetDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiTargetDO::getId).toList();
            case TERM -> aiTermMapper.selectList(new QueryWrapper<OmAiTermDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiTermDO::getId).toList();
            case ENUM_TYPE -> aiEnumTypeMapper.selectList(new QueryWrapper<OmAiEnumTypeDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiEnumTypeDO::getId).toList();
            case ENUM_DATA -> List.of();
            case OBJECT -> aiObjectMapper.selectList(new QueryWrapper<OmAiObjectDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiObjectDO::getId).toList();
            case ATTRIBUTE -> aiAttributeMapper.selectList(new QueryWrapper<OmAiAttributeDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiAttributeDO::getId).toList();
            case RELATION -> aiRelationMapper.selectList(new QueryWrapper<OmAiRelationDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiRelationDO::getId).toList();
            case AXIOM -> aiAxiomMapper.selectList(new QueryWrapper<OmAiAxiomDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiAxiomDO::getId).toList();
            case DATA_RULE -> aiDataRuleMapper.selectList(new QueryWrapper<OmAiDataRuleDO>()
                            .select("id").eq("task_id", taskId).ne("adopt_status", adoptedStatus)
                            .eq("del_flag", 0).orderByAsc("id"))
                    .stream().map(OmAiDataRuleDO::getId).toList();
        };
    }

    /** 更新候选数据采纳状态。 */
    private void updateCandidateAdoptStatus(OmAiCandidateTypeEnum type, Long id,
                                            OmAiAdoptStatusEnum status, Long formalId, String message) {
        switch (type) {
            case TARGET -> aiTargetMapper.updateById(adoptTargetStatus(id, status, formalId, message));
            case TERM -> aiTermMapper.updateById(adoptTermStatus(id, status, formalId, message));
            case ENUM_TYPE -> aiEnumTypeMapper.updateById(adoptEnumTypeStatus(id, status, formalId, message));
            case ENUM_DATA -> aiEnumDataMapper.updateById(adoptEnumDataStatus(id, status, formalId, message));
            case OBJECT -> aiObjectMapper.updateById(adoptObjectStatus(id, status, formalId, message));
            case ATTRIBUTE -> aiAttributeMapper.updateById(adoptAttributeStatus(id, status, formalId, message));
            case RELATION -> aiRelationMapper.updateById(adoptRelationStatus(id, status, formalId, message));
            case AXIOM -> aiAxiomMapper.updateById(adoptAxiomStatus(id, status, formalId, message));
            case DATA_RULE -> aiDataRuleMapper.updateById(adoptDataRuleStatus(id, status, formalId, message));
        }
    }

    private OmAiTargetDO adoptTargetStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiTargetDO candidate = new OmAiTargetDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiTermDO adoptTermStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiTermDO candidate = new OmAiTermDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiEnumTypeDO adoptEnumTypeStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiEnumTypeDO candidate = new OmAiEnumTypeDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiEnumDataDO adoptEnumDataStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiEnumDataDO candidate = new OmAiEnumDataDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiObjectDO adoptObjectStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiObjectDO candidate = new OmAiObjectDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiAttributeDO adoptAttributeStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiAttributeDO candidate = new OmAiAttributeDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiRelationDO adoptRelationStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiRelationDO candidate = new OmAiRelationDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiAxiomDO adoptAxiomStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiAxiomDO candidate = new OmAiAxiomDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    private OmAiDataRuleDO adoptDataRuleStatus(Long id, OmAiAdoptStatusEnum status, Long formalId, String message) {
        OmAiDataRuleDO candidate = new OmAiDataRuleDO();
        candidate.setId(id);
        candidate.setAdoptStatus(status.getCode());
        candidate.setAdoptedId(formalId);
        candidate.setAdoptMessage(message);
        return candidate;
    }

    /** 查询任务，不存在时抛出业务异常。 */
    private OmAiTaskDO getRequiredTask(Long id) {
        OmAiTaskDO task = taskMapper.selectById(id);
        if (task == null) {
            throw new ServiceException("AI建模任务不存在");
        }
        return task;
    }

    /** 读取整数值，空值按零处理。 */
    private int integerValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? 0 : ((Number) value).intValue();
    }

    /** 读取可空整数值。 */
    private Integer nullableIntegerValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : ((Number) value).intValue();
    }

    /** 读取必填长整数值。 */
    private Long longValue(Map<String, Object> row, String key) {
        return ((Number) row.get(key)).longValue();
    }

    /** 读取可空长整数值。 */
    private Long nullableLongValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : ((Number) value).longValue();
    }

    /** 读取可空字符串值。 */
    private String stringValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /** 采纳前校验正式功能必填文本，提示用户先修改候选数据。 */
    private void requireText(Map<String, Object> row, String key, String label) {
        String value = stringValue(row, key);
        if (value == null || value.isBlank()) {
            throw new ServiceException(label + "未抽取到，请先修改补充后再采纳");
        }
    }

    /** 读取字符串，空值转换为空字符串。 */
    private String emptyIfNull(Map<String, Object> row, String key) {
        return Optional.ofNullable(stringValue(row, key)).orElse("");
    }

    /** 兼容 Boolean、bit 和数值形式的布尔值。 */
    private boolean booleanValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return Boolean.TRUE.equals(value) || value instanceof Number number && number.intValue() == 1;
    }

    /** 限制数据库错误信息长度。 */
    private String limitMessage(String message) {
        String normalized = message == null ? "采纳失败" : message;
        return normalized.substring(0, Math.min(1000, normalized.length()));
    }

    /** 将候选优先级数值转换为正式枚举。 */
    private TargetPriorityEnum toPriority(int value) {
        if (value == 3) {
            return TargetPriorityEnum.HIGH;
        }
        if (value == 1) {
            return TargetPriorityEnum.LOW;
        }
        return TargetPriorityEnum.MIDDLE;
    }
}
