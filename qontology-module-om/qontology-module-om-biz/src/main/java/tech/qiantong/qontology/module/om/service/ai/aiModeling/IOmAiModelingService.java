package tech.qiantong.qontology.module.om.service.ai.aiModeling;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.OmAiTaskCreateReqVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.OmAiTaskPageReqVO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiStage.OmAiStageDO;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO;

/**
 * AI 辅助建模业务服务。
 */
public interface IOmAiModelingService {

    /** 获得 AI 建模任务分页列表。 */
    PageResult<OmAiTaskDO> page(OmAiTaskPageReqVO request);

    /** 获得 AI 建模任务详情。 */
    OmAiTaskDO get(Long id);

    /** 上传文件并创建 AI 建模任务。 */
    Long create(OmAiTaskCreateReqVO request, Long workspaceId, MultipartFile[] files);

    /** 启动已保存任务的异步解析。 */
    void start(Long id);

    /** 删除 AI 建模任务。 */
    int delete(Collection<Long> ids);

    /** 全量重新解析 AI 建模任务。 */
    void reparse(Long id);

    /** 获得任务上传文件。 */
    List<OmAiFileDO> files(Long taskId);

    /** 获得任务解析时间线。 */
    List<OmAiStageDO> stages(Long taskId);

    /** 获得候选数据来源证据。 */
    List<Map<String, Object>> evidence(String type, Long id);

    List<Map<String, Object>> candidateTargets(String type, Long id);

    void saveCandidateTargets(String type, Long id, List<Long> targetIds);

    List<Map<String, Object>> attributeBusinessTypes(Long taskId, String source);

    List<Map<String, Object>> attributeEnumOptions(Long taskId, String source);

    /** 获得一键采纳前置条件。 */
    Map<String, Object> adoptionRequirements(Long taskId);

    /** 采纳单条候选数据。 */
    int adopt(String type, Long id);

    /** 按依赖顺序采纳全部候选数据。 */
    Map<String, Integer> adoptAll(Long taskId);
}
