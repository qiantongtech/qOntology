package tech.qiantong.qontology.module.app.controller.admin.appTaskLog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.app.controller.admin.appTaskLog.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDO;
import tech.qiantong.qontology.module.app.dal.dataobject.appTaskLog.AppTaskLogDetailDO;
import tech.qiantong.qontology.module.app.service.appTaskLog.IAppTaskLogService;
import tech.qiantong.qontology.module.app.convert.appTaskLog.AppTaskLogConvert;

import java.util.Arrays;
import java.util.List;

/**
 * 抽取任务执行日志Controller
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Tag(name = "抽取任务执行日志")
@RestController
@RequestMapping("/app/taskLog")
@Validated
public class AppTaskLogController extends BaseController {
    @Resource
    private IAppTaskLogService appTaskLogService;

    /**
     * 查询抽取任务执行日志列表
     *
     * @param pageReqVO 分页请求
     * @return 执行日志分页列表
     */
    @Operation(summary = "查询抽取任务执行日志列表")
    @PreAuthorize("@ss.hasAnyPermi('app:appTaskLog:tasklog:list,app:appUnstructTask:unstructtask:taskLog')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppTaskLogRespVO>> list(AppTaskLogPageReqVO pageReqVO) {
        PageResult<AppTaskLogDO> page = appTaskLogService.getAppTaskLogPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(page, AppTaskLogRespVO.class));
    }

    /** 导出抽取任务执行日志列表。 */
    @Operation(summary = "导出抽取任务执行日志列表")
    @PreAuthorize("@ss.hasPermi('app:appTaskLog:tasklog:export')")
    @Log(title = "抽取任务执行日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @SuppressWarnings("unchecked")
    public void export(HttpServletResponse response, AppTaskLogPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppTaskLogDO> list = (List<AppTaskLogDO>) appTaskLogService
                .getAppTaskLogPage(exportReqVO).getRows();
        ExcelUtil<AppTaskLogRespVO> util = new ExcelUtil<>(AppTaskLogRespVO.class);
        util.exportExcel(response, AppTaskLogConvert.INSTANCE.convertToRespVOList(list), "抽取任务执行日志数据");
    }

    /**
     * 查询抽取任务执行日志步骤
     *
     * @param pageReqVO 分页请求
     * @return 日志步骤分页列表
     */
    @Operation(summary = "查询抽取任务执行日志步骤")
    @GetMapping("/detail/page")
    public CommonResult<PageResult<AppTaskLogDetailRespVO>> detailPage(AppTaskLogDetailPageReqVO pageReqVO) {
        PageResult<AppTaskLogDetailDO> page = appTaskLogService.getLogDetailPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(page, AppTaskLogDetailRespVO.class));
    }

    /**
     * 获取抽取任务执行日志详细信息
     *
     * @param id 日志编号
     * @return 日志详细信息
     */
    @Operation(summary = "获取抽取任务执行日志详细信息")
    @GetMapping("/{id}")
    public CommonResult<AppTaskLogRespVO> getInfo(@PathVariable("id") Long id) {
        AppTaskLogDO taskLogDO = appTaskLogService.getAppTaskLogById(id);
        return CommonResult.success(BeanUtils.toBean(taskLogDO, AppTaskLogRespVO.class));
    }

    /**
     * 删除抽取任务执行日志
     *
     * @param ids 日志编号数组
     * @return 删除记录数
     */
    @Operation(summary = "删除抽取任务执行日志")
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appTaskLogService.removeAppTaskById(Arrays.asList(ids)));
    }
}
