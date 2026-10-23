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
  <div class="app-container stagingIndex">
    <!-- 演示环境重要提醒 -->
    <GuideTip v-if="isDemo" tip-id="index" />
    <el-row :gutter="15">
      <el-col :xs="24" :sm="24" :md="18" :lg="18" class="home-gutter">
        <div class="userInfo module-1">
          <div class="info-main">
            <img :src="userStore.avatar" alt="" class="avatar" />
            <div class="info-con">
              <div class="info-con-name">
                {{ greeting }}，{{ userStore.nickName }}，欢迎回来！
              </div>
              <div class="info-con-desc">
                <span style="color: var(--el-color-primary)"> 系统管理员 </span>
                <el-divider direction="vertical" />
                {{ xljtcont }}
                <!-- 不要再纠缠于“长期”和“短期”，很多时候长期已经成为了现在不做事情的借口 -->
              </div>
            </div>
            <div class="info-btns">
              <div class="info-btn" @click="goprofile">
                <div class="icon"></div>
                <span>个人中心</span>
              </div>
              <div class="info-btn btn-dft" @click="logout">
                <div class="icon"></div>
                <span>退出登录</span>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="24" :md="6" :lg="6" class="home-gutter">
        <!-- 模块2 -->
        <!-- <div class="module-2">
                    <img src="@/assets/system/images/index/weather.png" alt="" srcset="" />
                </div> -->
        <div class="module-2">
          <!-- <iframe
            width="100%"
            scrolling="no"
            height="150"
            frameborder="0"
            allowtransparency="true"
            src="https://i.tianqi.com?c=code&id=21&icon=1&site=12"
          ></iframe> -->
          <Weather />
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :md="18" :lg="18" class="home-gutter">
        <!-- 模块3 -->
        <div class="module-3">
          <div
              class="module-item"
              v-for="(item, index) in module1"
              :key="index"
          >
            <!-- <router-link to="/sy/index"> -->
            <div class="module-item-t">
              <div class="module-item-t-l">
                <div class="name">{{ item.name }}</div>
                <span>{{ item.value }}</span>
              </div>
              <img :src="item.img" class="module-item-t-r" />
            </div>
            <!-- </router-link> -->

            <div class="module-item-border"></div>
            <div class="module-item-data">
              <span>周同比：</span>
              <span :class="[item.up ? 'data-up' : 'data-down']"
              >{{ item.speed }}%
              </span>
            </div>
          </div>
        </div>

        <el-row :gutter="15">
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <!-- 模块4 饼图 -->
            <div class="module-4 border-item">
              <div class="border-item-head">
                <span class="head-title">本体数据分布 </span>
              </div>
              <div class="border-item-body">
                <div class="chart-container" ref="module4ChartRef"></div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="24" :md="12" :lg="12">
            <!-- 模块3 柱状图 -->
            <div class="module-5 border-item">
              <div class="border-item-head">
                <span class="head-title">近7日本体数据新增</span>
              </div>
              <div class="border-item-body">
                <div class="chart-container" ref="module5ChartRef"></div>
              </div>
            </div>
          </el-col>
        </el-row>
      </el-col>

      <el-col :xs="24" :sm="24" :md="6" :lg="6" class="home-gutter">
        <div class="border-item module-6 home-gutter">
          <div class="border-item-head">
            <span class="head-title">新闻公告 </span>
            <el-link type="primary" :underline="false" @click="goxinwen('list')"
            >查看更多
            </el-link>
          </div>
          <div class="border-item-body">
            <div
                class="module-item"
                v-for="(item, index) in module6"
                :key="index"
                @click="goxinwen(item)"
            >
              <dict-tag :options="sys_notice_type" :value="item.noticeType" />
              <div class="value" :title="item.noticeTitle">
                {{ item.noticeTitle }}
              </div>
              <div class="time">
                {{ parseTime(item.createTime, "{y}-{m}-{d}") }}
              </div>
            </div>
          </div>
        </div>
        <div class="border-item module-7">
          <div class="news">
            <div class="border-item">
              <div class="border-item-head">
                <span class="head-title">快捷功能入口</span>
                <router-link to="/"> </router-link>
              </div>
              <div class="border-item-body">
                <div class="all-entrance">
                  <div
                      class="entrance-item"
                      v-for="item in visibleEntranceList"
                      :key="item.name"
                      role="button"
                      tabindex="0"
                      :aria-label="item.name"
                      @click="openEntrance(item)"
                      @keydown.enter="openEntrance(item)"
                      @keydown.space.prevent="openEntrance(item)"
                  >
                    <div class="image">
                      <img :src="item.iconPath" class="icon" />
                    </div>
                    <div class="name">{{ item.name }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :md="9" :lg="9">
        <div class="module-8 border-item">
          <div class="border-item-head">
            <span class="head-title">近半年本体数据新增趋势 </span>
            <!-- <el-link type="primary" :underline="false">查看更多 </el-link> -->
          </div>
          <div class="border-item-body">
            <div class="chart-container" ref="module8ChartRef"></div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="24" :md="15" :lg="15">
        <div class="module-9 border-item">
          <div class="border-item-head">
            <span class="head-title">建模目标</span>
            <el-select v-model="targetProjectId" class="target-project-select" placeholder="请选择项目" size="default"
                       filterable aria-label="切换建模目标项目"
            >
              <el-option v-for="project in targetProjects" :key="project.dictValue" :label="project.dictLabel" :value="String(project.dictValue)"/>
            </el-select>
          </div>
          <div class="border-item-body">
            <el-table :data="module9" style="width: 100%" height="100%">
              <el-table-column
                  fixed
                  prop="id"
                  label="编号"
                  align="center"
                  width="80"
                  sortable
              >
                <template #default="scope">
                  <div class="table-column-code">{{ scope.row.id }}</div>
                </template>
              </el-table-column>
              <el-table-column
                  prop="name"
                  label="目标问题"
                  width="300"
                  :show-overflow-tooltip="{ effect: 'light' }"
              />
              <el-table-column prop="acceptStatus" align="center" label="验证状态">
                <template #default="scope">
                  <dict-tag
                      :options="om_accept_status"
                      :value="scope.row.acceptStatus"
                  />
                </template>
              </el-table-column>
              <el-table-column
                  prop="priority"
                  align="center"
                  label="优先级"
              >
                <template #default="scope">
                  <dict-tag
                      :options="om_target_priority"
                      :value="scope.row.priority"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="createBy" align="center" label="创建人" />
              <el-table-column
                  prop="createTime"
                  sortable
                  align="center"
                  label="创建时间"
                  width="180"
              />
            </el-table>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- License agreement pop-up window -->
    <el-dialog
        v-model="licenseDialogVisible"
        :title="license.title"
        :close-on-click-modal="false"
        :show-close="false"
        @close="handleLicenseClose"
    >
      <div class="license-content">
        <p
            v-for="item in license.text.intro"
            :key="item"
            class="license-paragraph"
        >
          {{ item }}
        </p>

        <div
            v-for="(item, index) in license.text.terms"
            :key="item.title"
            class="license-term"
        >
          <div class="license-term-title">
            <span class="license-term-index">{{ index + 1 }}</span>
            <span>{{ item.title }}</span>
          </div>
          <p v-for="desc in item.desc" :key="desc" class="license-term-desc">
            {{ desc }}
          </p>
        </div>

        <div class="license-notice">
          <span>{{ license.text.notice.before }}</span>
          <a
              :href="license.text.notice.link"
              target="_blank"
              rel="noopener noreferrer"
          >
            {{ license.text.notice.linkText }}
          </a>
          <span>{{ license.text.notice.after }}</span>
        </div>

        <p
            v-for="item in license.text.footer"
            :key="item"
            class="license-footer-text"
        >
          {{ item }}
        </p>
      </div>
      <template #footer>
        <el-button @click="toPolicy">商用授权</el-button>
        <el-button
            type="primary"
            class="license-aware-btn"
            :disabled="licenseCountdown > 0"
            @click="handleLicenseClose"
        >
          {{
            licenseCountdown > 0
                ? `我已知晓 (${licenseCountdown}s)`
                : "我已知晓"
          }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Index">
import useUserStore from "@/store/system/user";
import { listNotice } from "@/api/system/system/notice.js";
import Weather from "@/components/Weather/index.vue";
import {projectDict} from '@/api/basic/project/project';
import {getProjectVersionDict} from '@/api/basic/project/projectVersion';
import * as echarts from "echarts";
// eslint-disable-next-line no-unused-vars
import {
  computed,
  onBeforeUnmount,
  onMounted,
  onActivated,
  onDeactivated,
  nextTick,
  ref,
  watch,
  getCurrentInstance,
} from "vue";

let { proxy } = getCurrentInstance();
const {om_accept_status, om_target_priority} = proxy.useDict('om_accept_status', 'om_target_priority');
const greeting = computed(() => {
  const hour = new Date().getHours();
  return hour < 6 ? '凌晨好' : hour < 12 ? '上午好' : hour < 18 ? '下午好' : '晚上好';
});
const router = useRouter();
const { sys_notice_type } = proxy.useDict("sys_notice_type");
const getAssetsFile = (url) => {
  return new URL(`../../assets/system/images/index/${url}`, import.meta.url)
      .href;
};
const getEntranceIcon = (url) => {
  return new URL(`../../assets/system/images/entrance/ontology/${url}`, import.meta.url)
      .href;
};

const license = {
  title: "开源协议",
  text: {
    intro: [
      " qOntology 依据 Apache License 2.0 提供，并受以下针对  qOntology 的特定附加条件约束：",
      "在本协议中，“ qOntology” 是指整个软件项目，包括但不限于其前端、后端、API 及相关文档的全部或部分内容。",
      "在本协议中，“制作方” 是指江苏千桐科技有限公司。",
    ],
    terms: [
      {
        title:
            "您可以将  qOntology 用于商业目的，但不得移除、隐藏或修改  qOntology 中显示的  qOntology 标识、版权声明、许可声明或归属信息。",
        desc: [
          "如果您希望移除、隐藏、遮蔽、替换或修改上述标识、版权声明、许可声明或归属信息，包括但不限于白标使用、OEM 分发、品牌重塑或将  qOntology 作为其他产品展示，您必须向制作方另行申请商业许可。",
        ],
      },
      {
        title: "作为贡献者，您应当同意：",
        desc: [
          "a. 制作方可根据需要调整开源协议，使其更为严格或宽松。",
          "b. 您贡献的代码可能被用于商业目的，包括但不限于其云服务业务运营。",
        ],
      },
    ],
    notice: {
      before:
          "除上述特定条件外，其他所有权利和限制均遵循 Apache License 2.0 协议。有关 Apache License 2.0 的详细信息，请访问 ",
      link: "http://www.apache.org/licenses/LICENSE-2.0",
      linkText: "http://www.apache.org/licenses/LICENSE-2.0",
      after: "。",
    },
    footer: ["本产品的交互设计受外观专利保护。", "© 2025 江苏千桐科技有限公司"],
  },
};

// License pop-up window logic
const LICENSE_CACHE_KEY = " qKnow_license_acknowledged";
const LICENSE_COUNTDOWN_SECONDS = 10;
const licenseDialogVisible = ref(false);
const licenseCountdown = ref(0);
let licenseCountdownTimer = null;

function clearLicenseCountdown() {
  if (licenseCountdownTimer) {
    clearInterval(licenseCountdownTimer);
    licenseCountdownTimer = null;
  }
}

function startLicenseCountdown() {
  clearLicenseCountdown();
  licenseCountdown.value = LICENSE_COUNTDOWN_SECONDS;
  licenseCountdownTimer = setInterval(() => {
    if (licenseCountdown.value <= 1) {
      clearLicenseCountdown();
      licenseCountdown.value = 0;
      return;
    }

    licenseCountdown.value -= 1;
  }, 1000);
}

/**
 * Check whether the user has agreed to the License agreement
 */
function checkLicense() {
  const acknowledged = localStorage.getItem(LICENSE_CACHE_KEY);
  if (!acknowledged) {
    licenseDialogVisible.value = true;
    startLicenseCountdown();
  }
}

/**
 * Handle License pop-up window closing event
 * Record that the user has agreed to the License agreement
 */
function handleLicenseClose() {
  clearLicenseCountdown();
  localStorage.setItem(LICENSE_CACHE_KEY, "true");
  licenseDialogVisible.value = false;
}

/**
 * Jump to License agreement page
 */
function toPolicy() {
  window.open("https://community.qontology.ai/business/policy.html");
}

// 首页展示用的本体工程快捷入口；图标来自平台提供的素材。
const entranceList = [
  {name: '项目管理', path: '/basic/project', perm: ['basic:project:project:list']},
  {name: '枚举管理', path: '/basic/basicEnum', perm: ['basic:enumeration:type:list']},
  {name: '数据源', path: '/basic/datasource', perm: ['basic:datasource:datasource:list']},
  {name: '文件分类', path: '/basic/fileCategory', perm: ['basic:file:category:list']},
  {name: '文件管理', path: '/basic/fileManager', perm: ['basic:file:manager:list']},
  {name: '建模目标', path: '/om/:versionId/target', perm: ['om:target:target:list']},
  {name: '语义资产', path: '/om/:versionId/semantic/term', perm: ['om:term:term:list']},
  {name: '本体建模', path: '/om/:versionId/ontology/objectType', perm: ['om:object:type:list']},
].map(item => ({...item, iconPath: getEntranceIcon(`${item.name}.png`)}));
const visibleEntranceList = computed(() => {
  const permissions = userStore.permissions || [];
  const routes = router.getRoutes();
  return entranceList.filter(item => {
    const allowed = !item.perm.length || permissions.some(permission => permission === '*:*:*' || item.perm.includes(permission));
    return allowed && routes.some(route => route.path === item.path);
  });
});
let entranceOpening = false;
async function openEntrance(item) {
  if (!item.path.includes(':versionId')) return routeTo(item.path);
  if (entranceOpening) return;
  entranceOpening = true;
  try {
    // 业务跳转读取真实版本，避免把演示数据中的编号带入业务页面。
    const projects = targetProjects.value.length ? targetProjects.value : (await projectDict())?.data || [];
    if (!projects.length) return proxy.$modal.msgWarning('请先创建项目');
    const project = projects.find(project => String(project.dictValue) === targetProjectId.value) || projects[0];
    const versions = (await getProjectVersionDict(project.dictValue))?.data || [];
    if (!versions.length) return proxy.$modal.msgWarning('当前项目暂无版本');
    await routeTo(item.path.replace(':versionId', encodeURIComponent(String(versions[0].dictValue))));
  } catch {
    // 请求失败由统一拦截器提示，保留在首页。
  } finally {
    entranceOpening = false;
  }
}

const chartIntances = [];
// eslint-disable-next-line no-unused-vars
const userStore = useUserStore();
// 以下统计、图表与建模目标均为首页演示数据。
const module1 = ref([
  {name: '项目总数', value: 12, up: true, speed: 20, img: getAssetsFile('1.png')},
  {name: '建模目标总数', value: 36, up: false, speed: 12.5, img: getAssetsFile('2.png')},
  {name: '对象类型总数', value: 128, up: true, speed: 16.4, img: getAssetsFile('3.png')},
  {name: '对象关系总数', value: 86, up: false, speed: 10.3, img: getAssetsFile('4.png')},
  {name: '术语总数', value: 298, up: true, speed: 18.3, img: getAssetsFile('5.png')},
]);

// 项目下拉仅切换展示选项，当前首页演示目标暂不联动过滤。
const targetProjectId = ref('');
const targetProjects = ref([]);
async function initTargetProjects() {
  try {
    targetProjects.value = (await projectDict())?.data || [];
    targetProjectId.value = targetProjects.value.length ? String(targetProjects.value[0].dictValue) : '';
  } catch {
    targetProjects.value = [];
  }
}

// 新闻公告跳转：查看更多进入列表，点击具体公告进入详情页。
function goxinwen(row) {
  if (row === "list") {
    proxy.$router.push("/system/notice");
  } else if (row && row.noticeId != null) {
    proxy.$router.push({
      path: "/system/notice/detail",
      query: { id: row.noticeId },
    });
  } else {
    proxy.$router.push("/system/notice");
  }
}

function goprofile() {
  proxy.$router.push("/user/profile"); // 内部页面路径
}

// 认证模式
const authType = import.meta.env.VITE_APP_AUTH_TYPE;
// 演示模式标识
const isDemo = import.meta.env.VITE_APP_MODE === "demo";
function logout() {
  ElMessageBox.confirm("确定注销并退出系统吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
      .then(() => {
        userStore.logOut().then(() => {
          if (authType === "sso") {
            // 退出统一认证中心的登录状态
            loginOut(userStore.userId).then(() => {
              location.href = "/index";
            });
          } else {
            location.href = "/index";
          }
        });
      })
      .catch(() => {});
}

async function routeTo(link, query = {}) {
  if (link && link.indexOf("http") !== -1) {
    window.location.href = link;
    return;
  }

  if (link) {
    if (link === router.currentRoute.value.path) {
      window.location.reload();
    } else {
      try {
        await router.push({ path: link, query });
        // 跳转成功后再刷新
        // window.location.reload();
      } catch (err) {
        console.error("路由跳转失败:", err);
      }
    }
  }
}
//获取心灵鸡汤内容
const xljtcont = ref("");
function getxljtcont() {
  let xljtlist = [
    { value: "统一业务语义，构建可复用的本体模型。" },
    { value: "从业务问题出发，让本体连接知识与应用。" },
  ];
  let num = Math.floor(Math.random() * xljtlist.length);
  xljtcont.value = xljtlist[num].value;
}

const module4ChartRef = ref(null);
function initModule4() {
  const intance = echarts.init(module4ChartRef.value, "macarons");
  let m2R2Data = [
    {value: 128, legendname: '对象类型', name: '对象类型'},
    {value: 312, legendname: '对象属性', name: '对象属性'},
    {value: 86, legendname: '对象关系', name: '对象关系'},
    {value: 256, legendname: '术语', name: '术语'},
    {value: 42, legendname: '枚举', name: '枚举'},
  ];

  let option = {
    title: [
      // {
      //     text: '标题',
      //     textStyle: {
      //         fontSize: 16,
      //         color: 'black'
      //     },
      //     left: '2%'
      // },
      {
        text: "100%",
        // subtext: 12312 + '个',
        textStyle: {
          fontSize: 30,
          color: "rgba(0,0,0,0.65)",
          fontFamily: "Sharp",
        },
        // subtextStyle: {
        //     fontSize: 20,
        //     color: 'black'
        // },
        textAlign: "center",
        x: "34.5%",
        y: "43%",
      },
    ],
    tooltip: {
      trigger: "item",
      formatter: function (parms) {
        let str =
            parms.seriesName +
            "</br>" +
            parms.marker +
            "" +
            parms.data.legendname +
            "</br>" +
            "数量：" +
            parms.data.value +
            "</br>" +
            "占比：" +
            parms.percent +
            "%";
        return str;
      },
    },
    legend: {
      type: "scroll",
      orient: "vertical",
      left: "75%",
      align: "left",
      top: "10%",
      // top: 'middle',
      itemWidth: 8, // 设置图例项的宽度
      itemHeight: 8, // 设置图例项的高度
      icon: "circle", // 设置图例图标为圆形
      // textStyle: {
      //     color: '#8C8C8C',
      //     fontSize: 14,
      //     lineHeight: 20 // 设置行高，以确保文本垂直居中
      // },

      formatter: function (name) {
        // 找到对应的项并计算百分比
        console.log(name, "============name");
        let total = m2R2Data.reduce((sum, item) => sum + item.value, 0);
        let item = m2R2Data.find((item) => item.name === name);
        // eslint-disable-next-line no-unused-vars
        let percent = item
            ? ((item.value / total) * 100).toFixed(2) + "%"
            : "0%";
        // return `${name} |  ${percent}  ${item.value}`; // 自定义图例显示
        // 使用 HTML 语法自定义颜色
        let arr = [
          "{a|" + name + "}",
          // '{b|' + '|   ' + percent + '}',
          // '{c|' + item.value + '}'
        ];
        return arr.join("  ");
        // return `${name}: <span style="color: red;">${percent}</span> (<span style="color: blue;">${percent}</span>)`;
      },
      textStyle: {
        lineHeight: 25, // 设置行高，以确保文本垂直居中

        // 添加
        padding: [0, 0, 0, 0],
        rich: {
          a: {
            fontSize: 14,
            width: 46,
          },
          b: {
            fontSize: 14,
            width: 70,
            color: "#888888",
          },
          c: {
            fontSize: 14,
            color: "rgba(0,0,0,0.65)",
          },
        },
      },
      // height:250
    },
    series: [
      {
        name: "本体数据",
        type: "pie",
        center: ["35%", "50%"],
        radius: ["35%", "55%"],
        clockwise: false, //饼图的扇区是否是顺时针排布
        avoidLabelOverlap: false,
        // label: {
        //     normal: {
        //         // show: true,
        //         // position: 'outter',
        //         formatter: function (parms) {
        //             return parms.data.value;
        //         }
        //     }
        // },
        label: {
          formatter: "{d}%", // 显示每个部分的名称和占比
          position: "outside", // 标签位置
        },
        itemStyle: {
          normal: {
            color: function (params) {
              let colorList = [
                {
                  c1: "#1E60FB",
                  c2: "#5D8EFE",
                },
                {
                  c1: "#6CD8D0",
                  c2: "#1DC7B5",
                },
                {
                  c1: "#F9D370",
                  c2: "#F7BD26",
                },
                {
                  c1: "#B28AE9",
                  c2: "#9358E3",
                },
                {
                  c1: "#EA7283",
                  c2: "#F53D57",
                },
              ];
              return new echarts.graphic.LinearGradient(1, 0, 0, 0, [
                {
                  //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
                  offset: 0,
                  color: colorList[params.dataIndex].c1,
                },
                {
                  offset: 1,
                  color: colorList[params.dataIndex].c2,
                },
              ]);
            },
          },
        },
        data: m2R2Data,
      },
    ],
  };
  intance.setOption(option);
  chartIntances.push(intance);
  //  // 窗口大小变化时，自动更新图表大小
  // window.addEventListener('resize', function() {
  //     intance.resize();
  // });
}

const module5ChartRef = ref(null);
function initModule5() {
  const intance = echarts.init(module5ChartRef.value, "macarons");
  intance.setOption({
    tooltip: {
      trigger: "axis",
      confine: true,
      axisPointer: { type: "shadow" },
      valueFormatter: value => `${value} 条`,
    },
    grid: {
      top: "15%",
      bottom: "10%",
      right: "5%",
    },
    legend: {
      show: true,
      itemGap: 50,
      data: ["新增本体数据"],
      icon: "circle",
      itemWidth: 8, // 设置图例项的宽度
      itemHeight: 8, // 设置图例项的高度
      right: "5%",
      top: "1%",
      textStyle: {
        color: "rgba(0,0,0,0.65)",
        fontSize: 15,
        lineHeight: 30, // 设置行高，以确保文本垂直居中
      },
    },
    xAxis: {
      type: "category",
      data: Array.from({length: 7}, (_, index) => {
        const day = new Date();
        day.setDate(day.getDate() - 6 + index);
        return `${String(day.getMonth() + 1).padStart(2, '0')}-${String(day.getDate()).padStart(2, '0')}`;
      }),
    },
    yAxis: {
      type: "value",
      max: 20,
    },
    series: [
      {
        type: "bar",
        name: "新增本体数据",
        barWidth: 30,
        itemStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
                0,
                0,
                0,
                1,
                [
                  {
                    offset: 0,
                    color: "#5b8dfe",
                  },
                  {
                    offset: 1,
                    color: "#2263fc",
                  },
                ],
                false
            ),
          },
        },
        data: [8, 12, 6, 15, 10, 18, 14],
      },
    ],
  });
  chartIntances.push(intance);
}

