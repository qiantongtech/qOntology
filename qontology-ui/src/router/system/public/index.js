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
/* Layout */
import Layout from '@/layout/index.vue';
/* AI模块公共路由 */
import AIPublicRouter from '../../ai/public/index.js';
// 系统模块公共路由
export default [
    {
        path: '/redirect',
        component: Layout,
        hidden: true,
        children: [
            {
                path: '/redirect/:path(.*)',
                component: () => import('@/views/system/redirect/index.vue')
            }
        ]
    },
    {
        path: '/sso',
        component: () => import('@/views/system/sso'),
        hidden: true
    },
    {
        path: '/login',
        component: () => import('@/views/system/login.vue'),
        hidden: true
    },
    {
        path: '/sso/login',
        component: () => import('../../../../ssoLogin/index.vue'),
        hidden: true
    },
    {
        path: '/register',
        component: () => import('@/views/system/register.vue'),
        hidden: true
    },
    {
        path: '/:pathMatch(.*)*',
        component: () => import('@/views/system/error/404.vue'),
        hidden: true
    },
    {
        path: '/401',
        component: () => import('@/views/system/error/401.vue'),
        hidden: true
    },
    {
        path: '/',
        component: Layout,
        hidden: true,
        redirect: '/index',
        children: [
            {
                path: 'index',
                component: () => import('@/views/system/index.vue'),
                name: 'Index',
                meta: { title: '首页', icon: 'dashboard', affix: true }
            }
        ]
    },
    // 兼容原有首页地址，统一进入固定的首页标签。
    {
        path: '/dashboard',
        redirect: '/index',
        hidden: true
    },
    {
        path: '/bases',
        component: Layout,
        redirect: 'message',
        children: [
            {
                path: 'message',
                component: () => import('@/views/system/system/message/index.vue'),
                name: 'Message',
                meta: { title: '我的消息', icon: 'message' },
                hidden: true
            }
        ]
    },
    {
        path: '/user',
        component: Layout,
        hidden: true,
        redirect: 'noredirect',
        children: [
            {
                path: 'profile',
                component: () => import('@/views/system/system/user/profile/index.vue'),
                name: 'Profile',
                meta: { title: '个人中心', icon: 'user' }
            }
        ]
    },
    {
        path: '/system/notice/detail',
        component: Layout,
        hidden: true,
        children: [
            {
                path: '',
                component: () => import('@/views/system/system/notice/detail.vue'),
                name: 'noticeDetail',
                meta: {
                    title: '公告详情',
                    noCache: true,
                    activeMenu: '/system/notice',
                    breadcrumbParent: [{ title: '通知公告', path: '/system/notice' }]
                }
            }
        ]
    },
       {
        path: '/system',
        component: Layout,
        redirect: 'ai',
        // hidden: true,
        children: [...AIPublicRouter]
    }
];
