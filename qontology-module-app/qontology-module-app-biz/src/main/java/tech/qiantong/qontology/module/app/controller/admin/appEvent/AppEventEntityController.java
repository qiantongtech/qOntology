/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).
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
 * Business License: https://community.qknow.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.app.controller.admin.appEvent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityPageReqVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntityRespVO;
import tech.qiantong.qontology.module.app.controller.admin.appEvent.vo.AppEventEntitySaveReqVO;
import tech.qiantong.qontology.module.app.convert.appEvent.AppEventEntityConvert;
import tech.qiantong.qontology.module.app.dal.dataobject.appEvent.AppEventEntityDO;
import tech.qiantong.qontology.module.app.service.appEvent.IAppEventEntityService;

import java.util.Arrays;
import java.util.List;

/**
 * 事件实体关联Controller
 *
 * @author qknow
 * @date 2025-11-11
 */
@Tag(name = "事件实体关联")
@RestController
@RequestMapping("/app/evententity")
@Validated
public class AppEventEntityController extends BaseController {
    @Resource
    private IAppEventEntityService AppEventEntityService;

    @Operation(summary = "查询事件实体关联列表")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<AppEventEntityRespVO>> list(AppEventEntityPageReqVO AppEventEntity) {
        PageResult<AppEventEntityDO> page = AppEventEntityService.getAppEventEntityPage(AppEventEntity);
        return CommonResult.success(BeanUtils.toBean(page, AppEventEntityRespVO.class));
    }

    @Operation(summary = "导出事件实体关联列表")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:export')")
    @Log(title = "事件实体关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AppEventEntityPageReqVO exportReqVO) {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AppEventEntityDO> list = (List<AppEventEntityDO>) AppEventEntityService.getAppEventEntityPage(exportReqVO).getRows();
        ExcelUtil<AppEventEntityRespVO> util = new ExcelUtil<>(AppEventEntityRespVO.class);
        util.exportExcel(response, AppEventEntityConvert.INSTANCE.convertToRespVOList(list), "应用管理数据");
    }

    @Operation(summary = "导入事件实体关联列表")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:import')")
    @Log(title = "事件实体关联", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<AppEventEntityRespVO> util = new ExcelUtil<>(AppEventEntityRespVO.class);
        List<AppEventEntityRespVO> importExcelList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = AppEventEntityService.importAppEventEntity(importExcelList, updateSupport, operName);
        return success(message);
    }

    @Operation(summary = "获取事件实体关联详细信息")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<AppEventEntityRespVO> getInfo(@PathVariable("id") Long id) {
        AppEventEntityDO AppEventEntityDO = AppEventEntityService.getAppEventEntityById(id);
        return CommonResult.success(BeanUtils.toBean(AppEventEntityDO, AppEventEntityRespVO.class));
    }

    @Operation(summary = "新增事件实体关联")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:add')")
    @Log(title = "事件实体关联", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody AppEventEntitySaveReqVO AppEventEntity) {
        AppEventEntity.setWorkspaceId(super.getWorkSpaceId());
        AppEventEntity.setCreatorId(getUserId());
        AppEventEntity.setCreateBy(getNickName());
        return CommonResult.toAjax(AppEventEntityService.createAppEventEntity(AppEventEntity));
    }

    @Operation(summary = "修改事件实体关联")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:edit')")
    @Log(title = "事件实体关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody AppEventEntitySaveReqVO AppEventEntity) {
        AppEventEntity.setWorkspaceId(getWorkSpaceId());
        AppEventEntity.setUpdaterId(getUserId());
        AppEventEntity.setUpdateBy(getNickName());
        return CommonResult.toAjax(AppEventEntityService.updateAppEventEntity(AppEventEntity));
    }

    @Operation(summary = "删除事件实体关联")
    @PreAuthorize("@ss.hasPermi('App:event:evententity:remove')")
    @Log(title = "事件实体关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(AppEventEntityService.removeAppEventEntity(Arrays.asList(ids)));
    }


}
