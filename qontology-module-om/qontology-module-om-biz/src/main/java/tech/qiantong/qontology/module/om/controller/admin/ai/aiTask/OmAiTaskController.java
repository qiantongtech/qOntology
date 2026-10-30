/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 */
package tech.qiantong.qontology.module.om.controller.admin.ai.aiTask;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiTask.vo.*;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiFile.vo.OmAiFileRespVO;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiStage.vo.OmAiStageRespVO;
import tech.qiantong.qontology.module.om.service.ai.aiTask.IOmAiTaskService;
import tech.qiantong.qontology.module.om.service.ai.aiModeling.IOmAiModelingService;
import tech.qiantong.qontology.module.om.service.ai.aiModeling.OmAiModelingStreamPublisher;

/** AI辅助建模任务 Controller。 */
@Tag(name = "AI辅助建模任务")
@RestController
@RequestMapping("/om/aiTask")
@Validated
public class OmAiTaskController extends BaseController {

    @Resource
    private IOmAiTaskService aiTaskService;
    @Resource
    private IOmAiModelingService modelingService;
    @Resource
    private OmAiModelingStreamPublisher streamPublisher;
    @Resource(name = "threadPoolTaskExecutor")
    private TaskExecutor taskExecutor;

    /** 查询AI辅助建模任务列表。 */
    @Operation(summary = "查询AI辅助建模任务列表")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmAiTaskRespVO>> list(OmAiTaskPageReqVO reqVO) {
        return CommonResult.success(BeanUtils.toBean(modelingService.page(reqVO), OmAiTaskRespVO.class));
    }

