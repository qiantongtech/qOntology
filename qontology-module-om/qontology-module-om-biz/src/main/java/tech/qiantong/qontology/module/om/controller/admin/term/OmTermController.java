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

package tech.qiantong.qontology.module.om.controller.admin.term;

import cn.hutool.core.date.DateUtil;
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
import tech.qiantong.qontology.common.exception.enums.GlobalErrorCodeConstants;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageParam;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.core.utils.poi.ExcelUtil;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermPageReqVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermRespVO;
import tech.qiantong.qontology.module.om.controller.admin.term.vo.OmTermSaveReqVO;
import tech.qiantong.qontology.module.om.convert.term.OmTermConvert;
import tech.qiantong.qontology.module.om.dal.dataobject.term.OmTermDO;
import tech.qiantong.qontology.module.om.service.term.IOmTermService;

/**
 * 术语Controller
 *
 * @author qOntology
 * @date 2026-10-10
 */
@Tag(name = "术语")
@RestController
@RequestMapping("/om/term")
@Validated
public class OmTermController extends BaseController {

    @Resource
    private IOmTermService omTermService;

    /**
     * 查询术语列表
     *
     * @param omTerm 分页查询条件
     * @return 分页查询结果
     */
    @Operation(summary = "查询术语列表")
    @PreAuthorize("@ss.hasPermi('om:term:term:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<OmTermRespVO>> list(OmTermPageReqVO omTerm) {
        PageResult<OmTermDO> page = omTermService.getOmTermPage(omTerm);
        return CommonResult.success(BeanUtils.toBean(page, OmTermRespVO.class));
    }

    /**
     * 导出术语数据
     *
     * @param response 文件下载响应
     * @param exportReqVO 导出筛选条件
     */
    @Operation(summary = "导出术语列表")
    @PreAuthorize("@ss.hasPermi('om:term:term:export')")
    @Log(title = "术语", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OmTermPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OmTermDO> list = (List<OmTermDO>) omTermService.getOmTermPage(exportReqVO).getRows();
        ExcelUtil<OmTermRespVO> util = new ExcelUtil<>(OmTermRespVO.class);
        util.exportExcel(response, OmTermConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    /**
     * 导入术语数据
     *
     * @param file 导入文件
     * @param updateSupport 是否覆盖更新已有记录
     * @return 业务处理结果
     */
    @Operation(summary = "导入术语列表")
    @PreAuthorize("@ss.hasPermi('om:term:term:import')")
    @Log(title = "术语", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<OmTermRespVO> util = new ExcelUtil<>(OmTermRespVO.class);
        List<OmTermRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = omTermService.importOmTerm(importExcelList, updateSupport, operName);
        return success(message);
    }

    /**
     * 查询术语详情
     *
     * @param id 记录 ID
     * @return 业务处理结果
     */
    @Operation(summary = "获取术语详细信息")
    @PreAuthorize("@ss.hasPermi('om:term:term:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<OmTermRespVO> getInfo(@PathVariable("id") Long id) {
        OmTermDO omTermDO = omTermService.getOmTermById(id);
        return CommonResult.success(BeanUtils.toBean(omTermDO, OmTermRespVO.class));
    }

    /**
     * 新增术语
     *
     * @param omTerm 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "新增术语")
    @PreAuthorize("@ss.hasPermi('om:term:term:add')")
    @Log(title = "术语", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody OmTermSaveReqVO omTerm) {
        omTerm.setWorkspaceId(getWorkSpaceId());
        return CommonResult.toAjax(omTermService.createOmTerm(omTerm));
    }

    /**
     * 修改术语
     *
     * @param omTerm 新增或修改的业务信息
     * @return 业务处理结果
     */
    @Operation(summary = "修改术语")
    @PreAuthorize("@ss.hasPermi('om:term:term:edit')")
    @Log(title = "术语", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody OmTermSaveReqVO omTerm) {
        return CommonResult.toAjax(omTermService.updateOmTerm(omTerm));
    }

    /**
     * 删除术语
     *
     * @param ids 记录 ID 数组
     * @return 业务处理结果
     */
    @Operation(summary = "删除术语")
    @PreAuthorize("@ss.hasPermi('om:term:term:remove')")
    @Log(title = "术语", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(omTermService.removeOmTerm(Arrays.asList(ids)));
    }
}
