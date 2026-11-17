#!/bin/bash
# ==================================================================
# 文件名: reset.sh
# 功能: 重置 qOntology 开源版系统演示数据
# 作者: qOntology
# 日期: 2026-07-07
# 注意: 脚本会自动定位自身所在的 docker 目录，无需固定安装到 /opt。
# ==================================================================

set -e  # 任意步骤失败时立即退出，避免只重置了一部分数据

# 自动取得当前脚本所在目录，支持部署包安装到任意路径。
COMPOSE_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
PROJECT_NAME=${QONTOLOGY_PROJECT_NAME:-qontology}
COMPOSE_CMD="docker compose"

cd "$COMPOSE_DIR"

# 统一封装 Compose 命令，后续不再依赖固定容器名称。
compose() {
    $COMPOSE_CMD --project-directory "$COMPOSE_DIR" --env-file "$COMPOSE_DIR/.env" \
        -p "$PROJECT_NAME" -f "$COMPOSE_DIR/docker-compose.yml" --profile all "$@"
}

# 基础环境和重置文件检查。
command -v docker >/dev/null 2>&1 || { echo "❌ 未安装 Docker"; exit 1; }
docker info >/dev/null 2>&1 || { echo "❌ Docker 未启动或无法访问"; exit 1; }
docker compose version >/dev/null 2>&1 || { echo "❌ 需要 Docker Compose v2"; exit 1; }
[ -f "./.env" ] || { echo "❌ 缺少 .env 文件"; exit 1; }
[ -f "./qOntology/mysql/init/init-show.sql" ] || { echo "❌ 缺少 init-show.sql"; exit 1; }
[ -f "./qOntology/neo4j/neo4j.dump" ] || { echo "❌ 缺少 neo4j.dump"; exit 1; }
compose config --quiet

echo "🔄 开始执行系统重置流程（共 4 个阶段）..."
echo "========================================"

# ==================================================================
# 阶段 A: 停止所有相关服务
# ==================================================================
echo "🛑 阶段 A: 停止所有服务"

# 先停止 Nginx，避免 API 重置期间用户继续发起请求。
echo "   → 停止 Nginx 服务..."
compose stop nginx >/dev/null 2>&1 || true

# 停止 API，释放 MySQL 和 Neo4j 的连接。
echo "   → 停止 qOntology API 服务..."
compose stop qontology-api >/dev/null 2>&1 || true

# Neo4j 必须停止后才能安全替换 data 目录。
echo "   → 停止 Neo4j 服务..."
compose stop neo4j >/dev/null 2>&1 || true

echo "✅ 阶段 A 完成：相关服务已停止"
echo "----------------------------------------"

# ==================================================================
# 阶段 B: 重置 MySQL 数据
# ==================================================================
echo "🔄 阶段 B: 初始化 MySQL 数据"

# 密码从 Compose/.env 注入的 MYSQL_ROOT_PASSWORD 读取，不再写死在脚本中。
echo "   → 执行 MySQL 初始化脚本 init-show.sql..."
compose exec -T mysql57 sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD"' \
    < ./qOntology/mysql/init/init-show.sql

echo "✅ 阶段 B 完成：MySQL 已初始化"
echo "----------------------------------------"

# ==================================================================
# 阶段 C: 重置 Neo4j 环境
# ==================================================================
echo "🔄 阶段 C: 从 neo4j.dump 重建 Neo4j 数据"

NEO4J_DATA="$COMPOSE_DIR/qOntology/neo4j/data"
NEO4J_DUMP="$COMPOSE_DIR/qOntology/neo4j/neo4j.dump"
NEO4J_DIR="$COMPOSE_DIR/qOntology/neo4j"
NEO4J_INIT_DIR="$COMPOSE_DIR/qOntology/neo4j-init"

# 按部署包初始目录重建：不保留备份，直接清空 neo4j 后复制 neo4j-init。
echo "   → 清空现有 Neo4j 目录..."
rm -rf "$NEO4J_DIR"
mkdir -p "$NEO4J_DIR"
echo "   → 从 neo4j-init 恢复配置和 dump..."
cp -R "$NEO4J_INIT_DIR/." "$NEO4J_DIR/"

# 数据以 neo4j.dump 为准：复制完成后清空 data，避免带入旧 system 库或事务日志。
rm -rf "$NEO4J_DATA"
mkdir -p "$NEO4J_DATA"

# 从 Compose 获取实际使用的 Neo4j 镜像，避免仓库地址或版本被写死。
NEO4J_IMAGE=$(compose config --images | awk '/neo4j:4[.]4[.]40$/ {print; exit}')
[ -n "$NEO4J_IMAGE" ] || { echo "❌ 无法解析 Neo4j 镜像"; exit 1; }

echo "   → 正在导入 neo4j.dump..."
# Git Bash 会自动转换 /data 等容器路径；关闭参数转换后原样传给 Docker。
# 直接运行 neo4j-admin，并使用 Neo4j 用户写入数据。
MSYS_NO_PATHCONV=1 MSYS2_ARG_CONV_EXCL='*' docker run --rm --user 7474:7474 \
    -v "$NEO4J_DATA:/data" \
    -v "$NEO4J_DUMP:/dump/neo4j.dump:ro" \
    --entrypoint neo4j-admin "$NEO4J_IMAGE" \
    load --database=neo4j --from=/dump/neo4j.dump --force

echo "   → 启动 Neo4j 服务并等待健康检查..."
compose up -d --pull never --wait --wait-timeout 600 neo4j

# 导入后必须检查节点数量，防止空 dump 或残留空库被当成成功。
NODE_COUNT=$(compose exec -T neo4j cypher-shell -u neo4j -p InC3tmU4bijT4vkl \
    'MATCH (n) RETURN count(n)' 2>/dev/null | awk 'NR==2 {gsub(/[^0-9]/, "", $0); print; exit}')
[ -n "$NODE_COUNT" ] && [ "$NODE_COUNT" -gt 0 ] || { echo "❌ Neo4j 导入后没有节点"; exit 1; }

echo "✅ 阶段 C 完成：Neo4j 已重置，共 ${NODE_COUNT} 个节点"
echo "----------------------------------------"

# ==================================================================
# 阶段 D: 重置 upload 数据
# ==================================================================
echo "🔄 阶段 D: 重置 upload 数据"

# 只清理 upload 目录，不影响 server 下的配置文件和日志目录。
rm -rf ./qOntology/server/upload
mkdir -p ./qOntology/server/upload

# 部署包存在 upload-init 时复制初始文件；不存在时保留空目录。
if [ -d ./qOntology/server/upload-init ]; then
    cp -R ./qOntology/server/upload-init/. ./qOntology/server/upload/
fi

echo "✅ 阶段 D 完成：upload 数据已初始化"
echo "----------------------------------------"

# ==================================================================
# 启动 qOntology API + 智能等待 + 启动 Nginx
# ==================================================================

# Compose 中 Nginx 依赖 API healthy；这里显式分阶段启动，输出更直观。
echo "   → 启动 qOntology API 服务..."
compose up -d --pull never --wait --wait-timeout 800 qontology-api

echo "   → API 已通过健康检查，启动 Nginx 网关..."
compose up -d --pull never --wait --wait-timeout 300 nginx

# ==================================================================
# 完成提示
# ==================================================================
echo "🎉 系统重置成功！所有组件已恢复初始状态。"
echo "   Neo4j 节点数：${NODE_COUNT}"
