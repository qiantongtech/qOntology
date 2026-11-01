<!--
 Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

 This file is part of qOntology Intelligent Agent Building Platform (Open Source Edition).

 qOntology is licensed under Apache License 2.0 with additional qOntology terms.
 You may use qOntology for commercial purposes, but you may not remove, hide,
 modify, or replace the qOntology logo, copyright notices, license notices,
 or attribution information without a separate commercial license.

 White-label use, OEM distribution, rebranding, or presenting qOntology as
 another product requires separate commercial authorization from
 Jiangsu Qiantong Technology Co., Ltd.

 Business License: https://community.qknow.ai/business/policy.html
 See the LICENSE file in the project root for full license information.
-->

<!-- 复杂详情路由模板
    {
        path: '/kg/manage',
        component: Layout,
        redirect: 'manage',
        hidden: true,
        children: [
            {
                path: 'tagEntityDetail',
                component: () => import('@/views/kg/manage/detail/index.vue'),
                name: 'tree',
                meta: { title: '实体标签详情', activeMenu: '/kg/tagEntity'  }
            }
        ]
    }
 -->

<template>
    <div class="app-container" ref="app-container">
        <div class="pagecont-top" v-show="showSearch" style="padding-bottom: 15px">
            <div class="infotop">
                <div class="infotop-title mb15">
                    {{ tagEntityDetail.id }}
                </div>
                <el-row :gutter="20">
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">ID</div>
                            <div class="infotop-row-value">{{ tagEntityDetail.id }}</div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">工作区id</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.workspaceId || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">图谱id</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.graphId || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">标签id</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.tagId || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">实体id</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.entityId || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">描述</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.description || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">备注</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.remark || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">创建人</div>
                            <div class="infotop-row-value">
                                {{ tagEntityDetail.createBy || '-' }}
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="infotop-row border-top">
                            <div class="infotop-row-lable">创建时间</div>
                            <div class="infotop-row-value">
                                {{ parseTime(tagEntityDetail.createTime, '{y}-{m}-{d}') }}
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>

        <div class="pagecont-bottom">
            <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
                <el-tab-pane label="组件一" name="1">
                    <component-one></component-one>
                </el-tab-pane>
                <el-tab-pane label="组件二" name="2">
                    <component-two></component-two>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script setup name="TagEntity">
    import { getTagEntity } from '@/api/kg/manage/tagEntity';
    import { useRoute } from 'vue-router';
    // import Method from "@/views/kg/manage/detail/method.vue";
    // import ComponentTwo from "@/views/kg/manage/detail/componentTwo.vue";

    const { proxy } = getCurrentInstance();

    const activeName = ref('1');

    const handleClick = (tab, event) => {
        console.log(tab, event);
    };

    const showSearch = ref(true);
    const route = useRoute();
    let id = route.query.id || 1;
    // 监听 id 变化
    watch(
        () => route.query.id,
        (newId) => {
            id = newId || 1; // 如果 id 为空，使用默认值 1
            getTagEntityDetailById();
        },
        { immediate: true } // `immediate` 为 true 表示页面加载时也会立即执行一次 watch
    );
    const data = reactive({
        tagEntityDetail: {},
        form: {}
    });

    const { tagEntityDetail, rules } = toRefs(data);

    /** 复杂详情页面上方表单查询 */
    function getTagEntityDetailById() {
        const _id = id;
        getTagEntity(_id).then((response) => {
            tagEntityDetail.value = response.data;
        });
    }

    getTagEntityDetailById();
</script>
