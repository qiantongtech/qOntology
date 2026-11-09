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
  <div class="app-container object-type-relation-page" ref="app-container">
    <GuideTip tip-id="om/object/relation.list" />
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef" />
      </template>

      <template #actions-data>
        <el-button
          v-hasPermi="['om:object:relation:add']"
          type="primary"
          plain
          :disabled="saving"
          @click="handleAdd"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
          v-hasPermi="['om:object:relation:remove']"
          type="danger"
          plain
          :disabled="!selectedIds.length || deleting"
          @click="handleDelete()"
          @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>批量删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <!-- 纯文本列沿用统一 text 插槽渲染。 -->
        <template #text="{ row, column_data }">{{ row[column_data.prop] }}</template>
        <!-- 反向名称为空时展示占位横线。 -->
        <template #reverseName="{ row }">{{ row.reverseName || '-' }}</template>
        <!-- 创建人与项目管理一致，双行展示姓名和联系电话。 -->
        <template #creator="{ row }">
          <div class="flex-column overflow-hidden">
            <span class="text-ellipsis">{{ row.createBy || '-' }}</span>
            <span v-if="creatorPhones[row.creatorId]" class="desc-text">{{ creatorPhones[row.creatorId] }}</span>
          </div>
        </template>
        <!-- 是否可逆接口用 1/0 存储，这里映射为 sys_yes_no 字典值渲染。 -->
        <template #reversible="{ row }">
          <dict-tag :options="sys_yes_no" :value="reversibleDictValue(row.reversibleFlag)" />
        </template>
        <template #handle="{ row }">
          <el-button
            v-hasPermi="['om:object:relation:query']"
            link
            type="primary"
            icon="View"
            @click="handleDetail(row)"
          >详情</el-button>
          <!-- 操作区仅三个按钮，直展不折叠到“更多”。 -->
          <el-button
            v-if="auth.hasPermi('om:object:relation:edit')"
            link
            type="primary"
            icon="Edit"
            :disabled="saving"
            @click="handleUpdate(row)"
          >修改</el-button>
          <el-button
            v-if="auth.hasPermi('om:object:relation:remove')"
            link
            type="danger"
            icon="Delete"
            :disabled="deleting"
            @click="handleDelete(row)"
          >删除</el-button>
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
      @closed="relationRef?.clearValidate()"
    >
      <guide-tip-panel
        type="hint"
        class="mb15"
        allow-wrap
        title="定义对象类型之间的业务联系，配置起点、终点、可逆性和关联建模目标，为业务对象建立清晰的语义关系。"
      />
      <el-form
        ref="relationRef"
        :model="form"
        :rules="rules"
        label-width="140px"
        class="column-form"
        @submit.prevent="submitForm"
      >
        <div class="h2-titles row-full">基础信息</div>

        <qt-form-item
          class="row-full"
          label="起点类型"
          prop="startTypeId"
        >
          <el-tree-select
            v-model="form.startTypeId"
            :data="typeOptions"
            :loading="typeOptionsLoading"
            :props="treeProps"
            value-key="id"
            placeholder="请选择起点类型"
            check-strictly
            clearable
            class="form-control-full"
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="对象类型关系名称"
          prop="name"
        >
          <el-input v-model="form.name" placeholder="请输入对象类型关系名称，例如：提供、采购物料" maxlength="128" />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="终点类型"
          prop="endTypeId"
        >
          <el-tree-select
            v-model="form.endTypeId"
            :data="typeOptions"
            :loading="typeOptionsLoading"
            :props="treeProps"
            value-key="id"
            placeholder="请选择终点类型"
            check-strictly
            clearable
            class="form-control-full"
          />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="是否可逆"
          prop="reversibleFlag"
        >
          <el-radio-group v-model="form.reversibleFlag" @change="handleReversibleChange">
            <el-radio
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :value="dict.value === 'Y' ? 1 : 0"
            >{{ dict.label }}</el-radio>
          </el-radio-group>
        </qt-form-item>

        <qt-form-item
          class="row-full"
          tip="填写反向查看该关系时的名称，例如“由供应商提供”。"
          v-if="form.reversibleFlag === 1"
          label="反向名称"
          prop="reverseName"
          required
        >
          <el-input v-model="form.reverseName" placeholder="请输入反向名称" maxlength="128" />
        </qt-form-item>

        <qt-form-item
          class="row-full"
          label="描述"
          prop="description"
        >
          <el-input
            v-model="form.description"
            class="character-count"
            type="textarea"
            :rows="4"
            placeholder="请输入关系描述，说明业务联系和适用场景"
            maxlength="256个字符"
            show-word-limit
          />
        </qt-form-item>

        <div class="row-full">
          <div class="h2-titles mb15">关联建模目标</div>
          <div class="mb15">
            <el-form-item prop="targetIds" label-width="0">
              <el-button type="primary" plain icon="Plus" :disabled="saving" @click="targetPickerOpen = true">
                导入建模目标
              </el-button>
              <span class="desc-text ml10">点击导入建模目标，选中结果将在下方列表中回显。</span>
            </el-form-item>
          </div>
          <TargetTable
            :targets="selectedTargets"
            editable
            :disabled="saving"
            @remove="removeTarget"
          />
        </div>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :disabled="saving" @click="open = false">取 消</el-button>
          <el-button
            type="primary"
            :loading="saving"
            :disabled="typeOptionsLoading"
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
      :load-options="listRelationTargetOptions"
      @confirm="addTargets"
    />
  </div>
