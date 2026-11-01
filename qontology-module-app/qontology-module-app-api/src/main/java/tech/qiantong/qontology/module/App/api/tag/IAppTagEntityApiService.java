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

package tech.qiantong.qontology.module.App.api.tag;

import tech.qiantong.qontology.module.App.api.tag.dto.AppTagEntityReqDTO;
import tech.qiantong.qontology.module.App.api.tag.dto.AppTagEntityRespDTO;

import java.util.List;

public interface IAppTagEntityApiService {

    /**
     * 获得知识标签列表
     *
     * @return 知识标签列表
     */
    List<AppTagEntityRespDTO> getAppTagEntityList(AppTagEntityReqDTO pageReqVO);


}
