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

<template>
    <el-dialog
        title="知识标签管理"
        v-model="isShow"
        width="1000px"
        draggable
        :append-to-body="true"
        :before-close="handleBeforeClose"
    >
        <template>
            <span role="heading" aria-level="2" class="el-dialog__title"> 知识标签管理 </span>
        </template>

        <el-form
            class="btn-style"
            :model="queryParams"
            ref="queryRef"
            :inline="true"
            label-width="68px"
            @submit.prevent
        >
            <el-form-item label="名称" prop="name">
                <el-input
                    class="el-form-input-width"
                    v-model="queryParams.name"
                    placeholder="请输入名称"
                    clearable
                    @keyup.enter="handleQuery"
                />
            </el-form-item>
            <el-form-item label="标签分类" prop="category">
                <el-select
                    v-model="queryParams.category"
                    class="el-form-input-width"
                    filterable
                    placeholder="请选择标签分类"
                    clearable
                >
                    <el-option
                        v-for="item in kg_tag_category"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value"
                    />
                </el-select>
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
            </el-form-item>
        </el-form>

        <el-table
            ref="tagTableRef"
            stripe
            v-loading="loading"
            :data="tagList"
            @selection-change="handleSelectionChange"
            :default-sort="defaultSort"
            @sort-change="handleSortChange"
            row-key="id"
        >
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column v-if="getColumnVisibility(1)" label="名称" align="center" prop="name">
                <template #default="scope">{{ scope.row.name || '-' }}</template>
            </el-table-column>
            <el-table-column
                v-if="getColumnVisibility(3)"
                label="分类名称"
                align="center"
                prop="category"
            >
                <template #default="scope">{{ getCategoryLabel(scope.row.category) }}</template>
            </el-table-column>
            <el-table-column
                v-if="getColumnVisibility(4)"
                label="描述"
                align="center"
                prop="description"
            >
                <template #default="scope">{{ scope.row.description || '-' }}</template>
            </el-table-column>
            <el-table-column
                v-if="getColumnVisibility(5)"
                label="备注"
                align="center"
                prop="remark"
            >
                <template #default="scope">{{ scope.row.remark || '-' }}</template>
            </el-table-column>
            <el-table-column
                v-if="getColumnVisibility(8)"
                label="创建人"
                align="center"
                prop="createBy"
            >
                <template #default="scope">{{ scope.row.createBy || '-' }}</template>
            </el-table-column>
            <el-table-column
                v-if="getColumnVisibility(9)"
                label="创建时间"
                align="center"
                prop="createTime"
                width="180"
                sortable="custom"
                :sort-orders="['descending', 'ascending']"
            >
                <template #default="scope">{{
                    parseTime(scope.row.createTime, '{y}-{m}-{d}')
                }}</template>
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

        <template #footer>
            <div class="dialog-footer">
                <el-button size="mini" @click="handleBeforeClose">取 消</el-button>
                <el-button type="primary" size="mini" @click="handleConfirm">确 定</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup name="TagModal">
    import { ref, reactive, toRefs, watch, getCurrentInstance, nextTick } from 'vue';
    import { listTag, getTag, delTag, addTag, updateTag } from '@/api/kg/manage/tag.js';
    import { getToken } from '@/utils/auth.js';
    import { useRouter } from 'vue-router';
    import { defineProps, defineEmits, defineExpose } from 'vue';
    // 1. 接收父组件参数
    const props = defineProps({
        visible: {
            type: Boolean,
            default: false
        },
        checkdTags: {
            type: Array, // 数组类型
            default: () => []
        },
        initQuery: {
            // 可选：父组件传递的初始查询条件
            type: Object,
            default: () => ({})
        }
    });

    // 2. 向父组件传递事件
    const emit = defineEmits([
        'update:visible', // 双向绑定更新显示状态
        'close', // 弹窗关闭事件
        'operateSuccess', // 操作成功事件（新增/修改/删除后）
        'confirm' // 确定按钮回调
    ]);

    // 3. 基础实例与路由
    const { proxy } = getCurrentInstance();
    const router = useRouter();
    const { kmc_know_valid, kg_tag_category } = proxy.useDict('kmc_know_valid', 'kg_tag_category');
    const tagTableRef = ref(null); // 表格ref
    const isShow = ref(props.visible); // 弹窗显示状态（内部维护）
    const tagList = ref([]); // 标签列表数据
    const tagCategoryList = ref([]); // 分类列表数据
    const columns = ref([
        // 表格列显隐配置（保留原有功能）
        { key: 1, label: '名称', visible: true },
        { key: 3, label: '分类名称', visible: true },
        { key: 4, label: '描述', visible: true },
        { key: 5, label: '备注', visible: true },
        { key: 8, label: '创建人', visible: true },
        { key: 9, label: '创建时间', visible: true }
    ]);

    // 弹窗状态
    const open = ref(false); // 添加/修改弹窗
    const openDetail = ref(false); // 详情弹窗（补充定义，避免报错）
    const loading = ref(false); // 加载状态
    const showSearch = ref(true); // 搜索栏显隐（保留，兼容原有功能）
    const ids = ref([]); // 选中的ID集合
    const single = ref(true); // 是否单选
    const multiple = ref(true); // 是否多选
    const total = ref(0); // 总条数
    const title = ref(''); // 弹窗标题
    const defaultSort = ref({ prop: 'createTime', order: 'desc' }); // 默认排序

    // 表单与查询参数
    const data = reactive({
        form: {
            // 表单数据
            id: null,
            name: null,
            categoryId: null,
            category: null,
            description: null,
            remark: null,
            validFlag: null,
            delFlag: null,
            createBy: null,
            createTime: null,
            updateBy: null,
            updateTime: null
        },
        queryParams: {
            // 查询参数
            pageNum: 1,
            pageSize: 10,
            name: null,
            categoryId: null,
            category: null,
            description: null,
            createTime: null
        },
        rules: {} // 表单验证规则
    });
    const { queryParams, form, rules } = toRefs(data);

    // 监听父组件visible变化
    watch(
        () => props.visible,
        (val) => {
            isShow.value = val;
            if (val) {
                Object.assign(queryParams.value, props.initQuery);
                getList();
            }
        },
        { immediate: true }
    );

    // 监听弹窗关闭状态
    watch(
        () => isShow.value,
        (val) => {
            if (!val) {
                emit('update:visible', false);
                emit('close');
            }
        }
    );

    // 子组件 script 中新增 watch
    // 修改 checkdTags 监听
    watch(
        () => props.checkdTags,
        (newVal) => {
            if (isShow.value && newVal?.length) {
                // 延长延迟至 200ms，确保第二次进入时表格数据加载完成
                setTimeout(() => {
                    refreshCheckdTags(newVal);
                }, 200);
            }
        },
        { deep: true } // 移除 immediate: true
    );

    // 表格列显隐控制
    const getColumnVisibility = (key) => {
        const column = columns.value.find((col) => col.key === key);
        return column ? column.visible : true;
    };

    // 核心业务逻辑
    // 查询标签列表
    function getList() {
        loading.value = true;
        listTag(queryParams.value)
            .then((response) => {
                tagList.value = response.data.rows;
                total.value = response.data.total;
                loading.value = false;
            })
            .catch(() => {
                loading.value = false;
            });
    }

    // 搜索
    function handleQuery() {
        queryParams.value.pageNum = 1;
        getList();
    }

    // 重置搜索
    function resetQuery() {
        proxy.resetForm('queryRef');
        handleQuery();
    }

    // 选择项变化
    function handleSelectionChange(selection) {
        ids.value = selection.map((item) => item.id);
        single.value = selection.length !== 1;
        multiple.value = !selection.length;
    }

    // 排序变化
    function handleSortChange(column) {
        queryParams.value.orderByColumn = column.prop;
        queryParams.value.isAsc = column.order;
        getList();
    }

    // 取消/关闭弹窗
    function handleBeforeClose() {
        isShow.value = false;
        // 清空表格选择、选中ID集合、确保下次进入重新勾选
        if (tagTableRef.value) {
            tagTableRef.value.clearSelection();
        }
        ids.value = []; // 重置选中ID集合
        emit('update:visible', false);
        emit('close');
    }
    // 确定按钮回调
    function handleConfirm() {
        if (ids.value.length === 0) {
            proxy.$message.warning('请选择标签');
            return;
        }
        emit('confirm', ids.value); // 向父组件传递选中的ID集合
        isShow.value = false;
        tagTableRef.value.clearSelection(); // 清空选择
    }

    // 取消/关闭弹窗（兼容原有cancel方法）
    function cancel() {
        open.value = false;
        openDetail.value = false;
        resetForm();
    }

    // 重置表单
    function resetForm() {
        form.value = {
            id: null,
            name: null,
            categoryId: null,
            category: null,
            description: null,
            remark: null
        };
        proxy.resetForm('tagRef');
    }

    // 映射分类名称
    const getCategoryLabel = (value) => {
        // 如果数组不存在或 value 为空，直接返回默认值
        if (!kg_tag_category || !value) return '-';

        // 遍历数组，找到 value 匹配的对象
        const item = kg_tag_category.value.find((item) => item.value === value);

        // 找到则返回 label，否则返回默认值
        return item ? item.label : '-';
    };

    // 暴露方法给父组件调用
    defineExpose({
        refreshCheckdTags: async (newCheckdTags) => {
            console.log('newCheckdTags-----123', newCheckdTags);
            if (!newCheckdTags?.length || !tagTableRef.value) return;

            // 等待表格数据加载完成（关键：确保 tagList 已更新）
            await new Promise((resolve) => {
                const checkDataLoaded = () => {
                    if (tagList.value.length > 0) {
                        resolve();
                    } else {
                        setTimeout(checkDataLoaded, 50); // 每 50ms 检查一次
                    }
                };
                checkDataLoaded();
            });

            // 1. 先清空已有选择
            tagTableRef.value.clearSelection();

            // 2. 提取需要勾选的 id 集合（转为数字类型，避免类型不匹配）
            const targetIds = newCheckdTags.map((tag) => Number(tag.id));

            // 3. 等待表格 DOM 完全渲染
            await nextTick();

            // 4. 遍历表格数据，精准匹配并勾选
            tagList.value.forEach((row) => {
                if (targetIds.includes(Number(row.id))) {
                    tagTableRef.value.toggleRowSelection(row, true);
                    console.log('勾选成功：', row.id, row.name);
                }
            });
        },
        refreshList: getList, // 刷新列表
        resetQuery: resetQuery, // 重置查询
        clearSelection: () => tagTableRef.value?.clearSelection() // 新增清空选择方法
    });
</script>

<style scoped>
    .app-container {
        padding: 10px 0;
    }

    .btn-style {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 15px;
    }

    .el-form-input-width {
        width: 240px;
    }

    .emptyBg {
        text-align: center;
        padding: 30px 0;
    }

    .emptyBg img {
        width: 120px;
        height: 120px;
        margin-bottom: 10px;
        opacity: 0.6;
    }

    .emptyBg p {
        color: #999;
        font-size: 14px;
    }

    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        padding-top: 15px;
    }

    /* 表格操作列样式 */
    :deep(.el-table .fixed-width) {
        width: 240px !important;
    }

    :deep(.el-link) {
        margin: 0 5px;
    }

    :deep(.el-dialog__body) {
        padding: 20px;
    }
</style>
