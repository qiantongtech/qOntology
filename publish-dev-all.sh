#!/bin/bash

# 前置步骤：
# sql更新脚本已在数据库中执行
# docker中mysql相关的演示站数据重置sql脚本已更新
# ./docker/qOntology/server/upload-init文件夹已更新

# 遇到错误立即停止执行
set -e

# ==========================================
# 1. 配置与变量定义
# ==========================================
OLD_VERSION="1.0.0"
NEW_VERSION="1.0.0"

# 用于校验版本号的基准 pom.xml 文件（通常是根目录的）
VERSION_CHECK_FILE="pom.xml"

# 镜像仓库配置
REGISTRY_URL="crpi-kf13onfj0v8f6jax.cn-shanghai.personal.cr.aliyuncs.com"
REPO_PATH="qiantongkeji/qontology-server-ce"
FULL_IMAGE_NAME="${REGISTRY_URL}/${REPO_PATH}"

# 远程服务器配置
REMOTE_HOST="192.168.0.100"
REMOTE_USER="qtt"
REMOTE_DIR="~/opt/qOntology"

# 排除目录列表
EXCLUDE_DIRS=(".git" "docker")

echo "🚀 准备将项目版本号从 $OLD_VERSION 升级至: $NEW_VERSION"

# ==========================================
# 2. 通用函数定义
# ==========================================

# 通用版本替换函数
replace_version() {
    local file=$1
    local pattern=$2
    local replacement=$3
    if [ -f "$file" ]; then
        echo "  -> 正在更新: $file"
        sed -i.bak -E "s|${pattern}|${replacement}|g" "$file"
        rm -f "${file}.bak"
    fi
}

# ==========================================
# 3. 升级项目版本号
# ==========================================
echo "📦 正在扫描并升级所有 pom.xml 文件..."

EXCLUDE_ARGS=""
for dir in "${EXCLUDE_DIRS[@]}"; do
    EXCLUDE_ARGS="$EXCLUDE_ARGS -not -path '*/$dir/*'"
done

eval "find . -name 'pom.xml' $EXCLUDE_ARGS" | while read -r pom_file; do
    replace_version "$pom_file" "<version>$OLD_VERSION</version>" "<version>$NEW_VERSION</version>"
done

# 升级 Docker 环境变量
ENV_FILE="./docker/.env"
replace_version "$ENV_FILE" "^QONTOLOGY_VERSION=$OLD_VERSION" "QONTOLOGY_VERSION=$NEW_VERSION"

echo "✅ 版本号升级完成！"

# ==========================================
# 4. Docker 环境准备与构建
# ==========================================

echo "🔑 正在检查阿里云镜像仓库登录状态..."
if ! docker login --username=zhuzhuxia12138 --password-stdin $REGISTRY_URL < /dev/null 2>/dev/null; then
    echo "⚠️ 需要手动输入密码进行登录..."
    docker login --username=zhuzhuxia12138 $REGISTRY_URL
fi

echo "🏗️ 正在配置 Docker Buildx Builder..."
docker buildx rm qontology-builder 2>/dev/null || true
docker buildx create --name qontology-builder --driver docker-container --use >/dev/null

echo "🚀 开始 Maven 构建、打包并推送 Docker 镜像..."
mvn -B clean deploy \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dmaven.deploy.skip=true \
    -Ddocker.build.skip=false \
    -Ddocker.push.skip=false

# ==========================================
# 5. 前端打包与资源拷贝
# ==========================================
echo "🎨 开始打包前端..."

cd qontology-ui
sh build-prod.sh || { echo "❌ 前端打包失败"; exit 1; }
cd ..

echo "📂 正在更新 Docker 目录下的前端资源..."
cp -r ./qontology-ui/dist ./docker/qOntology/nginx/

# ==========================================
# 6. 远程部署
# ==========================================
echo "🚀 正在部署到远程服务器 $REMOTE_HOST..."

ssh -t ${REMOTE_USER}@${REMOTE_HOST} "sudo rm -rf ${REMOTE_DIR}/docker"

echo "📤 正在上传 docker 文件..."
scp -r ./docker ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/

echo "🐳 正在远程启动 Docker 服务..."
ssh ${REMOTE_USER}@${REMOTE_HOST} << EOF
    cd ${REMOTE_DIR}/docker
    docker-compose -p qontology --profile all up -d
    docker restart qontology-nginx-1
EOF

echo "🎉 部署全部完成！"
