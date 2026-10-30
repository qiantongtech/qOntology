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
    <div ref="pageRef" class="app-container term-page">
        <GuideTip tip-id="om/semantic/term.list" />
        <div v-show="showSearch" class="pagecont-top">
            <el-form ref="queryRef" class="btn-style" :model="queryParams" :inline="true" @submit.prevent="handleQuery">
                <el-form-item label="名称" prop="stdName">
                    <el-input
                        v-model="queryParams.stdName"
                        class="el-form-input-width"
                        placeholder="请输入名称"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item label="英文名称" prop="enName">
                    <el-input
                        v-model="queryParams.enName"
                        class="el-form-input-width"
                        placeholder="请输入英文名称"
                        clearable
                        @keyup.enter="handleQuery"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" plain @click="handleQuery"><i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
                    </el-button>
                    <el-button @click="resetQuery"><i class="iconfont-mini icon-a-zu22378 mr5"></i>重置</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="pagecont-bottom">
            <div class="justify-between mb15">
                <el-row :gutter="15" class="btn-style">
                    <el-col :span="1.5">
                        <el-button v-hasPermi="['om:term:term:add']" type="primary" plain @click="handleAdd"><i class="iconfont-mini icon-xinzeng mr5"></i>新增
                        </el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
                            v-hasPermi="['om:term:term:remove']"
                            type="danger"
                            plain
                            :disabled="!selectedIds.length || deleting"
                            @click="handleDelete()"
                            ><i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
                        </el-button>
                    </el-col>
                </el-row>
                <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList" />
            </div>
            <el-table
                ref="tableRef"
                v-loading="loading"
                :data="termList"
                row-key="id"
                stripe
                :default-sort="defaultSort"
                @selection-change="handleSelectionChange"
                @sort-change="handleSortChange"
            >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column
                    v-if="isColumnVisible('id')"
                    prop="id"
                    label="编号"
                    width="80"
                    align="center"
                    sortable="custom"
                    :sort-orders="['descending', 'ascending', null]"
                />
                <el-table-column
                    v-if="isColumnVisible('stdName')"
                    prop="stdName"
                    label="名称"
                    min-width="140"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.stdName || '-' }}</template>
                </el-table-column>
                <el-table-column
                    v-if="isColumnVisible('description')"
                    prop="description"
                    label="描述"
                    min-width="240"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.description || '-' }}</template>
                </el-table-column>
                <el-table-column
                    v-if="isColumnVisible('enName')"
                    prop="enName"
                    label="英文名称"
                    min-width="150"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.enName || '-' }}</template>
                </el-table-column>
                <el-table-column
                    v-if="isColumnVisible('alias')"
                    prop="alias"
                    label="别名"
                    min-width="160"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.alias || '-' }}</template>
                </el-table-column>
                <el-table-column
                    v-if="isColumnVisible('abbr')"
                    prop="abbr"
                    label="缩写"
                    min-width="120"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.abbr || '-' }}</template>
                </el-table-column>
                <el-table-column
                    v-if="isColumnVisible('source')"
                    prop="source"
                    label="来源"
                    min-width="180"
                    align="left"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.source || '-' }}</template>
                </el-table-column>
                <el-table-column
                    label="创建人"
                    prop="createBy"
                    width="120"
                    align="center"
                    :show-overflow-tooltip="{ effect: 'light' }"
                />
                <el-table-column label="创建时间" prop="createTime" width="150" align="center" sortable="custom">
                    <template #default="scope">{{
                        parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') || '—'
                    }}</template>
                </el-table-column>
                <el-table-column
                    v-if="isColumnVisible('operation')"
                    label="操作"
                    align="center"
                    fixed="right"
                    width="240"
                    class-name="small-padding fixed-width"
                >
                    <template #default="{ row }">
                        <el-button
                            v-hasPermi="['om:term:term:query']"
                            link
                            type="primary"
                            icon="View"
                            @click="handleDetail(row)"
                        >
                            详情
                        </el-button>
                        <el-button
                            v-hasPermi="['om:term:term:edit']"
                            link
                            type="primary"
                            icon="Edit"
                            @click="handleUpdate(row)"
                        >
                            修改
                        </el-button>
                        <el-button
                            v-hasPermi="['om:term:term:remove']"
                            link
                            type="danger"
                            icon="Delete"
                            :disabled="deleting"
                            @click="handleDelete(row)"
                            >删除
                        </el-button>
                    </template>
                </el-table-column>
                <template #empty>
                    <div class="emptyBg">
                        <img src="@/assets/system/images/no_data/noData.png" alt="暂无记录" />
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
        <el-dialog
            v-model="open"
            :title="dialogTitle"
            width="800px"
            :append-to="pageRef"
            :close-on-press-escape="!saving"
            :before-close="handleBeforeClose"
            draggable
            @closed="clearValidation"
        >
            <el-form ref="termRef" :model="form" :rules="rules" label-width="90px" @submit.prevent="submitForm">
                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-form-item label="名称" prop="stdName">
                            <el-input v-model="form.stdName" placeholder="请输入名称" :maxlength="256" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="英文名称" prop="enName">
                            <el-input v-model="form.enName" placeholder="请输入英文名称" :maxlength="256" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="别名" prop="alias">
                            <div class="flex gap-2 alias-tags">
                                <el-tag
                                    v-for="alias in aliasTags"
                                    :key="alias"
                                    :title="alias"
                                    closable
                                    :disable-transitions="false"
                                    @close="removeAlias(alias)"
                                >
                                    {{ alias }}
                                </el-tag>
                                <el-input
                                    v-if="aliasInputVisible"
                                    ref="aliasInputRef"
                                    v-model="aliasInputValue"
                                    class="w-20 alias-input"
                                    size="small"
                                    placeholder="请输入别名"
                                    :maxlength="1024"
                                    :disabled="saving"
                                    @keydown.enter.stop.prevent="confirmAlias"
                                    @blur="confirmAlias"
                                />
                                <el-button
                                    v-else
                                    class="button-new-tag"
                                    size="small"
                                    :disabled="saving"
                                    @click="showAliasInput"
                                >
                                    + 添加别名
                                </el-button>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="缩写" prop="abbr">
                            <el-input v-model="form.abbr" placeholder="请输入缩写" :maxlength="256" />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="来源" prop="source">
                            <el-input
                                v-model="form.source"
                                class="term-character-count"
                                type="textarea"
                                :rows="3"
                                placeholder="请输入来源"
                                :maxlength="200"
                                show-word-limit
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="描述" prop="description">
                            <el-input
                                v-model="form.description"
                                class="term-character-count"
                                type="textarea"
                                :rows="4"
                                placeholder="请输入描述"
                                :maxlength="500"
                                show-word-limit
                            />
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button size="mini" @click="cancel">取 消</el-button>
                    <el-button type="primary" size="mini" @click="submitForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>
        <el-dialog
            v-model="detailOpen"
            class="term-detail-dialog"
            title="术语详情"
            width="800px"
            :append-to="pageRef"
            draggable
        >
            <el-form class="term-detail-form" :model="detail" label-width="90px" label-position="right">
                <el-row :gutter="20">
                    <el-col v-for="field in detailFields" :key="field.prop" :span="24">
                        <el-form-item :label="field.label" :prop="field.prop">
                            <div v-if="field.prop === 'alias'" class="form-readonly alias-tags">
                                <el-tag v-for="alias in detailAliases" :key="alias" :title="alias">{{ alias }}</el-tag>
                                <span v-if="!detailAliases.length">-</span>
                            </div>
                            <div v-else-if="field.prop === 'createTime'" class="form-readonly">
                                {{ parseTime(detail.createTime, '{y}-{m}-{d} {h}:{i}') || '-' }}
                            </div>
                            <div
                                v-else
                                class="form-readonly"
                                :class="{ textarea: ['source', 'description'].includes(field.prop) }"
                            >
                                {{ detail[field.prop] || '-' }}
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <template #footer>
                <el-button @click="detailOpen = false">关闭</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { onBeforeUnmount, computed, nextTick, watch, reactive, ref } from 'vue';
    import { useOntologyVersion } from '@/views/om/useOntologyVersion';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import { listTerm, getTerm, addTerm, updateTerm, delTerm } from '@/api/om/term/term';

    defineOptions({ name: 'OmTerm' });
    const { versionId, requireVersion } = useOntologyVersion();
    const pageRef = ref();
    const queryRef = ref();
    const termRef = ref();
    const tableRef = ref();
    const showSearch = ref(true);
    const loading = ref(false);
    const saving = ref(false);
    const deleting = ref(false);
    const open = ref(false);
    const detailOpen = ref(false);
    const dialogTitle = ref('新增术语');
    const termList = ref([]);
    const total = ref(0);
    const selectedIds = ref([]);
    const detail = ref({});
    const defaultSort = ref({ prop: 'createTime', order: 'descending' });
    const queryParams = reactive({
        pageNum: 1,
        pageSize: 10,
        stdName: '',
        enName: '',
        orderByColumn: defaultSort.value.prop,
        isAsc: defaultSort.value.order
    });
    const appliedFilters = reactive({ stdName: '', enName: '' });
    const columns = ref([
        { key: 0, prop: 'id', label: '编号', visible: true },
        { key: 1, prop: 'stdName', label: '名称', visible: true },
        { key: 2, prop: 'description', label: '描述', visible: true },
        { key: 3, prop: 'enName', label: '英文名称', visible: true },
        { key: 4, prop: 'alias', label: '别名', visible: true },
        { key: 5, prop: 'abbr', label: '缩写', visible: true },
        { key: 6, prop: 'source', label: '来源', visible: true },
        { key: 7, prop: 'operation', label: '操作', visible: true }
    ]);
    const detailFields = [
        { prop: 'id', label: '编号' },
        { prop: 'stdName', label: '名称' },
        { prop: 'enName', label: '英文名称' },
        { prop: 'alias', label: '别名' },
        { prop: 'abbr', label: '缩写' },
        { prop: 'source', label: '来源' },
        { prop: 'description', label: '描述' },
        { prop: 'createBy', label: '创建人' },
        { prop: 'createTime', label: '创建时间' }
    ];

    function isColumnVisible(prop) {
        return columns.value.find((column) => column.prop === prop)?.visible === true;
    }

    const emptyForm = () => ({
        id: undefined,
        versionId: versionId.value,
        stdName: '',
        enName: '',
        alias: '',
        abbr: '',
        source: '',
        description: ''
    });
    const form = ref(emptyForm());
    const aliasInputRef = ref();
    const aliasInputVisible = ref(false);
    const aliasInputValue = ref('');
    const aliasTags = computed(() => parseAliases(form.value.alias));
    const detailAliases = computed(() => parseAliases(detail.value.alias));
    const rules = {
        stdName: [
            { required: true, whitespace: true, message: '名称不能为空', trigger: 'blur' },
            { max: 256, message: '名称长度不能超过256个字符', trigger: 'blur' }
        ],
        enName: [
            { required: true, whitespace: true, message: '英文名称不能为空', trigger: 'blur' },
            { max: 256, message: '英文名称长度不能超过256个字符', trigger: 'blur' }
        ],
        alias: [
            { required: true, whitespace: true, message: '别名不能为空', trigger: 'blur' },
            { max: 1024, message: '别名长度不能超过1024个字符', trigger: 'blur' }
        ],
        abbr: [
            { required: true, whitespace: true, message: '缩写不能为空', trigger: 'blur' },
            { max: 256, message: '缩写长度不能超过256个字符', trigger: 'blur' }
        ],
        source: [
            { required: true, whitespace: true, message: '来源不能为空', trigger: 'blur' },
            { max: 200, message: '来源长度不能超过200个字符', trigger: 'blur' }
        ],
        description: [{ max: 500, message: '描述长度不能超过500个字符', trigger: 'blur' }]
    };
    let listRequest = 0;
    let contextRequest = 0;
    let detailRequest = 0;

    function parseAliases(value) {
        return [
            ...new Set(
                (value || '')
                    .split(/[,，]/)
                    .map((alias) => alias.trim())
                    .filter(Boolean)
            )
        ];
    }

    function resetAliasInput() {
        aliasInputVisible.value = false;
        aliasInputValue.value = '';
    }

    async function showAliasInput() {
        aliasInputVisible.value = true;
        await nextTick();
        aliasInputRef.value?.focus();
    }

    function confirmAlias(event) {
        if (event?.isComposing || saving.value) return false;
        const aliases = [...new Set([...aliasTags.value, ...parseAliases(aliasInputValue.value)])];
        const value = aliases.join('，');
        if (value.length > 1024) {
            ElMessage.warning('别名总长度不能超过1024个字符');
            return false;
        }
        form.value.alias = value;
        resetAliasInput();
        termRef.value?.validateField('alias').catch(() => {});
        return true;
    }

    function removeAlias(alias) {
        if (saving.value) return;
        form.value.alias = aliasTags.value.filter((value) => value !== alias).join('，');
        termRef.value?.validateField('alias').catch(() => {});
    }

    async function getList() {
        const requestId = ++listRequest;
        if (!versionId.value) {
            termList.value = [];
            total.value = 0;
            loading.value = false;
            return;
        }
        loading.value = true;
        try {
            const response = await listTerm({ ...queryParams, ...appliedFilters, versionId: versionId.value });
            if (requestId !== listRequest) return;
            termList.value = response.data.rows;
            total.value = response.data.total;
            selectedIds.value = [];
            tableRef.value?.clearSelection();
        } finally {
            if (requestId === listRequest) loading.value = false;
        }
    }

    function handleSortChange({ prop, order }) {
        queryParams.orderByColumn = order ? prop : defaultSort.value.prop;
        queryParams.isAsc = order || defaultSort.value.order;
        queryParams.pageNum = 1;
        getList();
    }

    function handleQuery() {
        appliedFilters.stdName = queryParams.stdName.trim();
        appliedFilters.enName = queryParams.enName.trim();
        queryParams.pageNum = 1;
        getList();
    }

    function resetQuery() {
        queryRef.value?.resetFields();
        handleQuery();
    }

    function handleSelectionChange(selection) {
        selectedIds.value = selection.map((row) => row.id);
    }

    function clearValidation() {
        termRef.value?.clearValidate();
        resetAliasInput();
    }

    async function handleAdd() {
        if (!requireVersion()) return;
        ++detailRequest;
        form.value = emptyForm();
        dialogTitle.value = '新增术语';
        open.value = true;
        await nextTick();
        clearValidation();
    }

    async function loadTerm(row, isDetail) {
        const requestId = ++detailRequest;
        const response = await getTerm(row.id);
        if (requestId !== detailRequest) return;
        if (isDetail) {
            detail.value = response.data;
            detailOpen.value = true;
        } else {
            form.value = { ...emptyForm(), ...response.data };
            dialogTitle.value = '修改术语';
            open.value = true;
            await nextTick();
            clearValidation();
        }
    }

    function handleUpdate(row) {
        return loadTerm(row, false);
    }

    function handleDetail(row) {
        return loadTerm(row, true);
    }

    function handleBeforeClose(done) {
        if (!saving.value) done();
    }

    function cancel() {
        if (!saving.value) open.value = false;
    }

    async function submitForm() {
        if (!requireVersion()) return;
        const submittingVersion = versionId.value;
        if (saving.value) return;
        if (aliasInputVisible.value && !confirmAlias()) return;
        const valid = await termRef.value.validate().catch(() => false);
        if (!valid || saving.value || submittingVersion !== versionId.value || !open.value) return;
        saving.value = true;
        try {
            const payload = {
                versionId: submittingVersion,
                stdName: (form.value.stdName ?? '').trim(),
                enName: (form.value.enName ?? '').trim(),
                alias: (form.value.alias ?? '').trim(),
                abbr: (form.value.abbr ?? '').trim(),
                source: (form.value.source ?? '').trim(),
                description: (form.value.description ?? '').trim()
            };
            if (form.value.id != null) {
                payload.id = form.value.id;
                await updateTerm(payload);
            } else {
                await addTerm(payload);
            }
            if (submittingVersion !== versionId.value) return;
            ElMessage.success(form.value.id != null ? '修改成功' : '新增成功');
            open.value = false;
            await getList();
        } finally {
            saving.value = false;
        }
    }

    async function handleDelete(row) {
        const context = contextRequest;
        const deletingVersion = versionId.value;
        if (deleting.value) return;
        const ids = row ? [row.id] : [...selectedIds.value];
        if (!ids.length) return;
        try {
            await ElMessageBox.confirm(
                row ? `确认删除术语“${row.stdName}”吗？` : `确认删除选中的 ${ids.length} 条术语吗？`,
                '删除确认',
                {
                    type: 'warning',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消'
                }
            );
        } catch {
            return;
        }
        if (deleting.value || context !== contextRequest || deletingVersion !== versionId.value) return;
        deleting.value = true;
        try {
            await delTerm(ids.join(','));
            if (context !== contextRequest || deletingVersion !== versionId.value) return;
            ElMessage.success('删除成功');
            if (queryParams.pageNum > 1 && termList.value.length <= ids.length) queryParams.pageNum -= 1;
            await getList();
        } finally {
            deleting.value = false;
        }
    }

    watch(
        versionId,
        () => {
            ++contextRequest;
            ++detailRequest;
            open.value = false;
            detailOpen.value = false;
            selectedIds.value = [];
            termList.value = [];
            queryParams.pageNum = 1;
            getList();
        },
        { immediate: true }
    );
    onBeforeUnmount(() => {
        ++contextRequest;
    });
