# 🚀 本体工程平台（qOntology） 快速开始指南

---

## 一、准备工作 <small>（必备环境）</small>

在运行系统之前，请确保您已安装以下环境：

|                                                         软件/工具                                                         |   版本要求   |  推荐版本  |
|:---------------------------------------------------------------------------------------------------------------------:|:--------:|:------:|
|              <img src="https://img.icons8.com/color/48/000000/java-coffee-cup-logo.png" width="24"/> JDK              |  >= 17   |   17   |
|                  <img src="https://img.icons8.com/color/48/000000/mysql-logo.png" width="24"/> MySQL                  | >= 5.7.0 |  5.7   |
| <img src="https://dist.neo4j.com/wp-content/uploads/20230926084108/Logo_FullColor_RGB_TransBG.svg" width="24"/> Neo4j |   4.x    | 4.4.40 |
|                    <img src="https://img.icons8.com/color/48/000000/redis.png" width="24"/> Redis                     |  >= 5.0  |  5.0   |
|               <img src="https://img.icons8.com/?size=100&id=jfjmkTUFX5Vf&format=png" width="24"/> Maven               |  >= 3.6  |  3.6   |
|                   <img src="https://img.icons8.com/color/48/000000/nodejs.png" width="24"/> Node.js                   |  >= 16   |   16   |

> **提示**: 前端安装完 `Node.js` 后，建议设置淘宝镜像源以提升依赖包下载速度。不推荐使用 `cnpm`，可能会引入一些不可预知的问题。
>
> ```bash
> npm config set registry https://registry.npmmirror.com
> ```

---

## 二、获取项目代码 <small>（下载与解压）</small>

前往 【[qOntology Gitee 下载页面](https://gitee.com/qiantongtech/qOntology)】 下载项目并解压到您的工作目录。

---

## 三、目录结构
### 2.1 项目结构;

```
├─qontology-framework       # 公共配置模块
├─   ├─qontology-ai         # AI模块
├─   ├─qontology-common     # 共通模块
├─   ├─qontology-core       # 核心领域对象与基础能力模块
├─   ├─qontology-config     # 配置模块
├─   ├─qontology-file       # 文件管理模块
├─   ├─qontology-generator  # 代码生成器
├─   ├─qontology-mybatis    # MyBatis 配置
├─   ├─qontology-neo4j      # Neo4j 模块
├─   ├─qontology-quartz     # 定时任务模块
├─   ├─qontology-redis      # Redis 模块
├─   ├─qontology-security   # 安全模块
├─   ├─qontology-websocket  # WebSocket 模块
├─qontology-module-ai       # AI模块
├─qontology-module-app      # 本体应用模块
├─qontology-module-om       # 本体建模模块
├─qontology-module-basic    # 基础管理模块
├─qontology-module-system   # 系统管理模块
├─qontology-server          # 启动项目
├─qontology-ui              # 前端模块
├─sql                       # sql脚本
├─README.md                 # 相关介绍
├─QUICKSTART.md                 # 快速启动
```

### 2.2 前端结构;

```
├─qontology-ui                  # 前端模块
├─   ├─public                   # 静态资源目录
├─   ├─src
├─   |  ├─api                   # 接口
├─   |  ├─assets                # 图片、样式等资源
├─   |  ├─components            # 通用组件
├─   |  ├─layout                # 布局
├─   |  ├─plugins               # 插件
├─   |  ├─router                # 路由
├─   |  ├─store                 # 状态管理
├─   |  ├─utils                 # 工具类
├─   |  ├─views                     # 页面视图
├─   |  |   ├─app                   # 本体应用模块
├─   |  |   ├─om                    # 本体建模模块
├─   |  |   ├─basic                 # 基础管理模块
├─   |  |   ├─system                # 系统管理模块
├─   ├─.env.development         # 开发环境配置
├─   ├─.env.production          # 生产环境配置
├─   ├─vite.config.js           # Vite配置文件
```

---

## 四、必要配置 <small>（数据库与服务配置）</small>

### 1. 创建MySQL数据库并导入数据

- 创建 MySQL 数据库：`qontology_dev`
- 导入数据脚本：`latest初始化`

### 2. 修改MySQL数据库连接配置

路径：`qOntology/qontology-server/src/main/resources/application-dev.yml`

```yaml
datasource:
  type: mysql

mysql:
  driver-class-name: com.mysql.cj.jdbc.Driver
  url: 数据库地址
  username: 数据库账号
  password: 数据库密码
```

### 3. 创建Neo4j数据库并导入数据

> - Neo4j版本要求4.4.x以上，推荐使用4.4.40版本。  
> - 由于Neo4j建议使用java 11版本，系统使用java 17版本运行。推荐使用docker安装Neo4j，避免环境冲突问题。  
> - 如果您坚持使用本地部署Neo4j，至少使用java 17及以上版本运行或配置两套java环境分别运行Neo4j及qOntology。

#### 步骤 1: 关闭Neo4j服务
- 进入Neo4j安装目录，在bin目录下，执行`./neo4j stop`将服务关掉。

#### 步骤 2: 导入数据库
- 进入Neo4j安装目录，在bin目录下，执行以下导入命令。

```bash
# 命令说明: --force 表示强制覆盖，database如果不存在，则创建新数据库。
# dump文件: sql/neo4j/neo4j.dump。      注：以下命令行中的路径请填写您的绝对路径。
./neo4j-admin load --from=dump文件路径 --database=您的数据库名称   --force 
```

- 进入Neo4j安装目录，在conf目录下，找到`neo4j.conf`文件，修改数据库名称:`dbms.default_database=您的数据库名称`

> Tips：若使用docker运行neo4j，发生停止主进程自动退出容器现象，可以修改容器启动方式，例如：通过使用 --init 参数，让主进程停止，容器不会立即退出。具体操作方式请自行百度或AI。

#### 步骤 3: 启动Neo4j服务
- 进入Neo4j安装目录，在bin目录下，执行`./neo4j start`启动服务

### 4. 修改Neo4j数据库连接配置

路径：`qOntology/qontology-framework/qontology-graphis/src/main/resources/application-graphis-dev.yml`

```yaml
neo4j:
  uri: 数据库地址
  user: 数据库账号
  password: 数据库密码
```

### 5. 配置服务端口和访问路径

路径：`qOntology/qontology-server/src/main/resources/application.yml`

```yaml
server:
  port: 端口            # 默认为8090
  servlet:
    context-path: /应用路径  # 应用访问路径，默认为空
```

---

## 六、后端运行 <small>（启动服务器）</small>

1. 使用 IntelliJ IDEA 或 Eclipse 导入项目。
2. IDE 将自动加载 Maven 依赖，初次加载可能较慢。
3. 运行 `tech.qiantong.qontology.server.QOntologyApplication.java`。启动成功后，可通过 `http://localhost:8090` 访问后端服务。
4. 出现以下提示，表示后端服务启动成功：

```
(♥◠‿◠)ﾉﾞ  qOntology本体工程平台启动成功   ლ(´ڡ`ლ)ﾞ  
       ___        _        _                    
  __ _/ _ \ _ __ | |_ ___ | | ___   __ _ _   _ 
 / _` | | | | '_ \| __/ _ \| |/ _ \ / _` | | | |