</template>

<script setup>
    import { computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch } from 'vue';
    import { useRouter } from 'vue-router';
    import { useOntologyVersion } from '@/views/om/useOntologyVersion';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import {
        listRelations,
        getRelation,
        listRelationTypes,
        addRelation,
        updateRelation,
        delRelation,
        listRelationTargets,
        listRelationTargetOptions
    } from '@/api/om/object/relation';
    import auth from '@/plugins/auth';
    import { useCreatorPhones } from '@/utils/useCreatorPhones';
    import TargetPicker from '../type/TargetPicker.vue';
    import TargetTable from '../type/TargetTable.vue';

    defineOptions({ name: 'OmObjectTypeRelation' });
    const props = defineProps({ versionId: { type: [String, Number], default: undefined } });
    const router = useRouter();
    const { versionId, requireVersion } = useOntologyVersion(() => props.versionId);
    const { proxy } = getCurrentInstance();
    const { creatorPhones } = useCreatorPhones();
    const { sys_yes_no } = proxy.useDict('sys_yes_no');
    /** 将 1/0 可逆标识转换为 sys_yes_no 字典值。 */
    const reversibleDictValue = (value) => (value === 1 ? 'Y' : 'N');
    /** 可逆关系展示双向箭头，不可逆展示单向箭头。 */
    const tableRef = ref();
    const relationRef = ref();
    const selectedIds = ref([]);
    const saving = ref(false);
    const deleting = ref(false);
    const open = ref(false);
    const targetPickerOpen = ref(false);
    const selectedTargets = ref([]);
    const typeOptions = ref([]);
    const typeOptionsLoading = ref(false);
    const dialogTitle = ref('新增对象类型关系');
    // 搜索下拉复用 sys_yes_no 字典，接口用 1/0 标识可逆。
    const reversibleOptions = computed(() =>
        sys_yes_no.value.map((dict) => ({ label: dict.label, value: dict.value === 'Y' ? 1 : 0 }))
    );

    /**
     * 分页查询关系列表；版本号缺失时返回空列表，避免无版本上下文发起无效查询。
     * @param {Object} params QtTable 传入的查询参数
     * @returns {Promise<Object>} 列表接口返回体
     */
    function fetchRelations(params) {
        if (!versionId.value) {
            return Promise.resolve({ data: { rows: [], total: 0 } });
        }
        return listRelations({ ...params, versionId: versionId.value });
    }

    // 表格配置：分页、排序、loading 与空数据图统一交给 QtTable。
    const tableStore = reactive({
        config: {
            sort: true,
            table: {
                stripe: true,
                rowKey: 'id',
                defaultSort: { prop: 'createTime', order: 'descending' },
                onSelectionChange: handleSelectionChange
            }
        },
        columns: [
            { type: 'selection', width: 55, minWidth: 55 },
            { label: '编号', prop: 'id', slot: 'text', width: 80, minWidth: 80, align: 'center', sortable: true },
            { label: '起点类型', prop: 'startTypeName', slot: 'text', minWidth: 140, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '关系名称', prop: 'name', slot: 'text', minWidth: 160, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '终点类型', prop: 'endTypeName', slot: 'text', minWidth: 140, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '描述', prop: 'description', slot: 'text', minWidth: 256, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '是否可逆', prop: 'reversibleFlag', slot: 'reversible', width: 100, minWidth: 10, showOverflowTooltip: { effect: 'light' },},
            { label: '反向名称', prop: 'reverseName', slot: 'reverseName', minWidth: 150, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '创建人', prop: 'createBy', slot: 'creator', width: 130, minWidth: 130, align: 'left', headerAlign: 'left', showOverflowTooltip: { effect: 'light' },},
            { label: '创建时间', prop: 'createTime', date: true, width: 160, minWidth: 160, align: 'left', sortable: true, showOverflowTooltip: { effect: 'light' },},
            { label: '操作', slot: 'handle', width: 250, minWidth: 250, fixed: 'right', showOverflowTooltip: { effect: 'light' },}
        ],
        func: fetchRelations,
        params: {
            name: null,
            reversibleFlag: null
        }
    });

    // 搜索项配置
    const searchStore = reactive({
        items: [
            { label: '对象类型关系名称', prop: 'name', component: { is: 'input', showOverflowTooltip: { effect: 'light' },
} },
            { label: '是否可逆', prop: 'reversibleFlag', component: { is: 'select', options: reversibleOptions, showOverflowTooltip: { effect: 'light' },
} }
        ]
    });
    const treeProps = { label: 'name', value: 'id', children: 'children' };
    const emptyForm = () => ({
        id: undefined,
        versionId: versionId.value,
        name: '',
        description: '',
        startTypeId: null,
        endTypeId: null,
        reversibleFlag: 0,
        reverseName: '',
        targetIds: []
    });
    const form = ref(emptyForm());
    const rules = {
        targetIds: [{ type: 'array', required: true, min: 1, message: '请至少关联一个建模目标', trigger: 'change' }],
        name: [
            { required: true, whitespace: true, message: '对象类型关系名称不能为空', trigger: 'blur' },
            {
                max: 128,
                message: '对象类型关系名称不能超过128个字符',
                trigger: 'blur'
            }
        ],
        startTypeId: [{ required: true, message: '请选择起点类型', trigger: 'change' }],
        endTypeId: [{ required: true, message: '请选择终点类型', trigger: 'change' }],
        reversibleFlag: [{ required: true, message: '请选择是否可逆', trigger: 'change' }],
        reverseName: [
            {
                validator: (_rule, value, callback) => {
                    if (form.value.reversibleFlag === 1 && !value?.trim()) {
                        callback(new Error('可逆关系的反向名称不能为空'));
                    } else {
                        callback();
                    }
                },
                trigger: 'blur'
            },
            { max: 128, message: '反向名称不能超过128个字符', trigger: 'blur' }
        ],
        description: [{ max: 256, message: '关系描述不能超过256个字符', trigger: 'blur' }]
    };
    let dialogRequest = 0;
    let contextRequest = 0;

    /** 刷新当前页列表。 */
    function refreshList() {
        tableRef.value?.getList();
    }

    /**
     * 多选框选中数据，供顶部批量删除使用。
     * @param {Array} selection 当前选中的行数据
     */
    function handleSelectionChange(selection) {
        selectedIds.value = selection.map((row) => row.id);
    }

    /** 新增按钮操作，同时加载对象类型下拉数据。 */
    async function handleAdd() {
        if (!requireVersion()) return;
        const request = ++dialogRequest;
        typeOptions.value = [];
        typeOptionsLoading.value = true;
        form.value = emptyForm();
        selectedTargets.value = [];
        targetPickerOpen.value = false;
        dialogTitle.value = '新增对象类型关系';
        open.value = true;
        nextTick(() => relationRef.value?.clearValidate());
        try {
            const response = await listRelationTypes(versionId.value);
            if (request !== dialogRequest || !open.value) return;
            typeOptions.value = proxy.handleTree(response.data || [], 'id', 'parentId');
        } finally {
            if (request === dialogRequest) typeOptionsLoading.value = false;
        }
    }

    /**
     * 修改按钮操作。
     * @param {Object} row 当前行数据
     */
    async function handleUpdate(row) {
        const request = ++dialogRequest;
        const [response, options, targetsResponse] = await Promise.all([
            getRelation(row.id),
            listRelationTypes(versionId.value),
            listRelationTargets(row.id)
        ]);
        if (request !== dialogRequest) return;
        typeOptions.value = proxy.handleTree(options.data || [], 'id', 'parentId');
        form.value = { ...emptyForm(), ...response.data };
        selectedTargets.value = targetsResponse.data || [];
        form.value.targetIds = selectedTargets.value.map((target) => String(target.id));
        targetPickerOpen.value = false;
        dialogTitle.value = '修改对象类型关系';
        open.value = true;
        nextTick(() => relationRef.value?.clearValidate());
    }

    /**
     * 详情按钮操作。
     * @param {Object} row 当前行数据
     */
    function handleDetail(row) {
        return router.push({
            name: 'OmObjectTypeRelationDetail',
            params: { id: String(row.id), versionId: versionId.value }
        });
    }

    /** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
    async function submitForm() {
        if (!requireVersion()) return;
        const submittingVersion = versionId.value;
        if (
            saving.value ||
            typeOptionsLoading.value ||
            !(await relationRef.value.validate().catch(() => false))
        )
            return;
        if (saving.value || typeOptionsLoading.value || submittingVersion !== versionId.value || !open.value) return;
        const context = contextRequest;
        ++dialogRequest;
        const data = {
            id: form.value.id,
            versionId: submittingVersion,
            name: form.value.name.trim(),
            description: (form.value.description || '').trim(),
            startTypeId: form.value.startTypeId,
            endTypeId: form.value.endTypeId,
            reversibleFlag: form.value.reversibleFlag,
            reverseName: form.value.reversibleFlag === 1 ? (form.value.reverseName || '').trim() : '',
            targetIds: selectedTargets.value.map((target) => String(target.id))
        };
        saving.value = true;
        try {
            await (data.id ? updateRelation(data) : addRelation(data));
            if (context !== contextRequest) return;
            ElMessage.success(data.id ? '修改成功' : '新增成功');
            open.value = false;
            refreshList();
        } finally {
            saving.value = false;
        }
    }

    /**
     * 保存期间拦截弹窗关闭回调。
     * @param {Function} done el-dialog 的 before-close 完成回调
     */
    function beforeClose(done) {
        if (!saving.value) done();
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
        nextTick(() => relationRef.value?.validateField('targetIds'));
    }

    /**
     * 移除已选建模目标。
     * @param {Object} target 待移除的目标
     */
    function removeTarget(target) {
        if (saving.value) return;
        selectedTargets.value = selectedTargets.value.filter((item) => String(item.id) !== String(target.id));
        form.value.targetIds = selectedTargets.value.map((item) => String(item.id));
        nextTick(() => relationRef.value?.validateField('targetIds').catch(() => {}));
    }

    /**
     * 删除按钮操作：行内删除取当前行编号，顶部批量删除取多选编号。
     * @param {Object} [row] 当前行数据，顶部批量按钮点击时为空
     */
    async function handleDelete(row) {
        if (deleting.value) return;
        const ids = row ? [row.id] : [...selectedIds.value];
        if (!ids.length) return;
        const context = contextRequest;
        try {
            await ElMessageBox.confirm(
                row ? `是否删除关系“${row.name}”？` : `是否删除选中的 ${ids.length} 条关系？`,
                '删除确认',
                { type: 'warning' }
            );
        } catch {
            return;
        }
        if (context !== contextRequest || deleting.value) return;
        deleting.value = true;
        try {
            await delRelation(ids);
            if (context !== contextRequest) return;
            ElMessage.success('删除成功');
            selectedIds.value = [];
            refreshList();
        } finally {
            deleting.value = false;
        }
    }

    // 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
    watch(versionId, async () => {
        ++contextRequest;
        ++dialogRequest;
        typeOptionsLoading.value = false;
        open.value = false;
        targetPickerOpen.value = false;
        selectedTargets.value = [];
        selectedIds.value = [];
        tableStore.params.name = null;
        tableStore.params.reversibleFlag = null;
        await nextTick();
        tableRef.value?.resetQuery();
    });
    /**
     * 切换是否可逆，不可逆时清空反向名称。
     * @param {number} value 当前选中的可逆标识
     */
    function handleReversibleChange(value) {
        if (value !== 1) form.value.reverseName = '';
        nextTick(() => relationRef.value?.clearValidate('reverseName'));
    }
    watch(open, (value) => {
        if (!value) {
            ++dialogRequest;
            typeOptionsLoading.value = false;
            targetPickerOpen.value = false;
        }
    });
    onBeforeUnmount(() => {
        ++contextRequest;
        ++dialogRequest;
    });
</script>

<style scoped lang="scss">

    /* 带标签的边：关系名称在上、箭头在下，整体居中。 */
    /* 反向名称：仅可逆关系展示在箭头下方。 */
    /* 表单控件撑满所在列宽，避免行内 style。 */
    .form-control-full {
        width: 100%;
    }
    .object-type-relation-page :deep(.el-dialog) {
        max-width: calc(100vw - 32px);
    }
    @media (max-width: 600px) {
    }
    .character-count :deep(.el-input__count) {
        background: transparent;
    }
</style>
