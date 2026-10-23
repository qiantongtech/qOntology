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

package tech.qiantong.qontology.module.basic.service.file;

import com.baomidou.mybatisplus.extension.service.IService;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFilePageReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileRespVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileSaveReqVO;
import tech.qiantong.qontology.module.basic.controller.admin.file.vo.BasicFileTrackReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.file.BasicFileDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 模型数据文件Service接口
 *
 * @author qModel
 * @date 2026-08-18
 */
public interface IBasicFileService extends IService<BasicFileDO> {

    /**
     * 获得模型数据文件分页列表
     *
     * @param pageReqVO 分页请求
     * @return 模型数据文件分页列表
     */
    PageResult<BasicFileDO> getModelDataFilePage(BasicFilePageReqVO pageReqVO);

    /**
     * 创建模型数据文件
     *
     * @param createReqVO 模型数据文件信息
     * @return 模型数据文件编号
     */
    Long createModelDataFile(BasicFileSaveReqVO createReqVO);

    /**
     * 更新模型数据文件
     *
     * @param updateReqVO 模型数据文件信息
     */
    int updateModelDataFile(BasicFileSaveReqVO updateReqVO);

    /**
     * 删除模型数据文件
     *
     * @param idList 模型数据文件编号
     */
    int removeModelDataFile(Collection<Long> idList);

    /**
     * 获得模型数据文件详情
     *
     * @param id 模型数据文件编号
     * @return 模型数据文件
     */
    BasicFileDO getModelDataFileById(Long id);

    /**
     * 获得全部模型数据文件列表
     *
     * @return 模型数据文件列表
     */
    List<BasicFileDO> getModelDataFileList();

    /**
     * 获得全部模型数据文件 Map
     *
     * @return 模型数据文件 Map
     */
    Map<Long, BasicFileDO> getModelDataFileMap();


    /**
     * 导入模型数据文件数据
     *
     * @param importExcelList 模型数据文件数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName        操作用户
     * @return 结果
     */
    String importModelDataFile(List<BasicFileRespVO> importExcelList, boolean isUpdateSupport, String operName);

    /**
     * 获取多少种类型及每种类型下面的总文件数量
     *
     * @return
     */
    List<Map<String, Object>> getFileTypes();

    /**
     * 追踪文件
     *
     * @param documentTrackList
     * @param userId
     * @param userName
     * @return
     */
    Boolean trackCount(List<BasicFileTrackReqVO> documentTrackList, Long userId, String userName);
}
