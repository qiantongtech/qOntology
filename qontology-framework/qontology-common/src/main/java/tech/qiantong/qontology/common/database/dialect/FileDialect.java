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

package tech.qiantong.qontology.common.database.dialect;

import org.springframework.web.multipart.MultipartFile;
import tech.qiantong.qontology.common.database.constants.DbQueryProperty;
import tech.qiantong.qontology.common.database.core.FileInfo;

import java.util.List;

/**
 * 数据资产-非结构化方言
 *
 * @author Chaos
 * @date 2025-07-16
 */
public interface FileDialect {

    List<FileInfo> getFiles(DbQueryProperty dbQueryProperty, String path);

    void uploadFile(DbQueryProperty dbQueryProperty, String path, MultipartFile file);

}
