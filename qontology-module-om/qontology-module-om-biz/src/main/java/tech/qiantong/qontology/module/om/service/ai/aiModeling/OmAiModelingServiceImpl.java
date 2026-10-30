package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.file.util.FileUploadUtil;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.OmAiTaskCreateReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.OmAiTaskPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumTypeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeAttributeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.object.vo.OmObjectTypeSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.omtarget.vo.OmTargetSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.relation.vo.OmObjectTypeRelationSaveReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermSaveReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiEvidence.OmAiEvidenceDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiEvidence.OmAiEvidenceMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiFile.OmAiFileMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiStage.OmAiStageMapper;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiTask.OmAiTaskMapper;
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
    private JdbcTemplate jdbcTemplate;
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

    /** 查询指定版本的 AI 建模任务分页列表。 */
    @Override
    public PageResult<OmAiTaskDO> page(OmAiTaskPageReqVO request) {
        return taskMapper.selectPage(request, new QueryWrapper<OmAiTaskDO>()
                .eq("version_id", request.getVersionId())
                .like(request.getTaskName() != null && !request.getTaskName().isBlank(),
                        "task_name", request.getTaskName())
                .eq(request.getStatus() != null, "status", request.getStatus())
                .orderByDesc("id"));
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
                .taskCode("AI_MODEL_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()))
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
        task.setStatus(OmAiTaskStatusEnum.RUNNING.getCode());
        task.setCurrentStage("FILE_PARSE");
        task.setStartedTime(new Date());
        task.setFailureReason(null);
        taskMapper.updateById(task);
        taskExecutor.execute(() -> asyncProcessor.process(id));
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
    public void reparse(Long id) {
        OmAiTaskDO task = getRequiredTask(id);
        if (OmAiTaskStatusEnum.RUNNING.getCode().equals(task.getStatus())) {
            throw new ServiceException("任务正在解析");
        }
        long fileCount = fileMapper.selectCount(new QueryWrapper<OmAiFileDO>().eq("task_id", id));
        if (fileCount == 0) {
            throw new ServiceException("任务没有原始文件，无法重新解析，请删除任务后重新上传文件");
        }
        clearTaskResults(id);
        task.setStatus(OmAiTaskStatusEnum.WAITING.getCode());
        task.setCandidateCount(0);
        task.setFinishedTime(null);
        task.setFailureReason(null);
        taskMapper.updateById(task);
        asyncProcessor.initializeStages(task);
        taskExecutor.execute(() -> asyncProcessor.process(id));
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
        for (OmAiCandidateTypeEnum candidateType : OmAiCandidateTypeEnum.values()) {
            jdbcTemplate.update("DELETE FROM " + candidateType.getTableName() + " WHERE task_id = ?", taskId);
        }
        jdbcTemplate.update("DELETE FROM om_ai_object_target WHERE task_id = ?", taskId);
        jdbcTemplate.update("DELETE FROM om_ai_relation_target WHERE task_id = ?", taskId);
        jdbcTemplate.update("DELETE FROM om_ai_data_evidence WHERE task_id = ?", taskId);
        jdbcTemplate.update("DELETE FROM om_ai_adopt_log WHERE task_id = ?", taskId);
        evidenceMapper.delete(new QueryWrapper<OmAiEvidenceDO>().eq("task_id", taskId));
        jdbcTemplate.update("UPDATE om_ai_file SET parse_status = ?, page_count = NULL, block_count = 0 WHERE task_id = ?",
                OmAiFileParseStatusEnum.WAITING.getCode(), taskId);
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
        String table = "object".equals(type) ? "om_ai_object_target"
                : "relation".equals(type) ? "om_ai_relation_target" : null;
        String column = "object".equals(type) ? "object_candidate_id" : "relation_candidate_id";
        if (table == null) throw new ServiceException("仅对象类型和对象关系支持关联建模目标");
        return jdbcTemplate.queryForList("SELECT t.id,t.question,t.question_source AS questionSource,"
                        + "t.business_scenarios AS businessScenarios,t.priority,t.related_element_count AS relatedElementCount "
                        + "FROM " + table + " l JOIN om_ai_target t ON t.id=l.target_candidate_id "
                        + "WHERE l." + column + "=? AND t.del_flag=0 ORDER BY t.id", id);
    }

    @Override
    public void saveCandidateTargets(String type, Long id, List<Long> targetIds) {
        String table = "object".equals(type) ? "om_ai_object_target"
                : "relation".equals(type) ? "om_ai_relation_target" : null;
        String column = "object".equals(type) ? "object_candidate_id" : "relation_candidate_id";
        if (table == null) throw new ServiceException("仅对象类型和对象关系支持关联建模目标");
        if (targetIds == null || targetIds.isEmpty()) throw new ServiceException("请至少关联一个建模目标");
        Map<String, Object> candidate = getCandidate("object".equals(type)
                ? OmAiCandidateTypeEnum.OBJECT : OmAiCandidateTypeEnum.RELATION, id);
        Long taskId = longValue(candidate, "task_id");
        jdbcTemplate.update("DELETE FROM " + table + " WHERE " + column + "=?", id);
        for (Long targetId : targetIds.stream().distinct().toList()) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM om_ai_target WHERE id=? AND task_id=? AND del_flag=0",
                    Integer.class, targetId, taskId);
            if (count == null || count == 0) throw new ServiceException("关联建模目标不存在或不属于当前任务");
            jdbcTemplate.update("INSERT INTO " + table + "(task_id," + column + ",target_candidate_id) VALUES (?,?,?)",
                    taskId, id, targetId);
        }
        jdbcTemplate.update("UPDATE om_ai_target t SET related_element_count="
                + "(SELECT COUNT(*) FROM om_ai_object_target ot WHERE ot.target_candidate_id=t.id)+"
                + "(SELECT COUNT(*) FROM om_ai_relation_target rt WHERE rt.target_candidate_id=t.id) WHERE t.task_id=?", taskId);
    }

    @Override
    public List<Map<String, Object>> attributeBusinessTypes(Long taskId, String source) {
        OmAiTaskDO task = getRequiredTask(taskId);
        if ("PROJECT".equals(source)) {
            return jdbcTemplate.queryForList("SELECT id,name,data_type AS dataType,description,unit,"
                    + "decimal_places AS decimalPlaces,format,enum_type AS enumType,enum_source AS enumSource,"
                    + "'PROJECT' AS businessTypeSource FROM om_data_type WHERE version_id=? AND valid_flag=1 AND del_flag=0 ORDER BY id DESC",
                    task.getVersionId());
        }
        if (!"GLOBAL".equals(source)) throw new ServiceException("类型来源只能为当前项目或全局");
        return jdbcTemplate.queryForList("SELECT id,name,data_type AS dataType,description,unit,"
                + "decimal_places AS decimalPlaces,format,enum_type AS enumType,'GLOBAL' AS enumSource,"
                + "'GLOBAL' AS businessTypeSource FROM basic_data_type WHERE valid_flag=1 AND del_flag=0");
    }

    @Override
    public List<Map<String, Object>> attributeEnumOptions(Long taskId, String source) {
        OmAiTaskDO task = getRequiredTask(taskId);
        if ("PROJECT".equals(source)) {
            return jdbcTemplate.queryForList("SELECT enum_name AS enumName,enum_type AS enumType,'PROJECT' AS enumSource "
                    + "FROM om_enum_type WHERE version_id=? AND status='0' AND del_flag=0", task.getVersionId());
        }
        if (!"GLOBAL".equals(source)) throw new ServiceException("枚举来源只能为当前项目或全局");
        return jdbcTemplate.queryForList("SELECT enum_name AS enumName,enum_type AS enumType,'GLOBAL' AS enumSource "
                + "FROM basic_enum_type WHERE status='0' AND del_flag=0");
    }

    /** 查询候选数据的原始文件来源和精确位置。 */
    @Override
    public List<Map<String, Object>> evidence(String type, Long id) {
        OmAiCandidateTypeEnum.fromCode(type);
        return jdbcTemplate.queryForList(
                "SELECT e.*, f.file_name, f.file_url, de.evidence_role "
                        + "FROM om_ai_data_evidence de JOIN om_ai_evidence e ON e.id = de.evidence_id "
                        + "JOIN om_ai_file f ON f.id = e.file_id "
                        + "WHERE de.data_type = ? AND de.data_id = ? ORDER BY e.file_id, e.block_no",
                type, id);
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
            jdbcTemplate.update("UPDATE " + candidateType.getTableName()
                            + " SET adopt_status = ?, adopted_id = ?, adopt_message = NULL WHERE id = ?",
                    OmAiAdoptStatusEnum.ADOPTED.getCode(), formalId, id);
            insertAdoptLog(candidate, candidateType, formalId, OmAiAdoptStatusEnum.ADOPTED, "采纳成功");
            return 1;
        } catch (Exception exception) {
            String message = limitMessage(exception.getMessage());
            jdbcTemplate.update("UPDATE " + candidateType.getTableName()
                            + " SET adopt_status = ?, adopt_message = ? WHERE id = ?",
                    OmAiAdoptStatusEnum.FAILED.getCode(), message, id);
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
        };
    }

    /** 采纳建模目标。 */
    private Long adoptTarget(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "question", "目标问题");
        requireText(row, "question_source", "问题来源");
        requireText(row, "business_scenarios", "业务场景");
        requireText(row, "accept_criterion", "验收标准");
        requireNotExists("SELECT COUNT(*) FROM om_target WHERE version_id=? AND question=? AND del_flag=0",
                "建模目标已存在，不能重复采纳", task.getVersionId(), stringValue(row, "question"));
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
        requireNotExists("SELECT COUNT(*) FROM om_term WHERE workspace_id=? AND version_id=? AND std_name=? AND del_flag=0",
                "术语名称已存在，不能重复采纳", task.getWorkspaceId(), task.getVersionId(),
                stringValue(row, "std_name"));
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
        requireNotExists("SELECT COUNT(*) FROM om_enum_type WHERE workspace_id=? AND version_id=? "
                        + "AND enum_name=? AND enum_type=? AND del_flag=0",
                "枚举名称和枚举类型已存在，不能重复采纳", task.getWorkspaceId(), task.getVersionId(),
                stringValue(row, "enum_name"), stringValue(row, "enum_type"));
        OmEnumTypeSaveReqVO request = new OmEnumTypeSaveReqVO();
        request.setWorkspaceId(task.getWorkspaceId());
        request.setVersionId(task.getVersionId());
        request.setEnumName(stringValue(row, "enum_name"));
        request.setEnumType(stringValue(row, "enum_type"));
        request.setStatus(Optional.ofNullable(stringValue(row, "status")).orElse("0"));
        request.setRemark(stringValue(row, "remark"));
        Long formalTypeId = enumTypeService.createOmEnumType(request);
        List<Map<String, Object>> children = jdbcTemplate.queryForList(
                "SELECT * FROM om_ai_enum_data WHERE enum_type_id = ? AND del_flag = 0 "
                        + "AND adopt_status <> ? ORDER BY enum_sort, id",
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
            jdbcTemplate.update("UPDATE om_ai_enum_data SET adopt_status = ?, adopted_id = ? WHERE id = ?",
                    OmAiAdoptStatusEnum.ADOPTED.getCode(), formalChildId, longValue(child, "id"));
        }
        return formalTypeId;
    }

    /** 采纳对象类型；父对象和关联建模目标必须已采纳。 */
    private Long adoptObject(OmAiTaskDO task, Map<String, Object> row) {
        requireText(row, "name", "对象类型名称");
        requireNotExists("SELECT COUNT(*) FROM om_object_type WHERE workspace_id=? AND version_id=? AND name=? AND del_flag=0",
                "对象类型名称已存在，不能重复采纳", task.getWorkspaceId(), task.getVersionId(),
                stringValue(row, "name"));
        Long parentCandidateId = nullableLongValue(row, "parent_candidate_id");
        Long parentId = 0L;
        if (parentCandidateId != null) {
            Map<String, Object> parent = requireAdopted(
                    OmAiCandidateTypeEnum.OBJECT, parentCandidateId, "上级对象类型");
            parentId = longValue(parent, "adopted_id");
        }
        List<Long> targetIds = requireLinkedTargetsAdopted(
                "SELECT target_candidate_id FROM om_ai_object_target WHERE object_candidate_id = ?",
                longValue(row, "id"));
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
        requireNotExists("SELECT COUNT(*) FROM om_object_type_attribute WHERE workspace_id=? AND version_id=? "
                        + "AND object_type_id=? AND name=? AND del_flag=0",
                "当前对象类型下属性名称已存在，不能重复采纳", task.getWorkspaceId(), task.getVersionId(),
                objectTypeId, stringValue(row, "name"));
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
        requireNotExists("SELECT COUNT(*) FROM om_object_type_relation WHERE workspace_id=? AND version_id=? "
                        + "AND start_type_id=? AND name=? AND end_type_id=? AND del_flag=0",
                "相同起点类型、关系名称和终点类型的关系已存在，不能重复采纳", task.getWorkspaceId(),
                task.getVersionId(), startTypeId, stringValue(row, "name"), endTypeId);
        List<Long> targetIds = requireLinkedTargetsAdopted(
                "SELECT target_candidate_id FROM om_ai_relation_target WHERE relation_candidate_id = ?",
                longValue(row, "id"));
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

    /** 校验关联表中的建模目标均已采纳，并返回正式目标编号。 */
    private List<Long> requireLinkedTargetsAdopted(String sql, Long candidateId) {
        List<Long> targetIds = new ArrayList<>();
        for (Map<String, Object> link : jdbcTemplate.queryForList(sql, candidateId)) {
            Long targetCandidateId = longValue(link, "target_candidate_id");
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
    private void requireNotExists(String sql, String message, Object... args) {
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, args);
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
                OmAiCandidateTypeEnum.ATTRIBUTE, OmAiCandidateTypeEnum.RELATION);
        for (OmAiCandidateTypeEnum type : order) {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT id FROM " + type.getTableName()
                            + " WHERE task_id = ? AND adopt_status <> ? AND del_flag = 0 ORDER BY id",
                    taskId, OmAiAdoptStatusEnum.ADOPTED.getCode());
            for (Map<String, Object> row : rows) {
                try {
                    adopt(type.getCode(), longValue(row, "id"));
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
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT * FROM " + type.getTableName() + " WHERE id = ? AND del_flag = 0", id);
        if (rows.isEmpty()) {
            throw new ServiceException("候选数据不存在");
        }
        return rows.get(0);
    }

    /** 写入候选数据采纳日志。 */
    private void insertAdoptLog(Map<String, Object> row, OmAiCandidateTypeEnum type, Long formalId,
                                OmAiAdoptStatusEnum status, String message) {
        jdbcTemplate.update("INSERT INTO om_ai_adopt_log"
                        + "(task_id, data_type, data_id, formal_id, status, message, create_time) "
                        + "VALUES (?, ?, ?, ?, ?, ?, NOW())",
                longValue(row, "task_id"), type.getCode(), longValue(row, "id"), formalId,
                status.getCode(), message);
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
