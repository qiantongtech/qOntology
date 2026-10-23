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

import tab from './tab';
import auth from './auth';
import cache from './cache';
import modal from './modal';
import download from './download';

export default function installPlugins(app) {
    // 页签操作
    app.config.globalProperties.$tab = tab;
    // 认证对象
    app.config.globalProperties.$auth = auth;
    // 缓存对象
    app.config.globalProperties.$cache = cache;
    // 模态框对象
    app.config.globalProperties.$modal = modal;
    // 下载文件
    app.config.globalProperties.$download = download;
}
