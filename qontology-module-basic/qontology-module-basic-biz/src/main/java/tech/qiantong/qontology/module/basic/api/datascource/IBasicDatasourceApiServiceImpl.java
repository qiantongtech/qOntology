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

package tech.qiantong.qontology.module.basic.api.datascource;

import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.core.utils.object.BeanUtils;
import tech.qiantong.qontology.module.basic.api.dto.datasource.BasicDatasourceReqDO;
import tech.qiantong.qontology.module.basic.api.dto.datasource.BasicDatasourceRespDTO;
import tech.qiantong.qontology.module.basic.api.service.asset.IBasicDatasourceApiService;
import tech.qiantong.qontology.module.basic.controller.admin.datasource.vo.BasicDatasourcePageReqVO;
import tech.qiantong.qontology.module.basic.dal.dataobject.datasource.BasicDatasourceDO;
import tech.qiantong.qontology.module.basic.service.datasource.IBasicDatasourceService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zyg
 * @version 1.0.0
 * <p>
 * class description goes here
 * descritpion:
 * @date 2025/11/17 14:04
 */
@Service("dataSourceApiService")
public class IBasicDatasourceApiServiceImpl implements IBasicDatasourceApiService {
    @Resource
    private IBasicDatasourceService basicDatasourceService;

    @Override
    public BasicDatasourceRespDTO getDatasourceById(Long id) {
        BasicDatasourceDO datasource = basicDatasourceService.getDaDatasourceById(id);
        return datasource == null ? null : BeanUtils.toBean(datasource, BasicDatasourceRespDTO.class);
    }

    @Override
    public List<BasicDatasourceRespDTO> getDmDatasource(BasicDatasourceReqDO reqDO) {
        BasicDatasourcePageReqVO bean = BeanUtils.toBean(reqDO, BasicDatasourcePageReqVO.class);
        ArrayList<BasicDatasourceRespDTO> dtos = Lists.newArrayList();
        List<?> rows = basicDatasourceService.getDmDatasourcePage(bean).getRows();
        for (Object row : rows) {
            BasicDatasourceRespDTO dto = BeanUtils.toBean(row, BasicDatasourceRespDTO.class);
            dtos.add(dto);
        }
        return dtos;
    }
}
