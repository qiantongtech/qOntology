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
  <div class="navbar" ref="navbar">
    <logo
      v-if="appStore.sidebar.hide && isOnlyLogoRoute"
      :collapse="false"
      class="navbar-logo"
      :current-route="route.path"
    />
    <hamburger
      id="hamburger-container"
      :is-active="appStore.sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />
    <breadcrumb
      id="breadcrumb-container"
      class="breadcrumb-container"
      v-if="!settingsStore.topNav"
    />
    <top-nav
      id="topmenu-container"
      class="topmenu-container"
      @getRouter="getRouter"
      :getRouterParam="getRouterParam"
      v-if="settingsStore.topNav"
      :class="{ 'has-navbar-logo': appStore.sidebar.hide && isOnlyLogoRoute }"
    />

    <div class="right-menu">
      <template v-if="appStore.device !== 'mobile'">

        <div style="width: 420px; margin-top: 10px" v-if="showSelector">
          <el-form
              class="btn-style"
              :model="route.params"
              ref="queryRef"
              :inline="true"
          >
            <el-form-item label="" prop="kbId">
              <el-select
                  style="width: 180px"
                  :fit-input-width="true"
                  v-model="projectId"
                  @change="projectIdChange"
                  placeholder="请选择所属项目"
                  popper-class="custom-option-style"
              >
                <el-option
                    v-for="item in projectOptions"
                    :key="item.dictValue"
                    :label="item.dictLabel"
                    :value="String(item.dictValue)"
                >
                  <template #default>
                    <template v-if="item.dictLabel.length > 10">
                      <el-tooltip
                          placement="left"
                          :content="item.dictLabel"
                          effect="dark"
                      >
                        <div class="ellipsis-option">{{ item.dictLabel }}</div>
                      </el-tooltip>
                    </template>
                    <template v-else>
                      <div class="ellipsis-option">{{ item.dictLabel }}</div>
                    </template>
                  </template>
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="" prop="projectVersionId">
              <el-select
                  style="margin-left: -15px; width: 120px"
                  :fit-input-width="true"
                  v-model="projectVersionId"

                  placeholder="请选择项目版本"
                  popper-class="custom-option-style"
              >
                <el-option
                    v-for="item in projectVersionOptions"
                    :key="item.dictValue"
                    :label="item.dictLabel"
                    :value="String(item.dictValue)"
                >
                  <template #default>
                    <template v-if="item.dictLabel.length > 10">
                      <el-tooltip
                          placement="left"
                          :content="item.dictLabel"
                          effect="dark"
                      >
                        <div class="ellipsis-option">{{ item.dictLabel }}</div>
                      </el-tooltip>
                    </template>
                    <template v-else>
                      <div class="ellipsis-option">{{ item.dictLabel }}</div>
                    </template>
                  </template>
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
        </div>


        <el-popover
          trigger="hover"
          popper-style="
                        width: 336px;
                        height: 360px;
                        background: #FFFFFF;
                        box-shadow: 0px 2px 8px 0px rgba(0,0,0,0.15);
                        padding:0;
                    "
        >
          <template #reference>
            <el-badge
              :value="msgCount"
              :max="99"
              class="badge"
              :class="msgCount > 0 ? 'flash' : ''"
              :offset="[0, 0]"
              :hidden="msgCount == 0"
            >
              <!--                            <i-->
              <!--                                class="iconfont right-menu-item hover-effect"-->
              <!--                                style="font-size: 22px"-->
              <!--                            >&#xebe7;</i>-->
              <i
                class="iconfont icon-a-dingbulingdangxianxing right-menu-item hover-effect"
                style="font-size: 20px"
              ></i>
            </el-badge>
          </template>
          <template #default>
            <el-tabs
              v-model="activeMsg"
              stretch
              class="mag-tabs"
              @tab-click="handleClick"
            >
              <el-tab-pane label="消息提醒" name="first">
                <div class="message-list">
                  <div
                    class="msg-item"
                    v-for="(msg, index) in messages"
                    :key="index"
                    v-show="messages.length > 0"
                    @click="clickViewMessage(msg)"
                  >
                    <img
                      class="icon"
                      src="@/assets/system/images/layout/msg/icon1.png"
                      alt=""
                    />
                    <div class="content">
                      <div class="title">{{ msg.title }}</div>
                      <div class="time">{{ msg.time }}</div>
                    </div>
                  </div>
                  <el-empty
                    v-show="
                      messages.length == 0 ||
                      messages == null ||
                      messages == 'undefined'
                    "
                    :image-size="100"
                    description="暂无消息"
                    class="empty-block"
                  />
                </div>
              </el-tab-pane>
              <el-tab-pane label="通知" name="second">
                <!--                <message-list :msg-category="'first'"></message-list>-->
                <div class="message-list">
                  <div
                    class="msg-item"
                    v-for="(msg, index) in noticeList"
                    :key="index"
                    v-show="msg.entityType == 1"
                    @click="clickViewMessage(msg)"
                  >
                    <img
                      class="icon"
                      src="@/assets/system/images/layout/msg/icon1.png"
                      alt=""
                    />
                    <div class="content">
                      <div class="title">{{ msg.title }}</div>
                      <div class="time">{{ msg.time }}</div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="公告" name="third">
                <!--                <message-list :msg-category="'second'"></message-list>-->
                <div class="message-list">
                  <div
                    class="msg-item"
                    v-for="(msg, index) in noticeList"
                    :key="index"
                    v-show="msg.entityType == 2"
                    @click="clickViewMessage(msg)"
                  >
                    <img
                      class="icon"
                      src="@/assets/system/images/layout/msg/icon1.png"
                      alt=""
                    />
                    <div class="content">
                      <div class="title">{{ msg.title }}</div>
                      <div class="time">{{ msg.time }}</div>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
            <div class="msg-btns">
              <div class="btn-item" @click="clearNotification">全部已读</div>
              <div class="btn-item" @click="messageDetail">查看更多</div>
            </div>
          </template>
        </el-popover>

        <!-- <div @click="handleRefreshClick" class="right-menu-item hover-effect">
          <i
            class="iconfont icon-a-shuaxinxianxing"
            style="font-size: 20px"
          ></i>
        </div> -->

        <header-search id="header-search" class="right-menu-item" />

        <!-- <screenfull id="screenfull" class="right-menu-item hover-effect" /> -->

        <!-- <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip> -->
      </template>
      <div class="avatar-container">
        <el-dropdown
          @command="handleCommand"
          class="right-menu-item hover-effect"
          trigger="click"
        >
          <div class="avatar-wrapper">
            <img :src="userStore.avatar" class="user-avatar" />
            <span class="nickName">{{ userStore.nickName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <router-link to="/user/profile">
                <el-dropdown-item>个人中心</el-dropdown-item>
              </router-link>
              <el-dropdown-item
                command="setLayout"
                v-if="settingsStore.showSettings"
              >
                <span>布局设置</span>
              </el-dropdown-item>
              <!-- <el-dropdown-item command="aboutUs" v-if="settingsStore.showSettings">
                                <span>关于我们</span>
                            </el-dropdown-item> -->
              <el-dropdown-item divided command="logout">
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!-- <MessageList></MessageList> -->

    <el-dialog
      title="消息详情"
      v-model="openView"
      width="800px"
      draggable
      destroy-on-close
      class="msg-dialog"
    >
      <el-form label-width="100px">
        <el-row :gutter="20">
          <el-col :offset="4">
            <el-form-item label="创建时间：">
              <div class="form-value-ifon">
                {{ viewData.createTime }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :offset="4">
            <el-form-item label="消息标题：">
              <div class="form-value-ifon">
                {{ viewData.title }}
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :offset="4">
            <el-form-item label="消息内容：">
              <div v-html="viewData.content"></div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="openView = false">关 闭</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog
      title="关于我们"
      class="about-dialog"
      v-model="activeOpen"
      append-to-body
      align-center
    >
      <div class="about-content-wrapper">
        <img
          src="@/assets/system/images/login/logo.png"
          alt="qData Logo"
          class="logo"
        />
        <div class="about-title">
          版本{{ currentVersion }}
          <!-- <span class="version-badge"></span> -->
        </div>
        <div class="copyright">©{{ year }}江苏千桐科技有限公司版权所有</div>
      </div>

      <template #footer>
        <div class="about-footer">
          <div v-if="!needUpdate" class="status-text">
            版本{{ currentVersion }}已是最新版本。
          </div>
          <div v-else class="status-text">
            版本{{ latestVersion }}
            <a
              href="https://gitee.com/qiantongtech/qOntology"
              target="_blank"
              rel="noopener noreferrer"
              class="update-link"
            >
              检查更新
            </a>
          </div>
          <div class="head-btns">
            <el-button type="primary" @click="openUpdateLog"
              >更新日志</el-button
            >
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus";
import Breadcrumb from "@/components/Breadcrumb";
import TopNav from "@/components/TopNav";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import HeaderSearch from "@/components/HeaderSearch";
import useAppStore from "@/store/system/app";
import useUserStore from "@/store/system/user";
import useSettingsStore from "@/store/system/settings";
import useTagsViewStore from "@/store/system/tagsView";
import defaultSettings from "@/settings";
import Logo from "./Sidebar/Logo";
import {
  getNum,
  listMessage,
  readAll,
} from "@/api/system/system/message/message";
import { loginOut } from "@/api/system/sso-auth.js";
// import MessageList from "@/views/system/system/message/components/messageList.vue";
import { onMounted, ref, watch } from "vue";
import moment from "moment";
import { listNotice } from "@/api/system/system/notice";
import { getCurrentAppVersion } from "@/api/system/update/update.js";
import picomatch from 'picomatch';
import {projectDict} from "../../api/basic/project/project.js";
import {getProjectVersionDict} from "../../api/basic/project/projectVersion.js";

// 认证模式
const authType = import.meta.env.VITE_APP_AUTH_TYPE;

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const userStore = useUserStore();
const settingsStore = useSettingsStore();
const { proxy } = getCurrentInstance();
const visitedViews = computed(() => useTagsViewStore().visitedViews);
const isOnlyLogoRoute = computed(() => {
  const navbarLogoRoutes = defaultSettings.navbarLogoRoutes || [];
  return navbarLogoRoutes.some((logoPath) => route.path.startsWith(logoPath));
});

// 默认选择的消息类型
const activeMsg = ref("first");

const openView = ref(false);
const viewData = ref({
  title: "",
  content: "",
  createTime: "",
});

// 获取当前年
const year = moment(new Date()).format("yyyy");
// 是否最新版本
const needUpdate = ref(false);
// 当前版本号
const currentVersion = ref("");
// 最新版本号
const latestVersion = ref("");

const isFlag = ref(false);
const showSelector = computed(() => isFlag.value);
const projectId = ref('');
const projectVersionId = ref('');
const projectOptions = ref([]);
const projectVersionOptions = ref([]);


// 项目版本下拉框逻辑
function projectVersionIdChange(value) {
  const currentPath = router.currentRoute.value.path;
  const pathArray = currentPath.split('/');
  // 仅在 om 模块页面内切换版本，避免字典初始化赋值时从其他页面拼出非法路径
  if (pathArray[1] !== 'om' || !pathArray[2] || !value) {
    return;
  }
  pathArray[2] = value;
  const obj = {
    path: pathArray.join('/')
  };
  proxy.$tab.openPage(obj);
  proxy.$tab.closeOtherPage(obj);
}

function getRouter(path) {
  const paths = ['om'];
  const firstPath = path.split('/')[1];
  isFlag.value = paths.includes(firstPath);
}

// 项目字典进行中的请求，避免 watcher 与 getRouterParam 重复触发
let projectPromise = null;

const getRouterParam = async (menuKey) => {
  // 非 om 菜单跳转不依赖项目/版本字典，无需等待，避免阻塞其他菜单跳转
  if (menuKey && menuKey.split('/')[1] !== 'om') {
    return { versionId: projectVersionId.value };
  }
  if (!projectVersionId.value) {
    await getProject();
  }
  return { versionId: projectVersionId.value }
}

watch(
    () => isFlag.value,
    (newVal) => {
      if (newVal) {
        getProject();
      }
    },
    { immediate: true }
);

watch(
    () => projectVersionId.value,
     (newVal) => {
      if (newVal && newVal !== '') {
        projectVersionIdChange(newVal);
      }
    },
    { immediate: true }
);

function getProject() {
  // 复用进行中的请求，防止短时间内重复调用
  if (projectPromise) {
    return projectPromise;
  }
  projectPromise = projectDict()
    .then((res) => {
      // 路由切换会取消未完成请求，响应拦截器对被取消的请求 resolve(null)
      const list = res?.data;
      if (!Array.isArray(list) || list.length === 0) {
        projectPromise = null;
        return;
      }
      projectOptions.value = list;
      projectId.value = list[0].dictValue;
      return projectIdChange();
    })
    .catch((err) => {
      // 异常提示已由响应拦截器统一处理，这里兜底避免未捕获的 Promise 异常
      projectPromise = null;
      console.error('获取项目字典失败:', err);
    });
  return projectPromise;
}

function projectIdChange() {
  return getProjectVersionDict(projectId.value)
    .then((res) => {
      const list = res?.data;
      if (!Array.isArray(list) || list.length === 0) {
        return;
      }
      projectVersionOptions.value = list;
      projectVersionId.value = list[0].dictValue;
    })
    .catch((err) => {
      console.error('获取项目版本字典失败:', err);
    });
}

onMounted(() => {
  //   // 获取版本更新信息
  //   getCurrentAppVersion().then((res) => {
  //     if (res.data != null) {
  //       // 是否最新版本
  //       needUpdate.value = res.data.needUpdate;
  //       // 本地版本号
  //       currentVersion.value = res.data.currentVersion;
  //       // 最新版本号
  //       latestVersion.value = res.data.latestVersion;
  //     }
  //   });
});

// 将 Markdown 转为 HTML，并进行安全过滤
// const sanitizedHtml = computed(() => {
//   if (!markdownContent.value) return '';
//
//   // 1. Markdown → HTML
//   const rawHtml = marked(markdownContent.value, {
//     breaks: true,   // 支持 \n 换行
//     gfm: true,      // GitHub Flavored Markdown
//   });
//
//   // 2. 防 XSS：净化 HTML
//   return DOMPurify.sanitize(rawHtml);
// });

function clickViewMessage(msg) {
  // 通知公告使用独立详情页，保证从顶部通知和首页公告进入的内容保持一致。
  if (msg && msg.noticeId != null) {
    router.push({
      path: "/system/notice/detail",
      query: { id: msg.noticeId },
    });
    return;
  }

  openView.value = true;
  viewData.value.title = msg.title;
  viewData.value.content = msg.noticeContent;
  viewData.value.createTime = msg.time;
}

function handleOpenNotice() {}

// 消息通知数量
const msgCount = ref(0);
const messages = ref([]);
const noticeList = ref([]);
const sessionValue = ref(null);
getMessageNum(); // 第一次主要获取消息

const wsUri =
  import.meta.env.VITE_APP_WEBSOCKET_API +
  "/websocket/message/" +
  userStore.userId;
// 建立socket连接
const ws = new WebSocket(wsUri);

const initWebSocket = () => {
  //查询通知公告
  listNotice().then((response) => {
    response.rows.forEach((item) => {
      item.title = item.noticeTitle;
      item.entityType = item.noticeType;
      item.time =
        item.updateTime != undefined && item.updateTime != null
          ? formatTimestamp(item.updateTime)
          : formatTimestamp(item.createTime);
    });
    noticeList.value = response.rows;
    // messages.value.push(messageData)
  });

  //查询未读消息通知
  listMessage({
    receiverId: userStore.userId,
    hasRead: 0,
    pageNum: 1,
    pageSize: 1000,
  }).then((response) => {
    response.data.rows.forEach((item) => {
      item.time = item.updateTime;
      item.entityType = item.category;
      // item.title = item.title
    });
    messages.value = [...response.data.rows, ...messages.value];
    msgCount.value = messages.value ? messages.value.length : 0;
  });
  ws.onmessage = (event) => {
    const messageData = JSON.parse(event.data);
    if (messageData) {
      messageData.time =
        messageData.updateTime != undefined && messageData.updateTime != null
          ? formatTimestamp(messageData.updateTime)
          : formatTimestamp(messageData.createTime);
      // messages.value.push(messageData)
      messages.value = [messageData, ...messages.value];
    }
    // 消息数量更新
    msgCount.value = messages.value ? messages.value.length : 0;
  };
};

onMounted(() => {
  initWebSocket();
});
// 页面注销
onBeforeUnmount(() => {
  ws.close(); // 关闭socket
});

// 格式化时间戳为 YYYY-MM-DD HH:mm:ss 格式
function formatTimestamp(timestamp) {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0"); // 月份从0开始
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");

  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

// 消息查询
function getMessageNum() {
  getNum();
}

// tab-click 事件处理函数
const handleClick = (tab) => {
  const label = tab.props.label;
  activeMsg.value = tab.props.name;
};

function toggleSideBar() {
  appStore.toggleSideBar();
}

const activeOpen = ref(false);

function handleAboutUs() {
  activeOpen.value = true;
}

function openUpdateLog() {
  window.open("https://gitee.com/qiantongtech/qOntology/releases", "_blank");
}
function handleCommand(command) {
  switch (command) {
    case "setLayout":
      setLayout();
      break;
    case "aboutUs":
      handleAboutUs();
      break;
    case "logout":
      logout();
      break;
    default:
      break;
  }
}

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

const emits = defineEmits(["setLayout"]);

function setLayout() {
  emits("setLayout");
}

function handleRefreshClick() {
  const activeView = visitedViews.value.find(
    (view) => view.path === route.path
  );
  proxy.$tab.refreshPage(activeView);
  if (route.meta.link) {
    useTagsViewStore().delIframeView(route);
  }
}

function messageDetail() {
  if (activeMsg.value == "first") {
    router.push({ path: "/bases/message" });
  } else {
    router.push({ path: "/system/notice" });
  }
}

function clearNotification() {
  readAll().then(() => {
    messages.value = [];
    msgCount.value = 0;
    ElMessage.success("已全部已读！");
  });
}
</script>

<style lang="scss" scoped>
.ellipsis-option {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.custom-option-style .el-select-dropdown__item {
  display: flex;
  align-items: center;
}

:deep(.el-select__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  border-radius: 2px !important;
}

.message-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  overflow-y: auto;
  overflow-x: hidden;
}

.msg-item {
  cursor: pointer;
  display: flex;
  align-items: center;
  width: 100%;
  padding: 10px 16px;
  margin-bottom: 10px;
  background: #f9f9f9;
  border-radius: 4px;
}

.icon {
  width: 34px;
  height: 34px;
  margin-right: 12px;
}

.content {
  .title {
    font-size: 14px;
    font-weight: 500;
    color: rgba(0, 0, 0, 0.85);
    margin-bottom: 6px;
  }

  .time {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.45);
  }
}

.rwgl-item {
  display: flex !important;
  align-items: center;
  img {
    height: 18px;
    display: block;
  }
}

.navbar {
  height: 60px;
  overflow: hidden;
  position: relative;
  text-align: center;
  line-height: 60px;
  background: #fff;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);

  ::v-deep(.el-menu) {
    background-color: transparent;
    padding-top: 0;
    padding-bottom: 0;
    display: flex;
    align-items: center;
    padding: 0 16px;
    border-bottom: none;
  }

  .navbar-logo {
    float: left;
    width: 200px !important;
    height: 100% !important;
    background-color: transparent !important;

    ::v-deep.sidebar-logo-link {
      background-color: transparent !important;
    }

    ::v-deep.sidebar-logo {
      height: 48px !important;
      margin-top: 6px !important;
      transform: none !important;
    }
  }

  ::v-deep .size-icon--style {
    line-height: 60px;
  }

  .hamburger-container {
    line-height: 60px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 40px;
    right: var(--topnav-right-offset, 430px);
    overflow: hidden;
    &.has-navbar-logo {
      left: 200px;
    }
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 60px;
    display: flex;

    &:focus {
      outline: none;
    }

    .page-form {
      display: flex;
      align-items: center;

      .el-form-item {
        margin-bottom: 0;
        margin-right: 15px;
      }
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin: 0 15px 0 0;

      .avatar-wrapper {
        display: flex;
        align-items: center;
        margin-top: 10px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 20px;
        }

        .nickName {
          font-size: 15px;
          /*font-weight: bold;*/
          // color: rgba(0, 0, 0, 0.65);
          color: var(--themeColor);
          display: inline-block;
          margin-left: 10px;
        }

        i {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }

  .flash ::v-deep .el-badge__content.is-fixed {
    animation: twinkle 1s infinite;
    /*margin-top: 16px;*/
    margin-right: 6px;
  }

  /* 定义闪烁的动画 */
  @keyframes twinkle {
    0% {
      opacity: 1; /* 完全可见 */
    }
    50% {
      opacity: 0.3; /* 半透明 */
    }
    100% {
      opacity: 1; /* 完全可见 */
    }
  }

  .item {
    height: 60px;
    line-height: 60px;
    display: inline-block;
    cursor: pointer;
  }

  .badge :deep(.el-badge__content.is-fixed) {
    top: 20px;
    transform: translateY(-50%) translateX(64%);
  }
}

.mag-tabs {
  height: calc(100% - 50px);

  ::v-deep .el-tabs__item {
    height: 50px;
    line-height: 50px;
  }

  ::v-deep .el-tabs__header {
    margin-bottom: 6px;
  }

  ::v-deep .el-tabs__content {
    height: calc(100% - 56px);

    .el-tab-pane {
      height: 100%;
    }
  }
}

.msg-btns {
  display: flex;
  height: 50px;
  line-height: 50px;
  border-top: 1px solid #e6e6e6;

  .btn-item {
    width: 50%;
    text-align: center;
    cursor: pointer;
    color: rgba(0, 0, 0, 0.85);

    &:last-child {
      border-left: 1px solid #e6e6e6;
    }
  }
}

#custom-header {
  background-color: rgb(248, 248, 248);
}

.el-dialog__header.show-close {
  text-align: left !important;
  padding: 9px 620px 9px 20px !important;
  background: rgb(248, 248, 248) !important;
}

.el-dialog__body {
  height: 500px;
}

/* 确保样式生效，增加选择器的优先级 */
.rounded-button,
.rounded-button .el-button {
  border-radius: 2px !important;
}

.about-content-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  //padding: 27px 0;
  //gap: 16px;

  .logo {
    height: 34px;
    width: 146px;
    margin-top: 27px;
  }

  .about-title {
    margin-top: 20px;
    font-size: 22px;
    font-weight: 500;
    color: #000000;

    .version-badge {
      background-color: #409eff;
      color: white;
      padding: 2px 8px;
      border-radius: 4px;
      margin-left: 6px;
    }
  }

  .copyright {
    color: #909399;
    font-size: 16px;
    margin-top: 27px;
  }
}

.about-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 35px;
  border-top: 1px solid var(--el-border-color-light); // 使用 Element Plus 主题变量

  .status-text {
    font-size: 18px;
    color: #000000;
  }
  .update-link {
    color: #126bed; // Element Plus 主色，也可以用 var(--el-color-primary)
    text-decoration: underline;
    cursor: pointer;
    font-size: 18px;
    transition: color 0.2s;

    &:hover {
      color: #66b1ff; // 鼠标悬停时颜色变亮
    }

    &:active {
      color: #3a8ee6; // 点击时颜色更深一点
    }
  }
  .head-btns {
    img {
      margin-right: 6px;
    }
    .currImg {
      display: inline-block;
    }

    .act {
      display: none;
    }

    .el-button {
      height: 34px;
      width: 114px;
      border-radius: 4px !important;
      font-size: 18px;

      &:hover {
        .act {
          display: inline-block;
        }

        .currImg {
          display: none;
        }
      }
    }
  }
}
.markdown-content {
  padding: 0 15px 15px 15px;
}
</style>
<style>
.about-dialog:not(.is-fullscreen) {
  margin: auto !important;
  width: 600px;
  height: 300px;
  padding: 0;
  .el-dialog__header {
    height: 47px !important;
    background: #f8f8f8 !important;
    line-height: 47px;
    padding-left: 27px;
    color: #333333;
  }
  .el-dialog__footer {
    padding-top: 0px;
  }
  .about-footer {
    padding: 12px 32px;
  }
}
</style>
