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

/* Layout */
import Layout from '@/layout/index.vue';

// 示例模块动公共路由
export default [
    {
        path: '/om',
        component: Layout,
        hidden: true,
        children: [
            {
                path: ':versionId/target/detail',
                component: () => import('@/views/om/omtarget/detail/index.vue'),
                hidden: true,
                name: 'OmTargetDetail',
                meta: { title: '建模目标详情', activeMenu: '/om/:versionId/target' },
            },
            {
                path: ':versionId/target/detail/object/type/detail/:id(\\d+)',
                component: () => import('@/views/om/om/object/type/detail/index.vue'),
                hidden: true,
                name: 'OmTargetDetailObjectType',
                meta: { title: '对象类型详情', activeMenu: '/om/:versionId/target'},
            },
            {
                path: ':versionId/aiModeling',
                component: () => import('@/views/om/aiModeling/index.vue'),
                name: 'AiModeling',
                meta: { title: 'AI辅助建模', activeMenu: '/om/:versionId/aiModeling' }
            },
            {
                path: ':versionId/aiModeling/detail/:id(\\d+)',
                component: () => import('@/views/om/aiModeling/detail/index.vue'),
                name: 'AiModelingDetail',
                meta: { title: 'AI辅助建模详情', activeMenu: '/om/:versionId/aiModeling' }
            }
        ]
    },
];
