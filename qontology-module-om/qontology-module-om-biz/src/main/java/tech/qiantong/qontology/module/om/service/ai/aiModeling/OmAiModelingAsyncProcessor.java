package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.ai.api.chat.AiChatMedia;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.module.ai.api.modelMarket.IAiModelApiService;
import tech.qiantong.qontology.module.basic.dal.mapper.dataType.BasicDataTypeMapper;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumDataMapper;
import tech.qiantong.qontology.core.domain.BaseEntity;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAttribute.OmAiAttributeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiAxiom.OmAiAxiomDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataEvidence.OmAiDataEvidenceDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiDataRule.OmAiDataRuleDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumData.OmAiEnumDataDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEnumType.OmAiEnumTypeDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObject.OmAiObjectDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiObjectTarget.OmAiObjectTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelation.OmAiRelationDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiRelationTarget.OmAiRelationTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTarget.OmAiTargetDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTerm.OmAiTermDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAxiom.OmAiAxiomMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataEvidence.OmAiDataEvidenceMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiDataRule.OmAiDataRuleMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEvidence.OmAiEvidenceMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiAttribute.OmAiAttributeMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumData.OmAiEnumDataMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEnumType.OmAiEnumTypeMapper;
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
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumDataMapper;
import tech.qiantong.qontology.module.om.dal.mapper.enumeration.OmEnumTypeMapper;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAxiomTypeEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiDataRuleTypeEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiDateFormatEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiExtractionRoundEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiModelingStageEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiRangeOperatorEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiCandidateTypeEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiEvidenceRoleEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiFileParseStatusEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiStageStatusEnum;
import tech.qiantong.qontology.module.om.enums.ai.OmAiTaskStatusEnum;

/**
 * AI 辅助建模异步解析处理器。
 *
 * <p>按照文档解析、证据索引、四轮顺序抽取和结果汇总的 Harness 流程执行。
 */
@Slf4j
@Component
public class OmAiModelingAsyncProcessor {

    private static final int WINDOW_SIZE = 30;
    private static final int WINDOW_OVERLAP = 2;
    /** AI 辅助建模固定使用的通义模型配置。 */
    private static final Long MODEL_KEY_ID = 1L;
    private static final String TEXT_MODEL_NAME = "qwen3.6-plus";
    private static final String VISION_MODEL_NAME = "qwen3.6-plus";
    private final Set<Long> runningTaskIds = ConcurrentHashMap.newKeySet();
    private final ThreadLocal<Long> currentCreatorId = new ThreadLocal<>();
    private final ThreadLocal<String> currentCreateBy = new ThreadLocal<>();
    private final ThreadLocal<Long> currentStageId = new ThreadLocal<>();
    private final ThreadLocal<OmAiStageDO> currentStage = new ThreadLocal<>();


    @Resource
    private OmAiTaskMapper taskMapper;

    @Resource
    private OmAiFileMapper fileMapper;

    @Resource
    private OmAiStageMapper stageMapper;

    @Resource
    private OmAiEvidenceMapper evidenceMapper;

    @Resource
    private IAiModelApiService modelApi;

    @Resource
    private AiChatService chatService;

    @Resource
    private OmAiPromptLoader promptLoader;

    @Resource
    private OmAiDocumentParser documentParser;

    @Resource
    private OmAiTargetMapper targetMapper;
    @Resource
    private OmAiEnumTypeMapper enumTypeMapper;
    @Resource
    private OmAiEnumDataMapper enumDataMapper;
    @Resource
    private OmAiObjectMapper objectMapper;
    @Resource
    private OmAiAttributeMapper attributeMapper;
    @Resource
    private OmAiRelationMapper relationMapper;
    @Resource
    private OmAiTermMapper termMapper;
    @Resource
    private OmAiAxiomMapper axiomMapper;
    @Resource
    private OmAiDataRuleMapper dataRuleMapper;
    @Resource
    private OmAiObjectTargetMapper objectTargetMapper;
    @Resource
    private OmAiRelationTargetMapper relationTargetMapper;
    @Resource
    private OmAiDataEvidenceMapper dataEvidenceMapper;
    @Resource
    private OmDataTypeMapper dataTypeMapper;
    @Resource
    private BasicDataTypeMapper basicDataTypeMapper;
    @Resource
    private OmEnumDataMapper formalEnumDataMapper;
    @Resource
    private OmEnumTypeMapper formalEnumTypeMapper;
    @Resource
    private BasicEnumDataMapper basicEnumDataMapper;

    @Resource
    private OmAiModelingStreamPublisher streamPublisher;

