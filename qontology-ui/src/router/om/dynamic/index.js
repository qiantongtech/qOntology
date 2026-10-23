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

import Layout from '@/layout/index.vue';

// 本体建模模块动态路由，基于用户权限动态加载。
// 详情父路径必须包含 /detail，避免与服务端下发的列表路由重名并匹配到空 Layout。
export default [
    {
        path: '/om/:versionId/ontology/objectRelation/detail',
        component: Layout,
        hidden: true,
        permissions: ['om:object:relation:query'],
        children: [
            {
                path: ':id(\\d+)',
                component: () => import('@/views/om/om/object/relation/detail/index.vue'),
                name: 'OmObjectTypeRelationDetail',
                meta: { title: '对象关系详情', activeMenu: '/om/:versionId/ontology/objectRelation', noCache: true }
            }
        ]
    },
    {
        path: '/om/:versionId/ontology/objectType/detail',
        component: Layout,
        hidden: true,
        permissions: ['om:object:type:query'],
        children: [
            {
                path: ':id(\\d+)',
                component: () => import('@/views/om/om/object/type/detail/index.vue'),
                name: 'OmObjectTypeDetail',
                meta: { title: '对象类型详情', activeMenu: '/om/:versionId/ontology/objectType', noCache: true },
                beforeEnter(to) {
                    // 清理旧链接携带的返回地址，列表归属由 activeMenu 统一确定。
                    if ('from' in to.query) {
                        const query = { ...to.query };
                        delete query.from;
                        return { name: 'OmObjectTypeDetail', params: to.params, query, replace: true };
                    }
                }
            }
        ]
    },
    {
        path: '/om/:versionId/object-type/detail/:id(\\d+)',
        hidden: true,
        permissions: ['om:object:type:query'],
        redirect(to) {
            const query = { ...to.query };
            delete query.from;
            return { name: 'OmObjectTypeDetail', params: { id: to.params.id, versionId: to.params.versionId }, query };
        }
    },
    {
        path: '/om/:versionId/semantic/enum/data',
        component: Layout,
        hidden: true,
        permissions: ['om:enumeration:data:list'],
        children: [
            {
                path: ':enumId(\\d+)',
                component: () => import('@/views/om/semantic/enum/data/index.vue'),
                name: 'OmEnumData',
                meta: { title: '枚举数据', activeMenu: '/om/:versionId/semantic/enum' }
            }
        ]
    }
];
