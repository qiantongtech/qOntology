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
            // 枚举详情沿用列表菜单高亮；禁用缓存，重新进入时加载最新概要与关联数据。
            {
                path: 'basicEnum/detail/:enumId(\\d+)',
                component: () => import('@/views/basic/enumeration/detail/index.vue'),
                permissions: ['basic:enumeration:type:query'],
                name: 'BasicEnumDetail',
                meta: { title: '枚举详情', activeMenu: '/basic/basicEnum', noCache: true }
            },
            // 项目详情沿用列表菜单高亮；禁用缓存，重新进入时加载最新概要与项目版本。
            {
                path: 'project/detail',
                component: () => import('@/views/basic/project/detail/index.vue'),
                permissions: ['basic:project:project:query'],
                name: 'BasicProjectDetail',
                meta: { title: '项目详情', activeMenu: '/basic/project', noCache: true }
            },
            {
                path: 'project/detail/versionDiff',
                hidden: true,
                component: () => import('@/views/basic/projectVersion/versionDiff.vue'),
                name: 'projectDetailVersionDiff',
                // 版本对比不属于项目详情入口，侧栏始终高亮版本管理；禁用缓存，避免对比结果沿用旧版本。
                meta: { title: '版本对比', activeMenu: '/basic/project', noCache: true }
            },
            // 文件详情已改为列表页弹窗只读态，不再占用独立路由。
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
