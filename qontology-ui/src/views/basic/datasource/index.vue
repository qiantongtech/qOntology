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
    <div class="pagecont-top" v-show="showSearch">
      <el-form
          class="btn-style"
          :model="queryParams"
          ref="queryRef"
          :inline="true"
          v-show="showSearch"
          @submit.prevent
      >
        <el-form-item label="名称" prop="datasourceName">
          <el-input
              class="el-form-input-width"
              v-model="queryParams.datasourceName"
              placeholder="请输入名称"
              clearable
              @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="类型" prop="datasourceType">
          <el-select
              class="el-form-input-width"
              v-model="queryParams.datasourceType"
              placeholder="请选择类型"
              clearable
          >
            <el-option
                v-for="dict in datasource_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
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
    </div>

    <div class="pagecont-bottom">
      <div class="justify-between mb15">
        <el-row :gutter="15" class="btn-style">
          <el-col :span="1.5">
            <el-button
                type="primary"
                plain
                @click="handleAdd"
                v-hasPermi="['basic:datasource:datasource:add']"
                @mousedown="(e) => e.preventDefault()"
            >
              <i class="iconfont-mini icon-xinzeng mr5"></i>新增
            </el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                       v-hasPermi="['basic:datasource:datasource:remove']"
                       @mousedown="(e) => e.preventDefault()">
              <i class="iconfont-mini icon-shanchu-huise mr5"></i>删除
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
          :data="daDatasourceList"
          @selection-change="handleSelectionChange"
          :default-sort="defaultSort"
          @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column v-if="getColumnVisibility(0)" label="编号" align="center" width="80"
            sortable="custom" :sort-orders="['descending', 'ascending']">
          <template #default="scope">
            {{ scope.row.id }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(1)" label="名称" prop="datasourceName" width="200"
            :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.datasourceName || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(2)" label="描述" prop="description"
            :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.description || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(3)" label="地址" prop="ip" width="250"
            :show-overflow-tooltip="{ effect: 'light' }">
          <template #default="scope">
            {{ scope.row.ip || '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(4)" label="类型" align="center" prop="datasourceType" width="100">
          <template #default="scope">
            <dict-tag :options="datasource_type" :value="scope.row.datasourceType"/>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(5)" label="创建人" align="center" prop="createBy" width="100">
          <template #default="scope">
            <span>{{ scope.row.createBy }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="getColumnVisibility(6)" label="创建时间" align="center" prop="createTime" width="180"
            sortable="custom" :sort-orders="['descending', 'ascending']">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="getColumnVisibility(6)" label="操作" align="center" width="240"
            class-name="small-padding fixed-width" fixed="right">
          <template #default="scope">
            <el-button v-hasPermi="['basic:datasource:datasource:edit']" link type="primary" icon="view"
                       @click="handleTestConnection(scope.row)">测试连接
            </el-button>
            <el-button
                v-hasPermi="['basic:datasource:datasource:edit']"
                link
                type="primary"
                icon="Edit"
                @click="handleUpdate(scope.row)"
            >修改
            </el-button>
            <el-popover placement="bottom" :width="150" trigger="click">
              <template #reference>
                <el-button link type="primary" icon="ArrowDown">更多</el-button>
              </template>
              <div class="butgdlist">
                <el-button
                    link
                    type="danger"
                    icon="Delete"
                    @click="handleDelete(scope.row)"
                    v-hasPermi="['basic:datasource:datasource:remove']"
                >删除
                </el-button>
                <el-button
                    link
                    type="primary"
                    icon="view"
                    @click="handleDetail(scope.row)"
                    v-hasPermi="['basic:datasource:datasource:edit']"
                >详情
                </el-button>
              </div>
            </el-popover>
          </template>
        </el-table-column>

        <template #empty>
          <div class="emptyBg">
            <img src="@/assets/system/images/no_data/noData.png" alt=""/>
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
              <el-select v-model="form.datasourceType" placeholder="请选择类型">
                <el-option
                    v-for="dict in datasource_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
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
import {useRoute, useRouter} from "vue-router";
import {parseTime} from "@/utils/anivia.js";

const {proxy} = getCurrentInstance();
const {datasource_type} = proxy.useDict('datasource_type');
const daDatasourceList = ref([]);

// 列显隐信息
const columns = ref([
  {key: 0, label: '编号', visible: true},
  {key: 1, label: '名称', visible: true},
  {key: 2, label: '描述', visible: true},
  {key: 3, label: '地址', visible: true},
  {key: 4, label: '类型', visible: true},
  {key: 5, label: '创建人', visible: true},
  {key: 6, label: '创建时间', visible: true},
  {key: 7, label: '操作', visible: true}
]);

const getColumnVisibility = (key) => {
  const column = columns.value.find((col) => col.key === key);
  // 如果没有找到对应列配置，默认显示
  if (!column) return true;
  // 如果找到对应列配置，根据visible属性来控制显示
  return column.visible;
};

const open = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref('');
const defaultSort = ref({prop: 'createTime', order: 'descending'});
const router = useRouter();
const route = useRoute();

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    datasourceName: null,
    datasourceType: null,
    datasourceConfig: null,
    ip: null,
    port: null,
    listCount: null,
    syncCount: null,
    dataSize: null,
    description: null,
    createTime: null,
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order
  },
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

const {queryParams, form, rules} = toRefs(data);

onMounted(() => {
  getList();
});

/** 查询数据源列表 */
function getList() {
  queryParams.value.graphId = route.params.kgId;
  loading.value = true;
  listDaDatasource(queryParams.value).then((response) => {
    daDatasourceList.value = response.data.rows;
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
              getList();
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
              getList();
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
        getList();
        proxy.$modal.msgSuccess('删除成功');
      })
      .catch(() => {
      });
}

</script>
<style scoped lang="scss">
.butgdlist {
  width: 100px;
}
</style>
