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

package tech.qiantong.qontology.module.om.api.export;

/**
 * 本体模型导出接口
 *
 * 只读导出指定版本的本体模型，项目及版本归属由调用方校验
 */
public interface OntologyExportApi {
    /**
     * 导出指定项目版本的 OWL 本体文档
     *
     * @param versionId 待导出的项目版本 ID
     * @param namespace 本体命名空间，作为导出资源标识的前缀
     * @param projectName 写入本体标签的项目名称
     * @param versionName 写入本体版本信息的版本名称
     * @return UTF-8 编码的本体文档字节数组
     */
    byte[] exportOwl(Long versionId, String namespace, String projectName, String versionName);

    /**
     * 导出指定项目版本的 Turtle 本体文档
     *
     * @param versionId 待导出的项目版本 ID
     * @param namespace 本体命名空间，作为导出资源标识的前缀
     * @param projectName 写入本体标签的项目名称
     * @param versionName 写入本体版本信息的版本名称
     * @return UTF-8 编码的本体文档字节数组
     */
    byte[] exportTurtle(Long versionId, String namespace, String projectName, String versionName);
}
