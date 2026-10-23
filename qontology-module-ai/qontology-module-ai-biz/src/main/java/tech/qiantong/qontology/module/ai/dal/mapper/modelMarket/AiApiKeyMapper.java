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
 * Business License: https://community.qontology.ai/business/policy.html
 * See the LICENSE file in the project root for full license information.
 */

package tech.qiantong.qontology.module.ai.dal.mapper.modelMarket;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tech.qiantong.qontology.module.ai.controller.admin.modelMarket.vo.AiApiKeyPageReqVO;
import tech.qiantong.qontology.module.ai.dal.dataobject.modelMarket.AiApiKeyDO;
import tech.qiantong.qontology.mybatis.core.mapper.BaseMapperX;

/**
 * API秘钥Mapper接口
 *
 * @author qontology
 * @date 2025-12-23
 */
public interface AiApiKeyMapper extends BaseMapperX<AiApiKeyDO> {
    @Select({"""
            <script>
            select aak.platform
            from ai_api_key aak
            where aak.del_flag = 0
            	and aak.valid_flag = 1
            	and aak .status <![CDATA[ <> ]]> 0
            <if test='reqVO.name != null and reqVO.name != "" ' >
                and aak.name like concat('%',#{reqVO.name},'%') \s
            </if>
            group by aak.platform
            </script>
            """})
    Page<AiApiKeyPageReqVO> selectPage(IPage<AiApiKeyPageReqVO> page,
                                @Param("reqVO") AiApiKeyPageReqVO reqVO);
}
