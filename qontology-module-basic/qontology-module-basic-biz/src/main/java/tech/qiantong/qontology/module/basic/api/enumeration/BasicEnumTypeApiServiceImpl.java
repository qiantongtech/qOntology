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

package tech.qiantong.qontology.module.basic.api.enumeration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import tech.qiantong.qontology.module.basic.api.dto.enumeration.BasicEnumTypeOptionDTO;
import tech.qiantong.qontology.module.basic.api.service.enumeration.IBasicEnumTypeApiService;
import tech.qiantong.qontology.module.basic.dal.dataobject.enumeration.BasicEnumTypeDO;
import tech.qiantong.qontology.module.basic.dal.mapper.enumeration.BasicEnumTypeMapper;

@Service
public class BasicEnumTypeApiServiceImpl implements IBasicEnumTypeApiService {
    @Resource
    private BasicEnumTypeMapper mapper;

    @Override
    public List<BasicEnumTypeOptionDTO> getAvailableEnumTypes() {
        return mapper.selectList(new QueryWrapper<BasicEnumTypeDO>()
                .eq("status", "0").eq("valid_flag", true).orderByAsc("enum_id"))
                .stream().map(type -> {
                    BasicEnumTypeOptionDTO option = new BasicEnumTypeOptionDTO();
                    option.setEnumName(type.getEnumName());
                    option.setEnumType(type.getEnumType());
                    return option;
                }).collect(Collectors.toList());
    }

    @Override
    public boolean isAvailableEnumType(String enumType) {
        return enumType != null && mapper.selectCount(new QueryWrapper<BasicEnumTypeDO>()
                .eq("enum_type", enumType).eq("status", "0").eq("valid_flag", true)) > 0;
    }
}
