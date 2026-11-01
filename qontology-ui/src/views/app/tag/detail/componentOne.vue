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
    <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
            <el-col :span="1.5">
                <el-button
                    type="primary"
                    plain
                    @click="handleAdd"
                    v-hasPermi="['manage:tag:add']"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <i class="iconfont-mini icon-xinzeng mr5"></i>新增
                </el-button>
            </el-col>
            <el-col :span="1.5">
                <el-button
                    type="warning"
                    plain
                    @click="handleExport"
                    v-hasPermi="['manage:tag:export']"
                    @mousedown="(e) => e.preventDefault()"
                >
                    <i class="iconfont-mini icon-download-line mr5"></i>导出
                </el-button>
            </el-col>
        </el-row>
        <div class="justify-end top-right-btn">
            <right-toolbar
                v-model:showSearch="showSearch"
                @queryTable="getList"
                :columns="columns"
            ></right-toolbar>
        </div>
    </div>
    <el-table
        stripe
        v-loading="loading"
        :data="tagList"
        @selection-change="handleSelectionChange"
        :default-sort="defaultSort"
        @sort-change="handleSortChange"
    >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column v-if="columns[0].visible" label="ID" align="center" prop="id" />
        <el-table-column
            v-if="columns[1].visible"
            label="工作区id"
            align="center"
            prop="workspaceId"
        >
            <template #default="scope">
                {{ scope.row.workspaceId || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[2].visible" label="图谱id" align="center" prop="graphId">
            <template #default="scope">
                {{ scope.row.graphId || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[3].visible" label="模型id" align="center" prop="modelId">
            <template #default="scope">
                {{ scope.row.modelId || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[4].visible" label="名称" align="center" prop="name">
            <template #default="scope">
                {{ scope.row.name || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[5].visible" label="描述" align="center" prop="description">
            <template #default="scope">
                {{ scope.row.description || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[6].visible" label="类型" align="center" prop="category">
            <template #default="scope">
                {{ scope.row.category || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[7].visible" label="状态" align="center" prop="status">
            <template #default="scope">
                {{ scope.row.status || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[8].visible" label="备注" align="center" prop="remark">
            <template #default="scope">
                {{ scope.row.remark || '-' }}
            </template>
        </el-table-column>
        <el-table-column v-if="columns[11].visible" label="创建人" align="center" prop="createBy">
            <template #default="scope">
                {{ scope.row.createBy || '-' }}
            </template>
        </el-table-column>
        <el-table-column
            v-if="columns[13].visible"
            label="创建时间"
            align="center"
            prop="createTime"
            width="180"
            sortable="custom"
            :sort-orders="['descending', 'ascending']"
        >
            <template #default="scope">
                <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
            </template>
        </el-table-column>
        <el-table-column
            label="操作"
            align="center"
            class-name="small-padding fixed-width"
            fixed="right"
            width="240"
        >
            <template #default="scope">
                <el-button
                    link
                    type="primary"
                    icon="Edit"
                    @click="handleUpdate(scope.row)"
                    v-hasPermi="['manage:tag:edit']"
                    >修改</el-button
                >
                <el-button
                    link
                    type="danger"
                    icon="Delete"
                    @click="handleDelete(scope.row)"
                    v-hasPermi="['manage:tag:remove']"
                    >删除</el-button
                >
                <el-button
                    link
                    type="primary"
                    icon="view"
                    @click="handleDetail(scope.row)"
                    v-hasPermi="['manage:tag:edit']"
                    >详情</el-button
                >
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

    <!-- 添加或修改知识标签对话框 -->
    <el-dialog
        :title="title"
        v-model="open"
        width="800px"
        :append-to="$refs['app-container']"
        draggable
    >
        <template #header="{ close, titleId, titleClass }">
            <span role="heading" aria-level="2" class="el-dialog__title">
                {{ title }}
                <el-icon size="20" style="color: #909399; font-size: 16px">
                    <InfoFilled />
                </el-icon>
            </span>
            <button aria-label="el.dialog.close" class="el-dialog__headerbtn" type="button">
                <i class="el-icon el-dialog__close"
                    ><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                        <path
                            fill="currentColor"
                            d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z"
                        ></path></svg
                ></i>
            </button>
        </template>
        <el-form ref="tagRef" :model="form" :rules="rules" label-width="80px">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="工作区id" prop="workspaceId">
                        <el-input v-model="form.workspaceId" placeholder="请输入工作区id" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="图谱id" prop="graphId">
                        <el-input v-model="form.graphId" placeholder="请输入图谱id" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="模型id" prop="modelId">
                        <el-input v-model="form.modelId" placeholder="请输入模型id" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <el-input v-model="form.name" placeholder="请输入名称" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="描述" prop="description">
                        <el-input
                            v-model="form.description"
                            type="textarea"
                            placeholder="请输入内容"
                        />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="类型" prop="category">
                        <el-input v-model="form.category" placeholder="请输入类型" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="备注" prop="remark">
                        <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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

    <!-- 知识标签详情对话框 -->
    <el-dialog
        :title="title"
        v-model="openDetail"
        width="800px"
        :append-to="$refs['app-container']"
        draggable
    >
        <template #header="{ close, titleId, titleClass }">
            <span role="heading" aria-level="2" class="el-dialog__title">
                {{ title }}
                <el-icon size="20" style="color: #909399; font-size: 16px">
                    <InfoFilled />
                </el-icon>
            </span>
            <button aria-label="el.dialog.close" class="el-dialog__headerbtn" type="button">
                <i class="el-icon el-dialog__close"
                    ><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                        <path
                            fill="currentColor"
                            d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z"
                        ></path></svg
                ></i>
            </button>
        </template>
        <el-form ref="tagRef" :model="form" label-width="80px">
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="工作区id" prop="workspaceId">
                        <div>
                            {{ form.workspaceId }}
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="图谱id" prop="graphId">
                        <div>
                            {{ form.graphId }}
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="模型id" prop="modelId">
                        <div>
                            {{ form.modelId }}
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="名称" prop="name">
                        <div>
                            {{ form.name }}
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="24">
                    <el-form-item label="描述" prop="description">
                        <div>
                            {{ form.description }}
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="类型" prop="category">
                        <div>
                            {{ form.category }}
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="状态" prop="status">
                        <div>
                            {{ form.status }}
                        </div>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item label="备注" prop="remark">
                        <div>
                            {{ form.remark }}
                        </div>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button size="mini" @click="cancel">关 闭</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup name="ComponentOne">
    import { listTag, getTag, delTag, addTag, updateTag } from '@/api/kg/manage/tag';

    const { proxy } = getCurrentInstance();

    const tagList = ref([]);

    // 列显隐信息
    const columns = ref([
        { key: 0, label: 'ID', visible: true },
        { key: 1, label: '工作区id', visible: true },
        { key: 2, label: '图谱id', visible: true },
        { key: 3, label: '模型id', visible: true },
        { key: 4, label: '名称', visible: true },
        { key: 5, label: '描述', visible: true },
        { key: 6, label: '类型', visible: true },
        { key: 7, label: '状态', visible: true },
        { key: 8, label: '备注', visible: true },
        { key: 9, label: '是否有效', visible: true },
        { key: 10, label: '删除标志', visible: true },
        { key: 11, label: '创建人', visible: true },
        { key: 12, label: '创建人id', visible: true },
        { key: 13, label: '创建时间', visible: true },
        { key: 14, label: '更新人', visible: true },
        { key: 15, label: '创建人id', visible: true },
        { key: 16, label: '更新时间', visible: true }
    ]);

    const open = ref(false);
    const openDetail = ref(false);
    const loading = ref(true);
    const showSearch = ref(true);
    const ids = ref([]);
    const single = ref(true);
    const multiple = ref(true);
    const total = ref(0);
    const title = ref('');
    const defaultSort = ref({ prop: 'createTime', order: 'desc' });

    const data = reactive({
        tagDetail: {},
        form: {},
        queryParams: {
            pageNum: 1,
            pageSize: 10,
            workspaceId: null,
            graphId: null,
            modelId: null,
            name: null,
            description: null,
            category: null,
            status: null,
            createTime: null
        },
        rules: {
            graphId: [{ required: true, message: '图谱id不能为空', trigger: 'blur' }],
            name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
            category: [{ required: true, message: '类型不能为空', trigger: 'blur' }],
            status: [{ required: true, message: '状态不能为空', trigger: 'change' }],
            validFlag: [{ required: true, message: '是否有效不能为空', trigger: 'blur' }],
            delFlag: [{ required: true, message: '删除标志不能为空', trigger: 'blur' }],
            createTime: [{ required: true, message: '创建时间不能为空', trigger: 'blur' }],
            updateTime: [{ required: true, message: '更新时间不能为空', trigger: 'blur' }]
        }
    });

    const { queryParams, form, tagDetail, rules } = toRefs(data);

    /** 查询知识标签列表 */
    function getList() {
        loading.value = true;
        listTag(queryParams.value).then((response) => {
            tagList.value = response.data.rows;
            total.value = response.data.total;
            loading.value = false;
        });
    }

    // 取消按钮
    function cancel() {
        open.value = false;
        openDetail.value = false;
        reset();
    }

    // 表单重置
    function reset() {
        form.value = {
            id: null,
            workspaceId: null,
            graphId: null,
            modelId: null,
            name: null,
            description: null,
            category: null,
            status: null,
            remark: null,
            validFlag: null,
            delFlag: null,
            createBy: null,
            creatorId: null,
            createTime: null,
            updateBy: null,
            updaterId: null,
            updateTime: null
        };
        proxy.resetForm('tagRef');
    }

    /** 搜索按钮操作 */
    function handleQuery() {
        queryParams.value.pageNum = 1;
        getList();
    }

    /** 重置按钮操作 */
    function resetQuery() {
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
    function handleSortChange(column, prop, order) {
        queryParams.value.orderByColumn = column.prop;
        queryParams.value.isAsc = column.order;
        getList();
    }

    /** 新增按钮操作 */
    function handleAdd() {
        reset();
        open.value = true;
        title.value = '添加知识标签';
    }

    /** 修改按钮操作 */
    function handleUpdate(row) {
        reset();
        const _id = row.id || ids.value;
        getTag(_id).then((response) => {
            form.value = response.data;
            open.value = true;
            title.value = '修改知识标签';
        });
    }

    /** 详情按钮操作 */
    function handleDetail(row) {
        reset();
        const _id = row.id || ids.value;
        getTag(_id).then((response) => {
            form.value = response.data;
            openDetail.value = true;
            title.value = '知识标签详情';
        });
    }

    /** 提交按钮 */
    function submitForm() {
        proxy.$refs['tagRef'].validate((valid) => {
            if (valid) {
                if (form.value.id != null) {
                    updateTag(form.value)
                        .then((response) => {
                            proxy.$modal.msgSuccess('修改成功');
                            open.value = false;
                            getList();
                        })
                        .catch((error) => {});
                } else {
                    addTag(form.value)
                        .then((response) => {
                            proxy.$modal.msgSuccess('新增成功');
                            open.value = false;
                            getList();
                        })
                        .catch((error) => {});
                }
            }
        });
    }

    /** 删除按钮操作 */
    function handleDelete(row) {
        const _ids = row.id || ids.value;
        proxy.$modal
            .confirm('是否确认删除知识标签编号为"' + _ids + '"的数据项？')
            .then(function () {
                return delTag(_ids);
            })
            .then(() => {
                getList();
                proxy.$modal.msgSuccess('删除成功');
            })
            .catch(() => {});
    }

    /** 导出按钮操作 */
    function handleExport() {
        proxy.download(
            'kg/tag/export',
            {
                ...queryParams.value
            },
            `tag_${new Date().getTime()}.xlsx`
        );
    }

    getList();
</script>