    /**
     * 异步执行 AI 辅助建模任务。
     *
     * @param taskId 任务编号
     */
    public void process(Long taskId) {
        OmAiTaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            runningTaskIds.remove(taskId);
            return;
        }
        try {
            startTask(task);
            initializeStages(task);
            runStage(task, OmAiModelingStageEnum.FILE_PARSE.getCode(), () -> parseSourceFiles(taskId));
            runStage(task, OmAiModelingStageEnum.EVIDENCE_INDEX.getCode(), this::summarizeEvidenceIndex);
            for (OmAiExtractionRoundEnum round : OmAiExtractionRoundEnum.values()) {
                runStage(task, round.getStage().getCode(), () -> executeRound(task, round));
            }
            runStage(task, OmAiModelingStageEnum.FINISH.getCode(), () -> finishTask(task));
            // 结果汇总阶段先完成推送，再发送任务完成事件并结束页面流。
            streamPublisher.publishTask(task);
        } catch (Exception exception) {
            failTask(task, exception);
        } finally {
            runningTaskIds.remove(taskId);
        }
    }

    /** 判断任务是否确实在当前应用进程的后台线程中执行。 */
    public boolean isRunning(Long taskId) {
        return taskId != null && runningTaskIds.contains(taskId);
    }

    /** 在线程池提交前预占任务，消除状态已改为解析中但后台线程尚未启动的重复提交窗口。 */
    public boolean reserve(Long taskId) {
        return taskId != null && runningTaskIds.add(taskId);
    }

    /** 后台任务提交失败时释放预占。 */
    public void release(Long taskId) {
        if (taskId != null) {
            runningTaskIds.remove(taskId);
        }
    }

    /** 将任务更新为解析中状态。 */
    private void startTask(OmAiTaskDO task) {
        task.setStatus(OmAiTaskStatusEnum.RUNNING.getCode());
        task.setCurrentStage(OmAiModelingStageEnum.FILE_PARSE.getCode());
        task.setStartedTime(new Date());
        task.setFailureReason(null);
        taskMapper.updateById(task);
        streamPublisher.publishTask(task);
    }

    /** 初始化解析时间线。 */
    public void initializeStages(OmAiTaskDO task) {
        if (stageMapper.selectCount(new QueryWrapper<OmAiStageDO>().eq("task_id", task.getId())) > 0) {
            return;
        }
        int sequence = 0;
        for (OmAiModelingStageEnum definition : OmAiModelingStageEnum.values()) {
            stageMapper.insert(OmAiStageDO.builder()
                    .taskId(task.getId())
                    .stageCode(definition.getCode())
                    .stageName(definition.getLabel())
                    .status(OmAiStageStatusEnum.WAITING.getCode())
                    .progress(0)
                    .summary(definition.getThinking())
                    .sequenceNo(++sequence)
                    .build());
        }
    }

    /** 执行单个时间线阶段并记录结果。 */
    private void runStage(OmAiTaskDO task, String stageCode, StageAction action) {
        OmAiStageDO stage = stageMapper.selectOne(new QueryWrapper<OmAiStageDO>()
                .eq("task_id", task.getId())
                .eq("stage_code", stageCode));
        long startedAt = System.currentTimeMillis();
        stage.setStatus(OmAiStageStatusEnum.RUNNING.getCode());
        stage.setProgress(10);
        stage.setSummary("正在执行：" + stage.getStageName() + "。思考过程：" + stageThinking(stageCode));
        stage.setStartedTime(new Date());
        stageMapper.updateById(stage);
        currentStageId.set(stage.getId());
        currentStage.set(stage);
        streamPublisher.publishStage(stage);
        task.setCurrentStage(stageCode);
        taskMapper.updateById(task);
        streamPublisher.publishTask(task);
        try {
            String result = action.run();
            stage.setSummary(stage.getSummary() != null && stage.getSummary().contains("模型")
                    ? trim(stage.getSummary() + "\n\n阶段完成：" + result, 2000)
                    : trim(result, 2000));
            stage.setStatus(OmAiStageStatusEnum.SUCCESS.getCode());
            stage.setProgress(100);
        } catch (Exception exception) {
            stage.setStatus(OmAiStageStatusEnum.FAILED.getCode());
            stage.setErrorMessage(trim(exception.getMessage(), 2000));
            throw exception;
        } finally {
            stage.setFinishedTime(new Date());
            stage.setDurationMs(System.currentTimeMillis() - startedAt);
            stageMapper.updateById(stage);
            streamPublisher.publishStage(stage);
            currentStageId.remove();
            currentStage.remove();
        }
    }

    /** 汇总文档解析结果。 */
    private String parseSourceFiles(Long taskId) {
        List<OmAiFileDO> files = fileMapper.selectList(
                new QueryWrapper<OmAiFileDO>().eq("task_id", taskId).orderByAsc("id"));
        if (files.isEmpty()) {
            throw new IllegalStateException("任务没有可解析的原始文件");
        }
        int totalBlocks = 0;
        int completedFiles = 0;
        for (OmAiFileDO file : files) {
            updateStageProgress(
                    10 + (completedFiles * 75 / files.size()),
                    "正在解析文件 " + (completedFiles + 1) + "/" + files.size() + "：" + file.getFileName(),
                    null, completedFiles, totalBlocks);
            try {
                List<OmAiDocumentParser.Block> blocks = documentParser.parse(
                        file.getFileName(), download(file.getFileUrl()));
                if (blocks.isEmpty()) {
                    throw new IllegalStateException("文件未解析出有效内容：" + file.getFileName());
                }
                for (OmAiDocumentParser.Block block : blocks) {
                    evidenceMapper.insert(OmAiEvidenceDO.builder()
                            .taskId(taskId).fileId(file.getId()).blockNo(block.no()).blockType(block.type())
                            .pageNo(block.pageNo()).chapterPath(block.chapterPath()).paragraphNo(block.paragraphNo())
                            .tableNo(block.tableNo()).rowNo(block.rowNo()).columnNo(block.columnNo())
                            .imageNo(block.imageNo()).lineStart(block.lineStart()).lineEnd(block.lineEnd())
                            .content(block.content()).contentHash(org.apache.commons.codec.digest.DigestUtils.sha256Hex(block.content()))
                            .build());
                }
                file.setParseStatus(OmAiFileParseStatusEnum.SUCCESS.getCode());
                file.setPageCount(blocks.stream().map(OmAiDocumentParser.Block::pageNo)
                        .filter(Objects::nonNull).max(Integer::compareTo).orElse(null));
                file.setBlockCount(blocks.size());
                fileMapper.updateById(file);
                totalBlocks += blocks.size();
                completedFiles++;
                updateStageProgress(
                        10 + (completedFiles * 80 / files.size()),
                        "已完成 " + completedFiles + "/" + files.size() + " 个文件，当前累计生成 " + totalBlocks + " 个证据块。",
                        null, completedFiles, totalBlocks);
            } catch (Exception exception) {
                file.setParseStatus(OmAiFileParseStatusEnum.FAILED.getCode());
                fileMapper.updateById(file);
                throw new IllegalStateException("文件解析失败：" + file.getFileName() + "，" + exception.getMessage(), exception);
            }
        }
        return "已解析 " + files.size() + " 个文件，生成 " + totalBlocks + " 个带原文位置的证据块";
    }

    /** 汇总证据索引策略。 */
    private String summarizeEvidenceIndex() {
        return "已按文件、页码、章节、段落和表格位置建立证据索引；抽取轮次只读取证据窗口和前序候选索引";
    }

    /** 执行一轮文本和图片抽取。 */
    private String executeRound(OmAiTaskDO task, OmAiExtractionRoundEnum round) {
        List<OmAiEvidenceDO> evidenceList = evidenceMapper.selectList(
                new QueryWrapper<OmAiEvidenceDO>()
                        .eq("task_id", task.getId())
                        .orderByAsc("file_id", "block_no"));
        if (evidenceList.isEmpty()) {
            throw new IllegalStateException("没有可用于本轮抽取的证据，任务已终止");
        }
        int textWindowCount = Math.max(1, (evidenceList.size() + WINDOW_SIZE - 1) / WINDOW_SIZE);
        int imageCount = Math.toIntExact(fileMapper.selectCount(new QueryWrapper<OmAiFileDO>()
                .eq("task_id", task.getId()).in("file_ext", List.of("png", "jpg", "jpeg"))));
        int totalCalls = textWindowCount + imageCount;
        int[] completedCalls = {0};
        int textCalls = executeTextWindows(task, round, evidenceList, totalCalls, completedCalls);
        int visionCalls = executeImageFiles(task, round, totalCalls, completedCalls);
//        return round.name() + "完成：文本窗口调用 " + textCalls + " 次，多模态调用 " + visionCalls + " 次";
        return round.getStage().getLabel();
    }

    /** 按窗口执行文本抽取。 */
    private int executeTextWindows(OmAiTaskDO task, OmAiExtractionRoundEnum round, List<OmAiEvidenceDO> evidenceList,
                                   int totalCalls, int[] completedCalls) {
        int calls = 0;
        for (int start = 0; start < evidenceList.size(); start += WINDOW_SIZE) {
            int fromIndex = Math.max(0, start - WINDOW_OVERLAP);
            int toIndex = Math.min(evidenceList.size(), start + WINDOW_SIZE + WINDOW_OVERLAP);
            String prompt = buildRoundPrompt(
                    task.getId(), round.getPromptResource(), evidenceList.subList(fromIndex, toIndex));
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "正在调用文本模型分析证据窗口 " + (calls + 1) + "，范围 " + (fromIndex + 1) + "-" + toIndex
                            + "，重点识别：" + stageThinking(round.getStage().getCode()),
                    TEXT_MODEL_NAME, toIndex - fromIndex, completedCalls[0]);
            AiChatRequest request = auditableStreamingRequest(
                    modelApi.getModelRef(MODEL_KEY_ID, TEXT_MODEL_NAME),
                    List.of(AiChatMessage.user(prompt)), 8000);
            String responseContent = streamModelResponse(
                    request, TEXT_MODEL_NAME, callProgress(completedCalls[0], totalCalls),
                    "文本证据窗口 " + (calls + 1));
            handleRoundResponse(task, round, responseContent);
            calls++;
            completedCalls[0]++;
            String analysisSummary = extractAnalysisSummary(responseContent);
            updateStageReasoning(joinReasoning(
                    Optional.ofNullable(currentStage.get()).map(OmAiStageDO::getReasoningContent).orElse(""),
                    "文本证据窗口 " + calls + " 分析结论", analysisSummary));
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "文本模型已完成第 " + calls + " 个证据窗口分析。模型分析摘要：" + analysisSummary,
                    TEXT_MODEL_NAME, toIndex - fromIndex, completedCalls[0]);
        }
        return calls;
    }

    /** 对图片文件执行同一轮多模态抽取。 */
    private int executeImageFiles(OmAiTaskDO task, OmAiExtractionRoundEnum round, int totalCalls, int[] completedCalls) {
        int calls = 0;
        List<OmAiFileDO> files = fileMapper.selectList(
                new QueryWrapper<OmAiFileDO>().eq("task_id", task.getId()));
        for (OmAiFileDO file : files) {
            if (!Set.of("png", "jpg", "jpeg").contains(file.getFileExt())) {
                continue;
            }
            String prompt = promptLoader.load(round.getPromptResource())
                    + "\n\n前序候选索引：" + loadEntityIndex(task.getId())
                    + "\n\n当前图片文件：" + file.getFileName();
            AiChatMessage message = AiChatMessage.user(
                    prompt,
                    List.of(new AiChatMedia(mime(file.getFileExt()), download(file.getFileUrl()), file.getFileName())));
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "正在调用多模态模型分析图片：" + file.getFileName() + "，重点识别：" + stageThinking(round.getStage().getCode()),
                    VISION_MODEL_NAME, 1, completedCalls[0]);
            AiChatRequest request = auditableStreamingRequest(
                    modelApi.getModelRef(MODEL_KEY_ID, VISION_MODEL_NAME),
                    List.of(message), 8000);
            String responseContent = streamModelResponse(
                    request, VISION_MODEL_NAME, callProgress(completedCalls[0], totalCalls),
                    "图片 " + file.getFileName());
            handleRoundResponse(task, round, responseContent);
            calls++;
            completedCalls[0]++;
            updateStageReasoning(joinReasoning(
                    Optional.ofNullable(currentStage.get()).map(OmAiStageDO::getReasoningContent).orElse(""),
                    "图片 " + file.getFileName() + " 分析结论", extractAnalysisSummary(responseContent)));
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "多模态模型已完成图片 " + file.getFileName() + " 的分析。模型分析摘要："
                            + extractAnalysisSummary(responseContent),
                    VISION_MODEL_NAME, 1, completedCalls[0]);
        }
        return calls;
    }

    /** 构建当前轮次的文本提示词。 */
    private String buildRoundPrompt(Long taskId, String promptResource, List<OmAiEvidenceDO> evidenceWindow) {
        StringBuilder source = new StringBuilder();
        for (OmAiEvidenceDO evidence : evidenceWindow) {
            source.append("[block=")
                    .append(evidence.getId())
                    .append(", file=")
                    .append(evidence.getFileId())
                    .append(", page=")
                    .append(evidence.getPageNo())
                    .append("] " )
                    .append(evidence.getContent())
                    .append('\n');
        }
        return promptLoader.load(promptResource)
                + "\n\n前序候选索引：" + loadEntityIndex(taskId)
                + "\n\n本批原始证据：\n" + source;
    }

    /** 处理当前轮次模型响应。 */
    private void handleRoundResponse(OmAiTaskDO task, OmAiExtractionRoundEnum round, String content) {
        persistCandidates(task, content);
    }

    /** 完成任务并统计候选数量。 */
    private String finishTask(OmAiTaskDO task) {
        Integer count = taskMapper.countAllCandidates(task.getId());
        if (count == null || count == 0) {
            throw new IllegalStateException("四轮抽取未生成任何候选数据，请检查模型配置、提示词或模型返回格式");
        }
        task.setCandidateCount(count);
        task.setStatus(OmAiTaskStatusEnum.SUCCESS.getCode());
        task.setCurrentStage(OmAiModelingStageEnum.FINISH.getCode());
        task.setFinishedTime(new Date());
        taskMapper.updateById(task);
        return "四轮抽取完成，共生成 " + count + " 条待审核候选数据";
    }

    /** 将任务和运行中阶段更新为失败。 */
    private void failTask(OmAiTaskDO task, Exception exception) {
        log.error("AI 建模任务执行失败，taskId={}", task.getId(), exception);
        task.setStatus(OmAiTaskStatusEnum.FAILED.getCode());
        task.setFailureReason(trim(exception.getMessage(), 1000));
        task.setFinishedTime(new Date());
        taskMapper.updateById(task);
        stageMapper.update(null, new UpdateWrapper<OmAiStageDO>()
                .eq("task_id", task.getId())
                .eq("status", OmAiStageStatusEnum.RUNNING.getCode())
                .set("status", OmAiStageStatusEnum.FAILED.getCode())
                .set("error_message", trim(exception.getMessage(), 2000))
                .set("finished_time", new Date()));
        streamPublisher.publishTask(task);
    }

    /** 读取媒体文件。 */
    private byte[] download(String url) {
        try (var input = new URL(url).openStream()) {
            return input.readAllBytes();
        } catch (Exception exception) {
            throw new IllegalStateException("媒体文件读取失败：" + url, exception);
        }
    }

    /** 获得图片 MIME 类型。 */
    private String mime(String extension) {
        return Set.of("jpg", "jpeg").contains(extension) ? "image/jpeg" : "image/png";
    }

    /** 加载前序候选索引。 */
    private String loadEntityIndex(Long taskId) {
        Map<String, Object> index = new LinkedHashMap<>();
        index.put("targets", targetMapper.selectCandidateIndex(taskId));
        index.put("enumTypes", enumTypeMapper.selectCandidateIndex(taskId));
        index.put("enumValues", enumDataMapper.selectCandidateIndex(taskId));
        index.put("objects", objectMapper.selectCandidateIndex(taskId));
        index.put("attributes", attributeMapper.selectCandidateIndex(taskId));
        index.put("relations", relationMapper.selectCandidateIndex(taskId));
        index.put("projectBusinessTypes", dataTypeMapper.selectAiOptionsByTaskId(taskId));
        index.put("globalBusinessTypes", basicDataTypeMapper.selectAiOptions());
        return JSON.toJSONString(index);
    }

    /** 解析并保存候选数据。 */
    private void persistCandidates(OmAiTaskDO task, String rawContent) {
        Long taskId = task.getId();
        currentCreatorId.set(task.getCreatorId());
        currentCreateBy.set(task.getCreateBy());
        try {
            JSONObject root = parseJsonObject(rawContent);
            persistTargets(taskId, root.getJSONArray("targets"));
            persistTerms(taskId, root.getJSONArray("terms"));
            persistEnums(taskId, root.getJSONArray("enumTypes"));
            persistObjects(taskId, root.getJSONArray("objects"));
            resolveObjectDependencies(taskId, root.getJSONArray("objects"));
            persistAttributes(taskId, root.getJSONArray("attributes"));
            persistRelations(taskId, root.getJSONArray("relations"));
            JSONArray axioms = firstArray(root, "axioms", "modelAxioms");
            JSONArray dataRules = firstArray(root, "dataRules", "rules");
            int axiomBefore = countCandidates(OmAiCandidateTypeEnum.AXIOM, taskId);
            int dataRuleBefore = countCandidates(OmAiCandidateTypeEnum.DATA_RULE, taskId);
            persistAxioms(taskId, axioms);
            persistDataRules(taskId, dataRules);
            int axiomSaved = countCandidates(OmAiCandidateTypeEnum.AXIOM, taskId) - axiomBefore;
            int dataRuleSaved = countCandidates(OmAiCandidateTypeEnum.DATA_RULE, taskId) - dataRuleBefore;
            log.info("第4轮候选落库完成，taskId={}, 公理返回={}, 公理保存={}, 规则返回={}, 规则保存={}",
                    taskId, axioms == null ? 0 : axioms.size(), axiomSaved,
                    dataRules == null ? 0 : dataRules.size(), dataRuleSaved);
            if (axioms != null && !axioms.isEmpty() && axiomSaved == 0) {
                throw new IllegalStateException("模型返回了模型公理，但全部未通过对象和关系引用校验，请查看第4轮过滤日志");
            }
            if (dataRules != null && !dataRules.isEmpty() && dataRuleSaved == 0) {
                throw new IllegalStateException("模型返回了数据规则，但全部未通过属性引用和类型校验，请查看第4轮过滤日志");
            }
            refreshTargetRelatedElementCounts(taskId);
        } finally {
            currentCreatorId.remove();
            currentCreateBy.remove();
        }
    }

    /** 统计指定类型的候选数据量。 */
    private int countCandidates(OmAiCandidateTypeEnum type, Long taskId) {
        Long count = switch (type) {
            case AXIOM -> axiomMapper.selectCount(
                    new QueryWrapper<OmAiAxiomDO>().eq("task_id", taskId).eq("del_flag", 0));
            case DATA_RULE -> dataRuleMapper.selectCount(
                    new QueryWrapper<OmAiDataRuleDO>().eq("task_id", taskId).eq("del_flag", 0));
            default -> throw new IllegalArgumentException("不支持的候选统计类型：" + type.getLabel());
        };
        return count.intValue();
    }

    /** 保存建模目标，抽取缺省值统一按低优先级、待验证处理。 */
    private void persistTargets(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            OmAiTargetDO candidate = new OmAiTargetDO();
            candidate.setTaskId(taskId);
            candidate.setCandidateKey(object.getString("candidateKey"));
            candidate.setQuestion(object.getString("question"));
            candidate.setQuestionSource(resolveEvidenceSource(
                    taskId, object.getJSONArray("evidenceBlockNos"), object.getString("questionSource")));
            candidate.setBusinessScenarios(object.getString("businessScenarios"));
            Integer priority = object.getInteger("priority");
            candidate.setPriority(priority == null || priority < 1 || priority > 3 ? 1 : priority);
            candidate.setAcceptCriterion(object.getString("acceptCriterion"));
            candidate.setAcceptStatus("0");
            candidate.setRelatedElementCount(0);
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            targetMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.TARGET, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    /** 保存术语；来源字段使用模型引用的原始证据内容，不能保存证据编号。 */
    private void persistTerms(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            OmAiTermDO candidate = new OmAiTermDO();
            candidate.setTaskId(taskId);
            candidate.setStdName(object.getString("stdName"));
            candidate.setEnName(object.getString("enName"));
            candidate.setAlias(String.valueOf(normalizeCandidateField(object.get("alias"))));
            candidate.setAbbr(object.getString("abbr"));
            candidate.setSource(resolveEvidenceSource(
                    taskId, object.getJSONArray("evidenceBlockNos"), object.getString("source")));
            candidate.setDescription(object.getString("description"));
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            termMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.TERM, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    /**
     * 根据模型引用的证据 ID 反查原文，作为建模目标和术语的业务来源内容。
     * 模型误把 84、91 等证据编号写进来源字段时不再直接保存。
     */
    private String resolveEvidenceSource(Long taskId, JSONArray evidenceIds, String modelSource) {
        if (evidenceIds != null && !evidenceIds.isEmpty()) {
            List<Long> ids = new ArrayList<>();
            for (Object value : evidenceIds) {
                try {
                    ids.add(Long.valueOf(String.valueOf(value)));
                } catch (NumberFormatException ignored) {
                    // 非法引用交给证据绑定及后续审核处理，不作为来源内容。
                }
            }
            if (!ids.isEmpty()) {
                List<String> contents = evidenceMapper.selectContents(taskId, ids);
                String source = contents.stream().filter(Objects::nonNull).map(String::trim)
                        .filter(item -> !item.isBlank()).distinct()
                        .collect(java.util.stream.Collectors.joining("；"));
                if (!source.isBlank()) {
                    return trim(source, 255);
                }
            }
        }
        String fallback = Optional.ofNullable(modelSource).orElse("").trim();
        return fallback.matches("[\\d\\s,，、;；]+") ? null : trim(fallback, 255);
    }

    /** 汇总对象类型和关系对建模目标的关联数量。 */
    private void refreshTargetRelatedElementCounts(Long taskId) {
        targetMapper.refreshRelatedElementCounts(taskId);
    }

    /** 保存对象类型候选。 */
    private void persistObjects(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            OmAiObjectDO candidate = new OmAiObjectDO();
            candidate.setTaskId(taskId);
            candidate.setName(object.getString("name"));
            candidate.setDescription(object.getString("description"));
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            objectMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.OBJECT, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    /** 截取模型返回的 JSON 对象。 */
    private JSONObject parseJsonObject(String rawContent) {
        String content = Optional.ofNullable(rawContent).orElse("{}").trim();
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start < 0 || end < start) {
            throw new IllegalArgumentException("模型未返回 JSON 对象");
        }
        return JSON.parseObject(content.substring(start, end + 1));
    }

    /** 提取模型显式返回的可审计分析摘要，不展示模型内部隐藏推理链。 */
    private String extractAnalysisSummary(String rawContent) {
        try {
            String summary = parseJsonObject(rawContent).getString("analysisSummary");
            return summary == null || summary.isBlank() || !containsChinese(summary)
                    ? "模型已完成本批证据分析并返回结构化候选结果。"
                    : trim(summary, 1200);
        } catch (Exception ignored) {
            return "模型已完成本批证据分析并返回结构化候选结果。";
        }
    }

    /** 判断模型摘要是否包含中文，避免供应商忽略提示词后将英文分析写入页面。 */
    private boolean containsChinese(String value) {
        return value != null && value.codePoints()
                .anyMatch(codePoint -> codePoint >= 0x4E00 && codePoint <= 0x9FFF);
    }

    /** 流式调用模型，页面仅展示中文、可审计的分析进度，不透传供应商隐藏推理。 */
    private String streamModelResponse(AiChatRequest request, String modelName, int progress, String batchName) {
        StringBuilder content = new StringBuilder();
        StringBuilder modelReasoning = new StringBuilder();
        Long streamingStageId = currentStageId.get();
        OmAiStageDO streamingStage = currentStage.get();
        if (streamingStageId == null || streamingStage == null) {
            throw new IllegalStateException("模型流式调用缺少当前阶段上下文");
        }
        String previousReasoning = Optional.of(streamingStage)
                .map(OmAiStageDO::getReasoningContent).orElse("");
        long[] lastUpdateTime = {0L};
        int[] lastContentLength = {-1};
        int[] lastReasoningLength = {-1};
        String[] lastAnalysisProcess = {""};
        chatService.stream(request, chunk -> {
            if (chunk.content() != null && !chunk.content().isEmpty()) {
                content.append(chunk.content());
            }
            if (chunk.reasoningContent() != null && !chunk.reasoningContent().isEmpty()) {
                modelReasoning.append(chunk.reasoningContent());
            }
            String analysisProcess = extractStreamingJsonString(content.toString(), "analysisProcess");
            long now = System.currentTimeMillis();
            boolean streamChanged = content.length() != lastContentLength[0]
                    || modelReasoning.length() != lastReasoningLength[0]
                    || !analysisProcess.equals(lastAnalysisProcess[0]);
            if (chunk.finished() || (streamChanged && now - lastUpdateTime[0] >= 1000L)) {
                String currentReasoning = composeVisibleReasoning(modelReasoning.toString(), analysisProcess);
                updateStageProgress(streamingStageId, streamingStage, progress,
                        analysisProcess.isBlank()
                                ? "正在分析" + batchName + "，模型推理过程正在流式输出。"
                                : "正在分析" + batchName + "，结构化中文分析过程正在流式输出。",
                        modelName, null, modelReasoning.length() + content.length());
                updateStageReasoning(streamingStageId, streamingStage,
                        joinReasoning(previousReasoning, batchName, currentReasoning));
                lastUpdateTime[0] = now;
                lastContentLength[0] = content.length();
                lastReasoningLength[0] = modelReasoning.length();
                lastAnalysisProcess[0] = analysisProcess;
            }
        });
        String result = removeThinkContent(content.toString());
        if (result.isBlank()) {
            throw new IllegalStateException("模型流式调用结束但未返回结构化正文：" + batchName);
        }
        return result;
    }

    /**
     * 组合页面可见的模型推理流和结构化分析过程。模型开始返回正文前直接展示推理分片，
     * 正文中的 analysisProcess 出现后继续追加，避免长时间显示固定等待文案。
     */
    private String composeVisibleReasoning(String modelReasoning, String analysisProcess) {
        String reasoning = Optional.ofNullable(modelReasoning).orElse("").trim();
        String analysis = Optional.ofNullable(analysisProcess).orElse("").trim();
        if (reasoning.isBlank() && analysis.isBlank()) {
            return "模型请求已建立，正在等待首个推理分片。";
        }
        if (reasoning.isBlank()) {
            return "【中文分析过程】\n" + analysis;
        }
        if (analysis.isBlank()) {
            return "【模型推理过程】\n" + trim(reasoning, 60000);
        }
        return "【模型推理过程】\n" + trim(reasoning, 60000)
                + "\n\n【中文分析过程】\n" + analysis;
    }

    /**
     * 从尚未接收完整的 JSON 流中提取指定字符串字段。
     * 支持转义字符和未闭合字段，供页面实时展示模型显式生成的中文可审计分析过程。
     */
    private String extractStreamingJsonString(String json, String fieldName) {
        if (json == null || json.isEmpty()) {
            return "";
        }
        String marker = "\"" + fieldName + "\"";
        int fieldIndex = json.indexOf(marker);
        if (fieldIndex < 0) {
            return "";
        }
        int colon = json.indexOf(':', fieldIndex + marker.length());
        if (colon < 0) {
            return "";
        }
        int quote = json.indexOf('\"', colon + 1);
        if (quote < 0) {
            return "";
        }
        StringBuilder value = new StringBuilder();
        boolean escaped = false;
        for (int index = quote + 1; index < json.length(); index++) {
            char character = json.charAt(index);
            if (escaped) {
                switch (character) {
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case '\\' -> value.append('\\');
                    case '\"' -> value.append('\"');
                    default -> value.append(character);
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '\"') {
                break;
            } else {
                value.append(character);
            }
        }
        return trim(value.toString(), 50000);
    }

    /** 从最终正文中移除 think 标签内容，保证后续只解析结构化 JSON。 */
    private String removeThinkContent(String rawContent) {
        return Optional.ofNullable(rawContent).orElse("")
                .replaceAll("(?s)<think>.*?</think>", "")
                .replaceAll("(?s)<think>.*$", "")
                .trim();
    }

    /** 合并当前阶段中多个模型调用批次的真实思考过程。 */
    private String joinReasoning(String previous, String batchName, String current) {
        if (current == null || current.isBlank()) {
            return Optional.ofNullable(previous).orElse("");
        }
        String prefix = previous == null || previous.isBlank() ? "" : previous + "\n\n";
        return trim(prefix + "【" + batchName + "】\n" + current, 100000);
    }

    /**
     * 构造包含模型推理通道和结构化中文分析过程的流式请求。
     * reasoningContent 与正文 analysisProcess 均通过当前阶段 SSE 实时推送。
     */
    private AiChatRequest auditableStreamingRequest(tech.qiantong.qontology.ai.api.model.AiModelRef model,
                                                     List<AiChatMessage> messages, int maxTokens) {
        return new AiChatRequest(model, messages, 0.0, maxTokens, Map.of("enable_thinking", true));
    }

    /** 保存枚举类型及全部枚举值。 */
    private void persistEnums(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            OmAiEnumTypeDO enumType = new OmAiEnumTypeDO();
            enumType.setTaskId(taskId);
            enumType.setEnumName(object.getString("enumName"));
            enumType.setEnumType(resolveEnumType(taskId, object));
            enumType.setStatus("0");
            enumType.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            enumType.setConfidence(object.getBigDecimal("confidence"));
            enumType.setDelFlag(false);
            applyCreator(enumType);
            enumTypeMapper.insert(enumType);
            bindEvidence(taskId, OmAiCandidateTypeEnum.ENUM_TYPE, enumType.getId(),
                    object.getJSONArray("evidenceBlockNos"));
            JSONArray values = object.getJSONArray("values");
            if (values == null) {
                continue;
            }
            for (Object item : values) {
                JSONObject enumValue = (JSONObject) item;
                OmAiEnumDataDO enumData = new OmAiEnumDataDO();
                enumData.setTaskId(taskId);
                enumData.setEnumTypeId(enumType.getId());
                enumData.setEnumSort(Optional.ofNullable(enumValue.getLong("enumSort")).orElse(0L));
                enumData.setEnumLabel(enumValue.getString("enumLabel"));
                enumData.setEnumValue(enumValue.getString("enumValue"));
                enumData.setStatus("0");
                enumData.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
                enumData.setDelFlag(false);
                applyCreator(enumData);
                enumDataMapper.insert(enumData);
                bindEvidence(taskId, OmAiCandidateTypeEnum.ENUM_DATA, enumData.getId(),
                        object.getJSONArray("evidenceBlockNos"));
            }
        }
    }

    /**
     * 枚举类型缺失时生成符合系统规则的任务内唯一编码。
     * 正常情况下由模型输出语义化英文编码，本方法仅作为可靠性兜底。
     */
    private String resolveEnumType(Long taskId, JSONObject enumObject) {
        String extractedType = enumObject.getString("enumType");
        if (extractedType != null && !extractedType.isBlank()) {
            String normalized = extractedType.trim().toLowerCase();
            if (normalized.matches("[a-z][a-z0-9_]*") && !candidateEnumTypeExists(taskId, normalized)) {
                return normalized;
            }
        }
        String candidateKey = Optional.ofNullable(enumObject.getString("candidateKey")).orElse("");
        int separator = candidateKey.indexOf(':');
        String keyCode = separator >= 0 ? candidateKey.substring(separator + 1).trim().toLowerCase() : candidateKey;
        if (keyCode.matches("[a-z][a-z0-9_]*") && !candidateEnumTypeExists(taskId, keyCode)) {
            return keyCode;
        }
        int sequence = 0;
        String candidate;
        do {
            candidate = "enum_type_" + alphabeticSequence(sequence++);
        } while (enumTypeExists(taskId, candidate));
        return candidate;
    }

    /** 将兜底序号编码为纯字母，避免生成 enum_type_2 这类无业务含义的数字编码。 */
    private String alphabeticSequence(int value) {
        StringBuilder result = new StringBuilder();
        int current = Math.max(0, value);
        do {
            result.append((char) ('a' + current % 26));
            current = current / 26 - 1;
        } while (current >= 0);
        return result.reverse().toString();
    }

    /** 判断枚举类型编码是否已存在于当前任务候选或任务所属正式版本。 */
    private boolean enumTypeExists(Long taskId, String enumType) {
        return enumTypeMapper.countTaskEnumType(taskId, enumType) > 0
                || formalEnumTypeMapper.countTaskEnumType(taskId, enumType) > 0;
    }

    /** 候选生成阶段仅避免任务内重复；正式表重复由采纳校验负责，不能破坏语义化编码。 */
    private boolean candidateEnumTypeExists(Long taskId, String enumType) {
        return enumTypeMapper.countTaskEnumType(taskId, enumType) > 0;
    }

    /** 解析对象父子关系及目标关联。 */
    private void resolveObjectDependencies(Long taskId, JSONArray objects) {
        if (objects == null) {
            return;
        }
        for (Object value : objects) {
            JSONObject object = (JSONObject) value;
            Long objectId = findObjectCandidateId(taskId, object.getString("name"));
            Long parentId = findObjectCandidateId(taskId, object.getString("parentName"));
            if (parentId != null) {
                OmAiObjectDO candidate = new OmAiObjectDO();
                candidate.setId(objectId);
                candidate.setParentCandidateId(parentId);
                objectMapper.updateById(candidate);
            }
            for (Long targetId : resolveTargetIds(taskId, object)) {
                OmAiObjectTargetDO link = new OmAiObjectTargetDO();
                link.setTaskId(taskId);
                link.setObjectCandidateId(objectId);
                link.setTargetCandidateId(targetId);
                objectTargetMapper.insertOrIgnore(link);
            }
        }
    }

    /** 保存对象属性。 */
    private void persistAttributes(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            Long objectId = findObjectCandidateId(taskId, object.getString("objectName"));
            if (objectId == null) {
                continue;
            }
            OmAiAttributeDO candidate = new OmAiAttributeDO();
            candidate.setTaskId(taskId);
            candidate.setObjectCandidateId(objectId);
            candidate.setName(object.getString("name"));
            candidate.setCode(object.getString("code"));
            candidate.setDescription(object.getString("description"));
            AttributeTypeConfiguration typeConfiguration = resolveAttributeTypeConfiguration(taskId, object);
            candidate.setTypeCategory(typeConfiguration.typeCategory());
            candidate.setBasicTypeCode(typeConfiguration.basicTypeCode());
            candidate.setBusinessTypeId(typeConfiguration.businessTypeId());
            candidate.setBusinessTypeSource(typeConfiguration.businessTypeSource());
            candidate.setUnit(object.getString("unit"));
            candidate.setDecimalPlaces(object.getInteger("decimalPlaces"));
            candidate.setFormat(object.getString("format"));
            candidate.setEnumType(object.getString("enumType"));
            String enumSource = object.getString("enumSource");
            if ((enumSource == null || enumSource.isBlank())
                    && Integer.valueOf(1).equals(typeConfiguration.typeCategory())
                    && Integer.valueOf(5).equals(typeConfiguration.basicTypeCode())
                    && object.getString("enumType") != null && !object.getString("enumType").isBlank()) {
                enumSource = "PROJECT";
            }
            candidate.setEnumSource(enumSource);
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            attributeMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.ATTRIBUTE, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    /**
     * 规范化模型返回的属性数据类型。系统只允许 1=基础数据类型、2=业务数据类型，
     * 模型返回 0、空值或无效业务类型时统一回退为基础数据类型，避免候选数据类型为空。
     */
    private AttributeTypeConfiguration resolveAttributeTypeConfiguration(Long taskId, JSONObject object) {
        Integer requestedCategory = object.getInteger("suggestedTypeCategory");
        Integer requestedBasicType = object.getInteger("suggestedBasicTypeCode");
        Long businessTypeId = object.getLong("businessTypeId");
        String businessTypeSource = Optional.ofNullable(object.getString("businessTypeSource"))
                .map(String::trim).map(value -> value.toUpperCase(Locale.ROOT)).orElse(null);

        boolean enumBasicType = Integer.valueOf(5).equals(requestedBasicType);
        if (!Integer.valueOf(1).equals(requestedCategory) && !enumBasicType && businessTypeId != null
                && Set.of("PROJECT", "GLOBAL").contains(businessTypeSource)
                && businessTypeExists(taskId, businessTypeId, businessTypeSource)) {
            return new AttributeTypeConfiguration(2, null, businessTypeId, businessTypeSource);
        }

        Integer basicTypeCode = requestedBasicType != null && requestedBasicType >= 0 && requestedBasicType <= 5
                ? requestedBasicType : resolveBusinessTypeBasicCode(taskId, businessTypeId, businessTypeSource);
        if (basicTypeCode == null) {
            basicTypeCode = 0;
            log.warn("属性数据类型缺失，已回退为文本基础类型，taskId={}, object={}, attribute={}",
                    taskId, object.getString("objectName"), object.getString("name"));
        }
        return new AttributeTypeConfiguration(1, basicTypeCode, null, null);
    }

    /** 校验模型引用的业务数据类型是否真实存在。 */
    private boolean businessTypeExists(Long taskId, Long businessTypeId, String source) {
        return resolveBusinessTypeBasicCode(taskId, businessTypeId, source) != null;
    }

    /** 获取业务数据类型的底层基础类型编码。 */
    private Integer resolveBusinessTypeBasicCode(Long taskId, Long businessTypeId, String source) {
        if (businessTypeId == null || !("PROJECT".equals(source) || "GLOBAL".equals(source))) {
            return null;
        }
        return "PROJECT".equals(source)
                ? dataTypeMapper.selectBasicTypeCode(taskId, businessTypeId)
                : basicDataTypeMapper.selectBasicTypeCode(businessTypeId);
    }

    /** 保存对象关系。 */
    private void persistRelations(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            Long startId = findObjectCandidateId(taskId, object.getString("startObjectName"));
            Long endId = findObjectCandidateId(taskId, object.getString("endObjectName"));
            if (startId == null || endId == null) {
                continue;
            }
            OmAiRelationDO candidate = new OmAiRelationDO();
            candidate.setTaskId(taskId);
            candidate.setName(object.getString("name"));
            candidate.setDescription(object.getString("description"));
            candidate.setStartObjectCandidateId(startId);
            candidate.setEndObjectCandidateId(endId);
            candidate.setReversibleFlag(Optional.ofNullable(object.getInteger("reversibleFlag")).orElse(0));
            candidate.setReverseName(object.getString("reverseName"));
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            relationMapper.insert(candidate);
            Long relationId = candidate.getId();
            bindEvidence(taskId, OmAiCandidateTypeEnum.RELATION, relationId,
                    object.getJSONArray("evidenceBlockNos"));
            for (Long targetId : resolveTargetIds(taskId, object)) {
                OmAiRelationTargetDO link = new OmAiRelationTargetDO();
                link.setTaskId(taskId);
                link.setRelationCandidateId(relationId);
                link.setTargetCandidateId(targetId);
                relationTargetMapper.insertOrIgnore(link);
            }
        }
    }

    /** 保存模型公理，并以前序候选属性的数据类型配置作为唯一事实源。 */
    private void persistAxioms(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            String type = normalizeAxiomType(firstString(object, "type", "axiomType"));
            if (!OmAiAxiomTypeEnum.contains(type) || object.getString("name") == null
                    || object.getString("name").isBlank()) {
                log.warn("忽略无法识别的模型公理，taskId={}, name={}, type={}",
                        taskId, object.getString("name"), object.getString("type"));
                continue;
            }
            String objectName = firstString(object, "objectName", "objectTypeName");
            String otherObjectName = firstString(object, "otherObjectName", "parentObjectName", "targetObjectName");
            String domainObjectName = firstString(object, "domainObjectName", "startObjectName");
            String rangeObjectName = firstString(object, "rangeObjectName", "endObjectName");
            String relationName = firstString(object, "relationName", "propertyName");
            String attributeName = firstString(object, "attributeName", "propertyName");
            Long objectId = resolveObjectCandidateId(taskId,
                    firstLong(object, "objectCandidateId", "objectId"), objectName);
            Long otherObjectId = resolveObjectCandidateId(taskId,
                    firstLong(object, "otherObjectCandidateId", "otherObjectId", "parentObjectCandidateId"),
                    otherObjectName);
            Long domainId = resolveObjectCandidateId(taskId,
                    firstLong(object, "domainCandidateId", "domainObjectCandidateId"), domainObjectName);
            Long rangeId = resolveObjectCandidateId(taskId,
                    firstLong(object, "rangeCandidateId", "rangeObjectCandidateId"), rangeObjectName);
            Long attributeId = resolveAttributeCandidateId(taskId, objectId,
                    firstLong(object, "attributeCandidateId", "attributeId"), attributeName);
            Map<String, Object> attribute = attributeId == null ? null : loadAttributeCandidate(taskId, attributeId);
            String resolvedRelationName = "DOMAIN_RANGE".equals(type)
                    ? resolveRelationName(taskId, relationName, domainId, rangeId) : relationName;
            if ((Set.of("SUBCLASS", "EQUIVALENT", "DISJOINT").contains(type)
                    && (objectId == null || otherObjectId == null))
                    || ("DOMAIN_RANGE".equals(type)
                    && (domainId == null || rangeId == null || resolvedRelationName == null))
                    || ("PROPERTY_RANGE".equals(type) && (objectId == null || attribute == null))) {
                log.warn("模型公理引用校验失败，taskId={}, name={}, type={}, object={}, other={}, domain={}, range={}, relation={}, attribute={}",
                        taskId, object.getString("name"), type, objectName, otherObjectName, domainObjectName,
                        rangeObjectName, relationName, attributeName);
                continue;
            }
            OmAiAxiomDO candidate = new OmAiAxiomDO();
            candidate.setTaskId(taskId);
            candidate.setName(object.getString("name"));
            candidate.setDescription(object.getString("description"));
            candidate.setType(type);
            candidate.setLevel(Optional.ofNullable(object.getString("level")).orElse("INFO"));
            candidate.setObjectCandidateId(objectId);
            candidate.setOtherObjectCandidateId(otherObjectId);
            candidate.setRelationName(resolvedRelationName);
            candidate.setDomainCandidateId(domainId);
            candidate.setRangeCandidateId(rangeId);
            candidate.setAttributeCandidateId(attributeId);
            copyAttributeTypeConfiguration(candidate, attribute);
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            axiomMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.AXIOM, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    /** 保存数据规则；任一引用或语义校验失败时整条规则不落库。 */
    private void persistDataRules(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            String type = normalizeDataRuleType(firstString(object, "type", "ruleType"));
            if (!OmAiDataRuleTypeEnum.contains(type) || object.getString("name") == null
                    || object.getString("name").isBlank()) {
                log.warn("忽略无法识别的数据规则，taskId={}, name={}, type={}",
                        taskId, object.getString("name"), object.getString("type"));
                continue;
            }
            JSONArray resolvedConditions = new JSONArray();
            JSONArray conditions = object.getJSONArray("conditions");
            boolean valid = conditions != null && !conditions.isEmpty();
            if (valid) {
                for (Object conditionValue : conditions) {
                    JSONObject condition = (JSONObject) conditionValue;
                    String objectName = firstString(condition, "objectName", "objectTypeName");
                    String attributeName = firstString(condition, "attributeName", "propertyName");
                    normalizeRuleCondition(condition);
                    Long objectId = resolveObjectCandidateId(taskId,
                            firstLong(condition, "objectCandidateId", "objectId"), objectName);
                    Long attributeId = resolveAttributeCandidateId(taskId, objectId,
                            firstLong(condition, "attributeCandidateId", "attributeId"), attributeName);
                    Map<String, Object> attribute = attributeId == null ? null : loadAttributeCandidate(taskId, attributeId);
                    if (objectId == null || attribute == null || !isValidRuleCondition(taskId, type, condition, attribute)) {
                        log.warn("数据规则条件校验失败，taskId={}, rule={}, type={}, object={}, attribute={}, operator={}, value={}",
                                taskId, object.getString("name"), type, objectName, attributeName,
                                condition.getString("operator"), condition.get("value"));
                        valid = false;
                        break;
                    }
                    JSONObject resolved = new JSONObject();
                    resolved.put("objectCandidateId", objectId);
                    resolved.put("attributeCandidateId", attributeId);
                    resolved.put("operator", "RANGE".equals(type) ? condition.getString("operator") : null);
                    resolved.put("value", "REQUIRED".equals(type) ? null : condition.get("value"));
                    resolvedConditions.add(resolved);
                }
            }
            if (!valid) {
                continue;
            }
            OmAiDataRuleDO candidate = new OmAiDataRuleDO();
            candidate.setTaskId(taskId);
            candidate.setName(object.getString("name"));
            candidate.setDescription(object.getString("description"));
            candidate.setType(type);
            candidate.setLevel(Optional.ofNullable(object.getString("level")).orElse("INFO"));
            candidate.setConditionRelation(
                    Optional.ofNullable(object.getString("conditionRelation")).orElse("OR"));
            candidate.setConditions(resolvedConditions.toJSONString());
            candidate.setFailureMessage(object.getString("failureMessage"));
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            dataRuleMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.DATA_RULE, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    /** 获取模型可能使用的兼容数组字段。 */
    private JSONArray firstArray(JSONObject object, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JSONArray value = object.getJSONArray(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /** 获取模型可能使用的兼容字符串字段。 */
    private String firstString(JSONObject object, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = object.getString(fieldName);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    /** 获取模型可能使用的兼容长整数字段。 */
    private Long firstLong(JSONObject object, String... fieldNames) {
        for (String fieldName : fieldNames) {
            Long value = object.getLong(fieldName);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /** 兼容常见公理类型别名，最终仍保存系统标准编码。 */
    private String normalizeAxiomType(String type) {
        String value = Optional.ofNullable(type).orElse("").trim().toUpperCase(Locale.ROOT)
                .replace('-', '_').replace(' ', '_');
        return switch (value) {
            case "SUBCLASS_OF", "SUB_CLASS" -> "SUBCLASS";
            case "EQUIVALENT_CLASS" -> "EQUIVALENT";
            case "DISJOINT_CLASS" -> "DISJOINT";
            case "DOMAINRANGE", "OBJECT_PROPERTY_DOMAIN_RANGE" -> "DOMAIN_RANGE";
            case "PROPERTY_VALUE_RANGE", "DATA_PROPERTY_RANGE" -> "PROPERTY_RANGE";
            default -> value;
        };
    }

    /** 兼容常见规则类型别名，最终仍保存系统标准编码。 */
    private String normalizeDataRuleType(String type) {
        String value = Optional.ofNullable(type).orElse("").trim().toUpperCase(Locale.ROOT)
                .replace('-', '_').replace(' ', '_');
        return switch (value) {
            case "MANDATORY", "NOT_NULL" -> "REQUIRED";
            case "ENUMERATION", "IN" -> "ENUM";
            case "VALUE_RANGE", "NUMERIC_RANGE" -> "RANGE";
            case "DATE_FORMAT", "REGEX" -> "FORMAT";
            default -> value;
        };
    }

    /** 将模型常见的比较符写法转换成系统规则编码。 */
    private void normalizeRuleCondition(JSONObject condition) {
        String operator = Optional.ofNullable(firstString(condition, "operator", "comparisonOperator"))
                .orElse("").toUpperCase(Locale.ROOT);
        operator = switch (operator) {
            case "<", "LESS_THAN" -> "LT";
            case "<=", "LESS_THAN_OR_EQUAL" -> "LE";
            case "=", "==", "EQUAL" -> "EQ";
            case "!=", "<>", "NOT_EQUAL" -> "NE";
            case ">=", "GREATER_THAN_OR_EQUAL" -> "GE";
            case ">", "GREATER_THAN" -> "GT";
            default -> operator;
        };
        condition.put("operator", operator);
        if (!condition.containsKey("value") && condition.containsKey("expectedValue")) {
            condition.put("value", condition.get("expectedValue"));
        }
    }

    /** 根据所属对象候选和属性名称查找属性候选。 */
    private Long findAttributeCandidateId(Long taskId, Long objectCandidateId, String attributeName) {
        if (objectCandidateId == null || attributeName == null || attributeName.isBlank()) {
            return null;
        }
        List<Map<String, Object>> values = attributeMapper.selectByObjectCandidate(taskId, objectCandidateId);
        String normalized = normalizeReference(attributeName);
        return values.stream()
                .filter(item -> normalized.equals(normalizeReference(String.valueOf(item.get("name")))))
                .map(item -> ((Number) item.get("id")).longValue()).findFirst().orElse(null);
    }

    /**
     * 优先使用模型从前序索引复制的属性候选 ID，并校验该属性确实属于当前任务和指定对象；
     * 旧模型未返回 ID 时再按对象名称和属性名称兼容解析。
     */
    private Long resolveAttributeCandidateId(Long taskId, Long objectCandidateId,
                                             Long attributeCandidateId, String attributeName) {
        if (attributeCandidateId != null && objectCandidateId != null) {
            Map<String, Object> attribute = loadAttributeCandidate(taskId, attributeCandidateId);
            if (attribute != null
                    && objectCandidateId.equals(numberLongValue(attribute.get("object_candidate_id")))) {
                return attributeCandidateId;
            }
        }
        return findAttributeCandidateId(taskId, objectCandidateId, attributeName);
    }

    /** 读取属性候选的权威数据类型配置。 */
    private Map<String, Object> loadAttributeCandidate(Long taskId, Long attributeCandidateId) {
        return attributeMapper.selectTypeConfiguration(taskId, attributeCandidateId);
    }

    /** 将属性的数据类型配置原样继承到 PROPERTY_RANGE 公理。 */
    private void copyAttributeTypeConfiguration(OmAiAxiomDO target, Map<String, Object> attribute) {
        if (attribute == null) {
            return;
        }
        target.setTypeCategory(stringValue(attribute.get("type_category")));
        target.setBasicTypeCode(stringValue(attribute.get("basic_type_code")));
        target.setBusinessTypeId(numberLongValue(attribute.get("business_type_id")));
        target.setBusinessTypeSource(stringValue(attribute.get("business_type_source")));
        target.setUnit(stringValue(attribute.get("unit")));
        target.setDecimalPlaces(numberLongValue(attribute.get("decimal_places")));
        target.setFormat(stringValue(attribute.get("format")));
        target.setEnumType(stringValue(attribute.get("enum_type")));
        target.setEnumSource(stringValue(attribute.get("enum_source")));
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Long numberLongValue(Object value) {
        return value instanceof Number number ? number.longValue() : null;
    }

    /**
     * 解析 Domain/Range 公理引用的前序关系。起止对象只有一条关系时直接采用候选真实名称；
     * 存在多条时才要求模型关系名称与候选关系名称精确匹配。
     */
    private String resolveRelationName(Long taskId, String relationName, Long domainId, Long rangeId) {
        if (domainId == null || rangeId == null) {
            return null;
        }
        List<String> names = relationMapper.selectNamesByObjects(taskId, domainId, rangeId);
        if (names.size() == 1) {
            return names.get(0);
        }
        if (relationName == null || relationName.isBlank()) {
            return null;
        }
        String normalized = normalizeReference(relationName);
        return names.stream().filter(name -> normalized.equals(normalizeReference(name)))
                .findFirst().orElse(null);
    }

    /** 按正式数据规则能力校验属性类型、比较符和值，禁止拼凑无意义规则。 */
    private boolean isValidRuleCondition(Long taskId, String type, JSONObject condition, Map<String, Object> attribute) {
        Integer basicTypeCode = resolveEffectiveBasicTypeCode(attribute);
        Object rawValue = condition.get("value");
        String value = rawValue == null ? "" : String.valueOf(rawValue).trim();
        if ("REQUIRED".equals(type)) {
            return true;
        }
        if ("RANGE".equals(type)) {
            if (basicTypeCode == null || !Set.of(1, 2).contains(basicTypeCode)
                    || !OmAiRangeOperatorEnum.contains(condition.getString("operator"))) {
                return false;
            }
            try {
                new java.math.BigDecimal(value);
                return basicTypeCode != 1 || value.matches("[+-]?\\d+");
            } catch (Exception ignored) {
                return false;
            }
        }
        if ("FORMAT".equals(type)) {
            return Integer.valueOf(4).equals(basicTypeCode) && OmAiDateFormatEnum.contains(value);
        }
        return "ENUM".equals(type) && Integer.valueOf(5).equals(basicTypeCode)
                && enumRuleValuesExist(taskId, attribute, value);
    }

    /** 解析基础类型；业务数据类型使用其定义中的底层基础类型。 */
    private Integer resolveEffectiveBasicTypeCode(Map<String, Object> attribute) {
        Integer typeCategory = attribute.get("type_category") instanceof Number number
                ? number.intValue() : null;
        if (Integer.valueOf(1).equals(typeCategory)) {
            Object code = attribute.get("basic_type_code");
            return code instanceof Number number ? number.intValue()
                    : code == null ? null : Integer.valueOf(String.valueOf(code));
        }
        Long businessTypeId = attribute.get("business_type_id") instanceof Number number
                ? number.longValue() : null;
        String source = Optional.ofNullable(attribute.get("business_type_source"))
                .map(String::valueOf).orElse("GLOBAL");
        if (businessTypeId == null || !("PROJECT".equals(source) || "GLOBAL".equals(source))) {
            return null;
        }
        return "PROJECT".equals(source)
                ? dataTypeMapper.selectBasicTypeCodeById(businessTypeId)
                : basicDataTypeMapper.selectBasicTypeCode(businessTypeId);
    }

    /** 属性类型规范化结果。 */
    private record AttributeTypeConfiguration(Integer typeCategory, Integer basicTypeCode,
                                              Long businessTypeId, String businessTypeSource) {
    }

    /** 校验枚举规则值确实属于属性绑定的枚举定义。 */
    private boolean enumRuleValuesExist(Long taskId, Map<String, Object> attribute, String values) {
        String enumType = Optional.ofNullable(attribute.get("enum_type")).map(String::valueOf).orElse("");
        String enumSource = Optional.ofNullable(attribute.get("enum_source")).map(String::valueOf).orElse("PROJECT");
        if (enumType.isBlank() || values.isBlank()) {
            return false;
        }
        for (String item : values.split("[,，]")) {
            String value = item.trim();
            if (value.isBlank()) {
                return false;
            }
            Integer candidateCount = enumDataMapper.countCandidateValue(taskId, enumType, value);
            Integer formalCount = "GLOBAL".equals(enumSource)
                    ? basicEnumDataMapper.countAiValue(enumType, value)
                    : formalEnumDataMapper.countAiValue(taskId, enumType, value);
            if ((candidateCount == null || candidateCount == 0) && (formalCount == null || formalCount == 0)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 解析候选数据引用的建模目标。稳定候选键优先，规范化后的目标问题作为兼容兜底。
     */
    private Set<Long> resolveTargetIds(Long taskId, JSONObject candidate) {
        List<Map<String, Object>> targets = targetMapper.selectCandidateIndex(taskId);
        Set<String> candidateKeys = normalizeValues(candidate.getJSONArray("targetCandidateKeys"), false);
        Set<String> targetQuestions = normalizeValues(candidate.getJSONArray("targetQuestions"), true);
        Set<Long> targetIds = new LinkedHashSet<>();
        for (Map<String, Object> target : targets) {
            String candidateKey = Optional.ofNullable(target.get("candidate_key"))
                    .map(String::valueOf).orElse("");
            String question = normalizeReference(Optional.ofNullable(target.get("question"))
                    .map(String::valueOf).orElse(""));
            if ((!candidateKey.isBlank() && candidateKeys.contains(candidateKey))
                    || (!question.isBlank() && targetQuestions.contains(question))) {
                targetIds.add(((Number) target.get("id")).longValue());
            }
        }
        return targetIds;
    }

    /** 将模型返回的引用数组转换成便于比较的集合。 */
    private Set<String> normalizeValues(JSONArray values, boolean normalizeText) {
        Set<String> result = new LinkedHashSet<>();
        if (values == null) {
            return result;
        }
        for (Object value : values) {
            String text = Optional.ofNullable(value).map(String::valueOf).orElse("").trim();
            text = normalizeText ? normalizeReference(text) : text;
            if (!text.isBlank()) {
                result.add(text);
            }
        }
        return result;
    }

    /** 忽略引用文本中的空白、常见标点和大小写差异。 */
    private String normalizeReference(String value) {
        return Optional.ofNullable(value).orElse("")
                .replaceAll("[\\s\\p{Punct}，。！？；：（）【】“”‘’]", "")
                .toLowerCase(Locale.ROOT);
    }

    /** 将候选字段中的数组转换为系统页面使用的中文逗号分隔文本。 */
    private Object normalizeCandidateField(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(String::valueOf).map(String::trim)
                    .filter(item -> !item.isBlank()).distinct().collect(java.util.stream.Collectors.joining("，"));
        }
        if (value instanceof JSONObject) {
            return JSON.toJSONString(value);
        }
        return value;
    }

    /** 获取阶段执行时展示的思考计划。 */
    private String stageThinking(String stageCode) {
        return OmAiModelingStageEnum.fromCode(stageCode).getThinking();
    }

    /** 根据模型调用批次计算当前阶段进度，保留结束汇总空间。 */
    private int callProgress(int completedCalls, int totalCalls) {
        return Math.min(95, 10 + (completedCalls * 85 / Math.max(1, totalCalls)));
    }

    /** 持久化并直接推送当前阶段的实时进度与模型流式内容。 */
    private void updateStageProgress(int progress, String summary, String modelName, Integer inputCount, Integer outputCount) {
        updateStageProgress(currentStageId.get(), currentStage.get(), progress, summary,
                modelName, inputCount, outputCount);
    }

    /** 使用显式阶段上下文更新进度，支持模型回调跨线程执行。 */
    private void updateStageProgress(Long stageId, OmAiStageDO stage, int progress, String summary,
                                     String modelName, Integer inputCount, Integer outputCount) {
        if (stageId == null || stage == null) {
            return;
        }
        stage.setProgress(Math.max(1, Math.min(99, progress)));
        stage.setSummary(trim(summary, 2000));
        if (modelName != null) {
            stage.setModelName(modelName);
        }
        if (inputCount != null) {
            stage.setInputCount(inputCount);
        }
        if (outputCount != null) {
            stage.setOutputCount(outputCount);
        }
        UpdateWrapper<OmAiStageDO> update = new UpdateWrapper<OmAiStageDO>()
                .eq("id", stageId)
                .set("progress", stage.getProgress())
                .set("summary", stage.getSummary());
        if (modelName != null) {
            update.set("model_name", modelName);
        }
        if (inputCount != null) {
            update.set("input_count", inputCount);
        }
        if (outputCount != null) {
            update.set("output_count", outputCount);
        }
        stageMapper.update(null, update);
        streamPublisher.publishStage(stage);
    }

    /** 持久化并推送模型真实的流式思考过程。 */
    private void updateStageReasoning(String reasoningContent) {
        updateStageReasoning(currentStageId.get(), currentStage.get(), reasoningContent);
    }

    /** 使用显式阶段上下文写入思考过程，避免异步模型线程丢失 ThreadLocal。 */
    private void updateStageReasoning(Long stageId, OmAiStageDO stage, String reasoningContent) {
        if (stageId == null || stage == null) {
            return;
        }
        stage.setReasoningContent(reasoningContent);
        stageMapper.update(null, new UpdateWrapper<OmAiStageDO>()
                .eq("id", stageId)
                .set("reasoning_content", reasoningContent));
        streamPublisher.publishStage(stage);
    }

    /** 绑定候选数据与来源证据。 */
    private void bindEvidence(Long taskId, OmAiCandidateTypeEnum type, Long dataId, JSONArray evidenceIds) {
        if (evidenceIds == null) {
            return;
        }
        for (Object evidenceId : evidenceIds) {
            OmAiDataEvidenceDO link = new OmAiDataEvidenceDO();
            link.setTaskId(taskId);
            link.setDataType(type.getCode());
            link.setDataId(dataId);
            link.setEvidenceId(Long.valueOf(String.valueOf(evidenceId)));
            link.setEvidenceRole(OmAiEvidenceRoleEnum.POSITIVE.getCode());
            applyCreator(link);
            dataEvidenceMapper.insertOrIgnore(link);
        }
    }

    /** 根据任务和名称查找最近的对象类型候选。 */
    private Long findObjectCandidateId(Long taskId, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = normalizeReference(value);
        return objectMapper.selectCandidateIndex(taskId).stream()
                .filter(item -> normalized.equals(normalizeReference(String.valueOf(item.get("name")))))
                .map(item -> ((Number) item.get("id")).longValue()).findFirst().orElse(null);
    }

    /**
     * 优先使用模型从前序索引复制的对象候选 ID，并校验该 ID 属于当前任务；
     * 旧模型未返回 ID 或 ID 无效时再按名称兼容解析。
     */
    private Long resolveObjectCandidateId(Long taskId, Long objectCandidateId, String objectName) {
        if (objectCandidateId != null && objectMapper.selectCandidateIndex(taskId).stream()
                .anyMatch(item -> objectCandidateId.equals(numberLongValue(item.get("id"))))) {
            return objectCandidateId;
        }
        return findObjectCandidateId(taskId, objectName);
    }

    /** 补充候选数据创建人信息。 */
    private void applyCreator(BaseEntity candidate) {
        candidate.setCreatorId(currentCreatorId.get());
        candidate.setCreateBy(currentCreateBy.get());
    }

    /** 截断文本。 */
    private static String trim(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    /** 阶段执行动作。 */
    @FunctionalInterface
    private interface StageAction {
        String run();
    }
}
