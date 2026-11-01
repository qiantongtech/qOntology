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

package tech.qiantong.qontology.module.om.service.ai.aiFile.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.qiantong.qontology.module.om.controller.admin.ai.aiFile.vo.*;
import tech.qiantong.qontology.module.om.dal.dataobject.ai.aiFile.OmAiFileDO;
import tech.qiantong.qontology.module.om.dal.mapper.ai.aiFile.OmAiFileMapper;
import tech.qiantong.qontology.module.om.service.ai.aiFile.IOmAiFileService;
import tech.qiantong.qontology.module.om.enums.ai.OmAiAdoptStatusEnum;
import tech.qiantong.qontology.common.exception.ServiceException;
import tech.qiantong.qontology.core.page.PageResult;
import tech.qiantong.qontology.core.utils.object.BeanUtils;

/** AI辅助建模文件 Service 实现类。 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OmAiFileServiceImpl extends ServiceImpl<OmAiFileMapper, OmAiFileDO> implements IOmAiFileService {
    @Resource
    private OmAiFileMapper mapper;

    /** 获得AI辅助建模文件分页列表。 */
    @Override
    public PageResult<OmAiFileDO> getOmAiFilePage(OmAiFilePageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    /** 创建AI辅助建模文件。 */
    @Override
    public Long createOmAiFile(OmAiFileSaveReqVO reqVO) {
        OmAiFileDO data = BeanUtils.toBean(reqVO, OmAiFileDO.class);
        
        mapper.insert(data);
        return data.getId();
    }

    /** 更新AI辅助建模文件。 */
    @Override
    public int updateOmAiFile(OmAiFileSaveReqVO reqVO) {
        OmAiFileDO existing = getRequired(reqVO.getId());
        
        OmAiFileDO data = BeanUtils.toBean(reqVO, OmAiFileDO.class);
        
        return mapper.updateById(data);
    }

    /** 批量删除AI辅助建模文件。 */
    @Override
    public int removeOmAiFile(Collection<Long> ids) {
        
        return mapper.deleteBatchIds(ids);
    }

    /** 获得AI辅助建模文件详情。 */
    @Override
    public OmAiFileDO getOmAiFileById(Long id) {
        return getRequired(id);
    }

    /** 校验记录存在并返回数据。 */
    private OmAiFileDO getRequired(Long id) {
        OmAiFileDO data = mapper.selectById(id);
        if (data == null) {
            throw new ServiceException("AI辅助建模文件不存在");
        }
        return data;
    }
}
