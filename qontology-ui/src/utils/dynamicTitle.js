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

import store from '@/store';
import defaultSettings from '@/settings';
import useSettingsStore from '@/store/system/settings';

/**
 * 动态修改标题
 */
export function useDynamicTitle() {
    const settingsStore = useSettingsStore();
    if (settingsStore.dynamicTitle) {
        document.title = settingsStore.title + ' - ' + defaultSettings.title;
    } else {
        document.title = defaultSettings.title;
    }
}