| (_| | |_| | | | | || (_) | | (_) | (_| | |_| |
 \__, |\___/|_| |_|\__\___/|_|\___/ \__, |\__, |
    |_|                             |___/ |___/                        
```

> **注意**: 仅启动后端服务不会显示静态页面，请继续部署前端服务。

---

## 七、前端运行 <small>（启动前端服务）</small>

### 1. 根据需要修改后台服务端口配置

```javascript
// vite 相关配置
server: {
  port: 80,
  host: true,
  open: true,
  proxy: {
    "/dev-api": {
      target: "http://localhost:8090",
      changeOrigin: true,
      rewrite: (p) => p.replace(/^\/dev-api/, ""),
    },
  }
}
```

### 2. 启动前端服务

#### 步骤 1: 进入项目目录

首先，导航到 `qontology-ui` 目录下：

```bash
# 进入项目目录
cd qontology-ui
```

#### 步骤 2: 安装依赖

接下来，安装项目的依赖包。这里我们使用淘宝镜像源以加速下载速度：

```bash
# 安装依赖（推荐使用淘宝镜像）
npm install --registry=https://registry.npmmirror.com
```

#### 步骤 3: 本地开发并启动项目

最后，运行以下命令来启动前端开发服务器：

```bash
# 本地开发 启动项目
npm run dev
```

### 3. 测试前端服务

打开浏览器，输入 `http://localhost`。默认账户/密码为 `qOntology/qOntology123`。若能正确展示登录页面，并能成功登录，菜单及页面展示正常，则表明环境搭建成功。

> **提示**: 因为本项目是前后端完全分离的，所以需要前后端都单独启动好，才能进行访问。

---

希望这个文档能够帮助您更好地理解和操作 **qOntology本体工程平台** 项目！

---

<p align="center">
  <img src="https://img.icons8.com/fluent/48/000000/checkmark.png" width="36"/>
  <strong>完成！</strong>
</p>
