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

package tech.qiantong.qontology.module.basic.controller.admin.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tech.qiantong.qontology.common.annotation.Anonymous;
import tech.qiantong.qontology.common.annotation.Log;
import tech.qiantong.qontology.common.config.AniviaConfig;
import tech.qiantong.qontology.common.enums.BusinessType;
import tech.qiantong.qontology.common.utils.StringUtils;
import tech.qiantong.qontology.common.utils.file.FileUtils;
import tech.qiantong.qontology.core.controller.BaseController;
import tech.qiantong.qontology.core.domain.CommonResult;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFilePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileTrackReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileDO;
import tech.qiantong.qontology.module.basic.service.file.IBasicFileService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 模型数据文件Controller
 *
 * @author qModel
 * @date 2026-08-18
 */
@Tag(name = "模型数据文件")
@RestController
@RequestMapping("/basic/file")
@Validated
public class BasicFileController extends BaseController {
    @Resource
    private IBasicFileService modelDataFileService;

    @Operation(summary = "查询模型数据文件列表")
    @PreAuthorize("@ss.hasPermi('basic:file:manager:list')")
    @GetMapping("/list")
    public CommonResult<PageResult<BasicFileRespVO>> list(BasicFilePageReqVO modelDataFile) {
        PageResult<BasicFileDO> page = modelDataFileService.getModelDataFilePage(modelDataFile);
        return CommonResult.success(BeanUtils.toBean(page, BasicFileRespVO.class));
    }

    @Operation(summary = "查询模型数据文件详情")
    @PreAuthorize("@ss.hasPermi('basic:file:manager:query')")
    @GetMapping("/{id}")
    public CommonResult<BasicFileRespVO> getInfo(@PathVariable("id") Long id) {
        BasicFileDO basicFileDO = modelDataFileService.getModelDataFileById(id);
        return CommonResult.success(BeanUtils.toBean(basicFileDO, BasicFileRespVO.class));
    }

    @Operation(summary = "新增模型数据文件")
    @PreAuthorize("@ss.hasPermi('basic:file:manager:add')")
    @Log(title = "模型数据文件", businessType = BusinessType.INSERT)
    @PostMapping
    public CommonResult<Long> add(@Valid @RequestBody BasicFileSaveReqVO modelDataFile) {
        modelDataFile.setCreatorId(getUserId());
        modelDataFile.setCreateBy(getNickName());
        return CommonResult.toAjax(modelDataFileService.createModelDataFile(modelDataFile));
    }

    @Operation(summary = "修改模型数据文件")
    @PreAuthorize("@ss.hasPermi('basic:file:manager:edit')")
    @Log(title = "模型数据文件", businessType = BusinessType.UPDATE)
    @PutMapping
    public CommonResult<Integer> edit(@Valid @RequestBody BasicFileSaveReqVO modelDataFile) {
        return CommonResult.toAjax(modelDataFileService.updateModelDataFile(modelDataFile));
    }

    @Operation(summary = "删除模型数据文件")
    @PreAuthorize("@ss.hasPermi('basic:file:manager:remove')")
    @Log(title = "模型数据文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public CommonResult<Integer> remove(@PathVariable Long[] ids) {
        return CommonResult.toAjax(modelDataFileService.removeModelDataFile(Arrays.asList(ids)));
    }

    @Operation(summary = "获取多少种类型及每种类型下面的总文件数量")
    @GetMapping("/getFileTypes")
    public CommonResult<List<Map<String, Object>>> getFileTypes() {
        List<Map<String, Object>> list = modelDataFileService.getFileTypes();
        return CommonResult.success(list);
    }

    @Operation(summary = "文件跟踪统计")
    @PostMapping("/trackCount")
    public CommonResult<Boolean> trackCount(@Valid @RequestBody List<BasicFileTrackReqVO> documentTrackList) {
        return CommonResult.success(modelDataFileService.trackCount(documentTrackList, getUserId(), getUsername()));
    }

    @Anonymous
    @Operation(summary = "公开预览模型数据文件")
    @GetMapping("/public/preview/{id}/**")
    public void publicPreview(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            BasicFileDO fileInfo = modelDataFileService.getModelDataFileById(id);
            if (fileInfo == null || StringUtils.isEmpty(fileInfo.getFilePath())) {
                throw new Exception("文件不存在");
            }
            // 本地资源路径
            String localPath = AniviaConfig.getProfile();
            // 数据库资源地址
            String downloadPath = localPath + "/" + fileInfo.getFilePath();
            // 下载名称
            String downloadName = StringUtils.substringAfterLast(downloadPath, "/");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(downloadPath, response.getOutputStream());
        } catch (Exception e) {
            logger.error("公开预览文件失败", e);
        }
    }
}
