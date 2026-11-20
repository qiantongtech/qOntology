package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tech.qiantong.qontology.common.constant.Constants;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatMedia;
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

    private static final String CHINESE_OUTPUT_SYSTEM_PROMPT = """
            你是中文业务建模助手。所有面向用户的自然语言内容必须使用简体中文。
            模型推理通道 reasoning_content 也必须使用简体中文；不要先用英文推理再翻译。
            analysisProcess、analysisSummary、解释、判断、校验结论和自检内容不得使用英文句子。
            JSON 字段名、枚举编码、程序标识符、模型名称、不可翻译的专有名称和原文引用可以保留英文。
            输出前必须检查自然语言内容；若存在英文推理、英文说明或英文自检语句，必须先翻译为中文再输出。
            """;
    @Value("${dromara.x-file-storage.local-plus[0].storage-path}")
    private String localStoragePath;
    private final Set<Long> runningTaskIds = ConcurrentHashMap.newKeySet();
    private final ThreadLocal<Long> currentCreatorId = new ThreadLocal<>();
    private final ThreadLocal<String> currentCreateBy = new ThreadLocal<>();
    private final ThreadLocal<Long> currentStageId = new ThreadLocal<>();
    private final ThreadLocal<OmAiStageDO> currentStage = new ThreadLocal<>();
    private final ThreadLocal<List<String>> currentWarnings = new ThreadLocal<>();
    private final ThreadLocal<Boolean> currentImageRecognitionEnabled = new ThreadLocal<>();
    private final ThreadLocal<Set<Long>> currentWindowEvidenceIds = new ThreadLocal<>();
    private final ThreadLocal<Map<OmAiCandidateTypeEnum, Set<Long>>> currentMissingEvidence = new ThreadLocal<>();


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
    private OmAiModelingProperties modelingProperties;

    @Resource(name = "aiModelingStreamExecutor")
    private TaskExecutor streamExecutor;

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
    private TransactionTemplate transactionTemplate;

    @Resource
    private OmAiModelingStreamPublisher streamPublisher;

    /** 运行已领取的任务，跳过此前已完成的阶段。 */
    public void runQueuedTask(Long taskId) {
        OmAiTaskDO task = taskMapper.selectById(taskId);
        if (task == null) {
            runningTaskIds.remove(taskId);
            return;
        }
        try {
            // 多实例可能同时提交同一排队任务，只有一个后台线程能够领取。
            int claimed = taskMapper.update(null, new UpdateWrapper<OmAiTaskDO>()
                    .eq("id", taskId)
                    .eq("status", OmAiTaskStatusEnum.WAITING.getCode())
                    .eq("current_stage", "QUEUED")
                    .eq("del_flag", 0)
                    .set("status", OmAiTaskStatusEnum.RUNNING.getCode()));
            if (claimed == 0) {
                return;
            }
            task.setStatus(OmAiTaskStatusEnum.RUNNING.getCode());
            currentWarnings.set(new ArrayList<>());
            currentImageRecognitionEnabled.set(true);
            startTask(task);
            initializeStages(task);
            runStageIfRequired(task, OmAiModelingStageEnum.FILE_PARSE.getCode(),
                    () -> parseSourceFiles(taskId));
            runStageIfRequired(task, OmAiModelingStageEnum.EVIDENCE_INDEX.getCode(),
                    this::summarizeEvidenceIndex);
            for (OmAiExtractionRoundEnum round : OmAiExtractionRoundEnum.values()) {
                runStageIfRequired(task, round.getStage().getCode(), () -> executeRound(task, round));
            }
            runStageIfRequired(task, OmAiModelingStageEnum.FINISH.getCode(), () -> finishTask(task));
            // 结果汇总阶段先完成推送，再发送任务完成事件并结束页面流。
            streamPublisher.publishTask(task);
        } catch (Exception exception) {
            failTask(task, exception);
        } finally {
            currentWarnings.remove();
            currentImageRecognitionEnabled.remove();
            currentWindowEvidenceIds.remove();
            currentMissingEvidence.remove();
            runningTaskIds.remove(taskId);
        }
    }

    private void runStageIfRequired(OmAiTaskDO task, String stageCode, StageAction action) {
        OmAiStageDO stage = stageMapper.selectOne(new QueryWrapper<OmAiStageDO>()
                .eq("task_id", task.getId()).eq("stage_code", stageCode));
        if (stage != null && OmAiStageStatusEnum.SUCCESS.getCode().equals(stage.getStatus())) {
            return;
        }
        runStage(task, stageCode, action);
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
            stage.setErrorMessage(userFacingFailureReason(exception));
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
        int failedFiles = 0;
        int recognizedImages = 0;
        int skippedImages = 0;
        for (OmAiFileDO file : files) {
            long existingBlocks = evidenceMapper.selectCount(new QueryWrapper<OmAiEvidenceDO>()
                    .eq("task_id", taskId).eq("file_id", file.getId()));
            if (OmAiFileParseStatusEnum.SUCCESS.getCode().equals(file.getParseStatus()) && existingBlocks > 0) {
                completedFiles++;
                totalBlocks += (int) existingBlocks;
                continue;
            }
            updateStageProgress(
                    10 + (completedFiles * 75 / files.size()),
                    "正在解析文件 " + (completedFiles + 1) + "/" + files.size() + "：" + file.getFileName(),
                    null, completedFiles, totalBlocks);
            try {
                // 仅重建当前失败或未完成文件的证据，避免断点续跑产生重复块。
                evidenceMapper.delete(new QueryWrapper<OmAiEvidenceDO>()
                        .eq("task_id", taskId).eq("file_id", file.getId()));
                Path sourcePath = resolveLocalFilePath(file.getFileUrl());
                OmAiDocumentParser.ParseResult parseResult = sourcePath == null
                        ? documentParser.parseWithMedia(file.getFileName(), download(file.getFileUrl()))
                        : documentParser.parseWithMedia(file.getFileName(), sourcePath);
                List<OmAiDocumentParser.Block> blocks = new ArrayList<>(parseResult.blocks());
                List<RecognizedMedia> recognizedMedia = new ArrayList<>();
                for (OmAiDocumentParser.MediaBlock media : parseResult.media()) {
                    String recognized = recognizeDocumentMedia(file, media);
                    if (recognized == null || recognized.isBlank()) {
                        skippedImages++;
                        continue;
                    }
                    recognizedMedia.add(new RecognizedMedia(media, recognized));
                    recognizedImages++;
                }
                if (!recognizedMedia.isEmpty()) {
                    blocks = mergeMediaBlocks(blocks, recognizedMedia);
                }
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
                failedFiles++;
                addWarning("文件“" + file.getFileName() + "”解析失败，已跳过该文件："
                        + trim(Optional.ofNullable(exception.getMessage()).orElse("未知错误"), 300));
                log.error("AI 建模文件解析失败，已跳过并继续其他文件，taskId={}, fileId={}, fileName={}",
                        taskId, file.getId(), file.getFileName(), exception);
            }
        }
        if (completedFiles == 0) {
            throw new IllegalStateException("全部文件解析失败，无法继续抽取");
        }
        return "成功解析 " + completedFiles + " 个文件，失败并跳过 " + failedFiles
                + " 个，生成 " + totalBlocks
                + " 个证据块；文档内图片识别成功 " + recognizedImages
                + " 张，跳过 " + skippedImages + " 张";
    }

    /** 将文档内图片识别结果按原始锚点插回正文证据顺序，并重新生成连续块编号。 */
    private List<OmAiDocumentParser.Block> mergeMediaBlocks(List<OmAiDocumentParser.Block> source,
                                                             List<RecognizedMedia> mediaList) {
        Map<Integer, List<RecognizedMedia>> anchored = mediaList.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> Optional.ofNullable(item.media().anchorBlockNo()).orElse(Integer.MAX_VALUE),
                        LinkedHashMap::new, java.util.stream.Collectors.toList()));
        List<OmAiDocumentParser.Block> merged = new ArrayList<>();
        int blockNo = 1;
        for (RecognizedMedia item : anchored.getOrDefault(0, List.of())) {
            OmAiDocumentParser.MediaBlock media = item.media();
            merged.add(new OmAiDocumentParser.Block(blockNo++, "IMAGE_TEXT", media.pageNo(),
                    media.chapterPath(), media.paragraphNo(), null, null, null, media.imageNo(),
                    null, null, item.content()));
        }
        for (OmAiDocumentParser.Block block : source) {
            merged.add(copyBlockWithNo(block, blockNo++));
            for (RecognizedMedia item : anchored.getOrDefault(block.no(), List.of())) {
                OmAiDocumentParser.MediaBlock media = item.media();
                merged.add(new OmAiDocumentParser.Block(blockNo++, "IMAGE_TEXT", media.pageNo(),
                        media.chapterPath(), media.paragraphNo(), null, null, null, media.imageNo(),
                        null, null, item.content()));
            }
        }
        for (Map.Entry<Integer, List<RecognizedMedia>> entry : anchored.entrySet()) {
            if (entry.getKey() == 0 || (entry.getKey() != Integer.MAX_VALUE
                    && source.stream().anyMatch(block -> block.no() == entry.getKey()))) {
                continue;
            }
            for (RecognizedMedia item : entry.getValue()) {
                OmAiDocumentParser.MediaBlock media = item.media();
                merged.add(new OmAiDocumentParser.Block(blockNo++, "IMAGE_TEXT", media.pageNo(),
                        media.chapterPath(), media.paragraphNo(), null, null, null, media.imageNo(),
                        null, null, item.content()));
            }
        }
        return merged;
    }

    private OmAiDocumentParser.Block copyBlockWithNo(OmAiDocumentParser.Block block, int no) {
        return new OmAiDocumentParser.Block(no, block.type(), block.pageNo(), block.chapterPath(),
                block.paragraphNo(), block.tableNo(), block.rowNo(), block.columnNo(), block.imageNo(),
                block.lineStart(), block.lineEnd(), block.content());
    }

    /** 使用主抽取模型识别文档内图片；模型明确不支持视觉输入时，当前任务后续图片全部跳过。 */
    private String recognizeDocumentMedia(OmAiFileDO file, OmAiDocumentParser.MediaBlock media) {
        if (Boolean.FALSE.equals(currentImageRecognitionEnabled.get())) {
            return null;
        }
        try {
            String prompt = "请识别这张业务文档图片中的全部可见文字、表格字段、流程节点和关系。"
                    + "按原始阅读顺序输出纯文本并保留关键换行；不要编造不可见内容，不要输出Markdown代码块。";
            AiChatRequest request = new AiChatRequest(
                    modelApi.getModelRef(modelingProperties.getModelKeyId(), modelingProperties.getModelName()),
                    List.of(AiChatMessage.system(CHINESE_OUTPUT_SYSTEM_PROMPT),
                            AiChatMessage.user(prompt, List.of(new AiChatMedia(
                                    media.mimeType(), media.data(), media.name())))),
                    0.0, 4000, Map.of("enable_thinking", false));
            String content = Optional.ofNullable(chatService.chat(request).content()).orElse("").trim();
            return content.isBlank() ? null : "【" + media.description() + "识别结果】\n"
                    + trim(content, modelingProperties.singleEvidenceMaxChars() * 2);
        } catch (Exception exception) {
            if (isImageCapabilityUnsupported(exception)) {
                currentImageRecognitionEnabled.set(false);
                addWarning("当前抽取模型“" + modelingProperties.getModelName() + "”不支持图片输入，已跳过文档内图片识别。");
                log.info("主抽取模型不支持图片输入，当前任务后续图片将跳过，model={}, fileId={}, cause={}",
                        modelingProperties.getModelName(), file.getId(), rootCauseMessage(exception));
                return null;
            }
            throw new IllegalStateException("文档内图片识别失败：" + media.name() + "，"
                    + trim(Optional.ofNullable(exception.getMessage()).orElse("未知错误"), 500), exception);
        }
    }

    private boolean isImageCapabilityUnsupported(Throwable throwable) {
        String message = rootCauseMessage(throwable).toLowerCase(java.util.Locale.ROOT);
        return message.contains("image input is not supported")
                || message.contains("does not support image")
                || message.contains("doesn't support image")
                || message.contains("vision is not supported")
                || message.contains("multimodal is not supported")
                || message.contains("unsupported image")
                || message.contains("unsupported media")
                || message.contains("不支持图片")
                || message.contains("不支持图像")
                || message.contains("不支持多模态");
    }

    private String rootCauseMessage(Throwable throwable) {
        Throwable current = throwable;
        String message = Optional.ofNullable(current.getMessage()).orElse("未知错误");
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
            if (current.getMessage() != null && !current.getMessage().isBlank()) {
                message = current.getMessage();
            }
        }
        return message;
    }

    /** 汇总证据索引策略。 */
    private String summarizeEvidenceIndex() {
        return "已按文件、页码、章节、段落和表格位置建立证据索引；抽取轮次只读取证据窗口和前序候选索引";
    }

    /** 执行一轮文档证据抽取。 */
    private String executeRound(OmAiTaskDO task, OmAiExtractionRoundEnum round) {
        List<OmAiEvidenceDO> evidenceList = evidenceMapper.selectList(
                new QueryWrapper<OmAiEvidenceDO>()
                        .eq("task_id", task.getId())
                        .orderByAsc("file_id", "block_no"));
        if (evidenceList.isEmpty()) {
            throw new IllegalStateException("没有可用于本轮抽取的证据，任务已终止");
        }
        List<EvidenceWindow> windows = buildEvidenceWindows(
                splitOversizedEvidence(evidenceList, modelingProperties.singleEvidenceMaxChars()),
                modelingProperties.getEvidenceWindowMaxChars());
        int totalCalls = windows.size();
        log.info("AI 建模文本证据已按上下文预算分窗，taskId={}, round={}, evidenceBlocks={}, windows={}, windowCharBudget={}",
                task.getId(), round.getCode(), evidenceList.size(), totalCalls,
                modelingProperties.getEvidenceWindowMaxChars());
        int[] completedCalls = {0};
        for (int index = 0; index < windows.size(); index++) {
            executeWindow(task, round, windows.get(index), index + 1, totalCalls, completedCalls);
        }
        validateRoundCoverage(task.getId(), round);
        return round.getStage().getLabel();
    }

    /** 仅上下文容量不足时拆窗；服务或落库异常保留已有结果并停止本轮。 */
    private void executeWindow(OmAiTaskDO task, OmAiExtractionRoundEnum round, EvidenceWindow window,
                               int windowNo, int totalCalls, int[] completedCalls) {
        long startedAt = System.currentTimeMillis();
        try {
            String prompt = buildRoundPrompt(task.getId(), round, window.evidence());
            if (prompt.length() > modelingProperties.getInputMaxChars()) {
                throw new EvidenceWindowTooLargeException("证据窗口输入超出配置预算，实际 " + prompt.length()
                        + " 字符，预算 " + modelingProperties.getInputMaxChars() + " 字符");
            }
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "正在分析证据窗口 " + windowNo + "，包含 " + window.evidence().size()
                            + " 个证据块、约 " + window.characterCount() + " 个字符，重点识别："
                            + stageThinking(round.getStage().getCode()),
                    modelingProperties.getModelName(), window.evidence().size(), completedCalls[0]);
            AiChatRequest request = auditableStreamingRequest(
                    modelApi.getModelRef(modelingProperties.getModelKeyId(), modelingProperties.getModelName()),
                    List.of(AiChatMessage.user(prompt)), maxOutputTokens(round));
            String batchName = "文本证据窗口 " + windowNo;
            String responseContent = streamModelResponse(
                    request, round, modelingProperties.getModelName(),
                    callProgress(completedCalls[0], totalCalls), batchName);
            handleRoundResponse(task, round, responseContent);
            log.info("AI 建模窗口完成，taskId={}, round={}, window={}, blocks={}, promptChars={}, responseChars={}, elapsedMs={}",
                    task.getId(), round.getCode(), windowNo, window.evidence().size(), prompt.length(),
                    responseContent.length(), System.currentTimeMillis() - startedAt);
            completedCalls[0]++;
            String analysisSummary = extractAnalysisSummary(responseContent);
            OmAiStageDO stage = currentStage.get();
            stage.setReasoningContent(joinReasoning(stage.getReasoningContent(), "分析结论", analysisSummary));
            applyStageProgress(stage, callProgress(completedCalls[0], totalCalls),
                    "证据窗口 " + windowNo + " 分析完成。模型分析摘要：" + analysisSummary,
                    modelingProperties.getModelName(), window.evidence().size(), completedCalls[0]);
            persistStageSnapshot(currentStageId.get(), stage);
            streamPublisher.publishStage(stage);
        } catch (Exception exception) {
            log.warn("AI 建模窗口失败，taskId={}, round={}, window={}, blocks={}, elapsedMs={}, cause={}",
                    task.getId(), round.getCode(), windowNo, window.evidence().size(),
                    System.currentTimeMillis() - startedAt, rootCauseMessage(exception));
            if (isContextLengthFailure(exception) && window.evidence().size() > 1) {
                int middle = window.evidence().size() / 2;
                EvidenceWindow left = evidenceWindow(window.evidence().subList(0, middle));
                EvidenceWindow right = evidenceWindow(window.evidence().subList(middle, window.evidence().size()));
                log.warn("AI 建模窗口执行失败，自动缩小窗口重试，taskId={}, round={}, window={}, blocks={}, cause={}",
                        task.getId(), round.getCode(), windowNo, window.evidence().size(), exception.getMessage());
                updateStageReasoning(joinReasoning(
                        Optional.ofNullable(currentStage.get()).map(OmAiStageDO::getReasoningContent).orElse(""),
                        "窗口降级", "窗口 " + windowNo + " 执行失败，已自动拆分为更小窗口重试："
                                + trim(exception.getMessage(), 500)));
                executeWindow(task, round, left, windowNo, totalCalls, completedCalls);
                executeWindow(task, round, right, windowNo, totalCalls, completedCalls);
                return;
            }
            throw exception;
        }
    }

    private boolean isContextLengthFailure(Exception exception) {
        if (exception instanceof EvidenceWindowTooLargeException) return true;
        String message = rootCauseMessage(exception).toLowerCase(Locale.ROOT);
        return message.contains("context length") || message.contains("maximum context")
                || message.contains("context_length_exceeded") || message.contains("input too long")
                || message.contains("上下文长度") || message.contains("输入过长");
    }

    /** 校验每轮最终落库覆盖度，避免数组合法但全部为空或全部被过滤后仍显示完全成功。 */
    private void validateRoundCoverage(Long taskId, OmAiExtractionRoundEnum round) {
        if (round == OmAiExtractionRoundEnum.FOUNDATION) {
            long targetCount = targetMapper.selectCount(new QueryWrapper<OmAiTargetDO>()
                    .eq("task_id", taskId).eq("del_flag", 0));
            if (targetCount == 0) {
                throw new IllegalStateException("未抽取到有效建模目标，无法继续建立对象、关系及目标关联，请检查文档是否明确描述了建模目的、业务问题或应用场景");
            }
        }
        long count = switch (round) {
            case FOUNDATION -> targetMapper.selectCount(new QueryWrapper<OmAiTargetDO>().eq("task_id", taskId).eq("del_flag", 0))
                    + termMapper.selectCount(new QueryWrapper<OmAiTermDO>().eq("task_id", taskId).eq("del_flag", 0))
                    + enumTypeMapper.selectCount(new QueryWrapper<OmAiEnumTypeDO>().eq("task_id", taskId).eq("del_flag", 0));
            case OBJECT -> objectMapper.selectCount(new QueryWrapper<OmAiObjectDO>().eq("task_id", taskId).eq("del_flag", 0));
            case MEMBERS -> attributeMapper.selectCount(new QueryWrapper<OmAiAttributeDO>().eq("task_id", taskId).eq("del_flag", 0))
                    + relationMapper.selectCount(new QueryWrapper<OmAiRelationDO>().eq("task_id", taskId).eq("del_flag", 0));
            case RULES -> axiomMapper.selectCount(new QueryWrapper<OmAiAxiomDO>().eq("task_id", taskId).eq("del_flag", 0))
                    + dataRuleMapper.selectCount(new QueryWrapper<OmAiDataRuleDO>().eq("task_id", taskId).eq("del_flag", 0));
        };
        if (count > 0) return;
        if (round == OmAiExtractionRoundEnum.RULES) {
            addWarning("规则轮未生成可落库的公理或数据规则，请人工确认文档是否包含明确约束。");
            return;
        }
        throw new IllegalStateException(round.getStage().getLabel() + "未生成任何可落库候选，可能是模型空返回或候选全部未通过格式与引用校验");
    }

    /** 构建当前轮次的文本提示词。 */
    private String buildRoundPrompt(Long taskId, OmAiExtractionRoundEnum round,
                                    List<OmAiEvidenceDO> evidenceWindow) {
        String roundPrompt = promptLoader.load(round.getPromptResource());
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
        int candidateBudget = Math.max(1500, Math.min(modelingProperties.candidateContextMaxChars(),
                modelingProperties.getInputMaxChars() - CHINESE_OUTPUT_SYSTEM_PROMPT.length()
                        - roundPrompt.length() - source.length() - 500));
        return roundPrompt
                + "\n\n前序候选上下文（包含全局精简目录和与当前证据相关的候选明细）："
                + loadRelevantEntityIndex(taskId, round, source.toString(), candidateBudget)
                + "\n\n本批原始证据：\n" + source;
    }

    /** 超大单块在内存中切片，复用原证据 ID，避免破坏已有证据绑定。 */
    private List<OmAiEvidenceDO> splitOversizedEvidence(List<OmAiEvidenceDO> evidenceList, int maxChars) {
        List<OmAiEvidenceDO> result = new ArrayList<>();
        for (OmAiEvidenceDO evidence : evidenceList) {
            String content = Optional.ofNullable(evidence.getContent()).orElse("");
            List<String> parts = splitText(content, maxChars);
            if (parts.size() == 1) {
                result.add(evidence);
                continue;
            }
            for (int index = 0; index < parts.size(); index++) {
                result.add(copyEvidenceWithContent(evidence,
                        "【原证据片段 " + (index + 1) + "/" + parts.size() + "】\n" + parts.get(index)));
            }
        }
        return result;
    }

    private List<String> splitText(String content, int maxChars) {
        if (content == null || content.length() <= maxChars) return List.of(Optional.ofNullable(content).orElse(""));
        List<String> parts = new ArrayList<>();
        int start = 0;
        while (start < content.length()) {
            int hardEnd = Math.min(content.length(), start + maxChars);
            int end = hardEnd;
            if (hardEnd < content.length()) {
                int newline = content.lastIndexOf('\n', hardEnd);
                int sentence = Math.max(content.lastIndexOf('。', hardEnd), content.lastIndexOf('；', hardEnd));
                int boundary = Math.max(newline, sentence);
                if (boundary > start + maxChars / 2) end = boundary + 1;
            }
            parts.add(content.substring(start, end).trim());
            start = end;
        }
        return parts.stream().filter(part -> !part.isBlank()).toList();
    }

    private OmAiEvidenceDO copyEvidenceWithContent(OmAiEvidenceDO source, String content) {
        return OmAiEvidenceDO.builder()
                .id(source.getId()).taskId(source.getTaskId()).fileId(source.getFileId())
                .blockNo(source.getBlockNo()).blockType(source.getBlockType()).pageNo(source.getPageNo())
                .chapterPath(source.getChapterPath()).paragraphNo(source.getParagraphNo())
                .tableNo(source.getTableNo()).rowNo(source.getRowNo()).columnNo(source.getColumnNo())
                .imageNo(source.getImageNo()).lineStart(source.getLineStart()).lineEnd(source.getLineEnd())
                .bboxJson(source.getBboxJson()).content(content).contentHash(source.getContentHash())
                .build();
    }

    /** 按字符预算构造证据窗口，并保留少量相邻块解决跨段引用。 */
    private List<EvidenceWindow> buildEvidenceWindows(List<OmAiEvidenceDO> evidenceList, int characterBudget) {
        List<EvidenceWindow> windows = new ArrayList<>();
        int start = 0;
        while (start < evidenceList.size()) {
            int end = start;
            int characters = 0;
            while (end < evidenceList.size()) {
                int nextSize = evidenceSize(evidenceList.get(end));
                if (end > start && characters + nextSize > characterBudget) {
                    break;
                }
                characters += nextSize;
                end++;
            }
            windows.add(new EvidenceWindow(List.copyOf(evidenceList.subList(start, end)), characters));
            if (end >= evidenceList.size()) {
                break;
            }
            // 固定保留两个相邻证据块，兼顾跨段语义和窗口数量。
            start = Math.max(start + 1, end - 2);
        }
        return windows;
    }

    private EvidenceWindow evidenceWindow(List<OmAiEvidenceDO> evidence) {
        List<OmAiEvidenceDO> values = List.copyOf(evidence);
        return new EvidenceWindow(values, values.stream().mapToInt(this::evidenceSize).sum());
    }

    private int evidenceSize(OmAiEvidenceDO evidence) {
        return Optional.ofNullable(evidence.getContent()).orElse("").length() + 80;
    }

    /** 处理当前轮次模型响应。 */
    private void handleRoundResponse(OmAiTaskDO task, OmAiExtractionRoundEnum round, String content) {
        transactionTemplate.executeWithoutResult(status -> persistCandidates(task, round, content));
    }

    /** 完成任务并统计候选数量。 */
    private String finishTask(OmAiTaskDO task) {
        Integer count = taskMapper.countAllCandidates(task.getId());
        if (count == null || count == 0) {
            throw new IllegalStateException("四轮抽取未生成任何候选数据，请检查模型配置、提示词或模型返回格式");
        }
        task.setCandidateCount(count);
        task.setStatus(OmAiTaskStatusEnum.SUCCESS.getCode());
        List<String> warnings = Optional.ofNullable(currentWarnings.get()).orElse(List.of());
        if (!warnings.isEmpty()) {
            updateStageReasoning(joinReasoning(
                    Optional.ofNullable(currentStage.get()).map(OmAiStageDO::getReasoningContent).orElse(""),
                    "质量警告", trim(String.join("；", warnings), 5000)));
        }
        task.setFailureReason(null);
        task.setCurrentStage(OmAiModelingStageEnum.FINISH.getCode());
        task.setFinishedTime(new Date());
        taskMapper.updateById(task);
        // MyBatis 默认忽略 null 更新，成功状态必须显式清空历史失败原因。
        taskMapper.update(null, new UpdateWrapper<OmAiTaskDO>()
                .eq("id", task.getId()).set("failure_reason", null));
        return "四轮抽取完成，共生成 " + count + " 条待审核候选数据"
                + (warnings.isEmpty() ? "" : "，存在 " + warnings.size() + " 条质量警告");
    }

    private void addWarning(String warning) {
        if (warning == null || warning.isBlank()) return;
        List<String> warnings = currentWarnings.get();
        if (warnings != null && warnings.stream().noneMatch(warning::equals)) warnings.add(warning);
    }

    /** 将任务和运行中阶段更新为失败。 */
    private void failTask(OmAiTaskDO task, Exception exception) {
        log.error("AI 建模任务执行失败，taskId={}", task.getId(), exception);
        String failureReason = userFacingFailureReason(exception);
        task.setStatus(OmAiTaskStatusEnum.FAILED.getCode());
        task.setFailureReason(trim(failureReason, 1000));
        task.setFinishedTime(new Date());
        taskMapper.updateById(task);
        stageMapper.update(null, new UpdateWrapper<OmAiStageDO>()
                .eq("task_id", task.getId())
                .eq("status", OmAiStageStatusEnum.RUNNING.getCode())
                .set("status", OmAiStageStatusEnum.FAILED.getCode())
                .set("error_message", trim(failureReason, 2000))
                .set("finished_time", new Date()));
        streamPublisher.publishTask(task);
    }

    /**
     * 将底层异常转换为用户可以判断责任归属和下一步动作的失败说明。
     * 技术细节仅保留根因摘要，避免把堆栈、接口地址或敏感配置暴露到页面。
     */
    private String userFacingFailureReason(Throwable throwable) {
        String detail = trim(rootCauseMessage(throwable), 300);
        String message = Optional.ofNullable(throwable.getMessage()).orElse(detail);
        String normalized = (message + " " + detail).toLowerCase(Locale.ROOT);

        if (normalized.contains("rejected from") || normalized.contains("task rejected")) {
            return "系统侧模型调用资源繁忙：上一轮模型请求仍在退出，新的补交或重试请求暂时无法执行。"
                    + "这不是文档内容问题，请稍后重新解析；若持续出现，请联系管理员检查模型流线程配置。";
        }
        if (normalized.contains("没有可解析") || normalized.contains("未解析出")
                || normalized.contains("文件读取失败") || normalized.contains("媒体文件读取失败")
                || normalized.contains("文件格式") || normalized.contains("文件损坏")
                || normalized.contains("加密") || normalized.contains("内容为空")) {
            return "文件或文档内容问题：系统未能从所选文件中读取可用于建模的有效内容。"
                    + "请检查文件是否存在、损坏、加密、为空或格式不受支持，处理后重新解析。具体原因：" + detail;
        }
        if (normalized.contains("配置") || normalized.contains("未配置")
                || normalized.contains("模型不存在") || normalized.contains("version")) {
            return "系统配置问题：当前建模任务缺少可用的项目版本或模型配置，用户无需修改文档。"
                    + "请联系管理员检查配置后重新解析。具体原因：" + detail;
        }
        if (normalized.contains("模型 json 补交超过")) {
            return "模型服务输出超时：本轮原请求没有完成可校验的结构化输出，格式补交也超过等待时限，"
                    + "且没有可安全保存的完整候选。这不是文件解析失败；此前完成阶段的结果会保留。"
                    + "请稍后重新解析，若重复出现请联系管理员检查模型响应速度。具体原因：" + detail;
        }
        if (normalized.contains("模型") || normalized.contains("json")
                || normalized.contains("timeout") || normalized.contains("timed out")
                || normalized.contains("超时") || normalized.contains("connection")
                || normalized.contains("连接") || normalized.contains("rate limit")
                || normalized.contains("too many requests") || normalized.contains("429")
                || normalized.contains("502") || normalized.contains("503")
                || normalized.contains("504") || normalized.contains("service unavailable")) {
            return "系统侧模型服务异常：文档已进入解析流程，但模型服务未返回完整、可校验的结构化结果，"
                    + "系统自动重试或格式补交后仍未恢复。这通常不是文档填写问题，请稍后重新解析；"
                    + "若持续出现，请联系管理员检查模型服务。";
        }
        return "系统处理异常：任务执行过程中发生未预期错误。请先重新解析；若重复出现，"
                + "请联系管理员并提供任务编号。";
    }

    /** 读取媒体文件。 */
    private byte[] download(String url) {
        try {
            if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) {
                try (var input = new URL(url).openStream()) {
                    return input.readAllBytes();
                }
            }
            return Files.readAllBytes(resolveLocalFilePath(url));
        } catch (Exception exception) {
            throw new IllegalStateException("媒体文件读取失败：" + url, exception);
        }
    }

    private Path resolveLocalFilePath(String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://"))) return null;
        String relativePath = Optional.ofNullable(url).orElse("").replace('\\', '/');
        if (relativePath.startsWith(Constants.RESOURCE_PREFIX + "/")) {
            relativePath = relativePath.substring(Constants.RESOURCE_PREFIX.length() + 1);
        }
        while (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        Path storageRoot = Path.of(localStoragePath).toAbsolutePath().normalize();
        Path filePath = storageRoot.resolve(relativePath).normalize();
        if (!filePath.startsWith(storageRoot)) {
            throw new IllegalArgumentException("非法文件路径");
        }
        return filePath;
    }

    /**
     * 加载与当前证据相关的前序候选。完整候选仍保存在数据库中作为外部记忆，
     * 模型上下文只接收精简目录和命中当前证据的候选明细，防止任务越执行上下文越大。
     */
    private String loadRelevantEntityIndex(Long taskId, OmAiExtractionRoundEnum round,
                                           String evidenceText, int maxChars) {
        Map<String, List<Map<String, Object>>> all = new LinkedHashMap<>();
        switch (round) {
            case FOUNDATION -> {
                all.put("targets", targetMapper.selectCandidateIndex(taskId));
                all.put("terms", termCandidateIndex(taskId));
                all.put("enumTypes", enumTypeMapper.selectCandidateIndex(taskId));
                all.put("enumValues", enumDataMapper.selectCandidateIndex(taskId));
            }
            case OBJECT -> {
                all.put("targets", targetMapper.selectCandidateIndex(taskId));
                all.put("objects", objectMapper.selectCandidateIndex(taskId));
            }
            case MEMBERS -> {
                all.put("targets", targetMapper.selectCandidateIndex(taskId));
                all.put("enumTypes", enumTypeMapper.selectCandidateIndex(taskId));
                all.put("enumValues", enumDataMapper.selectCandidateIndex(taskId));
                all.put("objects", objectMapper.selectCandidateIndex(taskId));
                all.put("attributes", attributeMapper.selectCandidateIndex(taskId));
                all.put("relations", relationMapper.selectCandidateIndex(taskId));
            }
            case RULES -> {
                all.put("enumTypes", enumTypeMapper.selectCandidateIndex(taskId));
                all.put("enumValues", enumDataMapper.selectCandidateIndex(taskId));
                all.put("objects", objectMapper.selectCandidateIndex(taskId));
                all.put("attributes", attributeMapper.selectCandidateIndex(taskId));
                all.put("relations", relationMapper.selectCandidateIndex(taskId));
            }
        }

        Map<String, Object> context = new LinkedHashMap<>();
        Map<String, Object> catalog = new LinkedHashMap<>();
        all.forEach((type, values) -> catalog.put(type, compactCatalog(values)));
        context.put("catalog", catalog);

        Map<String, List<Map<String, Object>>> relevant = new LinkedHashMap<>();
        all.forEach((type, values) -> relevant.put(type, selectRelevantCandidates(values, evidenceText, 40)));
        context.put("relevant", relevant);
        List<?> projectBusinessTypes = List.of();
        List<?> globalBusinessTypes = List.of();
        if (round == OmAiExtractionRoundEnum.MEMBERS) {
            projectBusinessTypes = dataTypeMapper.selectAiOptionsByTaskId(taskId);
            globalBusinessTypes = basicDataTypeMapper.selectAiOptions();
            context.put("projectBusinessTypes", trimList(projectBusinessTypes, 80));
            context.put("globalBusinessTypes", trimList(globalBusinessTypes, 80));
        }
        String json = JSON.toJSONString(context);
        if (json.length() <= maxChars) {
            return json;
        }
        Map<String, Object> reduced = new LinkedHashMap<>();
        reduced.put("catalogCounts", all.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size(),
                        (left, right) -> left, LinkedHashMap::new)));
        Map<String, Object> reducedRelevant = new LinkedHashMap<>();
        relevant.forEach((type, values) -> reducedRelevant.put(type, trimList(values, 15)));
        reduced.put("relevant", reducedRelevant);
        if (round == OmAiExtractionRoundEnum.MEMBERS) {
            reduced.put("projectBusinessTypes", trimList(projectBusinessTypes, 30));
            reduced.put("globalBusinessTypes", trimList(globalBusinessTypes, 30));
        }
        String reducedJson = JSON.toJSONString(reduced);
        if (reducedJson.length() <= maxChars) {
            return reducedJson;
        }
        Map<String, Object> minimal = new LinkedHashMap<>();
        minimal.put("catalogCounts", reduced.get("catalogCounts"));
        Map<String, Object> minimalRelevant = new LinkedHashMap<>();
        relevant.forEach((type, values) -> minimalRelevant.put(type, trimList(values, 5)));
        minimal.put("relevant", minimalRelevant);
        String minimalJson = JSON.toJSONString(minimal);
        if (minimalJson.length() <= maxChars) return minimalJson;
        return JSON.toJSONString(Map.of("catalogCounts", reduced.get("catalogCounts")));
    }

    private List<Map<String, Object>> termCandidateIndex(Long taskId) {
        return termMapper.selectList(new QueryWrapper<OmAiTermDO>()
                        .eq("task_id", taskId).eq("del_flag", 0).orderByAsc("id"))
                .stream().map(term -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", term.getId());
                    item.put("stdName", term.getStdName());
                    item.put("alias", term.getAlias());
                    item.put("abbr", term.getAbbr());
                    return item;
                }).toList();
    }

    /** 生成候选全局目录，只保留稳定标识和名称字段。 */
    private List<Map<String, Object>> compactCatalog(List<Map<String, Object>> values) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> value : values) {
            Map<String, Object> item = new LinkedHashMap<>();
            copyIfPresent(value, item, "id", "candidateKey", "name", "question", "enumName", "enumType",
                    "stdName", "alias", "abbr", "objectName", "startObjectName", "endObjectName",
                    "attributeName");
            result.add(item);
            if (result.size() >= 120) {
                break;
            }
        }
        return result;
    }

    /** 使用名称、编码和候选字段进行轻量词法召回，避免依赖额外基础设施即可控制上下文。 */
    private List<Map<String, Object>> selectRelevantCandidates(List<Map<String, Object>> values,
                                                                String evidenceText, int limit) {
        String normalizedEvidence = normalizeReference(evidenceText);
        return values.stream()
                .map(value -> Map.entry(value, candidateRelevance(value, normalizedEvidence)))
                .filter(entry -> entry.getValue() > 0)
                .sorted((left, right) -> Integer.compare(right.getValue(), left.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    private int candidateRelevance(Map<String, Object> candidate, String normalizedEvidence) {
        int score = 0;
        for (Object value : candidate.values()) {
            if (value == null || value instanceof Number || value instanceof Boolean) {
                continue;
            }
            String normalized = normalizeReference(String.valueOf(value));
            if (normalized.length() >= 2 && normalizedEvidence.contains(normalized)) {
                score += Math.min(20, normalized.length());
            }
        }
        return score;
    }

    private void copyIfPresent(Map<String, Object> source, Map<String, Object> target, String... keys) {
        for (String key : keys) {
            if (source.get(key) != null) {
                target.put(key, source.get(key));
            }
        }
    }

    private List<?> trimList(List<?> values, int limit) {
        return values.size() <= limit ? values : values.subList(0, limit);
    }

    /** 解析并保存候选数据。 */
    private void persistCandidates(OmAiTaskDO task, OmAiExtractionRoundEnum round, String rawContent) {
        Long taskId = task.getId();
        currentCreatorId.set(task.getCreatorId());
        currentCreateBy.set(task.getCreateBy());
        currentMissingEvidence.set(new LinkedHashMap<>());
        try {
            JSONObject root = parseJsonObject(rawContent);
            currentWindowEvidenceIds.set(prefetchEvidenceIds(taskId, root));
            switch (round) {
                case FOUNDATION -> {
                    persistTargets(taskId, roundArray(root, round, "targets"));
                    persistTerms(taskId, roundArray(root, round, "terms"));
                    persistEnums(taskId, roundArray(root, round, "enumTypes"));
                }
                case OBJECT -> {
                    JSONArray objects = roundArray(root, round, "objects");
                    persistObjects(taskId, objects);
                    resolveObjectDependencies(taskId, objects);
                    refreshTargetRelatedElementCounts(taskId);
                }
                case MEMBERS -> {
                    persistAttributes(taskId, roundArray(root, round, "attributes"));
                    persistRelations(taskId, roundArray(root, round, "relations"));
                    refreshTargetRelatedElementCounts(taskId);
                }
                case RULES -> {
                    JSONArray axioms = roundArray(root, round, "axioms", "modelAxioms");
                    JSONArray dataRules = roundArray(root, round, "dataRules", "rules");
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
                        addWarning("本窗口的公理未新增候选，可能已存在或未通过引用校验；其他候选已保留，请核查第4轮日志。");
                    }
                    if (dataRules != null && !dataRules.isEmpty() && dataRuleSaved == 0) {
                        addWarning("本窗口的数据规则未新增候选，可能已存在或未通过引用校验；其他候选已保留，请核查第4轮日志。");
                    }
                }
            }
            reportMissingEvidence(round);
        } finally {
            currentWindowEvidenceIds.remove();
            currentMissingEvidence.remove();
            currentCreatorId.remove();
            currentCreateBy.remove();
        }
    }

    private JSONArray roundArray(JSONObject root, OmAiExtractionRoundEnum round, String field, String... aliases) {
        Object value = root.get(field);
        for (String alias : aliases) {
            if (value == null) value = root.get(alias);
        }
        if (value instanceof JSONArray array) {
            JSONArray candidates = new JSONArray();
            int invalid = 0;
            for (Object item : array) {
                if (item instanceof JSONObject) candidates.add(item);
                else invalid++;
            }
            if (invalid > 0) {
                addWarning(round.getStage().getLabel() + "的 " + field + " 数组有 " + invalid
                        + " 条候选格式错误，已跳过；其他有效候选已保留。");
            }
            return candidates;
        }
        addWarning(round.getStage().getLabel() + "的模型响应"
                + (value == null ? "缺少" : "错误填写了") + field
                + " 数组；本窗口其他有效候选已保留，请人工核查该类型。");
        return null;
    }

    /** 只查询本窗口实际引用的证据 ID，避免每条候选再次查询证据表。 */
    private Set<Long> prefetchEvidenceIds(Long taskId, JSONObject root) {
        Set<Long> requested = new LinkedHashSet<>();
        collectEvidenceIds(root, requested);
        if (requested.isEmpty()) return Set.of();
        List<Long> ids = new ArrayList<>(requested);
        Set<Long> valid = new LinkedHashSet<>();
        for (int start = 0; start < ids.size(); start += 500) {
            List<Long> batch = ids.subList(start, Math.min(ids.size(), start + 500));
            evidenceMapper.selectList(new QueryWrapper<OmAiEvidenceDO>()
                            .select("id").eq("task_id", taskId).in("id", batch))
                    .forEach(item -> valid.add(item.getId()));
        }
        return valid;
    }

    private void collectEvidenceIds(Object value, Set<Long> ids) {
        if (value instanceof JSONObject object) {
            Object references = object.get("evidenceBlockNos");
            if (references instanceof JSONArray array) {
                for (Object reference : array) {
                    try {
                        ids.add(Long.valueOf(String.valueOf(reference)));
                    } catch (NumberFormatException ignored) {
                        // 无效编号会在候选绑定时标记为质量问题。
                    }
                }
            }
            object.values().forEach(item -> collectEvidenceIds(item, ids));
        } else if (value instanceof JSONArray array) {
            array.forEach(item -> collectEvidenceIds(item, ids));
        }
    }

    private void reportMissingEvidence(OmAiExtractionRoundEnum round) {
        Map<OmAiCandidateTypeEnum, Set<Long>> missing = currentMissingEvidence.get();
        if (missing == null) return;
        missing.forEach((type, ids) -> {
            if (ids.isEmpty()) return;
            String idList = ids.stream().limit(20).map(String::valueOf)
                    .collect(java.util.stream.Collectors.joining("、"));
            addWarning(round.getStage().getLabel() + "有 " + ids.size() + " 条" + type.getLabel()
                    + "候选缺少完整有效的证据关联，候选编号：" + idList
                    + (ids.size() > 20 ? " 等" : "") + "；请核查来源后再采纳。");
        });
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
            String question = cleanRequiredText(object.getString("question"));
            String candidateKey = cleanRequiredText(object.getString("candidateKey"));
            Long existingId = findTargetCandidateId(taskId, candidateKey, question);
            if (question == null) {
                continue;
            }
            if (existingId != null) {
                bindEvidence(taskId, OmAiCandidateTypeEnum.TARGET, existingId,
                        object.getJSONArray("evidenceBlockNos"));
                continue;
            }
            OmAiTargetDO candidate = new OmAiTargetDO();
            candidate.setTaskId(taskId);
            candidate.setCandidateKey(candidateKey);
            candidate.setQuestion(question);
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
            String stdName = cleanRequiredText(object.getString("stdName"));
            Long existingId = findTermCandidateId(taskId, stdName);
            if (stdName == null) {
                continue;
            }
            if (existingId != null) {
                bindEvidence(taskId, OmAiCandidateTypeEnum.TERM, existingId,
                        object.getJSONArray("evidenceBlockNos"));
                continue;
            }
            OmAiTermDO candidate = new OmAiTermDO();
            candidate.setTaskId(taskId);
            candidate.setStdName(stdName);
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
        long nextOrderNum = objectMapper.selectMaxOrderNum(taskId);
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            String name = cleanRequiredText(object.getString("name"));
            Long existingId = findObjectCandidateId(taskId, name);
            if (name == null) {
                continue;
            }
            Set<Long> targetIds = resolveTargetIds(taskId, object);
            if (targetIds.isEmpty() && existingId == null) {
                addWarning("对象类型“" + name + "”未能依据证据关联到建模目标，已跳过该候选；请检查目标定义或人工补充关联。");
                continue;
            }
            if (existingId != null) {
                bindEvidence(taskId, OmAiCandidateTypeEnum.OBJECT, existingId,
                        object.getJSONArray("evidenceBlockNos"));
                bindObjectTargets(taskId, existingId, targetIds);
                continue;
            }
            OmAiObjectDO candidate = new OmAiObjectDO();
            candidate.setTaskId(taskId);
            candidate.setName(name);
            String color = Optional.ofNullable(object.getString("color")).map(String::trim).orElse("");
            if (!color.matches("#[0-9a-fA-F]{6}")) {
                addWarning("对象类型“" + name + "”未返回有效颜色，已随机补充颜色。");
                color = String.format(Locale.ROOT, "#%06X", ThreadLocalRandom.current().nextInt(0x1000000));
            }
            candidate.setColor(color.toUpperCase(Locale.ROOT));
            candidate.setDescription(object.getString("description"));
            candidate.setOrderNum(++nextOrderNum);
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            objectMapper.insert(candidate);
            bindEvidence(taskId, OmAiCandidateTypeEnum.OBJECT, candidate.getId(),
                    object.getJSONArray("evidenceBlockNos"));
            bindObjectTargets(taskId, candidate.getId(), targetIds);
        }
    }

    private void bindObjectTargets(Long taskId, Long objectId, Set<Long> targetIds) {
        for (Long targetId : targetIds) {
            OmAiObjectTargetDO link = new OmAiObjectTargetDO();
            link.setTaskId(taskId);
            link.setObjectCandidateId(objectId);
            link.setTargetCandidateId(targetId);
            objectTargetMapper.insertOrIgnore(link);
        }
    }

    /** 截取模型返回的 JSON 对象。 */
    private JSONObject parseJsonObject(String rawContent) {
        String content = Optional.ofNullable(rawContent).orElse("{}").trim();
        content = content.replaceFirst("^```(?:json)?\\s*", "")
                .replaceFirst("\\s*```$", "").trim();
        int start = content.indexOf('{');
        if (start < 0) {
            throw new IllegalArgumentException("模型未返回 JSON 对象");
        }
        int end = findJsonObjectEnd(content, start);
        if (end < 0) {
            throw new IllegalArgumentException("模型返回的 JSON 对象未完整闭合，可能因输出长度限制被截断");
        }
        try {
            JSONObject result = JSON.parseObject(content.substring(start, end + 1));
            String trailing = content.substring(end + 1).replace("```", "").trim();
            if (!trailing.isBlank()) {
                log.warn("模型在首个完整 JSON 后返回了额外内容，已忽略，length={}", trailing.length());
            }
            return result;
        } catch (Exception exception) {
            throw new IllegalArgumentException("模型返回内容不是合法 JSON：" + trim(exception.getMessage(), 300), exception);
        }
    }

    /** 查找首个 JSON 对象的配对结束位置，忽略字符串内部的花括号。 */
    private int findJsonObjectEnd(String content, int start) {
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int index = start; index < content.length(); index++) {
            char character = content.charAt(index);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (character == '\\') {
                    escaped = true;
                } else if (character == '"') {
                    inString = false;
                }
                continue;
            }
            if (character == '"') {
                inString = true;
            } else if (character == '{') {
                depth++;
            } else if (character == '}' && --depth == 0) {
                return index;
            }
        }
        return -1;
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

    /** 流式调用模型，页面实时展示思考过程；缺少最终 JSON 时自动追加一次结构化收敛请求。 */
    private String streamModelResponse(AiChatRequest request, OmAiExtractionRoundEnum round,
                                       String modelName, int progress, String batchName) {
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
        long startedAt = System.currentTimeMillis();
        long[] lastChunkAt = {startedAt};
        AtomicBoolean receivedChunk = new AtomicBoolean(false);
        AtomicBoolean cancelled = new AtomicBoolean(false);
        CompletableFuture<Void> streamFuture = null;
        RuntimeException streamFailure = null;
        try {
            streamFuture = CompletableFuture.runAsync(() -> chatService.stream(request, chunk -> {
                if (cancelled.get()) return;
                long now = System.currentTimeMillis();
                receivedChunk.set(true);
                lastChunkAt[0] = now;
                if (chunk.content() != null && !chunk.content().isEmpty()) {
                    content.append(chunk.content());
                }
                if (chunk.reasoningContent() != null && !chunk.reasoningContent().isEmpty()) {
                    modelReasoning.append(chunk.reasoningContent());
                }
                boolean streamChanged = content.length() != lastContentLength[0]
                        || modelReasoning.length() != lastReasoningLength[0];
                if (!chunk.finished() && (!streamChanged || now - lastUpdateTime[0] < 1000L)) {
                    return;
                }
                String analysisProcess = extractStreamingJsonString(content.toString(), "analysisProcess");
                String currentReasoning = composeVisibleReasoning(modelReasoning.toString(), analysisProcess);
                applyStageProgress(streamingStage, progress,
                        analysisProcess.isBlank()
                                ? "正在分析" + batchName + "，模型思考过程正在流式输出。"
                                : "正在分析" + batchName + "，结构化分析过程正在流式输出。",
                        modelName, null, modelReasoning.length() + content.length());
                streamingStage.setReasoningContent(joinReasoning(previousReasoning, batchName, currentReasoning));
                streamPublisher.publishStage(streamingStage);
                lastUpdateTime[0] = now;
                lastContentLength[0] = content.length();
                lastReasoningLength[0] = modelReasoning.length();
            }), streamExecutor);
            while (!streamFuture.isDone()) {
                long now = System.currentTimeMillis();
                if (!receivedChunk.get() && now - startedAt > modelingProperties.firstChunkTimeoutMillis()) {
                    throw new IllegalStateException("等待模型首个分片超时：" + batchName);
                }
                if (receivedChunk.get() && now - lastChunkAt[0] > modelingProperties.idleTimeoutMillis()) {
                    throw new IllegalStateException("模型流式输出长时间无新分片：" + batchName);
                }
                if (now - startedAt > modelingProperties.streamMaxMillis()) {
                    throw new IllegalStateException("模型思考超过保护时长：" + batchName);
                }
                try {
                    streamFuture.get(500, TimeUnit.MILLISECONDS);
                } catch (TimeoutException ignored) {
                    // 周期性检查首包、空闲和总时长。
                }
            }
            streamFuture.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            streamFailure = new IllegalStateException("模型流式调用被中断：" + batchName, exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            streamFailure = cause instanceof RuntimeException runtimeException
                    ? runtimeException : new IllegalStateException("模型流式调用失败：" + batchName, cause);
        } catch (RuntimeException exception) {
            streamFailure = exception;
        } finally {
            cancelled.set(true);
            if (streamFuture != null && !streamFuture.isDone()) {
                streamFuture.cancel(true);
            }
        }
        log.info("AI 建模模型主请求结束，round={}, batch={}, elapsedMs={}, reasoningChars={}, contentChars={}, failure={}",
                round.getCode(), batchName, System.currentTimeMillis() - startedAt,
                modelReasoning.length(), content.length(),
                streamFailure == null ? "none" : trim(rootCauseMessage(streamFailure), 300));
        String finalVisibleReasoning = joinReasoning(previousReasoning, batchName,
                composeVisibleReasoning(modelReasoning.toString(),
                        extractStreamingJsonString(content.toString(), "analysisProcess")));
        updateStageReasoning(streamingStageId, streamingStage, finalVisibleReasoning);
        String result = removeThinkContent(content.toString());
        if (containsCompleteJson(result)) {
            return result;
        }
        String locallyRepaired = repairTrailingAnalysisFields(result, round);
        if (containsCompleteJson(locallyRepaired)) {
            log.info("模型候选数组完整，仅尾部分析字段被截断，已本地修复，batch={}", batchName);
            return locallyRepaired;
        }
        if (result.isBlank() && streamFailure != null && isContextLengthFailure(streamFailure)) {
            throw streamFailure;
        }
        updateStageProgress(streamingStageId, streamingStage, progress,
                "模型未返回完整候选 JSON，正在进行一次格式补交（最多等待"
                        + modelingProperties.recoveryTimeoutMillis() / 1000 + "秒）。",
                modelName, null, modelReasoning.length() + content.length());
        log.info("AI 建模触发结构化补交，round={}, batch={}, partialChars={}, mainFailure={}",
                round.getCode(), batchName,
                result.length(), streamFailure == null ? "none" : trim(rootCauseMessage(streamFailure), 300));
        String converged = removeThinkContent(
                convergeStructuredJson(request, round, batchName));
        if (!containsCompleteJson(converged)) {
            throw new StructuredOutputException("模型经格式补交后仍未返回完整 JSON：" + batchName, streamFailure);
        }
        return converged;
    }

    /**
     * 组合页面可见的模型思考流和结构化分析过程。
     */
    private String composeVisibleReasoning(String modelReasoning, String analysisProcess) {
        String reasoning = Optional.ofNullable(modelReasoning).orElse("").trim();
        String analysis = Optional.ofNullable(analysisProcess).orElse("").trim();
        if (reasoning.isBlank() && analysis.isBlank()) {
            return "模型请求已建立，正在等待首个分析分片。";
        }
        if (reasoning.isBlank()) {
            return "【分析过程】\n" + analysis;
        }
        if (analysis.isBlank()) {
            return "【模型思考过程】\n" + trim(reasoning, modelingProperties.visibleReasoningMaxChars());
        }
        return "【模型思考过程】\n" + trim(reasoning, modelingProperties.visibleReasoningMaxChars())
                + "\n\n【分析过程】\n" + analysis;
    }

    private boolean containsCompleteJson(String content) {
        try {
            parseJsonObject(content);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /** 首次请求保留可见思考；正文不完整时，复用原始证据和候选索引关闭思考生成 JSON。 */
    private String convergeStructuredJson(AiChatRequest originalRequest, OmAiExtractionRoundEnum round,
                                          String batchName) {
        List<AiChatMessage> messages = new ArrayList<>(originalRequest.messages());
        messages.add(AiChatMessage.user(buildRecoveryPrompt(round)));
        AiChatRequest convergenceRequest = new AiChatRequest(originalRequest.model(), messages, 0.0,
                Optional.ofNullable(originalRequest.maxTokens()).orElse(maxOutputTokens(round)),
                Map.of("enable_thinking", false,
                        "response_format", Map.of("type", "json_object")));
        try {
            String result = callStructuredRecovery(convergenceRequest, batchName);
            if (!containsCompleteJson(result)) {
                throw new IllegalStateException("JSON 补交结果仍不完整");
            }
            return result;
        } catch (Exception exception) {
            throw new StructuredOutputException("模型 JSON 格式补交请求失败：" + batchName
                    + "，原因：" + trim(rootCauseMessage(exception), 500), exception);
        }
    }

    private String buildRecoveryPrompt(OmAiExtractionRoundEnum round) {
        return "上一次请求未返回完整候选 JSON。请根据本次请求中的原始证据和前序候选索引，"
                + "重新抽取本轮候选并直接输出一个完整 JSON 对象。不要输出思考过程、analysisProcess、"
                + "analysisSummary、Markdown 或无证据支持的候选；没有依据的候选数组输出空数组。\n\n"
                + "【本轮字段规范】\n" + recoveryJsonSchema(round);
    }

    /** JSON 补交关闭思考但仍使用流式传输，避免大 JSON 等待完整响应时被网关取消。 */
    private String callStructuredRecovery(AiChatRequest request, String batchName) {
        StringBuilder content = new StringBuilder();
        AtomicBoolean receivedChunk = new AtomicBoolean(false);
        long startedAt = System.currentTimeMillis();
        long[] lastChunkAt = {startedAt};
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> chatService.stream(request, chunk -> {
            if (chunk.content() != null && !chunk.content().isEmpty()) {
                content.append(chunk.content());
                receivedChunk.set(true);
                lastChunkAt[0] = System.currentTimeMillis();
            }
        }), streamExecutor);
        try {
            while (!future.isDone()) {
                long now = System.currentTimeMillis();
                // 结构化输出可能在供应商完成整个 JSON 校验后才返回首个分片，
                // 因此补交阶段只受总时长保护，不能沿用普通思考流的首包超时。
                if (receivedChunk.get() && now - lastChunkAt[0] > modelingProperties.idleTimeoutMillis()) {
                    throw new IllegalStateException("模型 JSON 补交长时间无新分片：" + batchName);
                }
                if (now - startedAt > modelingProperties.recoveryTimeoutMillis()) {
                    throw new IllegalStateException("模型 JSON 补交超过 "
                            + modelingProperties.recoveryTimeoutMillis() / 1000 + " 秒保护时长：" + batchName);
                }
                try {
                    future.get(500, TimeUnit.MILLISECONDS);
                } catch (TimeoutException ignored) {
                    // 周期性检查首包、空闲和总时长。
                }
            }
            future.get();
            return content.toString().trim();
        } catch (InterruptedException exception) {
            future.cancel(true);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("模型 JSON 格式补交被中断：" + batchName, exception);
        } catch (ExecutionException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) throw runtimeException;
            throw new IllegalStateException("模型 JSON 格式补交调用失败：" + batchName, cause);
        } catch (RuntimeException exception) {
            future.cancel(true);
            throw exception;
        }
    }

    /** 候选数组优先输出后，若只截断了尾部分析字段，可直接本地补齐而无需再次调用模型。 */
    private String repairTrailingAnalysisFields(String content, OmAiExtractionRoundEnum round) {
        String json = Optional.ofNullable(content).orElse("").trim();
        int start = json.indexOf('{');
        if (start < 0) return json;
        json = json.substring(start);
        int analysisSummary = json.indexOf("\"analysisSummary\"");
        int analysisProcess = json.indexOf("\"analysisProcess\"");
        int cut = analysisSummary >= 0 && analysisProcess >= 0
                ? Math.min(analysisSummary, analysisProcess) : Math.max(analysisSummary, analysisProcess);
        if (cut < 0) return json;
        int comma = json.lastIndexOf(',', cut);
        if (comma < 0) return json;
        String candidateJson = json.substring(0, comma).trim() + "}";
        try {
            JSONObject root = parseJsonObject(candidateJson);
            for (String field : candidateArrayFields(round)) {
                if (!(root.get(field) instanceof JSONArray)) return json;
            }
            return candidateJson;
        } catch (Exception ignored) {
            return json;
        }
    }

    private List<String> candidateArrayFields(OmAiExtractionRoundEnum round) {
        return switch (round) {
            case FOUNDATION -> List.of("targets", "terms", "enumTypes");
            case OBJECT -> List.of("objects");
            case MEMBERS -> List.of("attributes", "relations");
            case RULES -> List.of("axioms", "dataRules");
        };
    }

    /** JSON 恢复只需要当前轮次的字段骨架，不重新携带原始业务提示词。 */
    private String recoveryJsonSchema(OmAiExtractionRoundEnum round) {
        return switch (round) {
            case FOUNDATION -> "{targets:[{question,questionSource,businessScenarios,priority,acceptCriterion,candidateKey,evidenceBlockNos,confidence}],terms:[{stdName,enName,alias,abbr,source,description,candidateKey,evidenceBlockNos,confidence}],enumTypes:[{enumName,enumType,description,candidateKey,values:[{enumLabel,enumValue,enumSort,description}],evidenceBlockNos,confidence}]}";
            case OBJECT -> "{objects:[{name,color,description,parentName,targetCandidateKeys,targetQuestions,evidenceBlockNos,confidence}]}";
            case MEMBERS -> "{attributes:[{objectCandidateId,objectName,name,code,description,suggestedTypeCategory,suggestedBasicTypeCode,businessTypeId,businessTypeSource,unit,decimalPlaces,format,enumType,enumSource,evidenceBlockNos,confidence}],relations:[{name,description,startObjectCandidateId,startObjectName,endObjectCandidateId,endObjectName,reversibleFlag,reverseName,targetCandidateKeys,targetQuestions,evidenceBlockNos,confidence}]}";
            case RULES -> "{axioms:[{name,description,type,level,objectCandidateId,objectName,otherObjectCandidateId,otherObjectName,relationName,domainCandidateId,domainObjectName,rangeCandidateId,rangeObjectName,attributeCandidateId,attributeName,evidenceBlockNos,confidence}],dataRules:[{name,description,type,level,conditionRelation,conditions:[{objectCandidateId,objectName,attributeCandidateId,attributeName,operator,value}],failureMessage,evidenceBlockNos,confidence}],issues:[{level,message,evidenceBlockNos}]}";
        };
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
        String combined = prefix + "【" + batchName + "】\n" + current;
        if (combined.length() <= modelingProperties.getReasoningMaxChars()) {
            return combined;
        }
        return "【较早的思考过程已省略，仅保留最近内容】\n"
                + tail(combined, modelingProperties.getReasoningMaxChars());
    }

    /**
     * 构造包含模型推理通道和结构化分析过程的流式请求。
     * reasoningContent 与正文 analysisProcess 均通过当前阶段 SSE 实时推送。
     */
    private AiChatRequest auditableStreamingRequest(tech.qiantong.qontology.ai.api.model.AiModelRef model,
                                                     List<AiChatMessage> messages, int maxTokens) {
        List<AiChatMessage> requestMessages = new ArrayList<>();
        requestMessages.add(AiChatMessage.system(CHINESE_OUTPUT_SYSTEM_PROMPT));
        requestMessages.addAll(messages);
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("enable_thinking", true);
        attributes.put("response_format", Map.of("type", "json_object"));
        attributes.put("thinking_budget", modelingProperties.getThinkingTokenBudget());
        return new AiChatRequest(model, requestMessages, 0.0, maxTokens, attributes);
    }

    private int maxOutputTokens(OmAiExtractionRoundEnum round) {
        return switch (round) {
            case FOUNDATION -> 10000;
            case OBJECT -> 5000;
            case MEMBERS -> 10000;
            case RULES -> 10000;
        };
    }

    /** 保存枚举类型及全部枚举值。 */
    private void persistEnums(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            String enumName = cleanRequiredText(object.getString("enumName"));
            if (enumName == null) {
                continue;
            }
            OmAiEnumTypeDO existing = findEnumTypeCandidate(taskId, enumName, object.getString("enumType"));
            if (existing != null) {
                if ((existing.getDescription() == null || existing.getDescription().isBlank())
                        && object.getString("description") != null) {
                    existing.setDescription(trim(object.getString("description"), 512));
                    enumTypeMapper.updateById(existing);
                }
                bindEvidence(taskId, OmAiCandidateTypeEnum.ENUM_TYPE, existing.getId(),
                        object.getJSONArray("evidenceBlockNos"));
                mergeEnumValues(taskId, existing.getId(), object.getJSONArray("values"),
                        object.getJSONArray("evidenceBlockNos"));
                continue;
            }
            OmAiEnumTypeDO enumType = new OmAiEnumTypeDO();
            enumType.setTaskId(taskId);
            enumType.setEnumName(enumName);
            enumType.setEnumType(resolveEnumType(taskId, object));
            enumType.setDescription(trim(object.getString("description"), 512));
            enumType.setStatus("0");
            enumType.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            enumType.setConfidence(object.getBigDecimal("confidence"));
            enumType.setDelFlag(false);
            applyCreator(enumType);
            enumTypeMapper.insert(enumType);
            bindEvidence(taskId, OmAiCandidateTypeEnum.ENUM_TYPE, enumType.getId(),
                    object.getJSONArray("evidenceBlockNos"));
            mergeEnumValues(taskId, enumType.getId(), object.getJSONArray("values"),
                    object.getJSONArray("evidenceBlockNos"));
        }
    }

    private void mergeEnumValues(Long taskId, Long enumTypeId, JSONArray values, JSONArray evidenceIds) {
        if (values == null) {
            return;
        }
        List<OmAiEnumDataDO> existingValues = enumDataMapper.selectList(new QueryWrapper<OmAiEnumDataDO>()
                .eq("task_id", taskId).eq("enum_type_id", enumTypeId).eq("del_flag", 0));
        Map<Map.Entry<String, String>, OmAiEnumDataDO> existingByValue = new LinkedHashMap<>();
        for (OmAiEnumDataDO candidate : existingValues) {
            existingByValue.putIfAbsent(Map.entry(normalizeReference(candidate.getEnumLabel()),
                    normalizeReference(candidate.getEnumValue())), candidate);
        }
        for (Object item : values) {
                if (!(item instanceof JSONObject enumValue)) {
                    addWarning("枚举类型 " + enumTypeId + " 包含格式错误的枚举值，已跳过该项。");
                    continue;
                }
                String label = cleanRequiredText(enumValue.getString("enumLabel"));
                String value = cleanRequiredText(enumValue.getString("enumValue"));
                String normalizedLabel = normalizeReference(label);
                String normalizedValue = normalizeReference(value);
                Map.Entry<String, String> valueKey = Map.entry(normalizedLabel, normalizedValue);
                OmAiEnumDataDO existing = existingByValue.get(valueKey);
                if (existing != null) {
                    if ((existing.getDescription() == null || existing.getDescription().isBlank())
                            && enumValue.getString("description") != null) {
                        existing.setDescription(trim(enumValue.getString("description"), 512));
                        enumDataMapper.updateById(existing);
                    }
                    bindEvidence(taskId, OmAiCandidateTypeEnum.ENUM_DATA, existing.getId(), evidenceIds);
                    continue;
                }
                if (label == null || value == null) {
                    continue;
                }
                OmAiEnumDataDO enumData = new OmAiEnumDataDO();
                enumData.setTaskId(taskId);
                enumData.setEnumTypeId(enumTypeId);
                enumData.setEnumSort(Optional.ofNullable(enumValue.getLong("enumSort")).orElse(0L));
                enumData.setEnumLabel(label);
                enumData.setEnumValue(value);
                enumData.setDescription(trim(enumValue.getString("description"), 512));
                enumData.setStatus("0");
                enumData.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
                enumData.setDelFlag(false);
                applyCreator(enumData);
                enumDataMapper.insert(enumData);
                existingByValue.put(valueKey, enumData);
                bindEvidence(taskId, OmAiCandidateTypeEnum.ENUM_DATA, enumData.getId(), evidenceIds);
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
        int requestedParents = 0;
        int resolvedParents = 0;
        for (Object value : objects) {
            JSONObject object = (JSONObject) value;
            Long objectId = findObjectCandidateId(taskId, object.getString("name"));
            String parentName = cleanRequiredText(object.getString("parentName"));
            if (parentName != null) {
                requestedParents++;
                Long parentId = findObjectCandidateId(taskId, parentName);
                if (applyParentCandidate(taskId, objectId, parentId, "第2轮对象抽取")) {
                    resolvedParents++;
                } else {
                    log.warn("对象父级引用未生效，taskId={}, object={}, parent={}",
                            taskId, object.getString("name"), parentName);
                }
            }
            if (objectId == null) {
                continue;
            }
        }
        log.info("对象层级解析完成，taskId={}, 模型返回父级={}, 成功设置={}",
                taskId, requestedParents, resolvedParents);
    }

    /** 设置对象父级，并阻止跨任务引用、自引用、覆盖冲突父级和循环层级。 */
    private boolean applyParentCandidate(Long taskId, Long objectId, Long parentId, String source) {
        if (objectId == null || parentId == null || objectId.equals(parentId)) {
            return false;
        }
        OmAiObjectDO object = objectMapper.selectById(objectId);
        OmAiObjectDO parent = objectMapper.selectById(parentId);
        if (object == null || parent == null || !taskId.equals(object.getTaskId()) || !taskId.equals(parent.getTaskId())) {
            return false;
        }
        if (object.getParentCandidateId() != null) {
            return object.getParentCandidateId().equals(parentId);
        }
        Set<Long> visited = new HashSet<>();
        Long cursor = parentId;
        while (cursor != null && visited.add(cursor)) {
            if (objectId.equals(cursor)) {
                log.warn("忽略会形成循环的对象父级，taskId={}, objectId={}, parentId={}, source={}",
                        taskId, objectId, parentId, source);
                return false;
            }
            OmAiObjectDO ancestor = objectMapper.selectById(cursor);
            if (ancestor == null || !taskId.equals(ancestor.getTaskId())) {
                return false;
            }
            cursor = ancestor.getParentCandidateId();
        }
        OmAiObjectDO update = new OmAiObjectDO();
        update.setId(objectId);
        update.setParentCandidateId(parentId);
        objectMapper.updateById(update);
        log.info("对象父级设置成功，taskId={}, objectId={}, parentId={}, source={}",
                taskId, objectId, parentId, source);
        return true;
    }

    /** 保存对象属性。 */
    private void persistAttributes(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            Long objectId = resolveObjectCandidateId(
                    taskId, object.getLong("objectCandidateId"), object.getString("objectName"));
            String name = cleanRequiredText(object.getString("name"));
            Long existingId = findAttributeCandidateId(taskId, objectId, name);
            if (objectId == null || name == null) {
                continue;
            }
            if (existingId != null) {
                bindEvidence(taskId, OmAiCandidateTypeEnum.ATTRIBUTE, existingId,
                        object.getJSONArray("evidenceBlockNos"));
                continue;
            }
            OmAiAttributeDO candidate = new OmAiAttributeDO();
            candidate.setTaskId(taskId);
            candidate.setObjectCandidateId(objectId);
            candidate.setName(name);
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
            Long startId = resolveObjectCandidateId(
                    taskId, object.getLong("startObjectCandidateId"), object.getString("startObjectName"));
            Long endId = resolveObjectCandidateId(
                    taskId, object.getLong("endObjectCandidateId"), object.getString("endObjectName"));
            String name = cleanRequiredText(object.getString("name"));
            Long existingId = findRelationCandidateId(taskId, startId, endId, name);
            Set<Long> targetIds = resolveTargetIds(taskId, object);
            String extractedReverseName = cleanRequiredText(object.getString("reverseName"));
            Integer reversibleFlag = extractedReverseName != null ? 1 : 0;
            String reverseName = reversibleFlag == 1 ? extractedReverseName : null;
            if (startId == null || endId == null || startId.equals(endId) || name == null) {
                continue;
            }
            if (targetIds.isEmpty() && existingId == null) {
                addWarning("对象类型关系“" + name + "”未能依据证据关联到建模目标，已跳过该候选；请检查目标定义或人工补充关联。");
                continue;
            }
            if (existingId != null) {
                OmAiRelationDO existing = relationMapper.selectById(existingId);
                if (existing != null) {
                    boolean changed = false;
                    if (reversibleFlag == 1 && (!Integer.valueOf(1).equals(existing.getReversibleFlag())
                            || existing.getReverseName() == null || existing.getReverseName().isBlank())) {
                        existing.setReversibleFlag(1);
                        if (existing.getReverseName() == null || existing.getReverseName().isBlank()) {
                            existing.setReverseName(reverseName);
                        }
                        changed = true;
                    }
                    if ((existing.getDescription() == null || existing.getDescription().isBlank())
                            && object.getString("description") != null) {
                        existing.setDescription(object.getString("description"));
                        changed = true;
                    }
                    if (changed) {
                        relationMapper.updateById(existing);
                    }
                }
                bindEvidence(taskId, OmAiCandidateTypeEnum.RELATION, existingId,
                        object.getJSONArray("evidenceBlockNos"));
                bindRelationTargets(taskId, existingId, targetIds);
                continue;
            }
            OmAiRelationDO candidate = new OmAiRelationDO();
            candidate.setTaskId(taskId);
            candidate.setName(name);
            candidate.setDescription(object.getString("description"));
            candidate.setStartObjectCandidateId(startId);
            candidate.setEndObjectCandidateId(endId);
            candidate.setReversibleFlag(reversibleFlag);
            candidate.setReverseName(reverseName);
            candidate.setAdoptStatus(OmAiAdoptStatusEnum.PENDING.getCode());
            candidate.setConfidence(object.getBigDecimal("confidence"));
            candidate.setDelFlag(false);
            applyCreator(candidate);
            relationMapper.insert(candidate);
            Long relationId = candidate.getId();
            bindEvidence(taskId, OmAiCandidateTypeEnum.RELATION, relationId,
                    object.getJSONArray("evidenceBlockNos"));
            bindRelationTargets(taskId, relationId, targetIds);
        }
    }

    private void bindRelationTargets(Long taskId, Long relationId, Set<Long> targetIds) {
        for (Long targetId : targetIds) {
            OmAiRelationTargetDO link = new OmAiRelationTargetDO();
            link.setTaskId(taskId);
            link.setRelationCandidateId(relationId);
            link.setTargetCandidateId(targetId);
            relationTargetMapper.insertOrIgnore(link);
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
            if ("SUBCLASS".equals(type)) {
                applyParentCandidate(taskId, objectId, otherObjectId, "第4轮SUBCLASS公理");
            }
            Long existingId = findAxiomCandidateId(taskId, type, objectId, otherObjectId,
                    resolvedRelationName, domainId, rangeId, attributeId);
            if (existingId != null) {
                bindEvidence(taskId, OmAiCandidateTypeEnum.AXIOM, existingId,
                        object.getJSONArray("evidenceBlockNos"));
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
            Long existingId = findDataRuleCandidateId(taskId, type, object.getString("name"),
                    resolvedConditions.toJSONString());
            if (existingId != null) {
                bindEvidence(taskId, OmAiCandidateTypeEnum.DATA_RULE, existingId,
                        object.getJSONArray("evidenceBlockNos"));
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

    /** 更新、持久化并推送非流式阶段的进度。 */
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
        applyStageProgress(stage, progress, summary, modelName, inputCount, outputCount);
        persistStageProgress(stageId, stage, modelName != null, inputCount != null, outputCount != null);
        streamPublisher.publishStage(stage);
    }

    /** 流式回调只更新内存对象并实时推送，模型请求结束后保存完整思考过程。 */
    private void applyStageProgress(OmAiStageDO stage, int progress, String summary,
                                    String modelName, Integer inputCount, Integer outputCount) {
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
    }

    private void persistStageProgress(Long stageId, OmAiStageDO stage, boolean includeModel,
                                      boolean includeInput, boolean includeOutput) {
        UpdateWrapper<OmAiStageDO> update = new UpdateWrapper<OmAiStageDO>()
                .eq("id", stageId)
                .set("progress", stage.getProgress())
                .set("summary", stage.getSummary());
        if (includeModel) {
            update.set("model_name", stage.getModelName());
        }
        if (includeInput) {
            update.set("input_count", stage.getInputCount());
        }
        if (includeOutput) {
            update.set("output_count", stage.getOutputCount());
        }
        stageMapper.update(null, update);
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
        persistStageSnapshot(stageId, stage);
        streamPublisher.publishStage(stage);
    }

    /** 一条 SQL 保存阶段进度、输出计数及完整思考快照。 */
    private void persistStageSnapshot(Long stageId, OmAiStageDO stage) {
        stageMapper.update(null, new UpdateWrapper<OmAiStageDO>()
                .eq("id", stageId)
                .set("progress", stage.getProgress())
                .set("summary", stage.getSummary())
                .set("model_name", stage.getModelName())
                .set("input_count", stage.getInputCount())
                .set("output_count", stage.getOutputCount())
                .set("reasoning_content", stage.getReasoningContent()));
    }

    /** 绑定候选数据与来源证据。 */
    private void bindEvidence(Long taskId, OmAiCandidateTypeEnum type, Long dataId, JSONArray evidenceIds) {
        if (dataId == null) return;
        if (evidenceIds == null || evidenceIds.isEmpty()) {
            recordMissingEvidence(type, dataId);
            return;
        }
        Set<Long> requestedIds = new LinkedHashSet<>();
        for (Object value : evidenceIds) {
            try {
                requestedIds.add(Long.valueOf(String.valueOf(value)));
            } catch (NumberFormatException exception) {
                log.warn("忽略模型返回的非法证据编号，taskId={}, type={}, dataId={}, evidenceId={}",
                        taskId, type.getCode(), dataId, value);
            }
        }
        if (requestedIds.isEmpty()) {
            recordMissingEvidence(type, dataId);
            return;
        }
        Set<Long> allowedIds = currentWindowEvidenceIds.get();
        Set<Long> validIds = allowedIds == null
                ? evidenceMapper.selectList(new QueryWrapper<OmAiEvidenceDO>()
                        .select("id").eq("task_id", taskId).in("id", requestedIds)).stream()
                .map(OmAiEvidenceDO::getId)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new))
                : requestedIds.stream().filter(allowedIds::contains)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        if (validIds.size() != requestedIds.size()) recordMissingEvidence(type, dataId);
        for (Long evidenceId : validIds) {
            OmAiDataEvidenceDO link = new OmAiDataEvidenceDO();
            link.setTaskId(taskId);
            link.setDataType(type.getCode());
            link.setDataId(dataId);
            link.setEvidenceId(evidenceId);
            link.setEvidenceRole(OmAiEvidenceRoleEnum.POSITIVE.getCode());
            applyCreator(link);
            dataEvidenceMapper.insertOrIgnore(link);
        }
    }

    private void recordMissingEvidence(OmAiCandidateTypeEnum type, Long dataId) {
        Map<OmAiCandidateTypeEnum, Set<Long>> missing = currentMissingEvidence.get();
        if (missing != null) {
            missing.computeIfAbsent(type, ignored -> new LinkedHashSet<>()).add(dataId);
        }
    }

    /** 根据任务和名称查找最近的对象类型候选。 */
    private Long findObjectCandidateId(Long taskId, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = normalizeReference(value);
        return objectMapper.selectCandidateIndex(taskId).stream()
                .filter(item -> normalized.equals(normalizeReference(stringValue(item.get("name")))))
                .map(item -> numberLongValue(item.get("id"))).filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    private Long findTargetCandidateId(Long taskId, String candidateKey, String question) {
        String normalizedKey = normalizeReference(candidateKey);
        String normalizedQuestion = normalizeReference(question);
        return targetMapper.selectCandidateIndex(taskId).stream()
                .filter(item -> (!normalizedKey.isBlank()
                        && normalizedKey.equals(normalizeReference(stringValue(item.get("candidateKey")))))
                        || (!normalizedQuestion.isBlank()
                        && normalizedQuestion.equals(normalizeReference(stringValue(item.get("question"))))))
                .map(item -> numberLongValue(item.get("id"))).filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    private Long findTermCandidateId(Long taskId, String stdName) {
        if (stdName == null) {
            return null;
        }
        String normalized = normalizeReference(stdName);
        return termMapper.selectList(new QueryWrapper<OmAiTermDO>()
                        .eq("task_id", taskId).eq("del_flag", 0)).stream()
                .filter(item -> normalized.equals(normalizeReference(item.getStdName())))
                .map(OmAiTermDO::getId).findFirst().orElse(null);
    }

    private Long findRelationCandidateId(Long taskId, Long startId, Long endId, String name) {
        if (startId == null || endId == null || name == null) {
            return null;
        }
        String normalized = normalizeReference(name);
        return relationMapper.selectList(new QueryWrapper<OmAiRelationDO>()
                        .select("id", "name").eq("task_id", taskId).eq("del_flag", 0)
                        .eq("start_object_candidate_id", startId).eq("end_object_candidate_id", endId)).stream()
                .filter(item -> normalized.equals(normalizeReference(item.getName())))
                .map(OmAiRelationDO::getId).findFirst().orElse(null);
    }

    private OmAiEnumTypeDO findEnumTypeCandidate(Long taskId, String enumName, String enumType) {
        String normalizedName = normalizeReference(enumName);
        String normalizedType = normalizeReference(enumType);
        return enumTypeMapper.selectList(new QueryWrapper<OmAiEnumTypeDO>()
                        .eq("task_id", taskId).eq("del_flag", 0)).stream()
                .filter(item -> normalizedName.equals(normalizeReference(item.getEnumName()))
                        || (!normalizedType.isBlank()
                        && normalizedType.equals(normalizeReference(item.getEnumType()))))
                .findFirst().orElse(null);
    }

    private OmAiEnumDataDO findEnumDataCandidate(Long taskId, Long enumTypeId, String label, String value) {
        String normalizedLabel = normalizeReference(label);
        String normalizedValue = normalizeReference(value);
        return enumDataMapper.selectList(new QueryWrapper<OmAiEnumDataDO>()
                        .eq("task_id", taskId).eq("enum_type_id", enumTypeId).eq("del_flag", 0)).stream()
                .filter(item -> normalizedLabel.equals(normalizeReference(item.getEnumLabel()))
                        && normalizedValue.equals(normalizeReference(item.getEnumValue())))
                .findFirst().orElse(null);
    }

    private Long findAxiomCandidateId(Long taskId, String type, Long objectId, Long otherObjectId,
                                      String relationName, Long domainId, Long rangeId, Long attributeId) {
        String normalizedRelation = normalizeReference(relationName);
        return axiomMapper.selectList(new QueryWrapper<OmAiAxiomDO>()
                        .eq("task_id", taskId).eq("type", type).eq("del_flag", 0)).stream()
                .filter(item -> Objects.equals(objectId, item.getObjectCandidateId())
                        && Objects.equals(otherObjectId, item.getOtherObjectCandidateId())
                        && Objects.equals(domainId, item.getDomainCandidateId())
                        && Objects.equals(rangeId, item.getRangeCandidateId())
                        && Objects.equals(attributeId, item.getAttributeCandidateId())
                        && normalizedRelation.equals(normalizeReference(item.getRelationName())))
                .map(OmAiAxiomDO::getId).findFirst().orElse(null);
    }

    private Long findDataRuleCandidateId(Long taskId, String type, String name, String conditions) {
        String normalizedName = normalizeReference(name);
        return dataRuleMapper.selectList(new QueryWrapper<OmAiDataRuleDO>()
                        .eq("task_id", taskId).eq("type", type).eq("del_flag", 0)).stream()
                .filter(item -> normalizedName.equals(normalizeReference(item.getName()))
                        || Objects.equals(conditions, item.getConditions()))
                .map(OmAiDataRuleDO::getId).findFirst().orElse(null);
    }

    private String cleanRequiredText(String value) {
        String text = Optional.ofNullable(value).orElse("").trim();
        return text.isBlank() || "null".equalsIgnoreCase(text) ? null : text;
    }

    /**
     * 优先使用模型从前序索引复制的对象候选 ID，并校验 ID 与名称一致；
     * 旧模型未返回 ID 时再按名称兼容解析。
     */
    private Long resolveObjectCandidateId(Long taskId, Long objectCandidateId, String objectName) {
        String normalizedName = normalizeReference(objectName);
        if (normalizedName.isBlank()) return null;
        return objectMapper.selectCandidateIndex(taskId).stream()
                .filter(item -> objectCandidateId == null
                        || objectCandidateId.equals(numberLongValue(item.get("id"))))
                .filter(item -> normalizedName.equals(normalizeReference(stringValue(item.get("name")))))
                .map(item -> numberLongValue(item.get("id"))).filter(Objects::nonNull)
                .findFirst().orElse(null);
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

    private static String tail(String value, int maxLength) {
        if (value == null || maxLength <= 0) return "";
        return value.length() > maxLength ? value.substring(value.length() - maxLength) : value;
    }

    private record EvidenceWindow(List<OmAiEvidenceDO> evidence, int characterCount) {
    }

    private record RecognizedMedia(OmAiDocumentParser.MediaBlock media, String content) {
    }

    private static final class EvidenceWindowTooLargeException extends RuntimeException {
        private EvidenceWindowTooLargeException(String message) {
            super(message);
        }
    }

    /** 已完成业务分析但最终结构化正文无法恢复；此类错误不得触发完整抽取重试。 */
    private static final class StructuredOutputException extends RuntimeException {
        private StructuredOutputException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /** 阶段执行动作。 */
    @FunctionalInterface
    private interface StageAction {
        String run();
    }
}
