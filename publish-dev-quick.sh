#!/bin/bash

# 前置步骤：
# sql更新脚本已在数据库中执行
# docker中mysql相关的演示站数据重置sql脚本已更新
# ./docker/qOntology/server/upload-init文件夹已更新

# 遇到错误立即停止执行
set -e

# ==========================================
# 配置
# ==========================================
REMOTE_HOST="192.168.0.100"
REMOTE_USER="qtt"

API_CONTAINER="qontology-qontology-api-1"
NGINX_CONTAINER="qontology-nginx-1"

API_JAR_DIR="/usr/app/jar"
API_JAR_NAME="qontology.jar"
NGINX_DIST_DIR="~/opt/qOntology/docker/qOntology/nginx"

LOCAL_JAR_PATH="./qontology-server/target/qontology-server.jar"
LOCAL_UI_DIR="./qontology-ui"

echo "🚀 开始线上热更新..."

# ==========================================
# 后端打包
# ==========================================
echo "📦 正在打包后端 jar 包..."
mvn -B clean package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

[ -f "$LOCAL_JAR_PATH" ] || { echo "❌ 未找到 jar: $LOCAL_JAR_PATH"; exit 1; }
echo "✅ 后端打包完成。"

# ==========================================
# 前端打包
# ==========================================
echo "🎨 正在打包前端 dist..."
cd "$LOCAL_UI_DIR" && sh build-prod.sh && cd ..

[ -d "$LOCAL_UI_DIR/dist" ] || { echo "❌ 未找到 dist 目录"; exit 1; }
echo "✅ 前端打包完成。"

# ==========================================
# 上传后端 jar 并替换容器内文件
# ==========================================
echo "📤 正在上传后端 jar 包至服务器..."
scp "$LOCAL_JAR_PATH" ${REMOTE_USER}@${REMOTE_HOST}:~/temp/${API_JAR_NAME}

echo "🔄 正在替换容器 ${API_CONTAINER} 中的 jar 包并重启..."
ssh ${REMOTE_USER}@${REMOTE_HOST} << EOF
    docker cp ~/temp/${API_JAR_NAME} ${API_CONTAINER}:${API_JAR_DIR}/${API_JAR_NAME}
    rm -f ~/temp/${API_JAR_NAME}
    docker restart ${API_CONTAINER}
EOF

echo "✅ 后端更新完成。"

# ==========================================
# 上传前端 dist 并重启 nginx
# ==========================================
echo "📤 正在上传前端 dist 文件至服务器..."
ssh -t ${REMOTE_USER}@${REMOTE_HOST} "sudo rm -rf ${NGINX_DIST_DIR}/dist"
scp -r ${LOCAL_UI_DIR}/dist ${REMOTE_USER}@${REMOTE_HOST}:${NGINX_DIST_DIR}/

echo "🔁 正在重启前端服务 ${NGINX_CONTAINER}..."
ssh ${REMOTE_USER}@${REMOTE_HOST} "docker restart ${NGINX_CONTAINER}"

echo "✅ 前端更新完成。"
echo "🎉 线上热更新全部完成！"
