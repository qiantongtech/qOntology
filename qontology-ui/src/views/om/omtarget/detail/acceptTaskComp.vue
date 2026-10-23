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
  <div class="justify-between mb15">
    <el-row :gutter="15" class="btn-style">
      <el-col :span="1.5">
        <el-button icon="Stamp" type="primary" plain @click="handleAdd" v-hasPermi="['om:omtarget:targettask:add']"
                   @mousedown="(e) => e.preventDefault()">
          验证
        </el-button>
      </el-col>
    </el-row>
    <div class="justify-end top-right-btn">
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" :columns="columns"></right-toolbar>
    </div>
  </div>
  <el-table stripe height="374px" v-loading="loading" :data="targetList"
            :default-sort="defaultSort" @sort-change="handleSortChange">
    <el-table-column v-if="columns[0].visible" label="编号" align="center" prop="id" width="80"
                     sortable="custom" :sort-orders="['descending', 'ascending']"/>
    <el-table-column v-if="columns[1].visible" label="验证结果" align="center" prop="acceptStatus" width="180">
      <template #default="scope">
        <dict-tag :options="om_accept_status" :value="scope.row.acceptResult"/>
      </template>
    </el-table-column>
    <el-table-column v-if="columns[2].visible" label="判断说明" align="left" prop="explain">
      <template #default="scope">
        {{ scope.row.taskExplain || '-' }}
      </template>
    </el-table-column>
    <el-table-column v-if="columns[3].visible" label="验证人" align="center" prop="createBy" width="180">
      <template #default="scope">
        {{ scope.row.createBy || '-' }}
      </template>
    </el-table-column>
    <el-table-column v-if="columns[4].visible" label="验证时间" align="center" prop="createTime" width="180"
                     sortable="custom" :sort-orders="['descending', 'ascending']">
      <template #default="scope">
        <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
      </template>
    </el-table-column>

    <el-table-column v-if="columns[5].visible" label="操作" align="center" class-name="small-padding fixed-width"
                     fixed="right" width="240">
      <template #default="scope">
        <el-button link type="primary" icon="view" @click="handleDetail(scope.row)"
                   v-hasPermi="['om:omtarget:targettask:query']">详情
        </el-button>
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
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
  />

  <!-- 添加或修改建模目标对话框 -->
  <el-dialog :title="title" v-model="open" width="800px" :append-to="$refs['app-container']" draggable>
    <template #header="{ close, titleId, titleClass }">
          <span role="heading" aria-level="2" class="el-dialog__title">
            {{ title }}
          </span>
      <button aria-label="el.dialog.close" class="el-dialog__headerbtn" type="button">
        <i class="el-icon el-dialog__close"><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
          <path fill="currentColor"
                d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z">
          </path>
        </svg></i>
      </button>
    </template>
    <el-form ref="targetRef" :model="form" :rules="rules" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="判断结果" prop="acceptResult" class="form-item-result">
            <el-radio-group v-model="form.acceptResult">
              <el-radio key="1" value="1">已覆盖</el-radio>
              <el-radio key="2" value="2">未覆盖</el-radio>
            </el-radio-group>
            <div class="info-tip"><el-icon size="12"><InfoFilled/></el-icon><span>判断结果用于标识目标问题是否被完全覆盖。</span></div>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="判断说明" prop="taskExplain">
            <el-input v-model="form.taskExplain" type="textarea" placeholder="请输入判断说明"  maxlength="200 个字符" show-word-limit/>
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


  <!-- 建模目标详情对话框 -->
  <el-dialog :title="title" v-model="openDetail" width="800px" :append-to="$refs['app-container']" draggable>
    <template #header="{ close, titleId, titleClass }">
        <span role="heading" aria-level="2" class="el-dialog__title">
          {{ title }}
        </span>
      <button aria-label="el.dialog.close" class="el-dialog__headerbtn" type="button">
        <i class="el-icon el-dialog__close">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
            <path fill="currentColor"
                  d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z">
            </path>
          </svg>
        </i>
      </button>
    </template>
    <el-form ref="targetRef" :model="form" label-width="80px">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="判断结果" prop="acceptResult" class="form-item-result">
            <dict-tag
                :options="om_accept_status"
                :value="form.acceptResult"
                class="con-value access-tag"
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="判断说明" prop="taskExplain">
            <div class="form-readonly textarea">
              {{ form.taskExplain }}
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

<script setup name="AcceptTaskComp">
import {addTargetTask, getTargetTask, listTargetTask,} from "@/api/om/omtarget/targetTask";

const {proxy} = getCurrentInstance();
const {om_accept_status} = proxy.useDict('om_accept_status');
const emit = defineEmits(['updateAcceptStatus'])

const targetList = ref([]);

// 列显隐信息
const columns = ref([
  {key: 0, label: "编号", visible: true},
  {key: 1, label: "验证结果", visible: true},
  {key: 2, label: "判断说明", visible: true},
  {key: 3, label: "验证人", visible: true},
  {key: 4, label: "验证时间", visible: true},
  {key: 5, label: "操作", visible: true},
]);

const open = ref(false);
const openDetail = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const defaultSort = ref({prop: "createTime", order: "descending"});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    orderByColumn: defaultSort.value.prop,
    isAsc: defaultSort.value.order
  },
  rules: {
    acceptResult: [{required: true, message: "判断结果不能为空", trigger: "blur"}],
    taskExplain: [{required: true, message: "判断说明不能为空", trigger: "blur"}],
  }
});

const {queryParams, form, rules} = toRefs(data);

const props = defineProps({
  targetDetail: {
    type: Object,
    default: () => {
    }
  }
})

onMounted(() => {
  getList();
})


/** 查询建模目标列表 */
function getList() {
  queryParams.value.targetId = props.targetDetail.id;
  loading.value = true;
  listTargetTask(queryParams.value).then(response => {
    targetList.value = response.data.rows;
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
    targetId: props.targetDetail.id,
    acceptResult: null,
    taskExplain: null,
  };
  proxy.resetForm("targetRef");
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
  title.value = "验证建模目标";
}

/** 详情按钮操作 */
function handleDetail(row) {
  reset();
  const _id = row.id || ids.value
  getTargetTask(_id).then(response => {
    form.value = response.data;
    openDetail.value = true;
    title.value = "验证任务详情";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["targetRef"].validate(valid => {
    if (valid) {
      addTargetTask(form.value).then(() => {
        emit('updateAcceptStatus',form.value.acceptResult);
        proxy.$modal.msgSuccess("验证成功");
        open.value = false;
        getList();
      })
    }
  });
}

</script>

<style scoped>
.info-tip {
  display: flex;
  flex-direction: row;
  align-items: center;
  font-size: x-small;
  color: #a8aaae;
  margin-left: 5px;
  justify-content: flex-start;
  gap: 5px;
  white-space: nowrap; /* 禁止换行，不裁切 */
}

.form-item-result :deep(.el-form-item__content) {
  flex-direction: column;
  align-items: flex-start;
}
</style>