const module6 = ref([]);

function initModule6() {
  let query = {
    pageNum: 1,
    pageSize: 5,
  };
  listNotice(query).then((response) => {
    module6.value = response.rows || [];
  });
}

const module8ChartRef = ref(null);
function initModule8() {
  const intance = echarts.init(module8ChartRef.value, "macarons");
  intance.setOption({
    tooltip: {
      trigger: "axis",
      confine: true,
      axisPointer: { type: "line" },
      valueFormatter: value => `${value} 条`,
    },
    // title: [
    //     {
    //         text: '告警趋势',
    //         textStyle: {
    //             fontSize: 14,
    //             color: 'rgba(0,0,0,0.65)'
    //         },
    //         right: '2%'
    //     }
    // ],
    legend: {
      show: true,
      itemGap: 50,
      data: ["新增本体数据"],
      icon: "circle",
      itemWidth: 6, // 设置图例项的宽度
      itemHeight: 6, // 设置图例项的高度
      right: "5%",
      top: "1%",
      textStyle: {
        color: "rgba(0,0,0,0.65)",
        fontSize: 15,
        lineHeight: 30, // 设置行高，以确保文本垂直居中
      },
    },
    grid: {
      top: "18%",
      bottom: "10%",
      right: "5%",
    },
    xAxis: {
      type: "category",
      data: Array.from({length: 6}, (_, index) => {
        const month = new Date();
        month.setDate(1);
        month.setMonth(month.getMonth() - 5 + index);
        return `${String(month.getFullYear()).slice(-2)}年${month.getMonth() + 1}月`;
      }),
    },
    yAxis: {
      type: "value",
      max: 250,
    },
    series: [
      {
        name: "新增本体数据",
        lineStyle: {
          normal: {
            color: "#5285fd",
          },
        },
        // itemStyle: {
        //     color: '#3571FC',
        //     borderColor: '#3571FC'
        // },

        // symbol: 'circle', // 数据点形状
        symbolSize: 8, // 数据点大小
        itemStyle: {
          color: "#427afd",
          borderColor: "#427afd", // 边框颜色
          borderWidth: 1, // 边框宽度
        },

        areaStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(
                0,
                0,
                0,
                1,
                [
                  {
                    offset: 0,
                    color: "rgba(204, 220, 254,1)",
                  },
                  {
                    offset: 1,
                    color: "rgba(204, 220, 254,0)",
                  },
                ],
                false
            ),
            // shadowColor: 'rgba(204, 220, 254,0.1)',
            // shadowBlur: 20
          },
        },
        data: [72, 96, 118, 152, 176, 210],
        type: "line",
      },
    ],
  });
  chartIntances.push(intance);
}

