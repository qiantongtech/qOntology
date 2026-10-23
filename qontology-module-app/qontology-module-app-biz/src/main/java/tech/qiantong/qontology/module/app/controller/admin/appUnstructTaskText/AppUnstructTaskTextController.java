package tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
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
import tech.qiantong.qontology.module.app.controller.admin.appUnstructTaskText.vo.*;
import tech.qiantong.qontology.module.app.dal.dataobject.appUnstructTaskText.AppUnstructTaskTextDO;
import tech.qiantong.qontology.module.app.convert.appUnstructTaskText.AppUnstructTaskTextConvert;
import tech.qiantong.qontology.module.app.service.appUnstructTaskText.IAppUnstructTaskTextService;

import java.util.Arrays;
import java.util.List;

/**
 * 任务文件段落关联Controller
 *
 * @author qOntology
 * @date 2026-10-20
 */
@Tag(name = "任务文件段落关联")
@RestController
@RequestMapping("/app/unstructTaskText")
@Validated
public class AppUnstructTaskTextController extends BaseController {
    @Resource
    private IAppUnstructTaskTextService appUnstructTaskTextService;

    /**
     * 查询任务文件段落关联列表
     *
     * @param pageReqVO 分页请求
     * @return 任务文件段落关联分页列表
     */
    @Operation(summary = "查询任务文件段落关联列表")
    @GetMapping("/list")
    public CommonResult<PageResult<AppUnstructTaskTextRespVO>> list(AppUnstructTaskTextPageReqVO pageReqVO) {
        PageResult<AppUnstructTaskTextDO> page = appUnstructTaskTextService.getAppUnstructTaskTextPage(pageReqVO);
        return CommonResult.success(BeanUtils.toBean(page, AppUnstructTaskTextRespVO.class));
    }

    /** 导出任务文件段落关联列表。 */
    @Operation(summary = "导出任务文件段落关联列表")
    @PostMapping("/export")
    @SuppressWarnings("unchecked")
    public void export(HttpServletResponse response, AppUnstructTaskTextPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppUnstructTaskTextDO> list = (List<AppUnstructTaskTextDO>) appUnstructTaskTextService
                .getAppUnstructTaskTextPage(exportReqVO).getRows();
        ExcelUtil<AppUnstructTaskTextRespVO> util = new ExcelUtil<>(AppUnstructTaskTextRespVO.class);
        util.exportExcel(response, AppUnstructTaskTextConvert.INSTANCE.convertToRespVOList(list), "任务文件段落关联数据");
    }

    /** 导入任务文件段落关联列表。 */
    @Operation(summary = "导入任务文件段落关联列表")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppUnstructTaskTextRespVO> util = new ExcelUtil<>(AppUnstructTaskTextRespVO.class);
        List<AppUnstructTaskTextRespVO> importExcelList = util.importExcel(file.getInputStream());
        String message = appUnstructTaskTextService.importAppUnstructTaskText(
                importExcelList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 获取任务文件段落关联详细信息
     *
     * @param id 段落编号
     * @return 段落关联详细信息
     */
    @Operation(summary = "获取任务文件段落关联详细信息")
    @GetMapping("/{id}")
    public CommonResult<AppUnstructTaskTextRespVO> getInfo(@PathVariable("id") Long id) {
        AppUnstructTaskTextDO textDO = appUnstructTaskTextService.getAppUnstructTaskTextById(id);
        return CommonResult.success(BeanUtils.toBean(textDO, AppUnstructTaskTextRespVO.class));
    }

    /**
     * 新增任务文件段落关联
     *
     * @param saveReqVO 段落关联信息
     * @return 新增段落编号
     */
    @Operation(summary = "新增任务文件段落关联")
    @Log(title = "任务文件段落关联", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppUnstructTaskTextSaveReqVO saveReqVO) {
        return CommonResult.toAjax(appUnstructTaskTextService.createAppUnstructTaskText(saveReqVO));
    }

    /**
     * 修改任务文件段落关联
     *
     * @param saveReqVO 段落关联信息
     * @return 修改记录数
     */
    @Operation(summary = "修改任务文件段落关联")
    @Log(title = "任务文件段落关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppUnstructTaskTextSaveReqVO saveReqVO) {
        return CommonResult.toAjax(appUnstructTaskTextService.updateAppUnstructTaskText(saveReqVO));
    }

    /**
     * 删除任务文件段落关联
     *
     * @param ids 段落编号数组
     * @return 删除记录数
     */
    @Operation(summary = "删除任务文件段落关联")
    @Log(title = "任务文件段落关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(appUnstructTaskTextService.removeAppUnstructTaskText(Arrays.asList(ids)));
    }
}
