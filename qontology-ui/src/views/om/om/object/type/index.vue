<!--
  Copyright © 2025-present Jiangsu Qiantong Technology Co., Ltd.

  This file is part of qOntology Ontology Engineering Platform (Open Source Edition).

  qOntology is licensed under Apache License 2.0 with additional qOntology terms.
  You may use qOntology for commercial purposes, but you may not remove, hide,
  modify, or replace the qOntology logo, copyright notices, license notices,
  or attribution information without a separate commercial license.

  White-label use, OEM distribution, rebranding, or presenting qOntology as
  another product requires separate commercial authorization from
  Jiangsu Qiantong Technology Co., Ltd.

  Business License: https://community.qontology.ai/business/policy.html
  See the LICENSE file in the project root for full license information.
-->

<template>
    <div class="app-container object-type-page" ref="app-container">
        <GuideTip tip-id="om/object/type.list" />
        <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
            <template #search>
                <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef" />
            </template>

            <template #actions-data>
                <el-button
                    v-hasPermi="['om:object:type:add']"
                    type="primary"
                    plain
                    @click="handleAdd()"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <i class="iconfont-mini icon-xinzeng mr5"></i>新增
                </el-button>
                <el-button
                    class="toggle-expand-all"
                    type="primary"
                    plain
                    @click="toggleExpandAll"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <svg-icon v-if="tableStore.config.table.defaultExpandAll" icon-class="toggle" />
                    <svg-icon v-else icon-class="expand" />
                    <span>{{ tableStore.config.table.defaultExpandAll ? '折叠' : '展开' }}</span>
                </el-button>
            </template>

            <qt-table v-bind="tableStore" ref="tableRef">
                <!-- 纯文本列沿用统一 text 插槽渲染。 -->
                <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
                <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
                <template #creator="{ row }">
                    <div class="flex-column overflow-hidden">
                        <span class="text-ellipsis" :title="row.createBy || ''">{{ row.createBy || '-' }}</span>
                        <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
                    </div>
                </template>
                <template #handle="{ row }">
                    <el-button
                        v-hasPermi="['om:object:type:query']"
                        link
                        type="primary"
                        icon="View"
                        @click="handleDetail(row)"
                        >详情</el-button
                    >
                    <el-button
                        v-hasPermi="['om:object:type:add']"
                        link
                        type="primary"
                        icon="Plus"
                        @click="handleAdd(row)"
                        >新增</el-button
                    >
                    <el-popover
                        v-if="auth.hasPermiOr(['om:object:type:edit', 'om:object:type:remove'])"
                        placement="bottom"
                        :width="107"
                        popper-class="handle-popover"
                        trigger="click"
                    >
                        <template #reference>
                            <el-button link type="primary" icon="ArrowDown">更多</el-button>
                        </template>
                        <el-button
                            v-if="auth.hasPermi('om:object:type:edit')"
                            link
                            type="primary"
                            icon="Edit"
                            @click="handleMoreCommand('edit', row)"
                            >修改</el-button
                        >
                        <el-button
                            v-if="auth.hasPermi('om:object:type:remove')"
                            link
                            type="danger"
                            icon="Delete"
                            :disabled="deleting"
                            @click="handleMoreCommand('delete', row)"
                            >删除</el-button
                        >
                    </el-popover>
                </template>
            </qt-table>
        </qt-wrap>
        <el-dialog
            v-model="open"
            :title="dialogTitle"
            width="1100px"
            :append-to="$refs['app-container']"
            :close-on-press-escape="!saving"
            :before-close="beforeClose"
            draggable
            destroy-on-close
            @closed="typeRef?.clearValidate()"
        >
            <el-form ref="typeRef" :model="form" :rules="rules" label-width="120px" @submit.prevent="submitForm">
                <el-row :gutter="20">
                    <el-col v-if="!editingRoot" :span="24">
                        <el-form-item label="上级对象类型" prop="parentId">
                            <el-tree-select
                                v-model="form.parentId"
                                :data="parentOptions"
                                :loading="parentOptionsLoading"
                                :props="{ label: 'name', value: 'id', children: 'children' }"
                                value-key="id"
                                placeholder="请选择上级对象类型，留空则为顶级对象类型"
                                check-strictly
                                clearable
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="对象类型名称" prop="name">
                            <div class="name-input">
                                <el-input v-model="form.name" placeholder="请输入对象类型名称" maxlength="128" />
                                <el-button v-hasPermi="['om:term:term:list']" type="primary" @click="openTermPicker">
                                    从术语表中选择
                                </el-button>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="显示顺序" prop="orderNum">
                            <el-input-number
                                v-model="form.orderNum"
                                :min="0"
                                :max="2147483647"
                                :precision="0"
                                controls-position="right"
                                class="form-control-full"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="描述" prop="description">
                            <el-input
                                v-model="form.description"
                                class="character-count"
                                type="textarea"
                                :rows="4"
                                maxlength="512"
                                show-word-limit
                                placeholder="请输入对象类型描述"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="关联建模目标" prop="targetIds">
                            <el-button type="primary" plain :disabled="saving" @click="targetPickerOpen = true">
                                导入建模目标
                            </el-button>
                        </el-form-item>
                        <div class="target-table">
                            <TargetTable
                                :targets="selectedTargets"
                                editable
                                :disabled="saving"
                                @remove="removeTarget"
                            />
                        </div>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button :disabled="saving" @click="open = false">取 消</el-button>
                    <el-button
                        type="primary"
                        :loading="saving"
                        :disabled="parentOptionsLoading"
                        @click="submitForm"
                    >确 定</el-button>
                </div>
            </template>
        </el-dialog>
        <TargetPicker
            v-model="targetPickerOpen"
            :selected-targets="selectedTargets"
            :version-id="versionId"
            :append-to="$refs['app-container']"
            @confirm="addTargets"
        />
        <el-dialog
            v-model="termOpen"
            title="从术语表中选择"
            width="1000px"
            :append-to="$refs['app-container']"
            draggable
        >
            <el-form class="btn-style" :inline="true" :model="termQuery" @submit.prevent="searchTerms">
                <el-form-item label="标准名称">
                    <el-input
                        v-model="termQuery.stdName"
                        class="el-form-input-width"
                        placeholder="请输入标准名称"
                        clearable
                        @keyup.enter="searchTerms"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" plain @click="searchTerms"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
                    </el-button>
                    <el-button @click="resetTerms"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
                </el-form-item>
            </el-form>
            <el-table
                ref="termTableRef"
                v-loading="termLoading"
                :data="terms"
                row-key="id"
                highlight-current-row
                stripe
                @current-change="selectedTerm = $event"
                @row-click="selectTerm"
            >
                <el-table-column type="index" label="序号" width="65" align="center" />
                <el-table-column
                    v-for="field in termFields"
                    :key="field.prop"
                    :prop="field.prop"
                    :label="field.label"
                    :min-width="field.width"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                />
            </el-table>
            <pagination
                v-show="termTotal > 0"
                :total="termTotal"
                v-model:page="termQuery.pageNum"
                v-model:limit="termQuery.pageSize"
                @pagination="getTerms"
            />
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="termOpen = false">取 消</el-button>
                    <el-button type="primary" :disabled="!selectedTerm || termLoading" @click="applyTerm">确 定
                    </el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { getCurrentInstance, nextTick, reactive, ref, watch, onBeforeUnmount } from 'vue';
    import { useRouter } from 'vue-router';
    import { useOntologyVersion } from '@/views/om/useOntologyVersion';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import {
        listType,
        listTypeExcludeChild,
        getType,
        addType,
        updateType,
        delType,
        listTypeTargets
    } from '@/api/om/object/type';
    import { listTerm } from '@/api/om/term/term';
    import auth from '@/plugins/auth';
    import { useCreatorPhones } from '@/utils/useCreatorPhones';
    import TargetPicker from './TargetPicker.vue';
    import TargetTable from './TargetTable.vue';

    defineOptions({ name: 'OmObjectType' });
    // 项目版本优先从 URL 获取，嵌入使用时兼容上层传入
    const props = defineProps({ versionId: { type: [Number, String], default: undefined } });
    const router = useRouter();
    const { proxy } = getCurrentInstance();
    const { versionId, requireVersion } = useOntologyVersion(() => props.versionId);
    const tableRef = ref();
    const typeRef = ref();
    const termTableRef = ref();
    const saving = ref(false);
    const deleting = ref(false);
    const open = ref(false);
    const termOpen = ref(false);
    const targetPickerOpen = ref(false);
    const selectedTargets = ref([]);
    const dialogTitle = ref('新增对象类型');
    const { creatorPhones } = useCreatorPhones();

    /**
     * 查询对象类型列表；版本号缺失时返回空列表，避免无版本上下文发起无效查询。
     * @returns {Promise<Object>} 列表接口返回体
     */
    function fetchObjectTypes() {
        if (!versionId.value) {
            return Promise.resolve({ data: [] });
        }
        return listType({ workspaceId: 1001, versionId: versionId.value, pageNum: 1, pageSize: -1 });
    }

    // 树表配置：不分页、行键与展开属性统一交给 QtTable 透传给 el-table。
    const tableStore = reactive({
        config: {
            notPagination: true,
            table: {
                stripe: true,
                rowKey: 'id',
                defaultExpandAll: true,
                treeProps: { children: 'children', hasChildren: 'hasChildren' }
            }
        },
        columns: [
            { label: '对象类型名称', prop: 'name', slot: 'text', minWidth: 250, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '描述', prop: 'description', slot: 'text', minWidth: 260, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '显示顺序', prop: 'orderNum', slot: 'text', width: 100, minWidth: 100 },
            { label: '属性数量', prop: 'attributeCount', slot: 'text', width: 90, minWidth: 90 },
            { label: '关系数量', prop: 'relationCount', slot: 'text', width: 90, minWidth: 90 },
            { label: '创建人', prop: 'createBy', slot: 'creator', width: 130, minWidth: 130, align: 'left', headerAlign: 'left' },
            { label: '创建时间', prop: 'createTime', date: true, width: 160, minWidth: 160, align: 'left' },
            { label: '操作', slot: 'handle', width: 170, minWidth: 170, fixed: 'right' }
        ],
        func: fetchObjectTypes,
        events: {
            // 平铺数据先构树，再按名称关键字前端过滤（保留父链）。
            formatData: (data, params) => filterTree(buildTree(data || []), (params.name || '').trim())
        },
        params: {
            name: null
        }
    });

    // 搜索项配置
    const searchStore = reactive({
        items: [{ label: '对象名称', prop: 'name', component: { is: 'input' } }]
    });
    const emptyForm = () => ({
        id: undefined,
        workspaceId: 1001,
        versionId: versionId.value,
        name: '',
        description: '',
        parentId: null,
        orderNum: 0,
        targetIds: []
    });
    const form = ref(emptyForm());
    // 以打开弹窗时的节点层级判断，清空普通节点父级不会立即隐藏控件
    const editingRoot = ref(false);
    const rules = {
        targetIds: [{ type: 'array', required: true, min: 1, message: '请至少关联一个建模目标', trigger: 'change' }],
        orderNum: [{ required: true, message: '显示顺序不能为空', trigger: 'blur' }],
        name: [
            { required: true, whitespace: true, message: '对象类型名称不能为空', trigger: 'blur' },
            {
                max: 128,
                message: '名称不能超过128个字符',
                trigger: 'blur'
            }
        ],
        description: [
            {
                max: 512,
                message: '描述不能超过512个字符',
                trigger: 'blur'
            }
        ]
    };

    /**
     * 将平铺数据构造成树结构。
     * @param {Array} items 平铺的对象类型列表
     * @returns {Array} 根节点列表
     */
    function buildTree(items) {
        const nodes = new Map(items.map((item) => [String(item.id), { ...item, children: [] }]));
        const roots = [];
        for (const node of nodes.values()) {
            const parent = nodes.get(String(node.parentId));
            if (parent && parent !== node) parent.children.push(node);
            else roots.push(node);
        }
        return roots;
    }

    /**
     * 按名称关键字过滤树节点，命中节点的子树不再下钻，未命中节点保留父链。
     * @param {Array} nodes 待过滤的树节点
     * @param {string} keyword 名称关键字
     * @returns {Array} 过滤后的树节点
     */
    function filterTree(nodes, keyword) {
        if (!keyword) return nodes;
        return nodes.flatMap((node) => {
            if (node.name?.toLowerCase().includes(keyword)) return [node];
            const children = filterTree(node.children, keyword);
            return children.length ? [{ ...node, children }] : [];
        });
    }

    const parentOptions = ref([]);
    const parentOptionsLoading = ref(false);
    let contextRequest = 0;
    let editRequest = 0;

    /**
     * 展开或折叠全部树节点。
     * 依赖 QtTable 重建表格，让 el-table 重新读取 defaultExpandAll。
     */
    function toggleExpandAll() {
        tableStore.config.table.defaultExpandAll = !tableStore.config.table.defaultExpandAll;
        tableRef.value?.reload();
    }

    /** 刷新当前页列表。 */
    function refreshList() {
        tableRef.value?.getList();
    }

    /**
     * 新增按钮操作。
     * @param {Object} [parent] 当前行数据，传入时作为上级对象类型
     */
    async function handleAdd(parent) {
        if (!requireVersion()) return;
        const request = ++editRequest;
        form.value = emptyForm();
        selectedTargets.value = [];
        targetPickerOpen.value = false;
        editingRoot.value = false;
        parentOptions.value = [];
        if (parent) {
            form.value.parentId = parent.id;
        }
        dialogTitle.value = '新增对象类型';
        parentOptionsLoading.value = true;
        open.value = true;
        nextTick(() => typeRef.value?.clearValidate());
        try {
            const response = await listType({ workspaceId: 1001, versionId: versionId.value, pageNum: 1, pageSize: -1 });
            if (request !== editRequest || !open.value) return;
            parentOptions.value = proxy.handleTree(response.data.rows || [], 'id');
        } finally {
            if (request === editRequest) parentOptionsLoading.value = false;
        }
    }

    /**
     * 修改按钮操作。
     * @param {Object} row 当前行数据
     */
    async function handleUpdate(row) {
        const request = ++editRequest;
        form.value = emptyForm();
        selectedTargets.value = [];
        targetPickerOpen.value = false;
        parentOptions.value = [];
        const [response, targetsResponse] = await Promise.all([getType(row.id), listTypeTargets(row.id)]);
        if (request !== editRequest) return;
        editingRoot.value = response.data.parentId == null || String(response.data.parentId) === '0';
        if (!editingRoot.value) {
            const optionsResponse = await listTypeExcludeChild(row.id);
            if (request !== editRequest) return;
            parentOptions.value = proxy.handleTree(optionsResponse.data || [], 'id');
        }
        form.value = {
            ...emptyForm(),
            ...response.data,
            parentId: editingRoot.value ? 0 : response.data.parentId
        };
        selectedTargets.value = targetsResponse.data || [];
        form.value.targetIds = selectedTargets.value.map((target) => String(target.id));
        dialogTitle.value = '修改对象类型';
        open.value = true;
        nextTick(() => typeRef.value?.clearValidate());
    }

    /**
     * 详情按钮操作。
     * @param {Object} row 当前行数据
     */
    function handleDetail(row) {
        router.push({
            name: 'OmObjectTypeDetail',
            params: { id: String(row.id), versionId: versionId.value }
        });
    }

    /** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
    async function submitForm() {
        if (!requireVersion()) return;
        const submittingVersion = versionId.value;
        if (saving.value || parentOptionsLoading.value) return;
        if (
            !(await typeRef.value.validate().catch(() => false)) ||
            saving.value ||
            parentOptionsLoading.value ||
            submittingVersion !== versionId.value ||
            !open.value
        )
            return;
        saving.value = true;
        try {
            const data = {
                id: form.value.id,
                workspaceId: 1001,
                versionId: submittingVersion,
                orderNum: form.value.orderNum,
                targetIds: selectedTargets.value.map((target) => String(target.id)),
                parentId:
                    editingRoot.value || form.value.parentId == null || form.value.parentId === ''
                        ? 0
                        : form.value.parentId,
                name: form.value.name.trim(),
                description: (form.value.description ?? '').trim()
            };
            await (data.id ? updateType(data) : addType(data));
            if (submittingVersion !== versionId.value) return;
            ElMessage.success(data.id ? '修改成功' : '新增成功');
            open.value = false;
            refreshList();
        } finally {
            saving.value = false;
        }
    }

    /**
     * 合并选入的建模目标，按编号去重。
     * @param {Array} targets 本次选入的目标列表
     */
    function addTargets(targets) {
        if (saving.value) return;
        const merged = new Map(selectedTargets.value.map((target) => [String(target.id), target]));
        targets.forEach((target) => merged.set(String(target.id), target));
        selectedTargets.value = Array.from(merged.values());
        form.value.targetIds = selectedTargets.value.map((target) => String(target.id));
        nextTick(() => typeRef.value?.validateField('targetIds'));
    }

    /**
     * 移除已选建模目标。
     * @param {Object} target 待移除的目标
     */
    function removeTarget(target) {
        if (saving.value) return;
        selectedTargets.value = selectedTargets.value.filter((item) => String(item.id) !== String(target.id));
        form.value.targetIds = selectedTargets.value.map((item) => String(item.id));
        nextTick(() => typeRef.value?.validateField('targetIds').catch(() => {}));
    }

    /**
     * 保存期间拦截弹窗关闭回调。
     * @param {Function} done el-dialog 的 before-close 完成回调
     */
    function beforeClose(done) {
        if (!saving.value) done();
    }

    /**
     * 行内「更多」下拉操作。
     * @param {string} command 操作类型：edit 修改，delete 删除
     * @param {Object} row 当前行数据
     */
    function handleMoreCommand(command, row) {
        if (command === 'edit' && auth.hasPermi('om:object:type:edit')) return handleUpdate(row);
        if (command === 'delete' && !deleting.value && auth.hasPermi('om:object:type:remove')) return handleDelete(row);
    }

    /**
     * 删除对象类型。
     * @param {Object} row 当前行数据
     */
    async function handleDelete(row) {
        const context = contextRequest;
        const deletingVersion = versionId.value;
        if (deleting.value || row?.id == null) return;
        try {
            await ElMessageBox.confirm(`是否删除对象类型“${row.name}”？`, '删除确认', { type: 'warning' });
        } catch {
            return;
        }
        if (deleting.value || context !== contextRequest || deletingVersion !== versionId.value) return;
        deleting.value = true;
        try {
            await delType(row.id);
            if (context !== contextRequest || deletingVersion !== versionId.value) return;
            ElMessage.success('删除成功');
            refreshList();
        } finally {
            deleting.value = false;
        }
    }

    const termQuery = reactive({ stdName: '', pageNum: 1, pageSize: 10 });
    const terms = ref([]);
    const termTotal = ref(0);
    const termLoading = ref(false);
    const selectedTerm = ref();
    const termFields = [
        { prop: 'stdName', label: '标准名称', width: 130 },
        {
            prop: 'enName',
            label: '英文名称',
            width: 130
        },
        { prop: 'alias', label: '别名', width: 140 },
        { prop: 'abbr', label: '缩写', width: 100 },
        {
            prop: 'source',
            label: '来源',
            width: 140
        },
        { prop: 'description', label: '描述', width: 200 }
    ];
    let termRequest = 0;

    /** 打开术语选择弹窗并加载首页术语。 */
    async function openTermPicker() {
        termOpen.value = true;
        resetTerms();
    }

    /** 分页查询术语列表。 */
    async function getTerms() {
        const request = ++termRequest;
        termLoading.value = true;
        selectedTerm.value = undefined;
        try {
            const response = await listTerm({ ...termQuery, workspaceId: 1001, versionId: versionId.value });
            if (request !== termRequest) return;
            terms.value = response.data.rows || [];
            termTotal.value = response.data.total || 0;
            await nextTick();
            termTableRef.value?.setCurrentRow();
        } finally {
            if (request === termRequest) termLoading.value = false;
        }
    }

    /**
     * 高亮选中的术语行。
     * @param {Object} row 当前行数据
     */
    function selectTerm(row) {
        selectedTerm.value = row;
        termTableRef.value?.setCurrentRow(row);
    }

    /** 术语查询回到首页。 */
    function searchTerms() {
        termQuery.pageNum = 1;
        getTerms();
    }

    /** 清空术语查询条件。 */
    function resetTerms() {
        termQuery.stdName = '';
        searchTerms();
    }

    /** 将选中术语的名称回填到对象类型名称，超长时提示手输。 */
    function applyTerm() {
        if (!selectedTerm.value) return;
        if ((selectedTerm.value.stdName || '').length > 128) {
            ElMessage.warning('术语名称超过128个字符，请手动输入对象类型名称');
            return;
        }
        form.value.name = selectedTerm.value.stdName;
        termOpen.value = false;
        nextTick(() => typeRef.value?.clearValidate());
    }

    // 版本切换后清空筛选并重置列表；首查由 QtTable 挂载时自动执行。
    watch(versionId, async () => {
        ++contextRequest;
        ++editRequest;
        parentOptionsLoading.value = false;
        open.value = false;
        termOpen.value = false;
        targetPickerOpen.value = false;
        tableStore.params.name = null;
        await nextTick();
        tableRef.value?.resetQuery();
    });
    watch(open, (value) => {
        if (!value) {
            ++editRequest;
            parentOptionsLoading.value = false;
            targetPickerOpen.value = false;
        }
    });
    onBeforeUnmount(() => {
        ++contextRequest;
        ++editRequest;
    });
</script>

<style scoped lang="scss">
    /* 表单控件撑满所在列宽，避免行内 style。 */
    .form-control-full {
        width: 100%;
    }

    .toggle-expand-all {
        min-width: 60px;
        height: 30px;
        padding: 0 12px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        line-height: 1;
        --el-button-text-color: #2666fb;
        --el-button-border-color: #7aa7ff;

        :deep(.svg-icon) {
            width: 12px;
            height: 12px;
            margin-right: 6px;
            color: currentColor;
            fill: currentColor;
            vertical-align: middle;
        }

        :deep(> span) {
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        span {
            line-height: 1;
        }
    }

    .name-input {
        display: flex;
        gap: 12px;
        width: 100%;
    }

    .target-table {
        margin: 0 0 10px 120px;
    }

    .object-type-page :deep(.el-dialog) {
        max-width: calc(100vw - 32px);
    }

    .character-count :deep(.el-input__count)::after {
        content: ' 个字符';
    }

    .character-count :deep(.el-textarea__inner) {
        padding-bottom: 24px;
    }

    @media (max-width: 600px) {
        .target-table {
            margin-left: 0;
        }
        .name-input {
            flex-wrap: wrap;
        }
    }
</style>