function demoDate(daysAgo) {
  const date = new Date();
  date.setDate(date.getDate() - daysAgo);
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} 09:30`;
}
const module9 = ref([
  {id: '1', name: '各组织负责哪些采购订单的维护和管理？', acceptStatus: '1', priority: '3', createBy: '吴同', createTime: demoDate(0)},
  {id: '2', name: '指定供应商能够提供哪些物料？', acceptStatus: '1', priority: '3', createBy: '吴同', createTime: demoDate(1)},
  {id: '3', name: '设备发生故障时需要检查哪些部件？', acceptStatus: '0', priority: '3', createBy: '吴同', createTime: demoDate(2)},
  {id: '4', name: '产品涉及哪些技术标准与质量要求？', acceptStatus: '0', priority: '2', createBy: '吴同', createTime: demoDate(3)},
  {id: '5', name: '物料通过哪些出库单流向哪些客户？', acceptStatus: '1', priority: '2', createBy: '吴同', createTime: demoDate(4)},
]);

function chartIntancesResize() {
  const charts = [
    {element: module4ChartRef.value, init: initModule4},
    {element: module5ChartRef.value, init: initModule5},
    {element: module8ChartRef.value, init: initModule8},
  ];
  charts.forEach(({element, init}) => {
    // 缓存页或路由切换期间容器可能隐藏，等尺寸恢复后再绘制。
    if (!element || !element.clientWidth || !element.clientHeight) return;
    const chart = echarts.getInstanceByDom(element);
    if (chart && !chart.isDisposed()) chart.resize();
    else init();
  });
}

let chartResizeFrame = null;
let chartResizeObserver = null;
let chartsActive = true;
let chartsDestroyed = false;

function stopChartResize() {
  chartResizeObserver?.disconnect();
  chartResizeObserver = null;
  if (chartResizeFrame !== null) {
    window.cancelAnimationFrame(chartResizeFrame);
    chartResizeFrame = null;
  }
}

function scheduleChartResize() {
  nextTick(() => {
    if (!chartsActive || chartsDestroyed) return;
    if (chartResizeFrame !== null) window.cancelAnimationFrame(chartResizeFrame);
    chartResizeFrame = window.requestAnimationFrame(() => {
      chartResizeFrame = null;
      if (chartsActive && !chartsDestroyed) chartIntancesResize();
    });
  });
}

function observeChartContainers() {
  if (!chartsActive || chartsDestroyed) return;
  if (!chartResizeObserver && typeof ResizeObserver !== 'undefined') {
    chartResizeObserver = new ResizeObserver(scheduleChartResize);
    [module4ChartRef.value, module5ChartRef.value, module8ChartRef.value].forEach(element => {
      if (element) chartResizeObserver.observe(element);
    });
  }
  scheduleChartResize();
}

// 获取当前实例
const instance = getCurrentInstance();

const handleSidebarStatusChange = () => {
  scheduleChartResize();
};

// 在组件销毁时移除事件监听
onBeforeUnmount(() => {
  chartsDestroyed = true;
  stopChartResize();
  chartIntances.forEach(chart => {
    if (!chart.isDisposed()) chart.dispose();
  });
  chartIntances.length = 0;
  clearLicenseCountdown();
  window.removeEventListener("resize", scheduleChartResize);
  instance.appContext.config.globalProperties.$bus.off(
      "getsidebarStatus",
      handleSidebarStatusChange
  );
});

onMounted(() => {
  initModule6();
  initTargetProjects();
  getxljtcont();
  nextTick(observeChartContainers);
  checkLicense();
  window.addEventListener("resize", scheduleChartResize);
  instance.appContext.config.globalProperties.$bus.on(
      "getsidebarStatus",
      handleSidebarStatusChange
  );
});

onActivated(() => {
  chartsActive = true;
  nextTick(observeChartContainers);
});

onDeactivated(() => {
  chartsActive = false;
  stopChartResize();
});
</script>

<style scoped lang="scss">
.home-gutter {
  position: relative;
}

.work-time-div {
  width: 85%;
  height: 99%;
  position: absolute;
  /* top: 20px; */
  right: 20px;
  background-color: #ffffff;
  /* color: white; */
  /* font-size: 18px; */
  /* font-weight: bold; */
  /* padding: 30px 30px; */
  border-radius: 2px;
  text-align: center;
  /* cursor: pointer; */
  /* box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); */
  /* transition: transform 0.3s, box-shadow 0.3s;
        }

        .work-time-div:hover {
            /*transform: scale(1.05);*/
  /*box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);*/
}

.work-time-div:active {
  transform: scale(1);
}

.stagingIndex {
  min-width: 1290px;
  max-width: 100%;
  background: #f0f2f5;

  ::v-deep .el-carousel {
    height: 100%;
    .el-carousel__container {
      height: 100%;
    }
    .el-carousel__arrow--left {
      display: none;
    }
    .el-carousel__arrow--right {
      display: none;
    }
  }
}

.module-4,
.module-5,
.module-8,
.module-9 {
  height: 350px !important;
  //   box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
}

.module-6 {
  height: 250px !important;
  //   box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
}
.module-report {
  margin-bottom: 15px;
  height: 60px !important;
}
.module-report .report-button {
  width: 100%;
  height: 60px;
  font-size: 22px;
  border-radius: 2px;
}

.home-gutter {
  margin-bottom: 15px;
}

.userInfo {
  height: 150px;
  padding: 35px 60px 0 32px;
  border-radius: 2px;
  justify-content: space-between;
  align-items: center;
  background: url(@/assets/system/images/index/content.png) no-repeat center;
  background-size: 100% 100%;
  //   box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
}

.userInfo .menu {
  margin-bottom: 16px;
}

.userInfo .menu .el-breadcrumb__inner.is-link {
  font-family: PingFang SC;
  font-size: 14px;
  color: #888888;
  line-height: 22px;
  font-weight: 400;
}

.userInfo .menu .el-breadcrumb__inner {
  font-family: PingFang SC;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  text-align: left;
  line-height: 22px;
  font-weight: 400;
}

.userInfo .menu .el-breadcrumb__separator {
  font-size: 14px;
  color: #888888;
}

.userInfo .info-main {
  min-width: 100%;
  display: flex;
  position: relative;
  flex-shrink: 0;
  align-items: center;
}

.userInfo .info-main .avatar {
  width: 72px;
  height: 72px;
  margin-right: 24px;
  border-radius: 50%;
}

.userInfo .info-main .info-con {
  height: 72px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.userInfo .info-main .info-con .info-con-name {
  font-family: PingFang SC;
  font-size: 20px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 28px;
  font-weight: 600;
}

.userInfo .info-main .info-con .info-con-desc {
  font-family: PingFang SC;
  font-size: 14px;
  color: #888888;
  line-height: 22px;
  font-weight: 400;
}

.info-btns {
  position: absolute;
  right: 0px;
  font-family: PingFang SC;
  font-size: 14px;
  font-weight: 500;

  img {
    margin-right: 5px;
  }

  .info-btn {
    cursor: pointer;
    color: #fff;
    width: 100px;
    height: 40px;
    display: inline-flex;
    justify-content: center;
    align-items: center;
    border-radius: 4px;
    background: var(--el-color-primary);

    .icon {
      width: 15px;
      height: 15px;
      background: url("@/assets/system/images/index/user-icon.png") no-repeat;
      margin-right: 5px;
    }
  }

  .btn-dft {
    color: var(--el-color-primary);
    border: 1px solid var(--el-color-primary);
    background: transparent;
    margin-left: 20px;

    .icon {
      width: 16px;
      height: 13px;
      background: url("@/assets/system/images/index/logout-icon.png") no-repeat;
    }
  }
}

.module-2 {
  height: 150px;
  // background-color: #fff;
  border-radius: 2px;
  text-align: center;
  //   box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);

  img {
    height: 100%;
  }
}

.module-2 #mobile12 .wtleft {
  width: 30%;
}

.module-3 {
  display: flex;

  justify-content: space-between;
  height: 150px;
  margin-bottom: 15px;
}

.module-3 .module-item {
  // width: 18.3%;
  // min-width: 185px;
  flex: 1;
  height: 100%;
  border-radius: 2px;
  padding: 20px;
  background: #fff;
  margin-right: 15px;
  //   box-shadow: 1px 1px 3px rgba(0, 0, 0, 0.2);
}
.module-3 .module-item:last-of-type {
  margin-right: 0px;
}

.module-3 .module-item .module-item-t {
  display: flex;

  align-items: center;

  justify-content: space-between;
}

.module-3 .module-item .module-item-t .module-item-t-l {
  font-family: PingFang SC;
  font-size: 14px;
  color: #888888;
  line-height: 22px;
  font-weight: 400;
}

.module-3 .module-item .module-item-t .module-item-t-l .name {
  margin-bottom: 12px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.module-3 .module-item .module-item-t .module-item-t-l span {
  font-family: Sharp;
  // font-size: 34px;
  font-size: 30px;
  color: #000;
}

.module-3 .module-item .module-item-t .module-item-t-r {
  width: 48px;
  height: 48px;
}

.module-3 .module-item .module-item-border {
  height: 1px;
  background: #e2e2e2;
  margin: 20px 0 12px;
}

.module-3 .module-item .module-item-data {
  display: flex;

  align-items: center;
  // color: #666;
  color: rgba(0, 0, 0, 0.65);
  font-size: 14px;
}

.module-3 .module-item .module-item-data .data-up {
  font-size: 14px;
  margin: 0 10px;
  // color: #000;
  color: rgba(0, 0, 0, 0.85);
  position: relative;
}

.module-3 .module-item .module-item-data .data-up:after {
  content: "";
  position: absolute;
  top: -2px;
  right: -16px;
  width: 0;
  height: 12px;
  border: 6px solid #f5222d;
  border-color: transparent transparent #f5222d transparent;
}

.module-3 .module-item .module-item-data .data-down {
  font-size: 14px;
  margin: 0 10px;
  color: #000;
  position: relative;
}

.module-3 .module-item .module-item-data .data-down:after {
  content: "";
  position: absolute;
  top: 4px;
  right: -16px;
  width: 0;
  height: 12px;
  border: 6px solid #f5222d;
  border-color: #52c41a transparent transparent transparent;
}

.module-3 .module-item .module-item-data .data-equal {
  font-size: 14px;
  margin: 0 10px;
  color: #000;
  position: relative;
}

.border-item {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 2px;
}

.border-item .border-item-head {
  height: 50px;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e8e8e8;
}

.target-project-select {
  width: 280px;
  max-width: 55%;
  flex-shrink: 0;
}

.target-project-select :deep(.el-select__wrapper) {
  min-height: 32px;
  font-size: 14px;
  border-radius: 2px;
}

.border-item .border-item-head .head-title {
  font-size: 16px;
  color: #000000d9;
  display: flex;
  align-items: center;
  font-family: PingFang SC;
  font-weight: 500;
}
.border-item .border-item-head .head-title-seach {
  cursor: pointer;
  font-size: 14px;
  color: #0f62ff;
  line-height: 0px;
  font-style: normal;
}

.border-item .border-item-head .head-title:before {
  display: inline-block;
  content: "";
  width: 6px;
  height: 16px;
  border-radius: 3px;
  background: var(--el-color-primary);
  margin-right: 8px;
}

.border-item .border-item-body {
  height: calc(100% - 50px);
  position: relative;
}

.border-item .border-item-body .chart-container {
  height: 100%;
}

.border-item .border-item-body .border-item-order {
  display: flex;
  justify-content: space-between;
  padding: 12px 20px 0 20px;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  line-height: 22px;
  font-weight: 400;
}

.border-item .border-item-body .border-item-order .name-span {
  width: 225px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}

.border-item .border-item-body .border-item-order .name-span:hover {
  color: #409eff;
}

.border-item .border-item-body .border-item-order .time-span {
  margin-left: 20px;
  width: 60px;
  min-width: 60px;
  height: 22px;
  font-size: 14px;
  font-family: HelveticaNeue;
  color: #888888;
  line-height: 22px;
}

.border-item .border-item-body .border-item-order .top {
  width: 50px;
  height: 22px;
  background: rgba(255, 0, 25, 0.06);
  border-radius: 2px;
  color: #ff0019;
}

.border-item .border-item-body .border-item-order .hui-span {
  background: #f0f2f5 !important;
  color: rgba(0, 0, 0, 0.65) !important;
}

.module3-item {
  background-color: #f7f7f7;
}

.module-6 {
  .border-item-body {
    overflow-y: auto;
    overflow-x: hidden;
    padding: 12px 0px;
  }
  .module-item {
    display: flex;
    align-items: center;
    padding: 6px 20px;
    cursor: pointer;
  }
  // .value,
  // .title {
  //     margin: 0 10px;
  // }

  .value {
    margin: 0 10px;
    font-size: 14px;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;
    flex: 1;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.65);
  }

  .time {
    width: 90px;
    text-align: right;
    font-size: 14px;
    color: #888888;
  }
}

.module-7 {
  height: auto;
  min-height: 250px;
  .news {
    height: auto;

    > .border-item {
      height: auto;
    }

    .border-item-body {
      height: auto;
      min-height: 200px;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .toAll {
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: #262626;
      font-weight: 400;

      &:hover {
        color: #0f62ff;
      }
    }

    .all-entrance {
      width: 100%;
      display: grid;
      grid-template-columns: repeat(4, 1fr);

      .entrance-item {
        padding: 14px 4px;
        text-align: center;
        font-size: 14px;
        cursor: pointer;
        border-radius: 4px;
        .name {
          margin-top: 5px;
          color: #5a5e66;
        }

        .image {
          height: 40px;
          display: flex;
          justify-content: center;

          // 图标圆角方形背景
          .icon-background {
            width: 40px;
            height: 40px;
            border-radius: 6px;
            position: relative;
            display: flex;
            justify-content: center;
            align-items: center;
          }

          // 图标
          .icon {
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 24px;
            color: #fff;
            line-height: 40px;
          }

          // 背景色：主色
          .color-primary {
            background-image: linear-gradient(
                to bottom right,
                #2162fc 30%,
                #4f84fd 80%
            );
          }

          // 背景色：橙色
          .color-orange {
            background-image: linear-gradient(
                to bottom right,
                #f59040 30%,
                #f9bd77 90%
            );
          }

          // 背景色：淡蓝色
          .color-pale-blue {
            background-image: linear-gradient(
                to bottom right,
                #348bf2 10%,
                #63abff 60%
            );
          }

          // 背景色：粉红色
          .color-pink {
            background-image: linear-gradient(
                to bottom right,
                #fb6594 20%,
                #fc92bb 80%
            );
          }
        }
      }
    }
  }
}

.module-9 {
  // ::v-deep .el-table {
  //     thead .el-table__cell.is-leaf {
  //         background-color: #fff !important;
  //     }
  // }
}
.module-9 {
  .border-item-body {
    padding: 15px 34px 15px 34px;
  }
}

.license-content {
  padding: 8px 24px 0;
  color: rgba(0, 0, 0, 0.82);
  font-family: PingFang SC, Microsoft YaHei, Helvetica Neue, Arial, sans-serif;
  font-size: 14px;
  line-height: 1.8;

  .license-paragraph,
  .license-term-desc,
  .license-footer-text {
    margin: 0;
    white-space: pre-wrap;
    word-break: break-word;
  }

  .license-paragraph {
    margin-bottom: 18px;
  }

  .license-paragraph:first-child {
    margin-bottom: 20px;
    color: rgba(0, 0, 0, 0.9);
    font-size: 16px;
    font-weight: 600;
    line-height: 1.6;
  }

  .license-term {
    padding: 18px 18px 17px 16px;
    background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
    border: 1px solid #d8e0ed;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.95);
  }

  .license-term + .license-term {
    margin-top: 8px;
  }

  .license-term-title {
    display: flex;
    align-items: flex-start;
    gap: 10px;
    color: rgba(0, 0, 0, 0.9);
    font-weight: 600;
    line-height: 1.7;
  }

  .license-term-index {
    flex: 0 0 20px;
    width: 20px;
    height: 20px;
    margin-top: 2px;
    border-radius: 50%;
    background: #1767ff;
    color: #ffffff;
    font-size: 12px;
    font-weight: 600;
    line-height: 20px;
    text-align: center;
  }

  .license-term-desc {
    margin-top: 14px;
    padding-left: 30px;
    color: rgba(0, 0, 0, 0.78);
  }

  .license-term-desc + .license-term-desc {
    margin-top: 2px;
  }

  .license-notice {
    position: relative;
    margin-top: 8px;
    padding: 12px 16px 12px 44px;
    color: rgba(0, 0, 0, 0.78);
    background: #fbfdff;
    border: 1px solid #cddcff;
    line-height: 1.8;
    word-break: break-word;

    a {
      color: #1767ff;
      text-decoration: none;
    }
  }

  .license-notice::before {
    content: "i";
    position: absolute;
    width: 18px;
    height: 18px;
    margin-left: -30px;
    margin-top: 3px;
    border: 1px solid #1767ff;
    border-radius: 50%;
    color: #1767ff;
    font-size: 13px;
    font-weight: 600;
    line-height: 17px;
    text-align: center;
  }

  .license-footer-text {
    margin-top: 18px;
    color: rgba(0, 0, 0, 0.82);
    font-weight: 600;
  }

  .license-footer-text:last-child {
    margin-bottom: 12px;
  }
}

.license-aware-btn {
  min-width: 120px;
}
</style>
<style lang="scss" scoped>
@media screen and (max-width: 1280px) {
}
@media screen and (max-width: 992px) {
  .stagingIndex {
    min-width: 100%;
    padding: 12px;
    .userInfo {
      height: 100px;
      padding: 15px 15px;
      .info-main {
        align-items: center;
        .avatar {
          width: 48px;
          height: 48px;
          margin-right: 12px;
        }
        .info-con {
          .info-con-name {
            font-size: 14px;
            line-height: 20px;
          }
          .info-con-desc {
            font-size: 10px;
            line-height: 20px;
          }
        }
      }
      .info-btns {
        display: none;
      }
    }
    .module-3 {
      flex-direction: column;
      height: auto;
    }
    .module-3 .module-item {
      width: 100%;
      margin-bottom: 15px;
    }
  }
}
@media screen and (max-width: 768px) {
  .stagingIndex {
    min-width: 100%;
    padding: 12px;
    .userInfo {
      height: 100px;
      padding: 15px 15px;
      .info-main {
        align-items: center;
        .avatar {
          width: 48px;
          height: 48px;
          margin-right: 12px;
        }
        .info-con {
          .info-con-name {
            font-size: 14px;
            line-height: 20px;
          }
          .info-con-desc {
            font-size: 10px;
            line-height: 20px;
          }
        }
      }
      .info-btns {
        display: none;
      }
    }
    .module-3 {
      flex-direction: column;
      height: auto;
    }
    .module-3 .module-item {
      width: 100%;
      margin-bottom: 15px;
    }
  }
}
@media screen and (max-width: 576px) {
  .stagingIndex {
    min-width: 100%;
    padding: 12px;
    .userInfo {
      height: 100px;
      padding: 15px 15px;
      .info-main {
        align-items: center;
        .avatar {
          width: 48px;
          height: 48px;
          margin-right: 12px;
        }
        .info-con {
          .info-con-name {
            font-size: 14px;
            line-height: 20px;
          }
          .info-con-desc {
            font-size: 10px;
            line-height: 20px;
          }
        }
      }
      .info-btns {
        display: none;
      }
    }
    .module-3 {
      flex-direction: column;
      height: auto;
    }
    .module-3 .module-item {
      width: 100%;
      margin-bottom: 15px;
    }
  }
}
</style>
