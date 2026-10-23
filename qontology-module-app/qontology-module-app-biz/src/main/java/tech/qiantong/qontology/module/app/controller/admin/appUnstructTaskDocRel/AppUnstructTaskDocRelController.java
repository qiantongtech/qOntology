package tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskDocRel.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskDocRel.AppUnstructTaskDocRelDO;
import tech.qiantong.qontology.module.app.convert.appUnstructTaskDocRel.AppUnstructTaskDocRelConvert;
import tech.qiantong.qontology.module.app.service.appUnstructTaskDocRel.IAppUnstructTaskDocRelService;

import java.util.Arrays;
import java.util.List;

/**
 * 任务文件关联Controller
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Tag(name = "任务文件关联")
@RestController
@RequestMapping("/app/unstructTaskDocRel")
@Validated
public class AppUnstructTaskDocRelController extends BaseController {
    @Resource
    private IAppUnstructTaskDocRelService appUnstructTaskDocRelService;

    /**
     * 查询任务文件关联列表
     *
     * @param pageReqVO 分页请求
     * @return 任务文件关联分页列表
     */
    @Operation(summary = "查询任务文件关联列表")
    @PreAuthorize("@ss.hasPermi('app:appUnstructTaskDocRel:unstructtaskdocrel:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppUnstructTaskDocRelRespVO>> list(AppUnstructTaskDocRelPageReqVO pageReqVO) {
        PageResult<AppUnstructTaskDocRelDO> page = appUnstructTaskDocRelService.getAppUnstructTaskDocRelPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(page, AppUnstructTaskDocRelRespVO.class));
    }

    /** 导出任务文件关联列表。 */
    @Operation(summary = "导出任务文件关联列表")
    @PostMapping("/export")
    @SuppressWarnings("unchecked")
    public void export(HttpServletResponse response, AppUnstructTaskDocRelPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppUnstructTaskDocRelDO> list = (List<AppUnstructTaskDocRelDO>) appUnstructTaskDocRelService
                .getAppUnstructTaskDocRelPage(exportReqVO).getRows();
        ExcelUtil<AppUnstructTaskDocRelRespVO> util = new ExcelUtil<>(AppUnstructTaskDocRelRespVO.class);
        util.exportExcel(response, AppUnstructTaskDocRelConvert.INSTANCE.convertToRespVOList(list), "任务文件关联数据");
    }

    /** 导入任务文件关联列表。 */
    @Operation(summary = "导入任务文件关联列表")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppUnstructTaskDocRelRespVO> util = new ExcelUtil<>(AppUnstructTaskDocRelRespVO.class);
        List<AppUnstructTaskDocRelRespVO> importExcelList = util.importExcel(file.getInputStream());
        String message = appUnstructTaskDocRelService.importAppUnstructTaskDocRel(
                importExcelList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 获取任务文件关联详细信息
     *
     * @param id 关联编号
     * @return 任务文件关联详细信息
     */
    @Operation(summary = "获取任务文件关联详细信息")
    @GetMapping("/{id}")
    public CommonResult<AppUnstructTaskDocRelRespVO> getInfo(@PathVariable("id") Long id) {
        AppUnstructTaskDocRelDO relationDO = appUnstructTaskDocRelService.getAppUnstructTaskDocRelById(id);
        return CommonResult.success(BeanUtils.toBean(relationDO, AppUnstructTaskDocRelRespVO.class));
    }

    /**
     * 新增任务文件关联
     *
     * @param saveReqVO 任务文件关联
     * @return 新增关联编号
     */
    @Operation(summary = "新增任务文件关联")
    @Log(title = "任务文件关联", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppUnstructTaskDocRelSaveReqVO saveReqVO) {
        return CommonResult.toAjax(appUnstructTaskDocRelService.createAppUnstructTaskDocRel(saveReqVO));
    }

    /**
     * 修改任务文件关联
     *
     * @param saveReqVO 任务文件关联
     * @return 修改记录数
     */
    @Operation(summary = "修改任务文件关联")
    @Log(title = "任务文件关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppUnstructTaskDocRelSaveReqVO saveReqVO) {
        return CommonResult.toAjax(appUnstructTaskDocRelService.updateAppUnstructTaskDocRel(saveReqVO));
    }

    /**
     * 删除任务文件关联
     *
     * @param ids 关联编号数组
     * @return 删除记录数
     */
    @Operation(summary = "删除任务文件关联")
    @Log(title = "任务文件关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appUnstructTaskDocRelService.removeAppUnstructTaskDocRel(Arrays.asList(ids)));
    }
}
