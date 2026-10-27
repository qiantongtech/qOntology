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
  <div class="app-container" v-loading="loading">
    <!--  版本信息  -->
    <div class="version-info">
      <!--   基准版本（左侧）   -->
      <div class="border-item">
        <div class="border-item-head">
          <span class="h2-titles">基准版本</span>
          <el-select
              v-model="currentVersion"
              placeholder="请选择版本"
              class="version-select"
              @change="currentVersionChange"
          >
            <el-option
                v-for="version in modelVersionDict"
                :key="version.dictValue"
                :label="version.dictLabel"
                :value="version.dictValue"
            />
          </el-select>
        </div>
        <div class="border-item-body">
          <div class="infotop">
            <el-row :gutter="2" style="margin-bottom: 5px">
              <el-col :span="12">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">创建时间</div>
                  <div class="infotop-row-value">{{
                      parseTime(currentVersionData.createTime, "{y}-{m}-{d} {h}:{i}")
                    }}
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="24">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">版本描述</div>
                  <div class="infotop-row-value">
                    <el-tooltip
                        :content="currentVersionData.description"
                        placement="top"
                        effect="light"
                        :disabled="!currentVersionData.description"
                        popper-class="tooltip-desc"
                    >
                      <span>{{ currentVersionData.description || "-" }}</span>
                    </el-tooltip>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <!--   对比版本（右侧）   -->
      <div class="border-item">
        <div class="border-item-head">
          <span class="h2-titles">对比版本</span>
          <el-select
              v-model="compareVersion"
              placeholder="请选择版本"
              class="version-select"
              @change="compareVersionChange"
          >
            <el-option
                v-for="version in modelVersionDict"
                :key="version.dictValue"
                :label="version.dictLabel"
                :value="version.dictValue"
            />
          </el-select>
        </div>
        <div class="border-item-body">
          <div class="infotop">
            <el-row :gutter="2" style="margin-bottom: 5px">
              <el-col :span="12">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">创建时间</div>
                  <div class="infotop-row-value">{{
                      parseTime(compareVersionData.createTime, "{y}-{m}-{d} {h}:{i}")
                    }}
                  </div>
                </div>
              </el-col>
            </el-row>
            <el-row :gutter="16">
              <el-col :span="24">
                <div class="infotop-row border-top">
                  <div class="infotop-row-lable">版本描述</div>
                  <div class="infotop-row-value">
                    <el-tooltip
                        :content="compareVersionData.description"
                        placement="top"
                        effect="light"
                        :disabled="!compareVersionData.description"
                        popper-class="tooltip-desc"
                    >
                      <span>{{ compareVersionData.description || "-" }}</span>
                    </el-tooltip>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
    </div>

    <!-- 文件配置 -->
    <div class="version-info" v-if="modelAccessType === 'PYTHON'">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">文件配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <!-- 左边：基准版本 -->
            <div class="table-wrap">
              <el-table :data="diffResult.current.fileResource" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <pre v-else-if="scope.row.type === 'json'">{{ scope.row.fieldValue }}</pre>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">文件配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.compare.fileResource" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <pre v-else-if="scope.row.type === 'json'">{{ scope.row.fieldValue }}</pre>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 请求配置 -->
    <div class="version-info" v-if="modelAccessType === 'API'">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">请求配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current.apiConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">请求配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.compare.apiConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 鉴权配置 -->
    <div class="version-info" v-if="modelAccessType === 'API'">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">鉴权配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current.authConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">鉴权配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <!-- 右边：对比版本 -->
            <div class="table-wrap">
              <el-table :data="diffResult.compare.authConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 连接配置 -->
    <div class="version-info" v-if="modelAccessType === 'REMOTE_EXEC'">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">连接配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current.connectConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">连接配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <!-- 右边：对比版本 -->
            <div class="table-wrap">
              <el-table :data="diffResult.compare.connectConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 执行配置 -->
    <div class="version-info" v-if="modelAccessType === 'REMOTE_EXEC'">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">执行配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current.exeConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">执行配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <!-- 右边：对比版本 -->
            <div class="table-wrap">
              <el-table :data="diffResult.compare.exeConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 结果配置 -->
    <div class="version-info" v-if="modelAccessType === 'REMOTE_EXEC'">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">结果配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current.resultConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">结果配置</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <!-- 右边：对比版本 -->
            <div class="table-wrap">
              <el-table :data="diffResult.compare.resultConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 参数定义 -->
    <div class="version-info">
      <!-- 左边：基准版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">参数定义</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.current.paramConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <pre v-else-if="scope.row.type === 'json'">{{ scope.row.fieldValue }}</pre>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <!-- 右边：对比版本 -->
      <div class="border-item">
        <div class="border-item-head">
          <div class="head-left">
            <span class="h2-titles">参数定义</span>
            <span class="info-tip">红色代表删除，绿色代表新增，黄色代表修改</span>
          </div>
        </div>
        <div class="border-item-body">
          <div class="version-compare">
            <div class="table-wrap">
              <el-table :data="diffResult.compare.paramConfig" :row-class-name="tableRowClassName">
                <el-table-column prop="date" label="字段名">
                  <template #default="scope">
                    <span>{{ scope.row.name }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="字段值">
                  <template #default="scope">
                    <dict-tag v-if="scope.row.type === 'select'"
                              :options="getDictOptions(scope.row.dict)"
                              :value="scope.row.fieldValue">
                    </dict-tag>
                    <pre v-else-if="scope.row.type === 'json'">{{ scope.row.fieldValue }}</pre>
                    <span v-else>{{ scope.row.fieldValue }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup name="ModelVersionDiff">
import {ref, onMounted} from "vue";
import {useRoute} from "vue-router";
import {parseTime} from "@/utils/anivia.js";

import {getModelVersionDict, getModelVersion} from "@/api/model/version";
import {getModelByVersion} from "@/api/model/model.js";

const route = useRoute();

const loading = ref(true)

const modelId = route.query.modelId || "";// 模型ID
const currentVersion = ref("");// 基准版本
const compareVersion = ref("");// 对比版本
const modelVersionDict = ref([]);// 模型版本字典
const modelAccessType = ref("");// 模型访问类型

const currentVersionData = ref({});// 基准版本数据
const compareVersionData = ref({});// 对比版本数据

const currentVersionModel = ref({});// 基准版本模型
const compareVersionModel = ref({});// 对比版本模型

const diffResult = ref({
  current: {
    apiConfig: [],
    authConfig: [],
    paramConfig: [],
    fileResource: [],

    connectConfig: [],
    exeConfig: [],
    resultConfig: [],
  },
  compare: {
    apiConfig: [],
    authConfig: [],
    paramConfig: [],
    fileResource: [],

    connectConfig: [],
    exeConfig: [],
    resultConfig: [],
  },
});

const dictMap = {
  "authTypeDict": [
    {"label": "无鉴权", "value": "NONE", "listClass": "primary"},
    {"label": "固定 Token / API Key", "value": "FIXED", "listClass": "primary"},
    {"label": "动态 Token API", "value": "DYNAMIC", "listClass": "primary"},
  ],
  "authMethodDict": [
    {"label": "Bearer Token", "value": "bearer", "listClass": "primary"},
    {"label": "API Key", "value": "apiKey", "listClass": "primary"},
    {"label": "动态 Token API", "value": "DYNAMIC", "listClass": "primary"},
  ],
  "injectPositionDict": [
    {"label": "Header", "value": "Header", "listClass": "primary"},
    {"label": "Query", "value": "Query", "listClass": "primary"},
  ],
}


const fieldConfigMap = {
  "requestMethod": {"name": "请求方法", "group": ["apiConfig"]},
  "apiUrl": {"name": "接口地址", "group": ["apiConfig"]},
  "contentType": {"name": "content-Type", "group": ["apiConfig"]},
  "timeoutSeconds": {"name": "超时时间", "group": ["apiConfig"]},

  "authType": {"name": "鉴权类型", "group": ["authConfig"], "type": "select", "dict": "authTypeDict"},
  "authMethod": {"name": "鉴权方式", "group": ["authConfig"], "type": "select", "dict": "authMethodDict"},
  "authInjectPosition": {"name": "注入位置", "group": ["authConfig"], "type": "select", "dict": "injectPositionDict"},
  "authKeyName": {"name": "Key", "group": ["authConfig"]},
  "authTokenValue": {"name": "Value", "group": ["authConfig"]},
  "authDynamicMethod": {"name": "Token 接口请求方法", "group": ["authConfig"]},
  "authDynamicUrl": {"name": "Token 接口地址", "group": ["authConfig"]},
  "authDynamicHeaders": {"name": "Token 请求头", "group": ["authConfig"]},
  "authDynamicParams": {"name": "Query 参数", "group": ["authConfig"]},
  "authExtractPath": {"name": "Token 提取路径", "group": ["authConfig"]},
  "authTokenPrefix": {"name": "值前缀", "group": ["authConfig"]},

  "inputSchema": {"name": "参数定义", "group": ["paramConfig"], "type": "json"},

  "fileName": {"name": "文件名", "group": ["fileResource"]},
  "fileSize": {"name": "文件大小", "group": ["fileResource"]},
  "scriptName": {"name": "主文件名称", "group": ["fileResource"]},

  "osType": {"name": "目标环境", "group": ["connectConfig"]},
  "host": {"name": "主机地址", "group": ["connectConfig"]},
  "port": {"name": "端口号", "group": ["connectConfig"]},
  "username": {"name": "用户名", "group": ["connectConfig"]},
  "authPassword": {"name": "密码", "group": ["connectConfig"]},
  "authKey": {"name": "密钥文件", "group": ["connectConfig"]},

  "workDir": {"name": "工作目录", "group": ["exeConfig"]},
  "timeout": {"name": "执行超时时间", "group": ["exeConfig"]},
  "cmdTemplate": {"name": "启动命令", "group": ["exeConfig"]},
  "envList": {"name": "环境变量", "group": ["exeConfig"]},

  "logExtType": {"name": "日志提取方式", "group": ["resultConfig"]},
  "resultExtType": {"name": "结果提取方式", "group": ["resultConfig"]},
  "resultFormat": {"name": "结果格式", "group": ["resultConfig"]},
  "logFilePath": {"name": "日志文件路径", "group": ["resultConfig"]},
  "startMarker": {"name": "结果开始标记", "group": ["resultConfig"]},
  "endMarker": {"name": "结果结束标记", "group": ["resultConfig"]},
  "resultFilePath": {"name": "结果文件路径", "group": ["resultConfig"]},
}

// 基准版本改变
async function currentVersionChange(val) {
  loading.value = true;
  currentVersion.value = val;
  const p1 = getModelVersion(modelId, currentVersion.value).then((res) => {
    currentVersionData.value = res.data;
  })
  const p2 = getModelByVersion(modelId, currentVersion.value).then((res) => {
    currentVersionModel.value = res.data;
  })
  await Promise.all([p1, p2]);
  diffData();
  loading.value = false;
}

// 对比版本改变
async function compareVersionChange(val) {
  loading.value = true;
  compareVersion.value = val;
  const p1 = getModelVersion(modelId, compareVersion.value).then((res) => {
    compareVersionData.value = res.data;
  })
  const p2 = getModelByVersion(modelId, compareVersion.value).then((res) => {
    compareVersionModel.value = res.data;
  })
  await Promise.all([p1, p2]);
  diffData();
  loading.value = false;
}

// 初始化数据
async function initData() {
  currentVersion.value = route.query.currentVersion || "";
  compareVersion.value = route.query.compareVersion || "";
  getModelVersionDict(modelId).then((res) => {
    modelVersionDict.value = res.data;
  })
  getModelVersion(modelId, currentVersion.value).then((res) => {
    currentVersionData.value = res.data;
  })
  getModelVersion(modelId, compareVersion.value).then((res) => {
    compareVersionData.value = res.data;
  })

  const p2 = getModelByVersion(modelId, currentVersion.value).then((res) => {
    currentVersionModel.value = res.data;
  })
  const p3 = getModelByVersion(modelId, compareVersion.value).then((res) => {
    compareVersionModel.value = res.data;
  })
  await Promise.all([p2, p3]);
  modelAccessType.value = currentVersionModel.value["accessType"];

  diffData();
  loading.value = false;
}

// 表格行样式
function tableRowClassName(row) {
  return row.row.changeType;
}

// 对比数据
function diffData() {
  let a = currentVersionModel.value.modelFileResourceRespVO || {};
  let b = compareVersionModel.value.modelFileResourceRespVO || {};
  if (modelAccessType.value === "API") {
    a = currentVersionModel.value.modelConfig || {};
    b = compareVersionModel.value.modelConfig || {};
  }else if (modelAccessType.value === "REMOTE_EXEC") {
    a = currentVersionModel.value.modelRemoteExec || {};
    b = compareVersionModel.value.modelRemoteExec || {};
  }

  const allFields = [...new Set([...Object.keys(a), ...Object.keys(b)])];
  const current = {apiConfig: [], authConfig: [], paramConfig: [], fileResource: [], connectConfig: [],
    exeConfig: [],
    resultConfig: [],};
  const compare = {apiConfig: [], authConfig: [], paramConfig: [], fileResource: [], connectConfig: [],
    exeConfig: [],
    resultConfig: [],};
  for (const field of allFields) {
    let fieldConfig = fieldConfigMap[field];
    if (!fieldConfig) {
      continue;
    }

    const hasA = Object.prototype.hasOwnProperty.call(a, field);
    const hasB = Object.prototype.hasOwnProperty.call(b, field);
    let valA = hasA ? a[field] : "";
    let valB = hasB ? b[field] : "";
    if (!valA && !valB) {
      continue;
    }
    const isEqual = JSON.stringify(valA) === JSON.stringify(valB);
    let typeA, typeB;
    if (valA && valB) {
      if (!isEqual) {
        typeA = "row-yellow";
        typeB = "row-yellow";
      }
    } else if (valA && !valB) {
      // a有 b无
      typeA = "row-green";
      typeB = "row-red";
    } else {
      // a无 b有
      typeA = "row-red";
      typeB = "row-green";
    }
    for (const group of fieldConfig.group) {
      current[group].push({field, fieldValue: valA, changeType: typeA, ...fieldConfig})
      compare[group].push({field, fieldValue: valB, changeType: typeB, ...fieldConfig})
    }
  }
  diffResult.value = {current, compare};
}

// 获取字典选项
function getDictOptions(dictName) {
  if (!dictName) return []
  // 返回对应字典数组，不存在返回空数组
  return dictMap[dictName] || []
}

onMounted(() => {
  initData();
});

</script>

<style lang="scss" scoped>
.border-item {
  width: 100%;
  background: #fff;
  border-radius: 2px;
  padding: 0 16px 12px;
  margin-bottom: 8px;

  .border-item-head {
    height: 50px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #dfe3e9;
    margin: 0 -16px;
    padding: 0 16px;

    .head-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }

  .border-item-body {
    padding: 12px 0 0;
    background-color: #fff;
  }
}

.version-select {
  width: 200px;
}

.version-info {
  width: 100%;
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
  flex-wrap: wrap;
  overflow: auto;

  .border-item {
    flex: 1;
    min-width: 480px;

    &:first-child {
      padding-right: 12px;
    }

    &:last-child {
      padding-left: 12px;
    }
  }
}

.version-compare {
  width: 100%;
  display: flex;
  gap: 0;
  position: relative;
  overflow: hidden;
  flex-wrap: wrap;

  .table-wrap {
    flex: 1;
    overflow: hidden;
    min-width: 480px;

    &:first-child {
      padding-right: 16px;
      //border-right: 1px solid #dfe3e9;
    }

    &:last-child {
      padding-left: 16px;
    }
  }
}
</style>

<style>
.el-table .row-yellow {
  --el-table-tr-bg-color: var(--el-color-warning-light-9);
}

.el-table .row-green {
  --el-table-tr-bg-color: var(--el-color-success-light-9);
}

.el-table .row-red {
  --el-table-tr-bg-color: var(--el-color-danger-light-9);
}
</style>
