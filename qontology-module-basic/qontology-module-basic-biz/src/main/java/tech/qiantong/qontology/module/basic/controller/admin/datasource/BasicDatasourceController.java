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

package tech.qiantong.qontology.module.basic.controller.admin.datasource;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.database.core.DbTable;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.AjaxResult;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.api.dto.datasource.DatasourceCreaTeTableReqDTO;
import tech.qiantong.qontology.module.basic.api.dto.model.BasicModelColumnReqDTO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourcePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourceRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourceSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicSearchTablePageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.datasource.BasicDatasourceDO;
import tech.qiantong.qontology.module.basic.service.datasource.IBasicDatasourceService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据源Controller
 *
 * @author lhs
 * @date 2025-01-21
 */
@Tag(name = "数据源")
@RestController
@RequestMapping("/basic/datasource")
@Validated
public class BasicDatasourceController extends BaseController {
    @Resource
    private IBasicDatasourceService basicDatasourceService;

    @Operation(summary = "查询数据源列表")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicDatasourceRespVO>> list(BasicDatasourcePageReqVO basicDatasource) {
        PageResult<BasicDatasourceDO> page = basicDatasourceService.getDmDatasourcePage(basicDatasource);
        return CommonResult.success(BeanUtils.toBean(page, BasicDatasourceRespVO.class));
    }

    @Operation(summary = "查询数据资产的数据源连接信息")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:list')")
    @GetMapping("/getDataSourceByAsset")
    public AjaxResult getDataSourceByAsset(BasicDatasourceRespVO daAsset) {
        List<BasicDatasourceDO> daAssetDOS = basicDatasourceService.getDataSourceByAsset(daAsset);
        return AjaxResult.success(daAssetDOS);
    }

    @Operation(summary = "查询数据源列表")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:list')")
    @GetMapping("/getDaDatasourceList")
    public CommonResult<List<BasicDatasourceRespVO>> getDaDatasourceList(BasicDatasourcePageReqVO daDatasource) {
        List<BasicDatasourceDO> page = basicDatasourceService.getDaDatasourceList(daDatasource);
        return CommonResult.success(BeanUtils.toBean(page, BasicDatasourceRespVO.class));
    }

    @Operation(summary = "获取数据源详细信息")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:query')")
    @GetMapping(value = "/{id}")
    public CommonResult<BasicDatasourceRespVO> getInfo(@PathVariable("id") Long id) {
        BasicDatasourceDO daDatasourceDO = basicDatasourceService.getDaDatasourceById(id);
        return CommonResult.success(BeanUtils.toBean(daDatasourceDO, BasicDatasourceRespVO.class));
    }

    @Operation(summary = "新增数据源")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:add')")
    @Log(title = "数据源", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicDatasourceSaveReqVO daDatasource) {
        daDatasource.setCreatorId(getUserId());
        daDatasource.setCreateBy(getNickName());
        daDatasource.setCreateTime(DateUtil.date());
        return CommonResult.toAjax(basicDatasourceService.createDaDatasource(daDatasource));
    }

    @Operation(summary = "修改数据源")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:edit')")
    @Log(title = "数据源", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicDatasourceSaveReqVO daDatasource) {
        daDatasource.setUpdaterId(getUserId());
        daDatasource.setUpdateBy(getNickName());
        daDatasource.setUpdateTime(DateUtil.date());
        return CommonResult.toAjax(basicDatasourceService.updateDaDatasource(daDatasource));
    }

    @Operation(summary = "删除数据源")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:remove')")
    @Log(title = "数据源", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(basicDatasourceService.removeDaDatasource(Arrays.asList(ids)));
    }

    @Operation(summary = "测试连接")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:remove')")
    @GetMapping("clientsTest/{id}")
    public AjaxResult clientsTest(@PathVariable("id") Long ids) {
        return basicDatasourceService.clientsTest(ids);
    }


    @Operation(summary = "获取数据源里面的数据表")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:query')")
    @GetMapping(value = "/tableList")
    public AjaxResult getTableList(BasicSearchTablePageReqVO dmSearchTablePageReqVo) {
        List<DbTable> tables = basicDatasourceService.getDbTables(dmSearchTablePageReqVo);
        return success(tables);
    }

    @Operation(summary = "获取数据源里面的数据表的数据字段")
    @PreAuthorize("@ss.hasPermi('basic:datasource:datasource:query')")
    @PostMapping(value = "/columnsList")
    public AjaxResult getColumnsList(@RequestBody JSONObject jsonObject) {
        List<BasicModelColumnReqDTO> columns = basicDatasourceService.getColumnsList(jsonObject);
        return success(columns);
    }

    @Operation(summary = "创建任务临时表")
    @PostMapping(value = "/createTaskTempTable")
    public AjaxResult createTaskTempTable(@RequestBody Map<String, Object> map) {
        DatasourceCreaTeTableReqDTO daDatasource = basicDatasourceService.createTaskTempTable(map);
        return success(daDatasource);
    }
}
