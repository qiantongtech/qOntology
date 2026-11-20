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
├─QUICKSTART.md             # 快速启动
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

- 首次部署时，创建 `qontology_dev`，并在该库中导入 [`latest初始化.sql`](sql/mysql/initialization/latest初始化.sql)。该脚本会重建表，已有环境应使用对应版本的升级脚本。
- 在应用实际连接的库中执行 `SHOW COLUMNS FROM om_object_type LIKE 'color';`。如果查询为空，从 [`change.sql`](sql/mysql/change.sql) 中依次执行开头的 `ALTER TABLE om_object_type` 和 `UPDATE om_object_type`，添加颜色字段并为已有对象类型分配颜色。

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

### 3. 创建 Neo4j 数据库并导入数据

演示数据位于 [`sql/neo4j/neo4j.dump`](sql/neo4j/neo4j.dump)，本地导入推荐使用 Neo4j 4.4.40。使用 Docker 部署时，启动脚本会在首次初始化 Neo4j 时自动导入安装包中的演示数据。

#### 步骤 1 停止本地 Neo4j 并备份

进入 Neo4j 安装目录。在 Linux 或 macOS 上执行 `./bin/neo4j stop`，在 Windows 服务模式下执行 `.\bin\neo4j.bat stop`。如果 Neo4j 在前台控制台运行，在启动它的窗口中按 `Ctrl+C`，等待服务退出。

如果已有数据，先用 `neo4j-admin dump --database=neo4j --to=<备份文件路径>` 备份。Windows 对应命令为 `neo4j-admin.bat`。

#### 步骤 2 导入演示数据

在 Neo4j 安装目录执行对应命令，将示例中的 dump 路径替换为本地实际路径。

Linux 或 macOS

```bash
./bin/neo4j-admin load --database=neo4j --from="/path/to/qOntology/sql/neo4j/neo4j.dump"
```

Windows PowerShell

```powershell
.\bin\neo4j-admin.bat load --database=neo4j --from="D:\qOntology\sql\neo4j\neo4j.dump"
```

如果已存在 `neo4j` 数据库，确认备份可用且确实要替换后，在导入命令末尾加 `--force`。这会覆盖现有的 `neo4j` 数据库。

如果本机使用 **Neo4j 5.x**，先在独立的 Neo4j 4.4.40 实例中导入并启动演示库，再正常停库，按官方指南迁移到 5.x。如果迁移仍提示 `Unable to find last transaction in log files`，可在该临时演示库中分两次提交创建和删除一个临时节点，再正常停库并重试迁移。[Neo4j 升级与迁移指南](https://neo4j.com/docs/upgrade-migration-guide/current/)

迁移后用 5.x 重新导出 dump。要将它导入其他 5.x 实例，使用 `neo4j-admin database load --from-path=<dump 所在目录> neo4j`。如果要覆盖现有库，先执行 `neo4j-admin database dump --to-path=<备份目录> neo4j`，再用 `--overwrite-destination=true` 导入。

#### 步骤 3 启动并验证

在 Linux 或 macOS 上执行 `./bin/neo4j start`，在 Windows 上执行 `.\bin\neo4j.bat console` 启动。打开 Neo4j Browser，连接 `neo4j` 数据库并执行以下查询，确认节点数大于 0。浏览器默认地址为 [http://localhost:7474](http://localhost:7474)。

```cypher
MATCH (n) RETURN count(n) AS totalNodes;
```

### 4. 修改Neo4j数据库连接配置

路径：`qOntology/qontology-framework/qontology-graphis/qontology-graphis-starter/src/main/resources/application-graphis-dev.yml`

```yaml
neo4j:
  uri: bolt://127.0.0.1:7687
  user: 数据库账号
  password: 数据库密码
```

### 5. 配置服务端口和访问路径

路径：`qOntology/qontology-server/src/main/resources/application.yml`

```yaml
server:
  port: 8099           # 后端监听端口
  servlet:
    context-path: /    # 应用访问路径
```

---

## 六、后端运行 <small>（启动服务器）</small>

1. 使用 IntelliJ IDEA 或 Eclipse 导入项目。
2. IDE 将自动加载 Maven 依赖，初次加载可能较慢。
3. 运行 `tech.qiantong.qontology.server.QOntologyApplication.java`。启动成功后，可通过 `http://localhost:8099` 访问后端服务。
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
      target: "http://localhost:8099",
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
