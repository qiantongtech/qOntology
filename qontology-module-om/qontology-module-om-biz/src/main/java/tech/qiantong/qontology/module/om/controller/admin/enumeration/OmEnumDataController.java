/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Ontology Engineering Platform (Open Source Edition).
 *
 * qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 * You may use qOntology for commercial purposes, but you may not remove, hide,
 * modify, or replace the qOntology logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qOntology as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.om.controller.admin.enumeration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataRespVO;
import tech.qiantong.qontology.module.om.controller.admin.enumeration.vo.OmEnumDataSaveReqVO;
import tech.qiantong.qontology.module.om.convert.enumeration.OmEnumDataConvert;
import tech.qiantong.qontology.module.om.dal.dataobject.enumeration.OmEnumDataDO;
import tech.qiantong.qontology.module.om.service.enumeration.IOmEnumDataService;

/**
 * 枚举数据Controller
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Tag(name = "枚举数据")
@RestController
@RequestMapping("/om/enumData")
@Validated
public class OmEnumDataController extends BaseController {

    @Resource
    private IOmEnumDataService omEnumDataService;

    /**
     * 查询枚举数据列表
     *
     * @param omEnumData 分页查询条件
     * @return 分页查询结果
     */
    @Operation(summary = "查询枚举数据列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmEnumDataRespVO>> list(OmEnumDataPageReqVO omEnumData) {
        PageResult<OmEnumDataDO> page = omEnumDataService.getOmEnumDataPage(omEnumData);
        return CommonResult.success(BeanUtils.toBean(page, OmEnumDataRespVO.class));
    }

    /**
     * 导出枚举数据数据
     *
     * @param response 文件下载响应
     * @param exportReqVO 导出筛选条件
     */
    @Operation(summary = "导出枚举数据列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:export')")
    @Log(title = "枚举数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmEnumDataPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OmEnumDataDO> list = (List<OmEnumDataDO>) omEnumDataService.getOmEnumDataPage(exportReqVO).getRows();
        ExcelUtil<OmEnumDataRespVO> util = new ExcelUtil<>(OmEnumDataRespVO.class);
        util.exportExcel(response, OmEnumDataConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /**
     * 导入枚举数据数据
     *
     * @param file 导入文件
     * @param updateSupport 是否覆盖更新已有记录
     * @param enumType 枚举类型编码
     * @return 业务处理结果
     */
    @Operation(summary = "导入枚举数据列表")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:import')")
    @Log(title = "枚举数据", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(
        MultipartFile file,
        boolean updateSupport,
        @RequestParam(required = false) String enumType
    ) throws Exception {
        ExcelUtil<OmEnumDataRespVO> util = new ExcelUtil<>(OmEnumDataRespVO.class);
        List<OmEnumDataRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        if (enumType != null && !enumType.isBlank()) {
            for (OmEnumDataRespVO item : importExcelList) {
                if (
                    !enumType.equals(item.getEnumType())
                ) throw new tech.qiantong.qontology.common.exception.ServiceException(
                    "导入数据的枚举类型与当前类型不一致"
                );
            }
        }
        String message = omEnumDataService.importOmEnumData(importExcelList, updateSupport, operName);
        return success(message);
    }

    /**
     * 查询枚举数据详情
     *
     * @param enumCode 枚举数据记录 ID
     * @return 业务处理结果
     */
    @Operation(summary = "获取枚举数据详细信息")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:query')")
    @GetMapping(value = "/{enumCode}")
    public CommonResult<OmEnumDataRespVO> getInfo(@PathVariable("enumCode") Long enumCode) {
        OmEnumDataDO omEnumDataDO = omEnumDataService.getOmEnumDataById(enumCode);
        return CommonResult.success(BeanUtils.toBean(omEnumDataDO, OmEnumDataRespVO.class));
    }

    /**
     * 新增枚举数据
     *
     * @param omEnumData 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "新增枚举数据")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:add')")
    @Log(title = "枚举数据", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmEnumDataSaveReqVO omEnumData) {
        omEnumData.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(omEnumDataService.createOmEnumData(omEnumData));
    }

    /**
     * 修改枚举数据
     *
     * @param omEnumData 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "修改枚举数据")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:edit')")
    @Log(title = "枚举数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmEnumDataSaveReqVO omEnumData) {
        return CommonResult.toAjax(omEnumDataService.updateOmEnumData(omEnumData));
    }

    /**
     * 删除枚举数据
     *
     * @param ids 记录 ID 数组
     * @return 业务处理结果
     */
    @Operation(summary = "删除枚举数据")
    @PreAuthorize("@ss.hasPermi('om:enumeration:data:remove')")
    @Log(title = "枚举数据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{enumCodes}")
    public CommonResult<Integer> remove(@PathVariable("enumCodes") Long[] ids) {
        return CommonResult.toAjax(omEnumDataService.removeOmEnumData(Arrays.asList(ids)));
    }
}
