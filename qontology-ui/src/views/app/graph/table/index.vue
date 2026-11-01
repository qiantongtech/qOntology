<!--
  Copyright (c) 2026 Jiangsu Qiantong Technology Co., Ltd.
   *
  Software Name: qKnow Knowledge Platform (Business Edition)
  Software Copyright Registration No. 15980140
   *
  [RIGHTS AND LICENSE STATEMENT]
  This file contains non-public commercial source code of which Jiangsu Qiantong
  Technology Co., Ltd. lawfully possesses complete intellectual property rights.
   *
  Access and use are limited to entities or individuals who have signed a valid
  commercial license agreement, within the scope stipulated in the agreement.
  The "accessibility" of this source code is premised on lawful authorization
  and does not constitute any form of transfer of intellectual property rights
  or implied licensing.
   *
  [PROHIBITIONS]
  Unless explicitly agreed in the license agreement, the following acts in any
  form are strictly prohibited:
  1. Copying, disseminating, disclosing, selling, renting, or redistributing
  this source code;
  2. Providing the software's functionality to third parties via SaaS, PaaS,
  cloud hosting, or other means;
  3. Using this software or its derivative versions to develop products that
  compete with the Right Holder;
  4. Providing or displaying this source code or related technical information
  to unauthorized third parties;
  5. Tampering with, circumventing, or destroying copyright notices, license
  verifications, or other technical protection measures.
   *
  [LEGAL LIABILITY]
  Any unauthorized use constitutes an infringement of trade secrets and
  intellectual property rights.
   *
  The Right Holder will strictly pursue liability for breach of contract and
  infringement in accordance with the commercial agreement and laws such as
  the "Copyright Law of the People's Republic of China" and the "Anti-Unfair
  Competition Law".
   *
  ============================================================================
   *
  Copyright (c) 2026 江苏千桐科技有限公司
   *
  软件名称：qKnow 智能体构建平台（商业版） | 软著登字第15980140号
   *
  【权利与授权声明】
  本文件属于江苏千桐科技有限公司依法享有完全知识产权的非公开商业源代码。
  仅限已签署有效商业授权合同的单位或个人在约定范围内查阅和使用。
  源代码的“可访问性”均以合法授权为前提，不构成任何形式的知识产权转让或默示授权。
   *
  【禁止事项】
  除授权合同明确约定外，严禁任何形式的：
  1. 复制、传播、披露、出售、出租或再分发本源代码；
  2. 通过 SaaS、PaaS、云托管等方式向第三方提供本软件功能；
  3. 将本软件或其衍生版本用于开发与权利人构成竞争的产品；
  4. 向未授权第三方提供或展示本源代码或相关技术信息；
  5. 篡改、规避或破坏版权标识、授权校验及其他技术保护措施。
   *
  【法律责任】
  任何未经授权的利用行为，均构成对商业秘密及知识产权的侵害。
  权利人将依据商业合同及《中华人民共和国著作权法》《反不正当竞争法》
  等法律法规，严厉追究违约与侵权责任。
-->