    /** 获取AI辅助建模任务详细信息。 */
    @Operation(summary = "获取AI辅助建模任务详细信息")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}")
    public CommonResult<OmAiTaskRespVO> getInfo(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(modelingService.get(id), OmAiTaskRespVO.class));
    }

    /** 上传多个文件并创建AI辅助建模任务。 */
    @Operation(summary = "上传多个文件并创建AI辅助建模任务")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:add')")
    @Log(title = "AI辅助建模任务", businessType = BusinessType.INSERT)
    @PostMapping(consumes = "multipart/form-data")
    public CommonResult<Long> create(@Valid OmAiTaskCreateReqVO reqVO,
                                     @RequestPart("files") MultipartFile[] files) {
        return CommonResult.success(modelingService.create(reqVO, getWorkSpaceId(), files));
    }

    /**
     * 第二步：启动解析并使用 SSE 持续推送任务、阶段、进度和模型流式输出。
     * 客户端断开连接只停止页面推送，不会终止后台解析任务。
     */
    @Operation(summary = "流式启动并查看AI辅助建模解析过程")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @PostMapping(value = "/{id}/analyze-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter analyzeStream(@PathVariable Long id) {
        SseEmitter emitter = new SseEmitter(0L);
        Runnable unsubscribe = streamPublisher.subscribe(id, event -> sendStreamEvent(emitter, event));
        emitter.onCompletion(unsubscribe);
        emitter.onTimeout(() -> {
            unsubscribe.run();
            emitter.complete();
        });
        emitter.onError(error -> unsubscribe.run());
        // 控制器先返回并建立 SSE 响应，再异步发送首帧和启动解析，避免首帧在响应提交前丢失。
        taskExecutor.execute(() -> initializeAnalysisStream(id, emitter, unsubscribe));
        return emitter;
    }

    /** 发送一次初始快照并启动解析；后续事件全部由解析线程主动推送。 */
    private void initializeAnalysisStream(Long id, SseEmitter emitter, Runnable unsubscribe) {
        try {
            Map<String, Object> snapshot = Map.of(
                    "eventType", "snapshot",
                    "task", BeanUtils.toBean(modelingService.get(id), OmAiTaskRespVO.class),
                    "stages", BeanUtils.toBean(modelingService.stages(id), OmAiStageRespVO.class));
            synchronized (emitter) {
                emitter.send(SseEmitter.event().name("snapshot").reconnectTime(3000L).data(snapshot));
            }
            modelingService.start(id);
        } catch (Exception exception) {
            unsubscribe.run();
            emitter.completeWithError(exception);
        }
    }

    /** 将解析线程产生的事件立即写入 SSE 连接。 */
    private void sendStreamEvent(SseEmitter emitter, Map<String, Object> event) {
        try {
            synchronized (emitter) {
                emitter.send(SseEmitter.event().name(String.valueOf(event.get("eventType"))).data(event));
            }
            Object taskValue = event.get("task");
            if (taskValue instanceof tech.qiantong.qontology.module.om.dal.dataobject.ai.aiTask.OmAiTaskDO task
                    && task.getStatus() != null && (task.getStatus() == 2 || task.getStatus() == 3)) {
                emitter.complete();
            }
        } catch (Exception disconnected) {
            emitter.complete();
        }
    }

    /** 修改AI辅助建模任务。 */
    @Operation(summary = "修改AI辅助建模任务")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:edit')")
    @Log(title = "AI辅助建模任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmAiTaskSaveReqVO reqVO) {
        return CommonResult.toAjax(aiTaskService.updateOmAiTask(reqVO));
    }

    /** 删除AI辅助建模任务。 */
    @Operation(summary = "删除AI辅助建模任务")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:remove')")
    @Log(title = "AI辅助建模任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(modelingService.delete(Arrays.asList(ids)));
    }

    /** 全量重新解析AI辅助建模任务。 */
    @Operation(summary = "全量重新解析AI辅助建模任务")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:reparse')")
    @PostMapping("/{id}/reparse")
    public CommonResult<Boolean> reparse(@PathVariable Long id) {
        modelingService.reparse(id);
        return CommonResult.success(true);
    }

    /** 查询任务上传文件。 */
    @Operation(summary = "查询任务上传文件")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}/files")
    public CommonResult<List<OmAiFileRespVO>> files(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(modelingService.files(id), OmAiFileRespVO.class));
    }

    /** 查询任务解析时间线。 */
    @Operation(summary = "查询任务解析时间线")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}/stages")
    public CommonResult<List<OmAiStageRespVO>> stages(@PathVariable Long id) {
        return CommonResult.success(BeanUtils.toBean(modelingService.stages(id), OmAiStageRespVO.class));
    }

    /** 查询候选对象类型或关系绑定的候选建模目标。 */
    @Operation(summary = "查询候选数据关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/candidate-targets")
    public CommonResult<List<Map<String, Object>>> candidateTargets(
            @RequestParam String type, @RequestParam Long id) {
        return CommonResult.success(modelingService.candidateTargets(type, id));
    }

    /** 修改候选对象类型或关系绑定的候选建模目标。 */
    @Operation(summary = "修改候选数据关联的建模目标")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:edit')")
    @PutMapping("/candidate-targets")
    public CommonResult<Boolean> saveCandidateTargets(@RequestBody Map<String, Object> request) {
        String type = String.valueOf(request.get("type"));
        Long id = Long.valueOf(String.valueOf(request.get("id")));
        List<Long> targetIds = ((List<?>) request.getOrDefault("targetIds", List.of())).stream()
                .map(value -> Long.valueOf(String.valueOf(value))).toList();
        modelingService.saveCandidateTargets(type, id, targetIds);
        return CommonResult.success(true);
    }

    /** 查询 AI 候选属性可用的业务数据类型。 */
    @Operation(summary = "查询AI候选属性可用业务数据类型")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}/attribute-business-types")
    public CommonResult<List<Map<String, Object>>> attributeBusinessTypes(
            @PathVariable Long id, @RequestParam String source) {
        return CommonResult.success(modelingService.attributeBusinessTypes(id, source));
    }

    /** 查询 AI 候选属性可用的枚举类型。 */
    @Operation(summary = "查询AI候选属性可用枚举类型")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:list')")
    @GetMapping("/{id}/attribute-enum-options")
    public CommonResult<List<Map<String, Object>>> attributeEnumOptions(
            @PathVariable Long id, @RequestParam String source) {
        return CommonResult.success(modelingService.attributeEnumOptions(id, source));
    }

    /** 查询一键采纳前置条件。 */
    @Operation(summary = "查询一键采纳前置条件")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:adopt')")
    @GetMapping("/{id}/adoption-requirements")
    public CommonResult<Map<String, Object>> adoptionRequirements(@PathVariable Long id) {
        return CommonResult.success(modelingService.adoptionRequirements(id));
    }

    /** 按依赖顺序一键采纳全部候选数据。 */
    @Operation(summary = "按依赖顺序一键采纳全部候选数据")
    @PreAuthorize("@ss.hasPermi('om:aiModeling:adopt')")
    @PostMapping("/{id}/adopt-all")
    public CommonResult<Map<String, Integer>> adoptAll(@PathVariable Long id) {
        return CommonResult.success(modelingService.adoptAll(id));
    }

}