</script>

<style scoped lang="scss">
    .alias-tags {
        display: flex;
        flex-wrap: wrap;
        align-items: center;
        gap: 8px;
        width: 100%;
    }

    .alias-tags .el-tag {
        max-width: 100%;
    }

    .alias-tags :deep(.el-tag__content) {
        min-width: 0;
        overflow: hidden;
        text-overflow: ellipsis;
    }

    .alias-input {
        width: 160px;
    }

    .form-readonly.alias-tags {
        padding-top: 4px;
        padding-bottom: 4px;
    }

    .term-detail-form {
        padding: 0;
    }

    .form-readonly {
        box-sizing: border-box;
        width: 100%;
        border: 1px solid #f1f1f1;
        padding: 0 10px;
        min-height: 34px;
        background-color: #fcfcfc;
        border-radius: 2px;
        color: #333;
        display: flex;
        align-items: center;
        white-space: pre-wrap;
        word-wrap: break-word;
        word-break: break-all;
    }

    .form-readonly.textarea {
        min-height: 85px;
        display: block;
    }

    .term-page :deep(.term-detail-dialog .el-form-item) {
        margin-bottom: 16px;
    }

    .term-page :deep(.term-detail-dialog .el-dialog__header) {
        padding: 9px 20px;
        background: #f8f8f8;
    }

    .term-page :deep(.term-detail-dialog .el-dialog__body) {
        padding: 20px 40px !important;
    }

    .term-page :deep(.term-detail-dialog .el-dialog__footer) {
        padding: 10px 20px 20px;
        border-top: 1px solid #f1f1f1;
    }

    .term-page :deep(.term-detail-dialog .el-dialog__footer .el-button) {
        min-width: 56px;
        height: 32px;
        padding: 8px 12px;
        font-size: 12px;
    }

    @media (max-width: 768px) {
        .term-page :deep(.term-detail-dialog .el-dialog__body) {
            padding: 20px 16px !important;
        }
    }

    .term-page :deep(.el-dialog) {
        max-width: calc(100vw - 32px);
    }

    .term-character-count :deep(.el-input__count)::after {
        content: ' 个字符';
    }

    .term-character-count :deep(.el-textarea__inner) {
        padding-bottom: 24px;
    }
</style>
