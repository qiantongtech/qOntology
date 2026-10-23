#!/bin/bash
# ==================================================================
# 文件名: reset.sh
# 功能: 重置 qOntology 开源版 系统演示数据
# 作者: qOntology
# 日期: 2026-07-07
# 注意: 必须在项目根目录下运行此脚本（即 docker/ 目录）
# ==================================================================

set -e  # 遇错立即退出

# docker-compose 项目的路径
COMPOSE_DIR="/home/qtt/opt/qOntology/docker"

cd "$COMPOSE_DIR"

echo "🔄 开始执行系统重置流程（共 3 个阶段）..."
echo "========================================"

# ==================================================================
# 阶段 A: 停止所有相关服务
# ==================================================================
echo "🛑 阶段 A: 停止所有服务"

# 停止 qOntology API 容器（忽略已停止的情况）
echo "   → 停止 qOntology API 服务..."
docker stop qontology-qontology-api-1

# ==================================================================
# 阶段 B: 重置 MySQL 数据
# ==================================================================
echo "🔄 阶段 B: 初始化 MySQL 并启动 qOntology API"

# 执行 SQL 初始化脚本（密码含特殊字符，用单引号包裹）
echo "   → 执行 MySQL 初始化脚本 init-show.sql..."
docker exec -i qontology-mysql57-1 mysql -uroot -p'lJ7gP0cA5dD2dA1hA3bJ' < ./qOntology/mysql/init/init-show.sql

echo "✅ 阶段 B 完成：MySQL 已初始化"
echo "----------------------------------------"


# ==================================================================
# 阶段 C: 重置 upload 数据
# ==================================================================
echo "🔄 阶段 C: 重置 upload 数据"


# 清理 upload 数据
sudo rm -rf ./qOntology/server/upload/*

# 重置数据
cp -r ./qOntology/server/upload-init/* ./qOntology/server/upload/
sudo chown -R $USER:$USER ./qOntology/server/upload


echo "✅ 阶段 C 完成：upload 数据已初始化"
echo "----------------------------------------"

# 启动 qOntology API 容器（忽略已停止的情况）
docker start qontology-qontology-api-1

# 启动 qOntology nginx 容器（忽略已停止的情况）
docker start qontology-qontology-nginx-1
# ==================================================================
# 完成提示
# ==================================================================
echo "🎉 系统重置成功！所有组件已恢复初始状态。"
