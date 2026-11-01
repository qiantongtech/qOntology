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
  <div class="app-container" ref="app-container">
    <GuideTip tip-id="basic/datasource.list"/>
    <qt-wrap :columns="tableStore.columns" :table-ref="tableRef">
      <template #search>
        <qt-search-bar
            :items="searchStore.items"
            :params="tableStore.params"
            :table-ref="tableRef"
        >
          <template #datasourceTypePrefix="{ value }">
            <img
                v-if="value && getDatasourceIcon(value)"
                :src="getDatasourceIcon(value)"
                class="prefix-icon"
                alt=""
            />
          </template>
          <template #datasourceTypeOption="{ option }">
            <div class="option-content">
              <img
                  v-if="getDatasourceIcon(option.value)"
                  :src="getDatasourceIcon(option.value)"
                  class="option-icon"
                  alt=""
              />
              <span class="option-text">{{ option.label }}</span>
            </div>
          </template>
        </qt-search-bar>
      </template>

      <template #actions-data>
        <el-button
            type="primary"
            plain
            @click="handleAdd"
            v-hasPermi="['basic:datasource:datasource:add']"
            @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-xinzeng mr5"></i>新增
        </el-button>
        <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['basic:datasource:datasource:remove']"
            @mousedown="(e) => e.preventDefault()"
        >
          <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
        </el-button>
      </template>

      <qt-table v-bind="tableStore" ref="tableRef">
        <template #datasourceType="{ row }">
          <QtTagGroup
              :datasource-type="row.datasourceType"
              :label="datasourceTypeMap[row.datasourceType] || row.datasourceType"
              center
          />
        </template>

        <template #handle="{ row }">
          <el-button
              v-hasPermi="['basic:datasource:datasource:edit']"
              link
              type="primary"
              icon="View"
              @click="handleTestConnection(row)"
          >测试连接
          </el-button>
          <el-button
              v-hasPermi="['basic:datasource:datasource:edit']"
              link
              type="primary"
              icon="Edit"
              @click="handleUpdate(row)"
          >修改
          </el-button>
          <el-popover
              placement="bottom"
              :width="120"
              trigger="click"
              popper-class="handle-popover"
          >
            <template #reference>
              <el-button link type="primary" icon="ArrowDown">更多</el-button>
            </template>
            <el-button
                link
                type="primary"
                icon="View"
                @click="handleDetail(row)"
                v-hasPermi="['basic:datasource:datasource:edit']"
            >详情
            </el-button>
            <el-button
                link
                type="danger"
                icon="Delete"
                @click="handleDelete(row)"
                v-hasPermi="['basic:datasource:datasource:remove']"
            >删除
            </el-button>
          </el-popover>
        </template>
      </qt-table>
    </qt-wrap>

    <!-- 添加或修改数据源对话框 -->
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
                </span>
      </template>
      <el-form
          ref="daDatasourceRef"
          :model="form"
          :rules="rules"
          label-width="110px"
          @submit.prevent
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="datasourceName">
              <el-input
                  v-model="form.datasourceName"
                  placeholder="请输入名称"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="类型" prop="datasourceType">
              <el-tree-select
                  v-model="form.datasourceType"
                  :data="datasource_type"
                  :props="{ value: 'value', label: 'label', children: 'children' }"
                  value-key="value"
                  check-strictly
                  filterable
                  placeholder="请选择类型"
              >
                <template #prefix>
                  <img
                      v-if="form.datasourceType && getDatasourceIcon(form.datasourceType)"
                      :src="getDatasourceIcon(form.datasourceType)"
                      class="prefix-icon"
                      alt=""
                  />
                </template>
                <template #default="{ data }">
                  <div class="option-content">
                    <img
                        v-if="getDatasourceIcon(data.value)"
                        :src="getDatasourceIcon(data.value)"
                        class="option-icon"
                        alt=""
                    />
                    <span class="option-text">{{ data.label }}</span>
                  </div>
                </template>
              </el-tree-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="账号" prop="username">
              <el-input v-model="form.username" placeholder="请输入账号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                  v-model="form.password"
                  placeholder="请输入密码"
                  v-if="title === '添加数据源'"
              />
              <el-input
                  type="password"
                  v-model="form.password"
                  placeholder="请输入密码"
                  v-if="title !== '添加数据源'"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12" v-if="form.datasourceType !== null">
            <el-form-item label="数据库名称" prop="dbname">
              <el-input v-model="form.dbname" placeholder="请输入数据库名称"/>
            </el-form-item>
          </el-col>
          <el-col
              :span="12"
              v-if="
                            form.datasourceType !== null &&
                            form.datasourceType !== 'DM8' &&
                            form.datasourceType !== 'MySql'
                        "
          >
            <el-form-item label="模式" prop="sid">
              <el-input v-model="form.sid" placeholder="请输入模式"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="地址" prop="ip">
              <el-input v-model="form.ip" placeholder="请输入地址"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="端口号" prop="port">
              <el-input v-model="form.port" placeholder="请输入端口号"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <el-input
                  type="textarea"
                  v-model="form.description"
                  placeholder="请输入描述"
                  maxlength="512个字符"
                  show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input
                  type="textarea"
                  v-model="form.remark"
                  placeholder="请输入备注"
                  maxlength="512个字符"
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

    <!-- 数据源详情对话框 -->
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
                </span>
      </template>
      <el-form ref="daDatasourceRef" :model="form" label-width="110px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="名称" prop="datasourceName">
              <div class="form-readonly">
                {{ form.datasourceName }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型" prop="datasourceType">
              <dict-tag :options="datasource_type" :value="form.datasourceType"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="账号" prop="username">
              <div class="form-readonly">
                {{ form.username }}
              </div>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <div class="form-readonly">***********</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="数据库名称" prop="dbname">
              <div class="form-readonly">
                {{ form.dbname }}
              </div>
            </el-form-item>
          </el-col>

          <el-col
              :span="12"
              v-if="
                            form.datasourceType !== 'DM8' &&
                            form.datasourceType !== null &&
                            form.datasourceType !== 'MySql'
                        "
          >
            <el-form-item label="模式" prop="sid">
              <div class="form-readonly">
                {{ form.sid }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="地址" prop="ip">
              <div class="form-readonly">
                {{ form.ip }}
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="端口号" prop="port">
              <div class="form-readonly">
                {{ form.port }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="描述" prop="description">
              <div class="form-readonly textarea">
                {{ form.description || '-' }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <div class="form-readonly textarea">
                {{ form.remark || '-' }}
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
  </div>
</template>

<script setup name="DaDatasource">
import {
  addDaDatasource,
  clientsTest,
  delDaDatasource,
  getDaDatasource,
  listDaDatasource,
  updateDaDatasource
} from '@/api/basic/datasource/daDatasource';
import {useRoute} from "vue-router";
import {getDatasourceIcon} from '@/utils/datasource.js';

const {proxy} = getCurrentInstance();
const {datasource_type} = proxy.useDict('datasource_type');
const route = useRoute();
const tableRef = ref(null);

const datasourceTypeMap = computed(() =>
    (datasource_type.value || []).reduce((map, dict) => {
        map[dict.value] = dict.label;
        return map;
    }, {})
);

// 搜索项配置
const searchStore = reactive({
  items: [
    {label: '名称', prop: 'datasourceName', component: {is: 'input'}},
    {
      label: '类型',
      prop: 'datasourceType',
      component: {
        is: 'select',
        options: datasource_type,
        prefixSlot: 'datasourceTypePrefix',
        optionSlot: 'datasourceTypeOption'
      }
    }
  ]
});

// 表格配置
const tableStore = reactive({
  config: {
    sort: true,
    table: {
      stripe: true,
      rowKey: 'id',
      defaultSort: {prop: 'createTime', order: 'descending'},
      onSelectionChange: handleSelectionChange
    }
  },
  params: {
    graphId: route.params.kgId,
    datasourceName: null,
    datasourceType: null
  },
  func: listDaDatasource,
  columns: [
    {type: 'selection', width: 55},
    {
      label: '编号',
      prop: 'id',
      width: 80,
      align: 'left',
      sortable: 'custom',
      sortOrders: ['descending', 'ascending']
    },
    {
      label: '名称',
      prop: 'datasourceName',
      width: 200,
      align: 'left',
      showOverflowTooltip: {effect: 'light'}
    },
    {
      label: '描述',
      prop: 'description',
      minWidth: 180,
      align: 'left',
      showOverflowTooltip: {effect: 'light'}
    },
    {
      label: '地址',
      prop: 'ip',
      width: 250,
      align: 'left',
      showOverflowTooltip: {effect: 'light'}
    },
    {label: '类型', slot: 'datasourceType', width: 120},
    {label: '创建人', prop: 'createBy', width: 100, align: 'left'},
    {
      label: '创建时间',
      prop: 'createTime',
      date: true,
      width: 160,
      align: 'left',
      sortable: 'custom',
      sortOrders: ['descending', 'ascending']
    },
    {label: '操作', slot: 'handle', width: 240, fixed: 'right'}
  ]
});

const open = ref(false);
const openDetail = ref(false);
const ids = ref([]);
const multiple = ref(true);
const title = ref('');

const data = reactive({
  form: {},
  rules: {
    datasourceName: [{required: true, message: '名称不能为空', trigger: 'blur'}],
    datasourceType: [{required: true, message: '类型不能为空', trigger: 'change'}],
    datasourceConfig: [
      {
        required: true,
        message: '数据源配置(json字符串)不能为空',
        trigger: 'blur'
      }
    ],
    ip: [{required: true, message: '地址不能为空', trigger: 'blur'}],
    port: [{required: true, message: '端口号不能为空', trigger: 'blur'}],
    username: [{required: true, message: '账号不能为空', trigger: 'blur'}],
    password: [{required: true, message: '密码不能为空', trigger: 'blur'}],
    dbname: [{required: true, message: '数据库名称不能为空', trigger: 'blur'}],
    sid: [{required: true, message: '模式不能为空', trigger: 'blur'}]
  }
});

const {form, rules} = toRefs(data);

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
    graphId: route.params.kgId,
    datasourceName: null,
    datasourceType: null,
    datasourceConfig: null,
    ip: null,
    port: null,
    listCount: null,
    syncCount: null,
    dataSize: null,
    description: null,
    validFlag: null,
    createBy: null,
    creatorId: null,
    createTime: null,
    updateBy: null,
    updaterId: null,
    updateTime: null,
    remark: null
  };
  proxy.resetForm('daDatasourceRef');
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = '新增数据源';
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _id = row.id || ids.value;
  getDaDatasource(_id).then((response) => {
    form.value = response.data;
    if (form.value.datasourceConfig) {
      const config = JSON.parse(form.value.datasourceConfig);
      form.value.username = config.username;
      form.value.password = config.password;
      form.value.dbname = config.dbname;
      form.value.sid = config.sid;
    }
    open.value = true;
    title.value = '修改数据源';
  });
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value;
  getDaDatasource(_id).then((response) => {
    form.value = response.data;
    if (form.value.datasourceConfig) {
      const config = JSON.parse(form.value.datasourceConfig);
      form.value.username = config.username;
      form.value.password = config.password;
      form.value.dbname = config.dbname;
      form.value.sid = config.sid;
    }
    openDetail.value = true;
    title.value = '数据源详情';
  });
}

/** 详情按钮操作 */
function handleTestConnection(row) {
  reset();
  const _id = row.id || ids.value;
  clientsTest(_id).then((response) => {
    console.log(response);
    proxy.$modal.msgSuccess(response.msg);
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs['daDatasourceRef'].validate((valid) => {
    if (valid) {
      if (form.value.id != null) {
        form.value.datasourceConfig = JSON.stringify({
          username: form.value.username,
          password: form.value.password,
          dbname: form.value.dbname,
          sid: form.value.sid
        });
        updateDaDatasource(form.value)
            .then((response) => {
              proxy.$modal.msgSuccess('修改成功');
              open.value = false;
              tableRef.value?.getList();
            })
            .catch((error) => {
            });
      } else {
        form.value.datasourceConfig = JSON.stringify({
          username: form.value.username,
          password: form.value.password,
          dbname: form.value.dbname,
          sid: form.value.sid
        });
        addDaDatasource(form.value)
            .then((response) => {
              proxy.$modal.msgSuccess('新增成功');
              open.value = false;
              tableRef.value?.getList();
            })
            .catch((error) => {
            });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _ids = row.id || ids.value;
  proxy.$modal
      .confirm('是否确认删除数据连接编号为"' + _ids + '"的数据项？')
      .then(function () {
        return delDaDatasource(_ids);
      })
      .then(() => {
        tableRef.value?.getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .catch(() => {
      });
}

</script>
<style scoped lang="scss">
.prefix-icon {
  width: 20px;
  height: 20px;
  object-fit: contain;
  display: block;
}

.option-content {
  display: flex;
  align-items: center;

  .option-icon {
    width: 20px;
    height: 20px;
    margin-right: 8px;
    object-fit: contain;
    flex-shrink: 0;
  }

  .option-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
