package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import tech.qiantong.qontology.ai.api.chat.AiChatMedia;
import tech.qiantong.qontology.ai.api.chat.AiChatMessage;
import tech.qiantong.qontology.ai.api.chat.AiChatRequest;
import tech.qiantong.qontology.ai.api.chat.AiChatService;
import tech.qiantong.qontology.module.ai.api.modelMarket.IAiModelApiService;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEvidence.OmAiEvidenceMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiFile.OmAiFileMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiStage.OmAiStageMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTask.OmAiTaskMapper;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
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
    private final ThreadLocal<Long> currentCreatorId = new ThreadLocal<>();
    private final ThreadLocal<String> currentCreateBy = new ThreadLocal<>();
    private final ThreadLocal<Long> currentStageId = new ThreadLocal<>();
    private final ThreadLocal<OmAiStageDO> currentStage = new ThreadLocal<>();

    private static final List<ExtractionRound> EXTRACTION_ROUNDS = List.of(
            new ExtractionRound("ROUND_1_FOUNDATION", "第1轮：目标、术语和枚举", "round-1-foundation.md", true),
            new ExtractionRound("ROUND_2_OBJECT", "第2轮：对象类型骨架", "round-2-object.md", true),
            new ExtractionRound("ROUND_3_MEMBERS", "第3轮：属性和关系", "round-3-members.md", true),
            new ExtractionRound("ROUND_4_RULES", "第4轮：模型公理和数据规则", "round-4-rules.md", false));

    private static final List<StageDefinition> STAGES = List.of(
            new StageDefinition("FILE_PARSE", "文档结构化解析", "读取上传文件，识别文档结构、段落、表格、图片及原文位置。"),
            new StageDefinition("EVIDENCE_INDEX", "证据索引构建", "将解析内容切分为可追溯证据块，并补充相邻上下文索引。"),
            new StageDefinition("ROUND_1_FOUNDATION", "第1轮：目标、术语和枚举", "结合证据上下文识别建模目标、术语与枚举候选数据。"),
            new StageDefinition("ROUND_2_OBJECT", "第2轮：对象类型骨架", "依据建模目标和基础候选数据识别对象类型层级与关联。"),
            new StageDefinition("ROUND_3_MEMBERS", "第3轮：属性和关系", "基于对象类型骨架识别属性、对象关系和建模目标关联。"),
            new StageDefinition("ROUND_4_RULES", "第4轮：模型公理和数据规则", "基于对象、属性与关系识别模型公理和数据规则。"),
            new StageDefinition("FINISH", "结果汇总", "校验候选数据及其依赖关联，统计并汇总本次解析结果。"));

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
    private JdbcTemplate jdbcTemplate;

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
            return;
        }
        try {
            startTask(task);
            initializeStages(task);
            runStage(task, "FILE_PARSE", () -> parseSourceFiles(taskId));
            runStage(task, "EVIDENCE_INDEX", this::summarizeEvidenceIndex);
            for (ExtractionRound round : EXTRACTION_ROUNDS) {
                runStage(task, round.stageCode(), () -> executeRound(task, round));
            }
            runStage(task, "FINISH", () -> finishTask(task));
            // 结果汇总阶段先完成推送，再发送任务完成事件并结束页面流。
            streamPublisher.publishTask(task);
        } catch (Exception exception) {
            failTask(task, exception);
        }
    }

    /** 将任务更新为解析中状态。 */
    private void startTask(OmAiTaskDO task) {
        task.setStatus(OmAiTaskStatusEnum.RUNNING.getCode());
        task.setCurrentStage("FILE_PARSE");
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
        for (StageDefinition definition : STAGES) {
            stageMapper.insert(OmAiStageDO.builder()
                    .taskId(task.getId())
                    .stageCode(definition.code())
                    .stageName(definition.name())
                    .status(OmAiStageStatusEnum.WAITING.getCode())
                    .progress(0)
                    .summary(definition.thinking())
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
    private String executeRound(OmAiTaskDO task, ExtractionRound round) {
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
        return round.name() + "完成：文本窗口调用 " + textCalls + " 次，多模态调用 " + visionCalls + " 次";
    }

    /** 按窗口执行文本抽取。 */
    private int executeTextWindows(OmAiTaskDO task, ExtractionRound round, List<OmAiEvidenceDO> evidenceList,
                                   int totalCalls, int[] completedCalls) {
        int calls = 0;
        for (int start = 0; start < evidenceList.size(); start += WINDOW_SIZE) {
            int fromIndex = Math.max(0, start - WINDOW_OVERLAP);
            int toIndex = Math.min(evidenceList.size(), start + WINDOW_SIZE + WINDOW_OVERLAP);
            String prompt = buildRoundPrompt(
                    task.getId(), round.promptResource(), evidenceList.subList(fromIndex, toIndex));
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "正在调用文本模型分析证据窗口 " + (calls + 1) + "，范围 " + (fromIndex + 1) + "-" + toIndex
                            + "，重点识别：" + stageThinking(round.stageCode()),
                    TEXT_MODEL_NAME, toIndex - fromIndex, completedCalls[0]);
            AiChatRequest request = thinkingStreamingRequest(
                    modelApi.getModelRef(MODEL_KEY_ID, TEXT_MODEL_NAME),
                    List.of(AiChatMessage.user(prompt)), 8000);
            String responseContent = streamModelResponse(
                    request, TEXT_MODEL_NAME, callProgress(completedCalls[0], totalCalls),
                    "文本证据窗口 " + (calls + 1));
            handleRoundResponse(task, round, responseContent);
            calls++;
            completedCalls[0]++;
            String analysisSummary = extractAnalysisSummary(responseContent);
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "文本模型已完成第 " + calls + " 个证据窗口分析。模型分析摘要：" + analysisSummary,
                    TEXT_MODEL_NAME, toIndex - fromIndex, completedCalls[0]);
        }
        return calls;
    }

    /** 对图片文件执行同一轮多模态抽取。 */
    private int executeImageFiles(OmAiTaskDO task, ExtractionRound round, int totalCalls, int[] completedCalls) {
        int calls = 0;
        List<OmAiFileDO> files = fileMapper.selectList(
                new QueryWrapper<OmAiFileDO>().eq("task_id", task.getId()));
        for (OmAiFileDO file : files) {
            if (!Set.of("png", "jpg", "jpeg").contains(file.getFileExt())) {
                continue;
            }
            String prompt = promptLoader.load(round.promptResource())
                    + "\n\n前序候选索引：" + loadEntityIndex(task.getId())
                    + "\n\n当前图片文件：" + file.getFileName();
            AiChatMessage message = AiChatMessage.user(
                    prompt,
                    List.of(new AiChatMedia(mime(file.getFileExt()), download(file.getFileUrl()), file.getFileName())));
            updateStageProgress(callProgress(completedCalls[0], totalCalls),
                    "正在调用多模态模型分析图片：" + file.getFileName() + "，重点识别：" + stageThinking(round.stageCode()),
                    VISION_MODEL_NAME, 1, completedCalls[0]);
            AiChatRequest request = thinkingStreamingRequest(
                    modelApi.getModelRef(MODEL_KEY_ID, VISION_MODEL_NAME),
                    List.of(message), 8000);
            String responseContent = streamModelResponse(
                    request, VISION_MODEL_NAME, callProgress(completedCalls[0], totalCalls),
                    "图片 " + file.getFileName());
            handleRoundResponse(task, round, responseContent);
            calls++;
            completedCalls[0]++;
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
    private void handleRoundResponse(OmAiTaskDO task, ExtractionRound round, String content) {
        if (round.persistCandidates()) {
            persistCandidates(task, content);
        }
    }

    /** 完成任务并统计候选数量。 */
    private String finishTask(OmAiTaskDO task) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT (SELECT COUNT(*) FROM om_ai_target WHERE task_id=? AND del_flag=0)+"
                        + "(SELECT COUNT(*) FROM om_ai_term WHERE task_id=? AND del_flag=0)+"
                        + "(SELECT COUNT(*) FROM om_ai_enum_type WHERE task_id=? AND del_flag=0)+"
                        + "(SELECT COUNT(*) FROM om_ai_enum_data WHERE task_id=? AND del_flag=0)+"
                        + "(SELECT COUNT(*) FROM om_ai_object WHERE task_id=? AND del_flag=0)+"
                        + "(SELECT COUNT(*) FROM om_ai_attribute WHERE task_id=? AND del_flag=0)+"
                        + "(SELECT COUNT(*) FROM om_ai_relation WHERE task_id=? AND del_flag=0)",
                Integer.class, task.getId(), task.getId(), task.getId(), task.getId(),
                task.getId(), task.getId(), task.getId());
        if (count == null || count == 0) {
            throw new IllegalStateException("四轮抽取未生成任何候选数据，请检查模型配置、提示词或模型返回格式");
        }
        task.setCandidateCount(count);
        task.setStatus(OmAiTaskStatusEnum.SUCCESS.getCode());
        task.setCurrentStage("FINISH");
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
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT 'target' type,id,candidate_key candidateKey,question name FROM om_ai_target WHERE task_id=? AND del_flag=0 "
                        + "UNION ALL SELECT 'term',id,NULL,std_name FROM om_ai_term WHERE task_id=? AND del_flag=0 "
                        + "UNION ALL SELECT 'enum_type',id,NULL,enum_name FROM om_ai_enum_type WHERE task_id=? AND del_flag=0 "
                        + "UNION ALL SELECT 'object',id,NULL,name FROM om_ai_object WHERE task_id=? AND del_flag=0 "
                        + "UNION ALL SELECT 'attribute',id,NULL,name FROM om_ai_attribute WHERE task_id=? AND del_flag=0 "
                        + "UNION ALL SELECT 'relation',id,NULL,name FROM om_ai_relation WHERE task_id=? AND del_flag=0 LIMIT 500",
                taskId, taskId, taskId, taskId, taskId, taskId);
        return JSON.toJSONString(rows);
    }

    /** 解析并保存候选数据。 */
    private void persistCandidates(OmAiTaskDO task, String rawContent) {
        Long taskId = task.getId();
        currentCreatorId.set(task.getCreatorId());
        currentCreateBy.set(task.getCreateBy());
        try {
            JSONObject root = parseJsonObject(rawContent);
            persistTargets(taskId, root.getJSONArray("targets"));
            persistArray(taskId, "om_ai_term", root.getJSONArray("terms"), Map.of(
                    "stdName", "std_name", "enName", "en_name", "alias", "alias",
                    "abbr", "abbr", "source", "source", "description", "description"));
            persistEnums(taskId, root.getJSONArray("enumTypes"));
            persistArray(taskId, "om_ai_object", root.getJSONArray("objects"),
                    Map.of("name", "name", "description", "description"));
            resolveObjectDependencies(taskId, root.getJSONArray("objects"));
            persistAttributes(taskId, root.getJSONArray("attributes"));
            persistRelations(taskId, root.getJSONArray("relations"));
            refreshTargetRelatedElementCounts(taskId);
        } finally {
            currentCreatorId.remove();
            currentCreateBy.remove();
        }
    }

    /** 保存建模目标，抽取缺省值统一按低优先级、待验证处理。 */
    private void persistTargets(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("task_id", taskId);
            row.put("candidate_key", object.getString("candidateKey"));
            row.put("question", object.getString("question"));
            row.put("question_source", object.getString("questionSource"));
            row.put("business_scenarios", object.getString("businessScenarios"));
            Integer priority = object.getInteger("priority");
            row.put("priority", priority == null || priority < 1 || priority > 3 ? 1 : priority);
            row.put("accept_criterion", object.getString("acceptCriterion"));
            row.put("accept_status", "0");
            row.put("related_element_count", 0);
            row.put("adopt_status", OmAiAdoptStatusEnum.PENDING.getCode());
            row.put("confidence", object.getBigDecimal("confidence"));
            row.put("del_flag", false);
            Long id = insertRow("om_ai_target", row);
            bindEvidence(taskId, "target", id, object.getJSONArray("evidenceBlockNos"));
        }
    }

    /** 汇总对象类型和关系对建模目标的关联数量。 */
    private void refreshTargetRelatedElementCounts(Long taskId) {
        jdbcTemplate.update("UPDATE om_ai_target t SET related_element_count = "
                        + "(SELECT COUNT(*) FROM om_ai_object_target ot WHERE ot.target_candidate_id=t.id) + "
                        + "(SELECT COUNT(*) FROM om_ai_relation_target rt WHERE rt.target_candidate_id=t.id) "
                        + "WHERE t.task_id=?", taskId);
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
            return summary == null || summary.isBlank()
                    ? "模型已完成本批证据分析并返回结构化候选结果。"
                    : trim(summary, 1200);
        } catch (Exception ignored) {
            return "模型已完成本批证据分析并返回结构化候选结果。";
        }
    }

    /** 流式调用思考模型，将 reasoning content 与最终结构化正文分开处理。 */
    private String streamModelResponse(AiChatRequest request, String modelName, int progress, String batchName) {
        StringBuilder content = new StringBuilder();
        StringBuilder explicitReasoning = new StringBuilder();
        Long streamingStageId = currentStageId.get();
        OmAiStageDO streamingStage = currentStage.get();
        if (streamingStageId == null || streamingStage == null) {
            throw new IllegalStateException("模型流式调用缺少当前阶段上下文");
        }
        String previousReasoning = Optional.of(streamingStage)
                .map(OmAiStageDO::getReasoningContent).orElse("");
        long[] lastUpdateTime = {0L};
        chatService.stream(request, chunk -> {
            if (chunk.content() != null && !chunk.content().isEmpty()) {
                content.append(chunk.content());
            }
            if (chunk.reasoningContent() != null && !chunk.reasoningContent().isEmpty()) {
                explicitReasoning.append(chunk.reasoningContent());
            }
            long now = System.currentTimeMillis();
            if (chunk.finished() || now - lastUpdateTime[0] >= 250L) {
                String fallbackReasoning = extractThinkReasoning(content.toString());
                String currentReasoning = explicitReasoning.isEmpty()
                        ? fallbackReasoning : explicitReasoning.toString();
                updateStageProgress(streamingStageId, streamingStage, progress,
                        currentReasoning.isBlank()
                                ? "正在分析" + batchName + "，等待模型返回思考过程..."
                                : "正在分析" + batchName + "，模型思考过程正在流式输出。",
                        modelName, null, content.length());
                updateStageReasoning(streamingStageId, streamingStage,
                        joinReasoning(previousReasoning, batchName, currentReasoning));
                lastUpdateTime[0] = now;
            }
        });
        return removeThinkContent(content.toString());
    }

    /** 兼容部分模型把思考过程放在正文中的 think 标签。 */
    private String extractThinkReasoning(String rawContent) {
        String value = Optional.ofNullable(rawContent).orElse("");
        StringBuilder reasoning = new StringBuilder();
        int cursor = 0;
        while (cursor < value.length()) {
            int start = value.indexOf("<think>", cursor);
            if (start < 0) {
                break;
            }
            int contentStart = start + "<think>".length();
            int end = value.indexOf("</think>", contentStart);
            if (reasoning.length() > 0) {
                reasoning.append('\n');
            }
            reasoning.append(value, contentStart, end < 0 ? value.length() : end);
            if (end < 0) {
                break;
            }
            cursor = end + "</think>".length();
        }
        return reasoning.toString().trim();
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

    /** 构造开启供应商思考模式的流式请求。 */
    private AiChatRequest thinkingStreamingRequest(tech.qiantong.qontology.ai.api.model.AiModelRef model,
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
            Map<String, Object> typeRow = new LinkedHashMap<>();
            typeRow.put("task_id", taskId);
            String enumName = object.getString("enumName");
            typeRow.put("enum_name", enumName);
            typeRow.put("enum_type", resolveEnumType(taskId, object.getString("enumType")));
            typeRow.put("status", "0");
            typeRow.put("adopt_status", OmAiAdoptStatusEnum.PENDING.getCode());
            typeRow.put("confidence", object.getBigDecimal("confidence"));
            typeRow.put("del_flag", false);
            Long typeId = insertRow("om_ai_enum_type", typeRow);
            bindEvidence(taskId, "enum_type", typeId, object.getJSONArray("evidenceBlockNos"));
            JSONArray values = object.getJSONArray("values");
            if (values == null) {
                continue;
            }
            for (Object item : values) {
                JSONObject enumValue = (JSONObject) item;
                Map<String, Object> valueRow = new LinkedHashMap<>();
                valueRow.put("task_id", taskId);
                valueRow.put("enum_type_id", typeId);
                valueRow.put("enum_sort", Optional.ofNullable(enumValue.getLong("enumSort")).orElse(0L));
                valueRow.put("enum_label", enumValue.getString("enumLabel"));
                valueRow.put("enum_value", enumValue.getString("enumValue"));
                valueRow.put("status", "0");
                valueRow.put("adopt_status", OmAiAdoptStatusEnum.PENDING.getCode());
                valueRow.put("del_flag", false);
                Long valueId = insertRow("om_ai_enum_data", valueRow);
                bindEvidence(taskId, "enum_data", valueId, object.getJSONArray("evidenceBlockNos"));
            }
        }
    }

    /**
     * 枚举类型缺失时生成符合系统规则的任务内唯一编码。
     * 正常情况下由模型输出语义化英文编码，本方法仅作为可靠性兜底。
     */
    private String resolveEnumType(Long taskId, String extractedType) {
        if (extractedType != null && !extractedType.isBlank()) {
            String normalized = extractedType.trim().toLowerCase();
            if (normalized.matches("[a-z][a-z0-9_]*") && !enumTypeExists(taskId, normalized)) {
                return normalized;
            }
        }
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM om_ai_enum_type WHERE task_id=? AND del_flag=0", Integer.class, taskId);
        int sequence = Optional.ofNullable(count).orElse(0) + 1;
        String candidate;
        do {
            candidate = "enum_type_" + sequence++;
        } while (enumTypeExists(taskId, candidate));
        return candidate;
    }

    /** 判断枚举类型编码是否已存在于当前任务候选或任务所属正式版本。 */
    private boolean enumTypeExists(Long taskId, String enumType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT ("
                        + "(SELECT COUNT(*) FROM om_ai_enum_type WHERE task_id=? AND enum_type=? AND del_flag=0) + "
                        + "(SELECT COUNT(*) FROM om_enum_type e INNER JOIN om_ai_task t "
                        + "ON e.workspace_id=t.workspace_id AND e.version_id=t.version_id "
                        + "WHERE t.id=? AND e.enum_type=? AND e.del_flag=0)"
                        + ")",
                Integer.class, taskId, enumType, taskId, enumType);
        return count != null && count > 0;
    }

    /** 解析对象父子关系及目标关联。 */
    private void resolveObjectDependencies(Long taskId, JSONArray objects) {
        if (objects == null) {
            return;
        }
        for (Object value : objects) {
            JSONObject object = (JSONObject) value;
            Long objectId = findCandidateId("om_ai_object", "name", taskId, object.getString("name"));
            Long parentId = findCandidateId("om_ai_object", "name", taskId, object.getString("parentName"));
            if (parentId != null) {
                jdbcTemplate.update("UPDATE om_ai_object SET parent_candidate_id=? WHERE id=?", parentId, objectId);
            }
            for (Long targetId : resolveTargetIds(taskId, object)) {
                jdbcTemplate.update(
                        "INSERT IGNORE INTO om_ai_object_target(task_id,object_candidate_id,target_candidate_id) VALUES (?,?,?)",
                        taskId, objectId, targetId);
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
            Long objectId = findCandidateId("om_ai_object", "name", taskId, object.getString("objectName"));
            if (objectId == null) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("task_id", taskId);
            row.put("object_candidate_id", objectId);
            row.put("name", object.getString("name"));
            row.put("code", object.getString("code"));
            row.put("description", object.getString("description"));
            row.put("type_category", object.getInteger("suggestedTypeCategory"));
            row.put("basic_type_code", object.getInteger("suggestedBasicTypeCode"));
            row.put("business_type_id", object.getLong("businessTypeId"));
            row.put("business_type_source", object.getString("businessTypeSource"));
            row.put("unit", object.getString("unit"));
            row.put("enum_type", object.getString("enumType"));
            row.put("adopt_status", OmAiAdoptStatusEnum.PENDING.getCode());
            row.put("confidence", object.getBigDecimal("confidence"));
            row.put("del_flag", false);
            Long id = insertRow("om_ai_attribute", row);
            bindEvidence(taskId, "attribute", id, object.getJSONArray("evidenceBlockNos"));
        }
    }

    /** 保存对象关系。 */
    private void persistRelations(Long taskId, JSONArray array) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            Long startId = findCandidateId("om_ai_object", "name", taskId, object.getString("startObjectName"));
            Long endId = findCandidateId("om_ai_object", "name", taskId, object.getString("endObjectName"));
            if (startId == null || endId == null) {
                continue;
            }
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("task_id", taskId);
            row.put("name", object.getString("name"));
            row.put("description", object.getString("description"));
            row.put("start_object_candidate_id", startId);
            row.put("end_object_candidate_id", endId);
            row.put("reversible_flag", Optional.ofNullable(object.getInteger("reversibleFlag")).orElse(0));
            row.put("reverse_name", object.getString("reverseName"));
            row.put("adopt_status", OmAiAdoptStatusEnum.PENDING.getCode());
            row.put("confidence", object.getBigDecimal("confidence"));
            row.put("del_flag", false);
            Long relationId = insertRow("om_ai_relation", row);
            bindEvidence(taskId, "relation", relationId, object.getJSONArray("evidenceBlockNos"));
            for (Long targetId : resolveTargetIds(taskId, object)) {
                jdbcTemplate.update(
                        "INSERT IGNORE INTO om_ai_relation_target(task_id,relation_candidate_id,target_candidate_id) VALUES (?,?,?)",
                        taskId, relationId, targetId);
            }
        }
    }

    /**
     * 解析候选数据引用的建模目标。稳定候选键优先，规范化后的目标问题作为兼容兜底。
     */
    private Set<Long> resolveTargetIds(Long taskId, JSONObject candidate) {
        List<Map<String, Object>> targets = jdbcTemplate.queryForList(
                "SELECT id,candidate_key,question FROM om_ai_target WHERE task_id=? AND del_flag=0", taskId);
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

    /** 保存普通候选数组。 */
    private void persistArray(Long taskId, String table, JSONArray array, Map<String, String> mapping) {
        if (array == null) {
            return;
        }
        for (Object value : array) {
            JSONObject object = (JSONObject) value;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("task_id", taskId);
            for (Map.Entry<String, String> entry : mapping.entrySet()) {
                Object field = object.get(entry.getKey());
                if (field != null) {
                    row.put(entry.getValue(), normalizeCandidateField(field));
                }
            }
            row.put("adopt_status", OmAiAdoptStatusEnum.PENDING.getCode());
            row.put("confidence", object.getBigDecimal("confidence"));
            row.put("del_flag", false);
            Long dataId = insertRow(table, row);
            bindEvidence(taskId, table.substring(6), dataId, object.getJSONArray("evidenceBlockNos"));
        }
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

    /** 插入动态候选数据并返回主键。 */
    private Long insertRow(String table, Map<String, Object> row) {
        row.putIfAbsent("creator_id", currentCreatorId.get());
        row.putIfAbsent("create_by", currentCreateBy.get());
        List<String> columns = new ArrayList<>(row.keySet());
        List<Object> arguments = row.values().stream().map(this::normalizeDatabaseValue).toList();
        String sql = "INSERT INTO " + table + " (" + String.join(",", columns) + ") VALUES ("
                + String.join(",", Collections.nCopies(columns.size(), "?")) + ")";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int index = 0; index < arguments.size(); index++) {
                statement.setObject(index + 1, arguments.get(index));
            }
            return statement;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    /** 将模型返回的数组和对象转换为数据库可保存的 JSON 文本，避免 JDBC 写入 Java 序列化字节。 */
    private Object normalizeDatabaseValue(Object value) {
        if (value instanceof JSONArray || value instanceof JSONObject
                || value instanceof Collection<?> || value instanceof Map<?, ?>) {
            return JSON.toJSONString(value);
        }
        return value;
    }

    /** 获取阶段执行时展示的思考计划。 */
    private String stageThinking(String stageCode) {
        return STAGES.stream().filter(stage -> stage.code().equals(stageCode))
                .map(StageDefinition::thinking).findFirst().orElse("按当前阶段目标处理文档和候选数据。");
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
    private void bindEvidence(Long taskId, String type, Long dataId, JSONArray evidenceIds) {
        if (evidenceIds == null) {
            return;
        }
        for (Object evidenceId : evidenceIds) {
            jdbcTemplate.update(
                    "INSERT IGNORE INTO om_ai_data_evidence(task_id,data_type,data_id,evidence_id,evidence_role) VALUES (?,?,?,?,?)",
                    taskId, type, dataId, Long.valueOf(String.valueOf(evidenceId)),
                    OmAiEvidenceRoleEnum.POSITIVE.getCode());
        }
    }

    /** 根据任务和名称查找最近候选数据。 */
    private Long findCandidateId(String table, String column, Long taskId, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String sql = "SELECT id FROM " + table + " WHERE task_id=? AND " + column
                + "=? AND del_flag=0 ORDER BY id DESC LIMIT 1";
        List<Long> values = jdbcTemplate.query(sql, (resultSet, rowNum) -> resultSet.getLong(1), taskId, value);
        return values.isEmpty() ? null : values.get(0);
    }

    /** 截断文本。 */
    private static String trim(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

    /** 抽取轮次定义。 */
    private record ExtractionRound(String stageCode, String name, String promptResource, boolean persistCandidates) {
    }

    /** 时间线阶段定义。 */
    private record StageDefinition(String code, String name, String thinking) {
    }

    /** 阶段执行动作。 */
    @FunctionalInterface
    private interface StageAction {
        String run();
    }
}
