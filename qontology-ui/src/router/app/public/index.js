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

import Layout from '@/layout/index.vue';

export default [
    {
        path: '/app/structTask/add',
        component: Layout,
        hidden: true,
        children: [{
            path: '',
            component: () => import('@/views/app/structTask/add/index.vue'),
            name: 'AppStructTaskAdd',
            meta: { title: '结构化抽取配置', activeMenu: '/app/structTask' }
        }]
    },
    {
        path: '/app/extractResults',
        component: Layout,
        hidden: true,
        children: [
            {
                path: '',
                component: () => import('@/views/app/extractResults/index.vue'),
                hidden: true,
                name: 'AppExtractResults',
                meta: {
                    title: '抽取结果',
                    activeMenu: '/app/unstructTask'
                }
            }
        ]
    }
];
