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
    <div ref="pageRef" class="app-container model-axiom-page">
        <GuideTip tip-id="om/constraint/model.list" />
        <div v-show="showSearch" class="pagecont-top">
            <el-form ref="queryRef" class="btn-style" :model="query" :inline="true" @submit.prevent="search">
                <el-form-item label="规则名称" prop="name">
                    <el-input
                        v-model="query.name"
                        class="el-form-input-width"
                        placeholder="请输入名称"
                        maxlength="128"
                        clearable
                        @keyup.enter="search"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" plain @click="search">
                        <i class="iconfont-mini icon-a-zu22377 mr5"></i>查询
                    </el-button>
                    <el-button @click="resetSearch"> <i class="iconfont-mini icon-a-zu22378 mr5"></i>重置 </el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="pagecont-bottom">
            <div class="justify-between mb15">
                <el-row :gutter="15" class="btn-style">
                    <el-col :span="1.5">
                        <el-button
                            v-hasPermi="['om:constraint:model:add']"
                            type="primary"
                            plain
                            :disabled="saving"
                            @click="handleAdd"
                        >
                            <i class="iconfont-mini icon-xinzeng mr5"></i>新增
                        </el-button>
                    </el-col>
                    <el-col :span="1.5">
                        <el-button
                            v-hasPermi="['om:constraint:model:remove']"
                            type="danger"
                            plain
                            :disabled="!selectedIds.length || deleting || loading"
                            @click="handleDelete()"
                        >
                            <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
                        </el-button>
                    </el-col>
                </el-row>
                <right-toolbar v-model:showSearch="showSearch" :columns="columns" @queryTable="getList" />
            </div>
            <el-table
                ref="tableRef"
                v-loading="loading"
                :data="rows"
                row-key="id"
                stripe
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column
                    v-if="visible('index')"
                    type="index"
                    :index="getRowIndex"
                    label="序号"
                    width="70"
                    align="center"
                />
                <el-table-column
                    v-if="visible('name')"
                    prop="name"
                    label="名称"
                    min-width="160"
                    :show-overflow-tooltip="{ effect: 'light' }"
                />
                <el-table-column
                    v-if="visible('description')"
                    prop="description"
                    label="描述"
                    min-width="240"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.description || '-' }}</template>
                </el-table-column>
                <el-table-column v-if="visible('type')" label="公理类型" min-width="145" align="center">
                    <template #default="{ row }">
                        <dict-tag :options="om_axiom_type" :value="row.type" />
                    </template>
                </el-table-column>
                <el-table-column v-if="visible('level')" label="级别" width="90" align="center">
                    <template #default="{ row }">
                        <dict-tag :options="om_rule_level" :value="row.level" />
                    </template>
                </el-table-column>
                <el-table-column
                    v-if="visible('createBy')"
                    prop="createBy"
                    label="创建人"
                    width="120"
                    align="center"
                    :show-overflow-tooltip="{ effect: 'light' }"
                >
                    <template #default="{ row }">{{ row.createBy || '-' }}</template>
                </el-table-column>
                <el-table-column v-if="visible('createTime')" label="创建时间" width="180" align="center">
                    <template #default="{ row }">{{
                        proxy.parseTime(row.createTime, '{y}-{m}-{d} {h}:{i}') || '-'
                    }}</template>
                </el-table-column>
                <el-table-column
                    v-if="visible('operation')"
                    label="操作"
                    width="210"
                    align="center"
                    fixed="right"
                    class-name="small-padding fixed-width"
                >
                    <template #default="{ row }">
                        <el-button
                            v-hasPermi="['om:constraint:model:query']"
                            link
                            type="primary"
                            icon="View"
                            @click="handleDetail(row)"
                            >详情
                        </el-button>
                        <el-button
                            v-hasPermi="['om:constraint:model:edit']"
                            link
                            type="primary"
                            icon="Edit"
                            :disabled="saving"
                            @click="handleUpdate(row)"
                            >修改
                        </el-button>
                        <el-button
                            v-hasPermi="['om:constraint:model:remove']"
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
                        <img src="@/assets/system/images/no_data/noData.png" alt="暂无模型公理" />
                        <p>{{ versionId ? '暂无模型公理' : '请先选择项目版本' }}</p>
                    </div>
                </template>
            </el-table>
            <pagination
                v-show="total > 0"
                :total="total"
                v-model:page="query.pageNum"
                v-model:limit="query.pageSize"
                @pagination="getList"
            />
        </div>
        <el-dialog
            v-model="open"
            :title="dialogTitle"
            width="860px"
            :append-to="pageRef"
            :close-on-press-escape="!saving"
            :before-close="beforeClose"
            draggable
            destroy-on-close
            @closed="handleDialogClosed"
        >
            <el-alert
                v-if="optionsError"
                :title="optionsError"
                type="error"
                :closable="false"
                show-icon
                class="options-error"
            >
                <el-button link type="primary" @click="loadOptions">重新加载</el-button>
            </el-alert>
            <el-form
                ref="ruleRef"
                :model="form"
                :rules="rules"
                label-width="120px"
                class="rule-form"
                :disabled="saving || optionsLoading"
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
                        :maxlength="500"
                        show-word-limit
                    />
                </el-form-item>
                <div class="h2-title">公理表达</div>
                <template v-if="form.type === 'DOMAIN_RANGE'">
                    <el-form-item label="对象类型关系" prop="relationId">
                        <el-select v-model="form.relationId" filterable placeholder="请选择关系">
                            <el-option
                                v-for="item in relationOptions"
                                :key="item.id"
                                :label="item.name"
                                :value="String(item.id)"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="Domain" prop="domainId">
                        <el-select v-model="form.domainId" filterable placeholder="请选择对象类型">
                            <el-option
                                v-for="item in objectTypeOptions"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="Range" prop="rangeId">
                        <el-select v-model="form.rangeId" filterable placeholder="请选择对象类型">
                            <el-option
                                v-for="item in objectTypeOptions"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            />
                        </el-select>
                    </el-form-item>
                </template>
                <template v-else>
                    <el-form-item :label="firstTypeLabel" prop="objectTypeId">
                        <el-select
                            v-model="form.objectTypeId"
                            filterable
                            placeholder="请选择对象类型"
                            @change="handleObjectTypeChange"
                        >
                            <el-option
                                v-for="item in objectTypeOptions"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            />
                        </el-select>
                    </el-form-item>
                    <template v-if="form.type === 'PROPERTY_RANGE'">
                        <el-form-item label="数据属性" prop="attributeId">
                            <el-select
                                v-model="form.attributeId"
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
                        <el-form-item label="值类型" prop="typeCategory">
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
                                filterable
                                :disabled="!form.objectTypeId"
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
                            :enum-error="enumsError"
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
                        <el-select v-model="form.otherTypeId" filterable placeholder="请选择对象类型">
                            <el-option
                                v-for="item in objectTypeOptions"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                                :disabled="item.id === form.objectTypeId"
                            />
                        </el-select>
                    </el-form-item>
                </template>
            </el-form>
            <template #footer>
                <div class="dialog-footer">
                    <el-button size="mini" :disabled="saving" @click="cancel">取 消</el-button>
                    <el-button type="primary" size="mini" :loading="saving" :disabled="saveDisabled" @click="submitForm">确 定</el-button>
                </div>
            </template>
        </el-dialog>
        <el-dialog
            v-model="detailOpen"
            title="模型公理详情"
            width="860px"
            :append-to="pageRef"
            draggable
            destroy-on-close
        >
            <el-form label-width="120px">
                <el-form-item v-for="field in detailFields" :key="field.label" :label="field.label + '：'">
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
                    <el-button size="mini" @click="detailOpen = false">关 闭</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
    import { computed, getCurrentInstance, nextTick, onBeforeUnmount, reactive, ref, watch } from 'vue';
    import { ElMessage, ElMessageBox } from 'element-plus';
    import { useOntologyVersion } from '@/views/om/useOntologyVersion';
    import useUserStore from '@/store/system/user';
    import { listRule, getRule, addRule, updateRule, delRule } from '@/api/om/constraint/rule';
    import { listRelationTypes, listRelations } from '@/api/om/object/relation';
    import { listAttributes, listAttributeBusinessDataTypes, listAttributeEnums } from '@/api/om/object/attribute';
    import AttributeTypeConfig from '../../object/type/detail/AttributeTypeConfig.vue';

    defineOptions({ name: 'OmModelAxiom' });
    const props = defineProps({ versionId: { type: [String, Number], default: undefined } });
    const { versionId, requireVersion } = useOntologyVersion(() => props.versionId);
    const { proxy } = getCurrentInstance();
    const userStore = useUserStore();
    const { basic_data_type, om_axiom_type, om_rule_level, om_attribute_data_type } = proxy.useDict(
        'basic_data_type',
        'om_axiom_type',
        'om_rule_level',
        'om_attribute_data_type'
    );
    const pageRef = ref();
    const tableRef = ref();
    const queryRef = ref();
    const ruleRef = ref();
    const rows = ref([]);
    const selectedIds = ref([]);
    const total = ref(0);
    const loading = ref(false);
    const saving = ref(false);
    const deleting = ref(false);
    const open = ref(false);
    const detailOpen = ref(false);
    const dialogTitle = ref('新增模型公理');
    const detail = ref({});
    const showSearch = ref(true);
    const query = reactive({ name: '', pageNum: 1, pageSize: 10 });
    const appliedName = ref('');
    const objectTypeOptions = ref([]);
    const attributeOptions = reactive({});
    const businessTypeOptions = reactive({});
    const optionsLoading = ref(false);
    const optionsError = ref('');
    const attributeLoadingCount = ref(0);
    const enumOptions = ref([]);
    const enumsLoading = ref(false);
    const enumsError = ref('');
    const columns = ref([
        { key: 0, prop: 'index', label: '序号', visible: true },
        { key: 1, prop: 'name', label: '名称', visible: true },
        { key: 2, prop: 'description', label: '描述', visible: true },
        { key: 3, prop: 'type', label: '公理类型', visible: true },
        { key: 4, prop: 'level', label: '级别', visible: true },
        { key: 5, prop: 'createBy', label: '创建人', visible: true },
        { key: 6, prop: 'createTime', label: '创建时间', visible: true },
        { key: 7, prop: 'operation', label: '操作', visible: true }
    ]);
    const visible = (prop) => columns.value.find((column) => column.prop === prop)?.visible;
    const typeLabel = (value) => proxy.selectDictLabel(om_axiom_type.value, value) || '-';
    const levelLabel = (value) => proxy.selectDictLabel(om_rule_level.value, value) || '-';
    const saveDisabled = computed(
        () =>
            optionsLoading.value ||
            attributeLoadingCount.value > 0 ||
            !!optionsError.value ||
            (form.value.type === 'PROPERTY_RANGE' &&
                form.value.typeCategory === 1 &&
                form.value.basicTypeCode === 5 &&
                enumsLoading.value)
    );
    const emptyForm = () => ({
        id: undefined,
        versionId: versionId.value,
        category: 'MODEL',
        name: '',
        description: '',
        level: 'INFO',
        type: 'SUBCLASS',
        objectTypeId: '',
        otherTypeId: '',
        relationId: '',
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
        description: [
            { required: true, whitespace: true, message: '描述不能为空', trigger: 'blur' },
            { max: 500, message: '描述不能超过500个字符', trigger: 'blur' }
        ],
        objectTypeId: [{ required: true, message: '请选择对象类型', trigger: 'change' }],
        otherTypeId: [{ required: true, message: '请选择另一对象类型', trigger: 'change' }],
        relationId: [{ required: true, message: '请选择对象类型关系', trigger: 'change' }],
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
    let listRequest = 0;
    let dialogRequest = 0;
    let contextRequest = 0;
    let enumRequest = 0;
    const relationOptions = ref([]);
    const firstTypeLabel = computed(() => {
        if (form.value.type === 'SUBCLASS') return '子类型';
        return form.value.type === 'PROPERTY_RANGE' ? '对象类型' : '对象类型A';
    });
    const secondTypeLabel = computed(() => (form.value.type === 'SUBCLASS' ? '父类型' : '对象类型B'));
    const currentAttributes = computed(() => attributeOptions[form.value.objectTypeId] || []);
    const currentBusinessTypes = computed(() =>
        (businessTypeOptions[form.value.objectTypeId] || []).map((item) => ({
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

    // 下拉选项需要读取全部分页数据，避免只显示第一页属性或关系
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

    async function listObjectTypeOptions(versionId) {
        const response = await listRelationTypes(versionId);
        return (response.data || []).map((item) => ({ ...item, id: String(item.id) }));
    }

    function listRelationOptions(versionId) {
        return loadAllPages((pageNum) => listRelations({ versionId, pageNum, pageSize: 100 }));
    }

    async function listObjectAttributeOptions(objectTypeId, versionId) {
        const results = await Promise.allSettled([
            loadAllPages((pageNum) => listAttributes({ objectTypeId, versionId, pageNum, pageSize: 100 })),
            listAttributeBusinessDataTypes(objectTypeId, 'PROJECT'),
            listAttributeBusinessDataTypes(objectTypeId, 'GLOBAL')
        ]);
        const failed = results.find((result) => result.status === 'rejected');
        if (failed) throw failed.reason;
        return {
            attributes: results[0].value.map((item) => ({ ...item, id: String(item.id) })),
            businessTypes: [
                ...(results[1].value.data || []).map((item) => ({ ...item, businessTypeSource: 'PROJECT' })),
                ...(results[2].value.data || []).map((item) => ({ ...item, businessTypeSource: 'GLOBAL' }))
            ]
        };
    }

    function getRowIndex(index) {
        return (query.pageNum - 1) * query.pageSize + index + 1;
    }

    async function getList() {
        const request = ++listRequest;
        if (!versionId.value) {
            rows.value = [];
            total.value = 0;
            loading.value = false;
            return;
        }
        loading.value = true;
        try {
            const response = await listRule({
                versionId: versionId.value,
                category: 'MODEL',
                name: appliedName.value,
                pageNum: query.pageNum,
                pageSize: query.pageSize
            });
            if (request !== listRequest) return;
            total.value = Number(response.data?.total || 0);
            const maxPage = Math.max(1, Math.ceil(total.value / query.pageSize));
            if (query.pageNum > maxPage) {
                query.pageNum = maxPage;
                return await getList();
            }
            rows.value = response.data?.rows || [];
            selectedIds.value = [];
            tableRef.value?.clearSelection();
        } finally {
            if (request === listRequest) loading.value = false;
        }
    }

    function search() {
        appliedName.value = query.name.trim();
        query.pageNum = 1;
        return getList();
    }

    function resetSearch() {
        queryRef.value?.resetFields();
        return search();
    }

    function handleSelectionChange(selection) {
        selectedIds.value = selection.map((row) => row.id);
    }

    async function loadObjectOptions(objectTypeId) {
        if (!objectTypeId) return;
        const request = dialogRequest;
        attributeLoadingCount.value++;
        try {
            const response = await listObjectAttributeOptions(objectTypeId, versionId.value);
            if (request !== dialogRequest) return;
            attributeOptions[objectTypeId] = response.attributes;
            businessTypeOptions[objectTypeId] = response.businessTypes;
        } catch {
            if (request === dialogRequest) optionsError.value = '属性或业务数据类型加载失败，请重新加载';
        } finally {
            if (request === dialogRequest) attributeLoadingCount.value--;
        }
    }

    async function loadEnums(objectTypeId = form.value.objectTypeId) {
        if (!objectTypeId) return;
        const request = ++enumRequest;
        const dialog = dialogRequest;
        enumsLoading.value = true;
        enumsError.value = '';
        const failures = [];
        try {
            await Promise.allSettled(
                ['PROJECT', 'GLOBAL'].map(async (source) => {
                    try {
                        const response = await listAttributeEnums(objectTypeId, source);
                        if (request !== enumRequest || dialog !== dialogRequest) return;
                        if (!Array.isArray(response.data)) throw new Error('枚举返回格式无效');
                        enumOptions.value = [
                            ...enumOptions.value.filter((item) => item.enumSource !== source),
                            ...response.data.map((item) => ({ ...item, enumSource: source }))
                        ];
                    } catch {
                        if (request !== enumRequest || dialog !== dialogRequest) return;
                        failures.push(source === 'PROJECT' ? '当前项目' : '全局');
                        enumsError.value = failures.join('、') + '枚举加载失败，可选择已加载的枚举；重新展开可重试。';
                    }
                })
            );
        } finally {
            if (request === enumRequest && dialog === dialogRequest) enumsLoading.value = false;
        }
    }

    function findBusinessType(record) {
        return (businessTypeOptions[record.objectTypeId] || []).find(
            (item) => item.businessTypeSource + ':' + item.id === record.businessTypeKey
        );
    }

    function resolvedConfig(record) {
        const selected = record.typeCategory === 2 ? findBusinessType(record) : undefined;
        return selected ? { ...selected, basicTypeCode: Number(selected.dataType) } : record;
    }

    async function loadOptions() {
        const request = dialogRequest;
        optionsLoading.value = true;
        optionsError.value = '';
        try {
            const results = await Promise.allSettled([
                listObjectTypeOptions(versionId.value),
                listRelationOptions(versionId.value)
            ]);
            const failed = results.find((result) => result.status === 'rejected');
            if (failed) throw failed.reason;
            if (request !== dialogRequest) return;
            objectTypeOptions.value = results[0].value;
            relationOptions.value = results[1].value;
            const record = detailOpen.value ? detail.value : form.value;
            const objectIds = getRecordObjectIds(record);
            await Promise.allSettled(objectIds.map(loadObjectOptions));
            if (request === dialogRequest && record.type === 'PROPERTY_RANGE') await loadEnums(record.objectTypeId);
        } catch {
            if (request === dialogRequest) optionsError.value = '选项加载失败，请重新加载';
        } finally {
            if (request === dialogRequest) optionsLoading.value = false;
        }
    }

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

    async function handleUpdate(row) {
        if (!requireVersion()) return;
        const request = ++dialogRequest;
        const response = await getRule(row.id, versionId.value, 'MODEL');
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

    async function handleDetail(row) {
        if (!requireVersion()) return;
        const request = ++dialogRequest;
        const response = await getRule(row.id, versionId.value, 'MODEL');
        if (request !== dialogRequest) return;
        attributeLoadingCount.value = 0;
        resetEnums();
        detail.value = response.data;
        await loadDetailOptions();
        if (request === dialogRequest) detailOpen.value = true;
    }

    async function loadDetailOptions() {
        const request = dialogRequest;
        optionsError.value = '';
        try {
            const options = await listObjectTypeOptions(versionId.value);
            if (request !== dialogRequest) return;
            objectTypeOptions.value = options;
            const relations = await listRelationOptions(versionId.value);
            if (request !== dialogRequest) return;
            relationOptions.value = relations;
            await Promise.allSettled(getRecordObjectIds(detail.value).map(loadObjectOptions));
            if (request === dialogRequest && detail.value.type === 'PROPERTY_RANGE')
                await loadEnums(detail.value.objectTypeId);
        } catch {
            if (request === dialogRequest) ElMessage.warning('部分选项加载失败，详情暂时显示对应编号');
        }
    }

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
            description: form.value.description.trim(),
            createBy: form.value.createBy || userStore.nickName || userStore.name || '当前用户'
        };
        if (data.type === 'PROPERTY_RANGE') {
            const basicType = data.typeCategory === 1 ? data.basicTypeCode : undefined;
            data.unit = [1, 2].includes(basicType) ? data.unit : null;
            data.decimalPlaces = basicType === 2 ? data.decimalPlaces : null;
            data.format = basicType === 4 ? data.format : null;
            data.enumType = basicType === 5 ? data.enumType : null;
            data.enumSource = basicType === 5 && data.enumType ? data.enumSource : null;
        }
        saving.value = true;
        try {
            await (data.id ? updateRule(data) : addRule(data));
            if (context !== contextRequest) return;
            ElMessage.success(data.id ? '修改成功' : '新增成功');
            open.value = false;
            await getList();
        } catch (error) {
            if (context === contextRequest) ElMessage.error(error.message || '保存失败');
        } finally {
            saving.value = false;
        }
    }

    async function handleDelete(row) {
        if (!requireVersion() || deleting.value) return;
        const ids = row ? [row.id] : [...selectedIds.value];
        if (!ids.length) return;
        const context = contextRequest;
        const deletingVersion = versionId.value;
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
            await delRule(ids, deletingVersion, 'MODEL');
            if (context !== contextRequest) return;
            ElMessage.success('删除成功');
            await getList();
        } finally {
            deleting.value = false;
        }
    }

    function cancel() {
        open.value = false;
    }

    function beforeClose(done) {
        if (!saving.value) done();
    }

    function handleDialogClosed() {
        ruleRef.value?.clearValidate();
    }

    function objectTypeLabel(id) {
        return objectTypeOptions.value.find((item) => item.id === id)?.name || id || '-';
    }

    function attributeLabel(objectTypeId, attributeId) {
        return (
            (attributeOptions[objectTypeId] || []).find((item) => item.id === attributeId)?.name || attributeId || '-'
        );
    }

    function getRecordObjectIds(record) {
        return record.type === 'PROPERTY_RANGE' && record.objectTypeId ? [record.objectTypeId] : [];
    }

    function handleTypeChange() {
        const { id, name, description, level, type, createBy, createTime } = form.value;
        form.value = { ...emptyForm(), id, name, description, level, type, createBy, createTime };
        resetEnums();
        nextTick(() => ruleRef.value?.clearValidate());
    }

    function handleObjectTypeChange() {
        form.value.attributeId = '';
        form.value.businessTypeKey = '';
        clearTypeConfig();
        resetEnums();
        if (form.value.type === 'PROPERTY_RANGE') {
            loadObjectOptions(form.value.objectTypeId);
            loadEnums();
        }
    }

    function handleValueTypeChange() {
        form.value.basicTypeCode = undefined;
        form.value.businessTypeKey = '';
        clearTypeConfig();
        nextTick(() => ruleRef.value?.clearValidate(['basicTypeCode', 'businessTypeKey']));
    }

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

    function handleBasicTypeChange() {
        clearTypeConfig();
    }

    function handleBusinessTypeChange() {
        clearTypeConfig();
    }

    function resetEnums() {
        ++enumRequest;
        enumOptions.value = [];
        enumsLoading.value = false;
        enumsError.value = '';
    }

    function validateExpression() {
        const record = form.value;
        const typeExists = (id) => objectTypeOptions.value.some((item) => item.id === id);
        if (record.type === 'DOMAIN_RANGE') {
            const relationExists = relationOptions.value.some((item) => String(item.id) === record.relationId);
            if (!relationExists || !typeExists(record.domainId) || !typeExists(record.rangeId)) {
                return '请选择有效的关系、Domain 和 Range';
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

    function getDetailFields(record) {
        if (record.type === 'DOMAIN_RANGE') {
            return [
                {
                    label: '对象类型关系',
                    value:
                        relationOptions.value.find((item) => String(item.id) === record.relationId)?.name ||
                        record.relationId
                },
                { label: 'Domain', value: objectTypeLabel(record.domainId) },
                { label: 'Range', value: objectTypeLabel(record.rangeId) }
            ];
        }
        if (record.type === 'PROPERTY_RANGE') {
            const businessType = findBusinessType(record);
            const fields = [
                { label: '对象类型', value: objectTypeLabel(record.objectTypeId) },
                { label: '数据属性', value: attributeLabel(record.objectTypeId, record.attributeId) },
                { label: '值类型', value: record.typeCategory === 1 ? '基础数据类型' : '业务数据类型' },
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

    watch(
        versionId,
        () => {
            ++contextRequest;
            ++dialogRequest;
            open.value = false;
            detailOpen.value = false;
            optionsLoading.value = false;
            attributeLoadingCount.value = 0;
            optionsError.value = '';
            resetEnums();
            objectTypeOptions.value = [];
            relationOptions.value = [];
            Object.keys(attributeOptions).forEach((key) => delete attributeOptions[key]);
            Object.keys(businessTypeOptions).forEach((key) => delete businessTypeOptions[key]);
            query.name = '';
            query.pageNum = 1;
            appliedName.value = '';
            rows.value = [];
            selectedIds.value = [];
            total.value = 0;
            getList();
        },
        { immediate: true }
    );

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
        ++listRequest;
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
    .options-error {
        margin-bottom: 16px;
    }

    .business-type-label {
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }
</style>
