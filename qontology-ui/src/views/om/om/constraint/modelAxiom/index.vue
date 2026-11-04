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
    <div class="app-container model-axiom-page" ref="app-container">
        <GuideTip tip-id="om/constraint/model.list" />

        <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
            <template #search>
                <qt-search-bar :items="searchStore.items" :params="tableStore.params" :table-ref="tableRef" />
            </template>

            <template #actions-data>
                <el-button
                    v-hasPermi="['om:constraint:modelaxi:add']"
                    type="primary"
                    plain
                    :disabled="saving"
                    @click="handleAdd"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <i class="iconfont-mini icon-xinzeng mr5"></i>新增
                </el-button>
                <el-button
                    v-hasPermi="['om:constraint:modelaxi:remove']"
                    type="danger"
                    plain
                    :disabled="!selectedIds.length || deleting"
                    @click="handleDelete()"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
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
                        v-hasPermi="['om:constraint:modelaxi:query']"
                        link
                        type="primary"
                        icon="View"
                        @click="handleDetail(row)"
                        >详情</el-button
                    >
                    <el-popover
                        v-if="auth.hasPermiOr(['om:constraint:modelaxi:edit', 'om:constraint:modelaxi:remove'])"
                        placement="bottom"
                        :width="107"
                        popper-class="handle-popover"
                        trigger="click"
                    >
                        <template #reference>
                            <el-button link type="primary" icon="ArrowDown">更多</el-button>
                        </template>
                        <el-button
                            v-if="auth.hasPermi('om:constraint:modelaxi:edit')"
                            link
                            type="primary"
                            icon="Edit"
                            :disabled="saving"
                            @click="handleUpdate(row)"
                            >修改</el-button
                        >
                        <el-button
                            v-if="auth.hasPermi('om:constraint:modelaxi:remove')"
                            link
                            type="danger"
                            icon="Delete"
                            :disabled="deleting"
                            @click="handleDelete(row)"
                            >删除</el-button
                        >
                    </el-popover>
                </template>
            </qt-table>
        </qt-wrap>
        <el-dialog
            v-model="open"
            :title="dialogTitle"
            width="860px"
            :append-to="$refs['app-container']"
            :close-on-press-escape="!saving"
            :before-close="beforeClose"
            draggable
            destroy-on-close
            @closed="handleDialogClosed"
        >
            <el-form
                ref="ruleRef"
                :model="form"
                :rules="rules"
                label-width="120px"
                class="rule-form"

                @submit.prevent="submitForm"
            >
                <div class="h2-title">基本信息</div>
                <el-form-item label="名称" prop="name">
                    <el-input v-model="form.name" placeholder="请输入模型公理名称" maxlength="128" />
                </el-form-item>
                <el-form-item label="类型" prop="type">
                    <el-select v-model="form.type" placeholder="请选择类型" @change="handleTypeChange">
                        <el-option
                            v-for="item in om_axiom_type"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                        />
                    </el-select>
                </el-form-item>
                <el-form-item label="级别" prop="level">
                    <el-radio-group v-model="form.level">
                        <el-radio v-for="item in om_rule_level" :key="item.value" :value="item.value">{{
                            item.label
                        }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="描述" prop="description">
                    <el-input
                        v-model="form.description"
                        class="character-count"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入描述"
                        :maxlength="512"
                        show-word-limit
                    />
                </el-form-item>
                <div class="h2-title">公理表达</div>
                <template v-if="form.type === 'DOMAIN_RANGE'">
                    <el-form-item label="关系" prop="relationName">
                        <el-input v-model="form.relationName" placeholder="请输入关系名称" maxlength="128" clearable />
                    </el-form-item>
                    <el-form-item label="Domain" prop="domainId">
                        <el-tree-select
                            v-model="form.domainId"
                            :data="objectTypeTree"
                            :props="objectTypeTreeProps"
                            value-key="id"
                            :loading="optionsLoading"
                            check-strictly
                            filterable
                            clearable
                            placeholder="请选择对象类型"
                            @change="handleDomainChange"
                        />
                    </el-form-item>
                    <el-form-item label="Range" prop="rangeId">
                        <el-tree-select
                            v-model="form.rangeId"
                            :data="rangeObjectTypeTree"
                            :props="objectTypeTreeProps"
                            value-key="id"
                            :loading="optionsLoading"
                            check-strictly
                            filterable
                            clearable
                            placeholder="请选择对象类型"
                        />
                    </el-form-item>
                </template>
                <template v-else>
                    <el-form-item :label="firstTypeLabel" prop="objectTypeId">
                        <el-tree-select
                            v-model="form.objectTypeId"
                            :data="objectTypeTree"
                            :props="objectTypeTreeProps"
                            value-key="id"
                            :loading="optionsLoading"
                            check-strictly
                            filterable
                            clearable
                            placeholder="请选择对象类型"
                            @change="handleObjectTypeChange"
                        />
                    </el-form-item>
                    <template v-if="form.type === 'PROPERTY_RANGE'">
                        <el-form-item label="对象类型属性" prop="attributeId">
                            <el-select
                                v-model="form.attributeId"
                                :loading="optionsLoading || attributeLoadingCount > 0"
                                filterable
                                :disabled="!form.objectTypeId"
                                placeholder="请选择当前对象类型的属性"
                            >
                                <el-option
                                    v-for="item in currentAttributes"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item label="数据类型" prop="typeCategory">
                            <el-radio-group v-model="form.typeCategory" @change="handleValueTypeChange">
                                <el-radio
                                    v-for="item in om_attribute_data_type"
                                    :key="item.value"
                                    :value="Number(item.value)"
                                >
                                    {{ item.label }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="form.typeCategory === 1" label="基础数据类型" prop="basicTypeCode">
                            <el-select
                                v-model="form.basicTypeCode"
                                placeholder="请选择基础数据类型"
                                clearable
                                @change="handleBasicTypeChange"
                            >
                                <el-option
                                    v-for="item in basic_data_type"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="Number(item.value)"
                                />
                            </el-select>
                        </el-form-item>
                        <el-form-item v-else label="业务数据类型" prop="businessTypeKey">
                            <el-select
                                v-model="form.businessTypeKey"
                                :loading="optionsLoading"
                                filterable
                                placeholder="请选择业务数据类型"
                                clearable
                                @change="handleBusinessTypeChange"
                            >
                                <template #label>
                                    <span class="business-type-label">
                                        {{ selectedBusinessType?.name || form.businessTypeKey }}
                                        <el-tag
                                            :type="
                                                selectedBusinessType?.businessTypeSource === 'PROJECT'
                                                    ? 'primary'
                                                    : 'info'
                                            "
                                            size="small"
                                        >
                                            {{
                                                selectedBusinessType?.businessTypeSource === 'PROJECT'
                                                    ? '当前项目'
                                                    : '全局'
                                            }}
                                        </el-tag>
                                    </span>
                                </template>
                                <el-option-group
                                    v-for="group in businessTypeGroups"
                                    :key="group.source"
                                    :label="group.label"
                                >
                                    <el-option
                                        v-for="item in group.options"
                                        :key="item.key"
                                        :label="item.name"
                                        :value="item.key"
                                    />
                                </el-option-group>
                            </el-select>
                        </el-form-item>
                        <el-form-item v-if="form.typeCategory === 2 && selectedBusinessType" label="基础数据类型">
                            <div class="form-readonly">{{ basicTypeLabel(effectiveConfig.basicTypeCode) }}</div>
                        </el-form-item>
                        <AttributeTypeConfig
                            v-if="form.typeCategory === 1 || selectedBusinessType"
                            :model="effectiveConfig"
                            :readonly="form.typeCategory === 2"
                            :enum-options="enumOptions"
                            :enum-loading="enumsLoading"
                            @enum-open="loadEnums()"
                        />
                        <el-form-item
                            v-if="form.typeCategory === 2 && selectedBusinessType?.description"
                            label="类型描述"
                        >
                            <div class="form-readonly textarea">{{ selectedBusinessType.description }}</div>
                        </el-form-item>
                    </template>
                    <el-form-item v-else :label="secondTypeLabel" prop="otherTypeId">
                        <el-tree-select
                            v-model="form.otherTypeId"
                            :data="otherObjectTypeTree"
                            :props="objectTypeTreeProps"
                            value-key="id"
                            :loading="optionsLoading"
                            check-strictly
                            filterable
                            clearable
                            placeholder="请选择对象类型"
                        />
                    </el-form-item>
                </template>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button :disabled="saving" @click="cancel">取 消</el-button>
                    <el-button type="primary" :loading="saving" :disabled="saveDisabled" @click="submitForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>
        <el-dialog
            v-model="detailOpen"
            title="模型公理详情"
            width="860px"
            :append-to="$refs['app-container']"
            draggable
            destroy-on-close
        >
            <el-form label-width="120px">
                <el-form-item v-for="field in detailFields" :key="field.label" :label="field.label">
                    <div class="form-readonly" :class="{ textarea: field.multiline }">{{ field.value || '-' }}</div>
                </el-form-item>
                <AttributeTypeConfig
                    v-if="detail.type === 'PROPERTY_RANGE'"
                    :model="detailConfig"
                    readonly
                    :enum-options="enumOptions"
                />
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="detailOpen = false">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import { useOntologyVersion } from '@/views/om/useOntologyVersion';
    import { listModelAxiom, getModelAxiom, addModelAxiom, updateModelAxiom, delModelAxiom } from '@/api/om/constraint/modelAxiom';
    import { listRelationTypes } from '@/api/om/object/relation';
    import { listAttributes, listAttributeBusinessDataTypes, listAttributeEnums } from '@/api/om/object/attribute';
    import AttributeTypeConfig from '../../object/type/detail/AttributeTypeConfig.vue';
    import auth from '@/plugins/auth';
    import { useCreatorPhones } from '@/utils/useCreatorPhones';

    defineOptions({ name: 'OmModelAxiom' });
    const props = defineProps({ versionId: { type: [String, Number], default: undefined } });
    const { versionId, requireVersion } = useOntologyVersion(() => props.versionId);
    const { proxy } = getCurrentInstance();
    const { basic_data_type, om_axiom_type, om_rule_level, om_attribute_data_type } = proxy.useDict(
        'basic_data_type',
        'om_axiom_type',
        'om_rule_level',
        'om_attribute_data_type'
    );
    const tableRef = ref();
    const ruleRef = ref();
    const selectedIds = ref([]);
    const saving = ref(false);
    const deleting = ref(false);
    const open = ref(false);
    const detailOpen = ref(false);
    const dialogTitle = ref('新增模型公理');
    const detail = ref({});
    const { creatorPhones } = useCreatorPhones();

    /**
     * 分页查询模型公理列表；版本号缺失时返回空列表，避免无版本上下文发起无效查询。
     * @param {Object} params QtTable 传入的查询参数
     * @returns {Promise<Object>} 列表接口返回体
     */
    function fetchModelAxioms(params) {
        if (!versionId.value) {
            return Promise.resolve({ data: { rows: [], total: 0 } });
        }
        return listModelAxiom({ ...params, versionId: versionId.value });
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
            { label: '编号', prop: 'id', slot: 'text', width: 80, minWidth: 80, align: 'left', sortable: true },
            { label: '名称', prop: 'name', slot: 'text', minWidth: 200, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '描述', prop: 'description', slot: 'text', minWidth: 240, align: 'left', showOverflowTooltip: { effect: 'light' } },
            { label: '公理类型', prop: 'type', dict: 'om_axiom_type', width: 150, minWidth: 150 },
            { label: '级别', prop: 'level', dict: 'om_rule_level', width: 90, minWidth: 90 },
            { label: '创建人', prop: 'createBy', slot: 'creator', width: 130, minWidth: 130, align: 'left', headerAlign: 'left' },
            { label: '创建时间', prop: 'createTime', date: true, width: 160, minWidth: 160, align: 'left', sortable: true },
            { label: '操作', slot: 'handle', width: 170, minWidth: 170, fixed: 'right' }
        ],
        func: fetchModelAxioms,
        params: {
            name: null,
            type: null,
            level: null
        }
    });

    // 搜索项配置
    const searchStore = reactive({
        items: [
            { label: '公理名称', prop: 'name', component: { is: 'input' } },
            { label: '公理类型', prop: 'type', component: { is: 'select', options: om_axiom_type } },
            { label: '级别', prop: 'level', component: { is: 'select', options: om_rule_level } }
        ]
    });
    const objectTypeOptions = ref([]);
    const objectTypeTreeProps = { label: 'name', value: 'id', children: 'children' };
    const objectTypeTree = computed(() =>
        proxy.handleTree(objectTypeOptions.value.map((item) => ({ ...item })), 'id', 'parentId')
    );
    const otherObjectTypeTree = computed(() =>
        proxy.handleTree(
            objectTypeOptions.value.map((item) => ({ ...item, disabled: item.id === form.value.objectTypeId })),
            'id',
            'parentId'
        )
    );
    const rangeObjectTypeTree = computed(() =>
        proxy.handleTree(
            objectTypeOptions.value.map((item) => ({ ...item, disabled: item.id === form.value.domainId })),
            'id',
            'parentId'
        )
    );
    const attributeOptions = reactive({});
    const businessTypeOptions = ref([]);
    const optionsLoading = ref(false);
    const optionsLoadFailed = ref(false);
    const attributeLoadingCount = ref(0);
    const enumOptions = ref([]);
    const enumsLoading = ref(false);
    const typeLabel = (value) => proxy.selectDictLabel(om_axiom_type.value, value) || '-';
    const levelLabel = (value) => proxy.selectDictLabel(om_rule_level.value, value) || '-';
    const saveDisabled = computed(
        () =>
            optionsLoading.value ||
            attributeLoadingCount.value > 0 ||
            optionsLoadFailed.value ||
            (form.value.type === 'PROPERTY_RANGE' &&
                form.value.typeCategory === 1 &&
                form.value.basicTypeCode === 5 &&
                enumsLoading.value)
    );
    const emptyForm = () => ({
        id: undefined,
        versionId: versionId.value,
        name: '',
        description: '',
        level: 'INFO',
        type: 'SUBCLASS',
        objectTypeId: '',
        otherTypeId: '',
        relationName: '',
        domainId: '',
        rangeId: '',
        attributeId: '',
        typeCategory: 1,
        basicTypeCode: undefined,
        businessTypeKey: '',
        unit: '',
        decimalPlaces: 2,
        format: '',
        enumType: undefined,
        enumSource: 'PROJECT'
    });
    const form = ref(emptyForm());
    const rules = {
        name: [
            { required: true, whitespace: true, message: '模型公理名称不能为空', trigger: 'blur' },
            { max: 128, message: '名称不能超过128个字符', trigger: 'blur' }
        ],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        level: [{ required: true, message: '请选择级别', trigger: 'change' }],
        objectTypeId: [{ required: true, message: '请选择对象类型', trigger: 'change' }],
        otherTypeId: [{ required: true, message: '请选择另一对象类型', trigger: 'change' }],
        relationName: [
            { required: true, whitespace: true, message: '关系名称不能为空', trigger: 'blur' },
            { max: 128, message: '关系名称不能超过128个字符', trigger: 'blur' }
        ],
        domainId: [{ required: true, message: '请选择Domain', trigger: 'change' }],
        rangeId: [{ required: true, message: '请选择Range', trigger: 'change' }],
        attributeId: [{ required: true, message: '请选择数据属性', trigger: 'change' }],
        typeCategory: [{ required: true, message: '请选择值类型', trigger: 'change' }],
        basicTypeCode: [{ required: true, message: '请选择基础数据类型', trigger: 'change' }],
        businessTypeKey: [{ required: true, message: '请选择业务数据类型', trigger: 'change' }],
        unit: [{ max: 32, message: '单位不能超过32个字符', trigger: 'blur' }],
        decimalPlaces: [
            {
                validator: (_rule, value, callback) => {
                    if (form.value.typeCategory !== 1 || form.value.basicTypeCode !== 2 || value == null)
                        return callback();
                    const valid = Number.isInteger(value) && value >= 0 && value <= 2147483647;
                    callback(valid ? undefined : new Error('请输入有效的小数位数'));
                },
                trigger: 'change'
            }
        ],
        format: [{ max: 128, message: '日期格式不能超过128个字符', trigger: 'blur' }],
        enumType: [
            {
                validator: (_rule, value, callback) => {
                    if (form.value.typeCategory !== 1 || form.value.basicTypeCode !== 5 || !value) return callback();
                    const valid = enumOptions.value.some(
                        (item) => item.enumType === value && item.enumSource === form.value.enumSource
                    );
                    callback(valid ? undefined : new Error('请选择有效的枚举类型'));
                },
                trigger: 'change'
            }
        ]
    };
    let dialogRequest = 0;
    let contextRequest = 0;
    let enumRequest = 0;
    const firstTypeLabel = computed(() => {
        if (form.value.type === 'SUBCLASS') return '子类型';
        return form.value.type === 'PROPERTY_RANGE' ? '对象类型' : '对象类型A';
    });
    const secondTypeLabel = computed(() => (form.value.type === 'SUBCLASS' ? '父类型' : '对象类型B'));
    const currentAttributes = computed(() => attributeOptions[form.value.objectTypeId] || []);
    const currentBusinessTypes = computed(() =>
        businessTypeOptions.value.map((item) => ({
            ...item,
            key: item.businessTypeSource + ':' + item.id
        }))
    );
    const selectedBusinessType = computed(() => findBusinessType(form.value));
    const effectiveConfig = computed(() => resolvedConfig(form.value));
    const detailConfig = computed(() => resolvedConfig(detail.value));
    const basicTypeLabel = (value) => proxy.selectDictLabel(basic_data_type.value, value) || '未设置';
    const businessTypeGroups = computed(() =>
        [
            { source: 'PROJECT', label: '当前项目' },
            { source: 'GLOBAL', label: '全局' }
        ]
            .map((group) => ({
                ...group,
                options: currentBusinessTypes.value.filter((item) => item.businessTypeSource === group.source)
            }))
            .filter((group) => group.options.length)
    );

    const detailFields = computed(() => [
        { label: '名称', value: detail.value.name },
        { label: '类型', value: typeLabel(detail.value.type) },
        { label: '级别', value: levelLabel(detail.value.level) },
        { label: '描述', value: detail.value.description, multiline: true },
        ...getDetailFields(detail.value),
        { label: '创建人', value: detail.value.createBy },
        { label: '创建时间', value: proxy.parseTime(detail.value.createTime, '{y}-{m}-{d} {h}:{i}') }
    ]);

    /**
     * 逐页读取下拉选项，避免只显示第一页属性或关系。
     * @param {Function} loadPage 按页号拉取单页数据的回调
     * @returns {Promise<Array>} 合并后的全量行数据
     */
    async function loadAllPages(loadPage) {
        const rows = [];
        let pageNum = 1;
        let total = 0;
        do {
            const response = await loadPage(pageNum++);
            const page = response.data || {};
            const batch = page.rows || [];
            rows.push(...batch);
            total = Number(page.total || 0);
            if (!batch.length) break;
        } while (rows.length < total);
        return rows;
    }

    /**
     * 拉取指定版本的对象类型选项。
     * @param {string|number} versionId 项目版本编号
     * @returns {Promise<Array>} 对象类型列表
     */
    async function listObjectTypeOptions(versionId) {
        const response = await listRelationTypes(versionId);
        return (response.data || []).map((item) => ({
            ...item,
            id: String(item.id),
            parentId: item.parentId == null ? null : String(item.parentId)
        }));
    }

    /**
     * 拉取指定对象类型的全量属性选项。
     * @param {string} objectTypeId 对象类型编号
     * @param {string|number} versionId 项目版本编号
     * @returns {Promise<Array>} 属性列表
     */
    async function listObjectAttributeOptions(objectTypeId, versionId) {
        const rows = await loadAllPages((pageNum) => listAttributes({ objectTypeId, versionId, pageNum, pageSize: 100 }));
        return rows.map((item) => ({ ...item, id: String(item.id) }));
    }

    /**
     * 拉取业务数据类型选项，按项目版本和全局范围加载，不依赖具体对象类型。
     * @param {string|number} versionId 项目版本编号
     * @returns {Promise<Array>} 带来源标识的业务数据类型列表
     */
    async function listBusinessTypeOptions(versionId) {
        const results = await Promise.allSettled([
            listAttributeBusinessDataTypes(undefined, 'PROJECT', versionId),
            listAttributeBusinessDataTypes(undefined, 'GLOBAL', versionId)
        ]);
        const failed = results.find((result) => result.status === 'rejected');
        if (failed) throw failed.reason;
        return [
            ...(results[0].value.data || []).map((item) => ({ ...item, businessTypeSource: 'PROJECT' })),
            ...(results[1].value.data || []).map((item) => ({ ...item, businessTypeSource: 'GLOBAL' }))
        ];
    }

    /** 刷新当前模型公理列表。 */
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

    /**
     * 拉取并缓存指定对象类型的属性选项。
     * @param {string} objectTypeId 对象类型编号
     */
    async function loadObjectOptions(objectTypeId) {
        if (!objectTypeId) return;
        const request = dialogRequest;
        attributeLoadingCount.value++;
        try {
            const response = await listObjectAttributeOptions(objectTypeId, versionId.value);
            if (request !== dialogRequest) return;
            attributeOptions[objectTypeId] = response;
        } catch {
            if (request === dialogRequest) optionsLoadFailed.value = true;
        } finally {
            if (request === dialogRequest) attributeLoadingCount.value--;
        }
    }

    /**
     * 拉取指定对象类型的枚举选项（项目与全局）。
     * @param {string} [objectTypeId] 对象类型编号，缺省取当前表单选中值
     */
    async function loadEnums(objectTypeId = form.value.objectTypeId) {
        if (!objectTypeId) return;
        const request = ++enumRequest;
        const dialog = dialogRequest;
        enumsLoading.value = true;
        try {
            await Promise.allSettled(
                ['PROJECT', 'GLOBAL'].map(async (source) => {
                    const response = await listAttributeEnums(objectTypeId, source);
                    if (request !== enumRequest || dialog !== dialogRequest) return;
                    if (!Array.isArray(response.data)) throw new Error('枚举返回格式无效');
                    enumOptions.value = [
                        ...enumOptions.value.filter((item) => item.enumSource !== source),
                        ...response.data.map((item) => ({ ...item, enumSource: source }))
                    ];
                })
            );
        } finally {
            if (request === enumRequest && dialog === dialogRequest) enumsLoading.value = false;
        }
    }

    /**
     * 按业务数据类型唯一键查找选项。
     * @param {Object} record 含 businessTypeKey 的记录
     * @returns {Object|undefined} 匹配的业务数据类型
     */
    function findBusinessType(record) {
        return businessTypeOptions.value.find(
            (item) => item.businessTypeSource + ':' + item.id === record.businessTypeKey
        );
    }

    /**
     * 将业务数据类型的基础类型信息合并回记录，供只读展示与提交使用。
     * @param {Object} record 当前表单或详情记录
     * @returns {Object} 解析后的配置对象
     */
    function resolvedConfig(record) {
        const selected = record.typeCategory === 2 ? findBusinessType(record) : undefined;
        return selected ? { ...selected, basicTypeCode: Number(selected.dataType) } : record;
    }

    /** 加载弹窗所需的对象类型、业务数据类型与属性选项。 */
    async function loadOptions() {
        const request = dialogRequest;
        optionsLoading.value = true;
        optionsLoadFailed.value = false;
        try {
            const results = await Promise.allSettled([
                listObjectTypeOptions(versionId.value),
                listBusinessTypeOptions(versionId.value)
            ]);
            const failed = results.find((result) => result.status === 'rejected');
            if (failed) throw failed.reason;
            if (request !== dialogRequest) return;
            objectTypeOptions.value = results[0].value;
            businessTypeOptions.value = results[1].value;
            const record = detailOpen.value ? detail.value : form.value;
            const objectIds = getRecordObjectIds(record);
            await Promise.allSettled(objectIds.map(loadObjectOptions));
            if (request === dialogRequest && record.type === 'PROPERTY_RANGE') await loadEnums(record.objectTypeId);
        } catch {
            if (request === dialogRequest) optionsLoadFailed.value = true;
        } finally {
            if (request === dialogRequest) optionsLoading.value = false;
        }
    }

    /** 新增按钮操作。 */
    function handleAdd() {
        if (!requireVersion()) return;
        ++dialogRequest;
        attributeLoadingCount.value = 0;
        resetEnums();
        form.value = emptyForm();
        detailOpen.value = false;
        dialogTitle.value = '新增模型公理';
        open.value = true;
        nextTick(() => ruleRef.value?.clearValidate());
        loadOptions();
    }

    /**
     * 修改按钮操作。
     * @param {Object} row 当前行数据
     */
    async function handleUpdate(row) {
        if (!requireVersion()) return;
        const request = ++dialogRequest;
        const response = await getModelAxiom(row.id);
        if (request !== dialogRequest) return;
        attributeLoadingCount.value = 0;
        resetEnums();
        form.value = { ...emptyForm(), ...response.data };
        detailOpen.value = false;
        dialogTitle.value = '修改模型公理';
        open.value = true;
        nextTick(() => ruleRef.value?.clearValidate());
        loadOptions();
    }

    /**
     * 详情按钮操作。
     * @param {Object} row 当前行数据
     */
    async function handleDetail(row) {
        if (!requireVersion()) return;
        const request = ++dialogRequest;
        const response = await getModelAxiom(row.id);
        if (request !== dialogRequest) return;
        attributeLoadingCount.value = 0;
        resetEnums();
        detail.value = response.data;
        await loadDetailOptions();
        if (request === dialogRequest) detailOpen.value = true;
    }

    /** 详情弹窗打开前加载展示所需的选项数据。 */
    async function loadDetailOptions() {
        const request = dialogRequest;
        optionsLoadFailed.value = false;
        try {
            const options = await listObjectTypeOptions(versionId.value);
            if (request !== dialogRequest) return;
            objectTypeOptions.value = options;
            const businessTypes = await listBusinessTypeOptions(versionId.value);
            if (request !== dialogRequest) return;
            businessTypeOptions.value = businessTypes;
            await Promise.allSettled(getRecordObjectIds(detail.value).map(loadObjectOptions));
            if (request === dialogRequest && detail.value.type === 'PROPERTY_RANGE')
                await loadEnums(detail.value.objectTypeId);
        } catch {
            // 接口异常由统一请求拦截器提示
        }
    }

    /** 提交新增/修改表单，提交期间锁定确定按钮避免重复入库。 */
    async function submitForm() {
        if (!requireVersion() || saving.value || saveDisabled.value) return;
        const request = dialogRequest;
        const context = contextRequest;
        if (!(await ruleRef.value.validate().catch(() => false))) return;
        if (request !== dialogRequest || !open.value || saving.value || saveDisabled.value) return;
        const error = validateExpression();
        if (error) {
            ElMessage.warning(error);
            return;
        }
        const data = {
            ...JSON.parse(JSON.stringify(form.value)),
            versionId: versionId.value,
            name: form.value.name.trim(),
            description: form.value.description.trim()
        };
        saving.value = true;
        try {
            await (data.id ? updateModelAxiom(data) : addModelAxiom(data));
            if (context !== contextRequest) return;
            ElMessage.success(data.id ? '修改成功' : '新增成功');
            open.value = false;
            refreshList();
        } finally {
            saving.value = false;
        }
    }

    /**
     * 删除按钮操作：行内删除取当前行编号，顶部按钮删除取多选编号。
     * @param {Object} [row] 当前行数据，顶部批量按钮点击时为 MouseEvent
     */
    async function handleDelete(row) {
        if (!requireVersion() || deleting.value) return;
        const ids = row ? [row.id] : [...selectedIds.value];
        if (!ids.length) return;
        const context = contextRequest;
        try {
            const message = row
                ? '是否删除模型公理“' + row.name + '”？'
                : '是否删除选中的 ' + ids.length + ' 条模型公理？';
            await ElMessageBox.confirm(message, '删除确认', { type: 'warning' });
        } catch {
            return;
        }
        if (context !== contextRequest || deleting.value) return;
        deleting.value = true;
        try {
            await delModelAxiom(ids);
            if (context !== contextRequest) return;
            ElMessage.success('删除成功');
            refreshList();
        } finally {
            deleting.value = false;
        }
    }

    /** 关闭新增/修改弹窗；保存中禁止关闭。 */
    function cancel() {
        if (saving.value) return;
        open.value = false;
    }

    /**
     * 保存期间拦截弹窗关闭回调。
     * @param {Function} done el-dialog 的 before-close 完成回调
     */
    function beforeClose(done) {
        if (!saving.value) done();
    }

    /** 弹窗关闭后清除表单校验提示。 */
    function handleDialogClosed() {
        ruleRef.value?.clearValidate();
    }

    /**
     * 将对象类型编号转为名称，未加载时回显编号。
     * @param {string} id 对象类型编号
     * @returns {string} 对象类型名称
     */
    function objectTypeLabel(id) {
        return objectTypeOptions.value.find((item) => item.id === id)?.name || id || '-';
    }

    /**
     * 将属性编号转为名称。
     * @param {string} objectTypeId 对象类型编号
     * @param {string} attributeId 属性编号
     * @returns {string} 属性名称
     */
    function attributeLabel(objectTypeId, attributeId) {
        return (
            (attributeOptions[objectTypeId] || []).find((item) => item.id === attributeId)?.name || attributeId || '-'
        );
    }

    /**
     * 取出记录需要加载属性选项的对象类型编号。
     * @param {Object} record 当前表单或详情记录
     * @returns {Array<string>} 对象类型编号列表
     */
    function getRecordObjectIds(record) {
        return record.type === 'PROPERTY_RANGE' && record.objectTypeId ? [record.objectTypeId] : [];
    }

    /** 切换公理类型时重置除基本信息外的字段。 */
    function handleTypeChange() {
        const { id, name, description, level, type, createBy, createTime } = form.value;
        // 通过表单重置表达字段，避免清空选值触发异步必填校验
        ruleRef.value?.resetFields([
            'objectTypeId',
            'otherTypeId',
            'relationName',
            'domainId',
            'rangeId',
            'attributeId',
            'typeCategory',
            'basicTypeCode',
            'businessTypeKey',
            'unit',
            'decimalPlaces',
            'format',
            'enumType'
        ]);
        form.value = { ...emptyForm(), id, name, description, level, type, createBy, createTime };
        resetEnums();
        nextTick(() => ruleRef.value?.clearValidate());
    }

    /** Domain 与 Range 重复时清空 Range。 */
    function handleDomainChange() {
        if (form.value.domainId && form.value.domainId === form.value.rangeId) form.value.rangeId = '';
    }

    /** 切换对象类型时清空属性、值类型配置与枚举选项。 */
    function handleObjectTypeChange() {
        form.value.attributeId = '';
        clearTypeConfig();
        resetEnums();
        if (form.value.type === 'PROPERTY_RANGE') {
            loadObjectOptions(form.value.objectTypeId);
            loadEnums();
        }
    }

    /** 切换基础/业务数据类型时清空已选类型与值配置。 */
    function handleValueTypeChange() {
        form.value.basicTypeCode = undefined;
        form.value.businessTypeKey = '';
        clearTypeConfig();
        nextTick(() => ruleRef.value?.clearValidate(['basicTypeCode', 'businessTypeKey']));
    }

    /** 清空单位、小数位、格式与枚举等值配置。 */
    function clearTypeConfig() {
        Object.assign(form.value, {
            unit: '',
            decimalPlaces: 2,
            format: '',
            enumType: undefined,
            enumSource: 'PROJECT'
        });
        nextTick(() => ruleRef.value?.clearValidate(['unit', 'decimalPlaces', 'format', 'enumType']));
    }

    /** 切换基础数据类型时重置值配置。 */
    function handleBasicTypeChange() {
        clearTypeConfig();
    }

    /** 切换业务数据类型时重置值配置。 */
    function handleBusinessTypeChange() {
        clearTypeConfig();
    }

    /** 清空枚举选项并释放加载中状态。 */
    function resetEnums() {
        ++enumRequest;
        enumOptions.value = [];
        enumsLoading.value = false;
    }

    /**
     * 校验公理表达的业务约束（表单规则无法覆盖的部分）。
     * @returns {string} 错误提示文案，校验通过时为空字符串
     */
    function validateExpression() {
        const record = form.value;
        const typeExists = (id) => objectTypeOptions.value.some((item) => item.id === id);
        if (record.type === 'DOMAIN_RANGE') {
            if (record.domainId && record.domainId === record.rangeId) return 'Domain 和 Range 不能选择相同的对象类型';
            if (!record.relationName?.trim()) return '请输入关系名称';
            if (!typeExists(record.domainId) || !typeExists(record.rangeId)) {
                return '请选择有效的Domain和Range';
            }
            return '';
        }
        if (!typeExists(record.objectTypeId)) return '请选择有效的对象类型';
        if (record.type !== 'PROPERTY_RANGE') {
            if (!typeExists(record.otherTypeId) || record.objectTypeId === record.otherTypeId) {
                return '请选择两个不同的对象类型';
            }
            return '';
        }
        if (!currentAttributes.value.some((item) => item.id === record.attributeId)) {
            return '请选择当前对象类型的有效属性';
        }
        if (
            record.typeCategory === 1 &&
            !basic_data_type.value.some((item) => Number(item.value) === record.basicTypeCode)
        ) {
            return '请选择有效的基础数据类型';
        }
        if (
            record.typeCategory === 2 &&
            !currentBusinessTypes.value.some((item) => item.key === record.businessTypeKey)
        ) {
            return '请选择有效的业务数据类型';
        }
        return '';
    }

    /**
     * 按公理类型组装详情弹窗的展示字段。
     * @param {Object} record 详情记录
     * @returns {Array<Object>} 展示字段列表
     */
    function getDetailFields(record) {
        if (record.type === 'DOMAIN_RANGE') {
            return [
                {
                    label: '关系',
                    value: record.relationName
                },
                { label: 'Domain', value: objectTypeLabel(record.domainId) },
                { label: 'Range', value: objectTypeLabel(record.rangeId) }
            ];
        }
        if (record.type === 'PROPERTY_RANGE') {
            const businessType = findBusinessType(record);
            const fields = [
                { label: '对象类型', value: objectTypeLabel(record.objectTypeId) },
                { label: '对象类型属性', value: attributeLabel(record.objectTypeId, record.attributeId) },
                { label: '数据类型', value: record.typeCategory === 1 ? '基础数据类型' : '业务数据类型' },
                {
                    label: '数据类型',
                    value:
                        record.typeCategory === 1
                            ? proxy.selectDictLabel(basic_data_type.value, record.basicTypeCode)
                            : businessType?.name || record.businessTypeKey
                }
            ];
            if (record.typeCategory === 2) {
                fields.push({ label: '基础数据类型', value: basicTypeLabel(resolvedConfig(record).basicTypeCode) });
                if (businessType?.description)
                    fields.push({ label: '类型描述', value: businessType.description, multiline: true });
            }
            return fields;
        }
        return [
            { label: record.type === 'SUBCLASS' ? '子类型' : '对象类型A', value: objectTypeLabel(record.objectTypeId) },
            { label: record.type === 'SUBCLASS' ? '父类型' : '对象类型B', value: objectTypeLabel(record.otherTypeId) }
        ];
    }

    // 版本切换后清空筛选并重置分页刷新列表；首查由 QtTable 挂载时自动执行。
    watch(versionId, async () => {
        ++contextRequest;
        ++dialogRequest;
        open.value = false;
        detailOpen.value = false;
        optionsLoading.value = false;
        attributeLoadingCount.value = 0;
        optionsLoadFailed.value = false;
        resetEnums();
        objectTypeOptions.value = [];
        Object.keys(attributeOptions).forEach((key) => delete attributeOptions[key]);
        businessTypeOptions.value = [];
        tableStore.params.name = null;
        tableStore.params.type = null;
        tableStore.params.level = null;
        await nextTick();
        tableRef.value?.resetQuery();
    });

    watch(open, (value) => {
        if (value) return;
        ++dialogRequest;
        attributeLoadingCount.value = 0;
        optionsLoading.value = false;
        ++enumRequest;
        enumsLoading.value = false;
    });

    onBeforeUnmount(() => {
        ++contextRequest;
        ++dialogRequest;
        ++enumRequest;
    });
</script>

<style scoped lang="scss">
    .model-axiom-page :deep(.el-dialog) {
        max-width: calc(100vw - 32px);
    }

    .rule-form {
        min-height: 390px;
    }

    .rule-form .el-select {
        width: 100%;
    }

    .h2-title {
        font-size: 16px;
        color: rgba(0, 0, 0, 0.85);
        display: flex;
        align-items: center;
        font-family: PingFang SC;
        font-weight: 500;
        margin: 8px 0;
    }
    .h2-title::before {
        display: inline-block;
        content: '';
        width: 6px;
        height: 16px;
        border-radius: 3px;
        background: var(--el-color-primary);
        margin-right: 8px;
    }

    .character-count :deep(.el-input__count) {
        background: transparent;
    }

    .character-count :deep(.el-input__count)::after {
        content: '个字符';
    }

    .character-count :deep(.el-textarea__inner) {
        padding-bottom: 24px;
    }
    .business-type-label {
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }
</style>
