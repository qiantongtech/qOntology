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
  <!-- 新增、修改共用字段布局；字段按菜单类型动态显隐。 -->
  <el-dialog
    v-model="visible"
    :title="title"
    width="900px"
    append-to="body"
    draggable
    :close-on-click-modal="false"
    :close-on-press-escape="!saving"
    :show-close="!saving"
    :before-close="handleBeforeClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      :disabled="saving"
      label-width="100px"
      @submit.prevent="submitForm"
    >
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item label="上级菜单" prop="parentId">
            <el-tree-select
              v-model="form.parentId"
              :data="menuOptions"
              :props="{ value: 'menuId', label: 'menuName', children: 'children' }"
              value-key="menuId"
              check-strictly
              placeholder="选择上级菜单"
              class="field-full"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="菜单类型" prop="menuType">
            <el-radio-group v-model="form.menuType">
              <el-radio value="M">目录</el-radio>
              <el-radio value="C">菜单</el-radio>
              <el-radio value="F">按钮</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item label="菜单图标" prop="icon">
            <el-popover placement="bottom-start" :width="540" trigger="click">
              <template #reference>
                <el-input
                  v-model="form.icon"
                  placeholder="点击选择图标"
                  readonly
                  @blur="showSelectIcon"
                >
                  <template #prefix>
                    <svg-icon v-if="form.icon" :icon-class="form.icon" class="el-input__icon icon-prefix" />
                    <el-icon v-else class="icon-prefix"><search /></el-icon>
                  </template>
                </el-input>
              </template>
              <icon-select ref="iconSelectRef" :active-icon="form.icon" @selected="selected" />
            </el-popover>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="显示排序" prop="orderNum">
            <el-input-number
              v-model="form.orderNum"
              class="field-full"
              controls-position="right"
              :min="0"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="菜单名称" prop="menuName">
            <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item prop="routeName">
            <template #label>
              <span>
                <el-tooltip
                  content="默认不填则和路由地址相同：如地址为：`user`，则名称为`User`（注意：因为router会删除名称相同路由，为避免名字的冲突，特殊情况下请自定义，保证唯一性）"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                路由名称
              </span>
            </template>
            <el-input v-model="form.routeName" placeholder="请输入路由名称" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item prop="path">
            <template #label>
              <span>
                <el-tooltip
                  content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                路由地址
              </span>
            </template>
            <el-input v-model="form.path" placeholder="请输入路由地址" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip
                  content="选择是外链则路由地址需要以`http(s)://`开头"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                是否外链
              </span>
            </template>
            <el-radio-group v-model="form.isFrame">
              <el-radio value="0">是</el-radio>
              <el-radio value="1">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item prop="component">
            <template #label>
              <span>
                <el-tooltip
                  content="访问的组件路径，如：`system/user/index`，默认在`views`目录下"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                组件路径
              </span>
            </template>
            <el-input v-model="form.component" placeholder="请输入组件路径" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'M'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip
                  content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                权限字符
              </span>
            </template>
            <el-input v-model="form.perms" maxlength="100" placeholder="请输入权限标识" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip
                  content='访问路由的默认传递参数，如：`{"id": 1, "name": "ry"}`'
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                路由参数
              </span>
            </template>
            <el-input v-model="form.query" maxlength="255" placeholder="请输入路由参数" />
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType == 'C'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip
                  content="选择是则会被`keep-alive`缓存，需要匹配组件的`name`和地址保持一致"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                是否缓存
              </span>
            </template>
            <el-radio-group v-model="form.isCache">
              <el-radio value="0">缓存</el-radio>
              <el-radio value="1">不缓存</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col v-if="form.menuType != 'F'" :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip
                  content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                显示状态
              </span>
            </template>
            <el-radio-group v-model="form.visible">
              <el-radio
                v-for="dict in sys_show_hide"
                :key="dict.value"
                :value="dict.value"
              >{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item>
            <template #label>
              <span>
                <el-tooltip
                  content="选择停用则路由将不会出现在侧边栏，也不能被访问"
                  placement="top"
                >
                  <el-icon class="info-icon"><InfoFilled /></el-icon>
                </el-tooltip>
                菜单状态
              </span>
            </template>
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="dict in sys_normal_disable"
                :key="dict.value"
                :value="dict.value"
              >{{ dict.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button :disabled="saving" @click="visible = false">取 消</el-button>
        <el-button
          type="primary"
          :loading="saving"
          :disabled="validating"
          @click="submitForm"
        >确 定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup name="MenuFormDialog">
import { getCurrentInstance, nextTick, ref } from "vue";

import { addMenu, getMenu, updateMenu } from "@/api/system/system/menu.js";
import IconSelect from "@/components/IconSelect/index.vue";

// 弹窗独立管理数据加载、校验及保存，父页面仅负责打开与刷新列表。
const emit = defineEmits(["success"]);
const { proxy } = getCurrentInstance();
const { sys_show_hide, sys_normal_disable } = proxy.useDict("sys_show_hide", "sys_normal_disable");

const visible = ref(false);
const title = ref("");
const saving = ref(false);
const validating = ref(false);
const formRef = ref();
const iconSelectRef = ref(null);
const form = ref({});
const menuOptions = ref([]);
let openRequest = 0;

const rules = {
  menuName: [{ required: true, message: "菜单名称不能为空", trigger: "blur" }],
  orderNum: [{ required: true, message: "菜单顺序不能为空", trigger: "blur" }],
  path: [{ required: true, message: "路由地址不能为空", trigger: "blur" }]
};

/** 每次打开重建表单；只采纳最新详情请求，避免快速切换记录时回显旧数据。 */
async function openDialog(options) {
  if (saving.value) return;
  const request = ++openRequest;
  const response = options.menuId != null ? await getMenu(options.menuId) : null;
  if (request !== openRequest) return;
  title.value = options.title;
  menuOptions.value = options.menuOptions || [];
  form.value = response
    ? { ...response.data }
    : {
        menuId: undefined,
        parentId: options.parentId ?? 0,
        menuName: undefined,
        icon: undefined,
        menuType: 'M',
        orderNum: 0,
        isFrame: '1',
        isCache: '0',
        visible: '0',
        status: '0'
      };
  visible.value = true;
  await nextTick();
  formRef.value?.clearValidate();
}

/** 保存期间阻止标题栏关闭，确保请求完成后才结束当前编辑。 */
function handleBeforeClose(done) {
  if (!saving.value) done();
}

/** 图标输入框失焦时重置图标选择面板。 */
function showSelectIcon() {
  iconSelectRef.value?.reset();
}

/** 选中图标后回填表单。 */
function selected(name) {
  form.value.icon = name;
}

/** 校验并保存当前菜单；重复点击和已关闭的弹窗均不提交。 */
async function submitForm() {
  if (validating.value || saving.value || !visible.value) return;
  validating.value = true;
  const draft = form.value;
  try {
    const valid = await formRef.value.validate().catch(() => false);
    if (!valid || !visible.value || draft !== form.value) return;
    saving.value = true;
    const editing = draft.menuId != null;
    await (editing ? updateMenu({ ...draft }) : addMenu({ ...draft }));
    proxy.$modal.msgSuccess(editing ? "修改成功" : "新增成功");
    visible.value = false;
    emit("success");
  } finally {
    validating.value = false;
    saving.value = false;
  }
}

defineExpose({ openDialog });
</script>

<style scoped lang="scss">
.field-full {
  width: 100%;
}

.icon-prefix {
  height: 32px;
  width: 16px;
}

.info-icon {
  color: var(--qt-text-secondary);
}
</style>