<template>
    <div class="tableView" v-loading="loading">
        <el-form
            class="view-form"
            :model="queryParams"
            ref="queryRef"
            :inline="true"
            @submit.prevent
        >
            <div>
                <el-form-item label="主体" prop="startEntityName">
                    <el-input
                        class="el-form-input-width"
                        v-model="queryParams.startEntityName"
                        placeholder="请输入主体名称"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="主体概念" prop="startSchemaName">
                    <el-input
                        class="el-form-input-width"
                        v-model="queryParams.startSchemaName"
                        placeholder="请输入主体概念"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="谓词/关系" prop="relationName">
                    <el-input
                        class="el-form-input-width"
                        v-model="queryParams.relationName"
                        placeholder="请输入谓词/关系"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="客体" prop="endEntityName">
                    <el-input
                        class="el-form-input-width"
                        v-model="queryParams.endEntityName"
                        placeholder="请输入客体名称"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button
                        plain
                        type="primary"
                        @click="handleQuery"
                        @mousedown="(e) => e.preventDefault()"
                    >
                        <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
                    </el-button>
                    <el-button @click="resetQuery" @mousedown="(e) => e.preventDefault()">
                        <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置
                    </el-button>
                    <el-button
                        icon="Sort"
                        @click="toggleExpand"
                        @mousedown="(e) => e.preventDefault()"
                    >
                        <i class="iconfont-mini expandedIcon"></i>
                        {{ isExpanded ? '合并' : '展开' }}
                    </el-button>
                </el-form-item>
            </div>
            <div>
                <el-form-item label="客体概念" prop="endSchemaName" v-if="isExpanded">
                    <el-input
                        class="el-form-input-width"
                        v-model="queryParams.endSchemaName"
                        placeholder="请输入客体概念"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="创建时间" v-if="isExpanded">
                    <el-date-picker
                        style="width: 210px"
                        class="el-form-input-width"
                        v-model="dateRange"
                        value-format="YYYY-MM-DD"
                        type="daterange"
                        range-separator="-"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                    ></el-date-picker>
                </el-form-item>
            </div>
        </el-form>
        <div class="view-main">
            <el-table
                stripe
                :data="modelList"
                @selection-change="handleSelectionChange"
                :default-sort="defaultSort"
                @sort-change="handleSortChange"
            >
                <el-table-column
                    v-if="getColumnVisibility(1)"
                    label="主体"
                    align="center"
                    prop="startName"
                >
                    <template #default="scope">
                        {{ scope.row.startName || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(2)"
                    label="主体概念"
                    align="center"
                    prop="startSchemaName"
                >
                    <template #default="scope">
                        {{ scope.row.startSchemaName || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(3)"
                    label="谓词/关系"
                    align="center"
                    prop="relationType"
                >
                    <template #default="scope">
                        {{ scope.row.relationType }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(4)"
                    label="客体"
                    align="center"
                    prop="endName"
                >
                    <template #default="scope">
                        {{ scope.row.endName || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(5)"
                    label="客体概念"
                    align="center"
                    prop="endSchemaName"
                >
                    <template #default="scope">
                        {{ scope.row.endSchemaName || '-' }}
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="getColumnVisibility(6)"
                    label="创建时间"
                    align="center"
                    prop="createTime"
                    width="180"
                    sortable="custom"
                    :sort-orders="['descending', 'ascending']"
                >
                    <template #default="scope">
                        <span>{{
                            scope.row.createTime
                                ? parseTime(scope.row.createTime, '{y}-{m}-{d}')
                                : '-'
                        }}</span>
                    </template>
                </el-table-column>

                <template #empty>
                    <div class="emptyBg">
                        <img src="@/assets/system/images/no_data/noData.png" alt="" />
                        <p>暂无记录</p>
                    </div>
                </template>
            </el-table>
            <pagination
                v-show="total > 0"
                :total="total"
                v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize"
                @pagination="getList"
            />
        </div>
    </div>
</template>

<script setup name="TableView">
    import { getTableGraph } from '@/api/app/graph/index';

    const { proxy } = getCurrentInstance();

    const modelList = ref([]);

    // 列显隐信息
    const columns = ref([
        { key: 1, label: '主体', visible: true },
        { key: 2, label: '主体概念', visible: true },
        { key: 3, label: '谓词/关系', visible: true },
        { key: 4, label: '客体', visible: true },
        { key: 5, label: '客体概念', visible: true },
        { key: 6, label: '创建时间', visible: true }
    ]);

    const getColumnVisibility = (key) => {
        const column = columns.value.find((col) => col.key === key);
        // 如果没有找到对应列配置，默认显示
        if (!column) return true;
        // 如果找到对应列配置，根据visible属性来控制显示
        return column.visible;
    };
    const loading = ref(false);
    const ids = ref([]);
    const single = ref(true);
    const multiple = ref(true);
    const total = ref(0);
    const dateRange = ref([]);
    const defaultSort = ref({ prop: 'createTime', order: 'desc' });
    const route = useRoute();

    const data = reactive({
        form: {},
        queryParams: {
            pageNum: 1,
            pageSize: 10,
            startEntityName: '',
            endEntityName: '',
            startSchemaName: '',
            relationName: '',
            endName: '',
            endSchemaName: ''
        },
        rules: {
            name: [{ required: true, message: '名称不能为空', trigger: 'blur' }]
        }
    });

    const { queryParams } = toRefs(data);
    const isExpanded = ref(false);
    // 切换展开/合并状态
    function toggleExpand() {
        isExpanded.value = !isExpanded.value;
    }
    /** 查询图谱模型管理列表 */
    function getList() {
        queryParams.value.graphId = route.params.kgId;
        loading.value = true;
        let param = { ...queryParams.value };
        if (dateRange.value && dateRange.value.length > 0) {
            param.beginTime = dateRange.value[0];
            param.endTime = dateRange.value[1];
        }
        getTableGraph(param)
            .then((response) => {
                modelList.value = response.data.relationships;
                total.value = response.data.total;
            })
            .finally(() => {
                loading.value = false;
            });
    }

    /** 搜索按钮操作 */
    function handleQuery() {
        queryParams.value.pageNum = 1;
        getList();
    }

    /** 重置按钮操作 */
    function resetQuery() {
        dateRange.value = [];
        proxy.resetForm('queryRef');
        handleQuery();
    }

    // 多选框选中数据
    function handleSelectionChange(selection) {
        ids.value = selection.map((item) => item.id);
        single.value = selection.length != 1;
        multiple.value = !selection.length;
    }

    /** 排序触发事件 */
    function handleSortChange(column) {
        queryParams.value.orderByColumn = column.prop;
        queryParams.value.isAsc = column.order;
        getList();
    }
    getList();
</script>

<style scoped lang="scss">
    .tableView {
        height: auto;
    }
    .expandedIcon {
        //padding-left: 1px;
    }
</style>
