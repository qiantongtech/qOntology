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

package tech.qiantong.qontology.module.basic.api.dataType;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import tech.qiantong.qontology.module.basic.api.dto.dataType.BasicDataTypeOptionDTO;
import tech.qiantong.qontology.module.basic.api.service.dataType.IBasicDataTypeApiService;
import tech.qiantong.qontology.module.basic.dal.dataobject.dataType.BasicDataTypeDO;
import tech.qiantong.qontology.module.basic.dal.mapper.dataType.BasicDataTypeMapper;

@Service
public class BasicDataTypeApiServiceImpl implements IBasicDataTypeApiService {
    @Resource
    private BasicDataTypeMapper mapper;

    @Override
    public List<BasicDataTypeOptionDTO> getAvailableDataTypes() {
        return mapper.selectList(new QueryWrapper<BasicDataTypeDO>()
                .eq("valid_flag", true).orderByAsc("id")).stream()
                .map(this::toOption).collect(Collectors.toList());
    }

    @Override
    public BasicDataTypeOptionDTO getAvailableDataType(Long id) {
        BasicDataTypeDO type = id == null ? null : mapper.selectById(id);
        return type == null || !Boolean.TRUE.equals(type.getValidFlag()) ? null : toOption(type);
    }

    private BasicDataTypeOptionDTO toOption(BasicDataTypeDO type) {
        BasicDataTypeOptionDTO option = new BasicDataTypeOptionDTO();
        option.setId(type.getId());
        option.setName(type.getName());
        option.setDataType(type.getDataType() == null ? null : type.getDataType().getCode());
        option.setDescription(type.getDescription());
        option.setUnit(type.getUnit());
        option.setDecimalPlaces(type.getDecimalPlaces());
        option.setFormat(type.getFormat());
        option.setEnumType(type.getEnumType());
        option.setEnumSource("GLOBAL");
        return option;
    }
}
