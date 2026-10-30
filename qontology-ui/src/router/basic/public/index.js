/*
 * Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.
 *
 * This file is part of qModel Module Platform (Open Source Edition).
 *
 * qModel is licensed under Apache License 2.0 with additional qModel terms.
 * You may use qModel for commercial purposes, but you may not remove, hide,
 * modify, or replace the qModel logo, copyright notices, license notices,
 * or attribution information without a separate commercial license.
 *
 * White-label use, OEM distribution, rebranding, or presenting qModel as
 * another product requires separate commercial authorization from
 * Jiangsu Qiantong Technology Co., Ltd.
 *
 * Business License: `https://qmodel.tech/`
 * See the LICENSE file in the project root for full license information.
 */

/* Layout */
import Layout from '@/layout/index.vue';

// 示例模块动公共路由
export default [
    {
        path: '/basic',
        component: Layout,
        hidden: true,
        children: [
            {
                path: 'basicEnum/enum/data/:enumId(\\d+)',
                component: () => import('@/views/basic/enumeration/data/index.vue'),
                permissions: ['basic:enumeration:data:list'],
                name: 'BasicEnumData',
                meta: { title: '枚举数据', activeMenu: '/basic/basicEnum' }
            },
            {
                path: 'llm/modelMarket/detail',
                hidden: true,
                component: () => import('@/views/basic/llm/modelMarket/detail/index.vue'),
                name: 'ModelMarketDetail',
                meta: { title: '模型详情', activeMenu: '/basic/llm/modelMarket' }
            },
            {
                path: 'llm/myModel/detail',
                hidden: true,
                component: () => import('@/views/basic/llm/modelMarket/detail/index.vue'),
                name: 'myModelDetail',
                meta: { title: '模型详情', activeMenu: '/basic/llm/myModel' }
            }
        ]
    },
];
