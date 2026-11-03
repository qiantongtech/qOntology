#!/bin/sh
# qOntology Docker 管理脚本，兼容 Linux/macOS 的 POSIX sh。
set -eu
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
PROJECT_NAME=${QONTOLOGY_PROJECT_NAME:-qontology}
OFFLINE=0
FORCE=0
COMPOSE_KIND=plugin
fail() { printf '错误：%s\n' "$*" >&2; exit 1; }
warn() { printf '警告：%s\n' "$*" >&2; }
usage() {
    cat <<'HELP'
用法：sh qontology.sh <命令> [选项]
Windows 请双击 start-all.bat 启动，uninstall.bat 完整卸载。
  start [--offline] [--force]  检查环境、使用本地镜像并按需拉取后启动
  doctor [--force]            检查环境、配置、部署文件、资源及端口
  check-images               逐个检查远程镜像，汇总无法访问的镜像
  status | logs | stop | restart
  uninstall                  确认后删除容器和网络，保留数据卷及挂载数据
--offline 仅使用本地镜像，启动时禁止拉取
--force 仅放行 CPU/内存不足；不会跳过配置、端口、缺失文件检查
可在任意目录执行；QONTOLOGY_PROJECT_NAME 可覆盖默认项目名 qontology。
HELP
}
version_ge() {
    awk -v a="$1" -v b="$2" 'BEGIN {split(a,x,".");split(b,y,".");for(i=1;i<=3;i++){if(x[i]+0>y[i]+0)exit 0;if(x[i]+0<y[i]+0)exit 1}exit 0}'
}
compose() {
    if [ "$COMPOSE_KIND" = plugin ]; then docker compose "$@"; else docker-compose "$@"; fi
}
stack() {
    compose --project-directory "$SCRIPT_DIR" --env-file "$SCRIPT_DIR/.env" -p "$PROJECT_NAME" -f "$SCRIPT_DIR/docker-compose.yml" --profile all "$@"
}
# 交给 Compose 解析 dotenv，绝不 source/eval 用户配置。
config_value() {
    stack config --environment | awk -v key="$1" 'index($0,key "=")==1 {print substr($0,length(key)+2);exit}'
}
require_file() { [ -f "$SCRIPT_DIR/$1" ] || fail "部署包缺少文件：$1"; }
init() {
    case "$PROJECT_NAME" in ''|[!a-z0-9]*|*[!a-z0-9_-]*) fail '项目名必须以小写字母或数字开头，仅可包含小写字母、数字、下划线、连字符。';; esac
    command -v docker >/dev/null 2>&1 || fail '未安装 Docker，请安装 Docker Engine 或 Docker Desktop。'
    if docker compose version >/dev/null 2>&1; then COMPOSE_KIND=plugin
    elif command -v docker-compose >/dev/null 2>&1; then COMPOSE_KIND=standalone
    else fail '未安装 Docker Compose v2（最低 2.20.2，配置使用 include）。'; fi
    version=$(compose version --short | sed 's/^v//;s/[^0-9.].*$//')
    version_ge "$version" 2.20.2 || fail "Compose $version 过旧，最低需要 2.20.2。"
    docker info >/dev/null 2>&1 || fail '无法连接 Docker，请启动 Docker 并检查当前用户权限。'
    engine=$(docker version --format '{{.Server.Version}}')
    version_ge "$engine" 20.10.0 || fail 'Docker Engine 最低需要 20.10.0。'
    [ "$(docker info --format '{{.OSType}}')" = linux ] || fail '请切换到 Linux 容器模式。'
    context=$(docker context show)
    endpoint=${DOCKER_HOST:-$(docker context inspect "$context" --format '{{.Endpoints.docker.Host}}')}
    case "$endpoint" in ssh://*|tcp://*|http://*|https://*) fail '当前 Docker 指向远程主机。请在部署主机上执行，绑定挂载依赖该主机的文件。';; esac
    require_file .env
    stack config --quiet || fail 'Compose 配置无效，请检查 .env 和 compose 文件。'
    db=$(config_value DB_TYPE)
    [ "${db:-mysql}" = mysql ] || fail '该部署仅支持 MySQL，不支持 DB_TYPE 切换。'
}
check_package() {
    for file in qOntology/nginx/dist/index.html qOntology/nginx/nginx.conf qOntology/nginx/sites/qontology.conf qOntology/server/application.yml qOntology/server/application-prod.yml qOntology/mysql/conf.d/my.cnf qOntology/mysql/init/init-system.sql qOntology/mysql/init/init-show.sql; do
        require_file "$file"
    done
    printf '部署文件检查通过。\n'
}
check_resources() {
    cpu=$(docker info --format '{{.NCPU}}')
    memory=$(docker info --format '{{.MemTotal}}')
    if [ "$cpu" -lt 4 ] || [ "$memory" -lt 6442450944 ]; then
        [ "$FORCE" -eq 1 ] || fail 'Docker 资源低于建议的 4 CPU / 6 GiB；可用 --force 放行资源检查。'
        warn '已放行资源不足，可能导致服务启动失败。'
    fi
    free_kb=$(df -Pk "$SCRIPT_DIR" | awk 'NR==2 {print $4}')
    [ "${free_kb:-0}" -ge 20971520 ] || warn '部署目录剩余空间不足 20 GiB，请同时检查 Docker 数据盘。'
    arch=$(docker info --format '{{.Architecture}}')
    case "$arch" in arm64|aarch64) warn '当前为 ARM：默认 MySQL 5.7 使用 linux/amd64，需要模拟支持；拉取成功并不代表可以运行。';; esac
}
host_listening() {
    if command -v lsof >/dev/null 2>&1; then lsof -nP -iTCP:"$1" -sTCP:LISTEN >/dev/null 2>&1
    elif command -v ss >/dev/null 2>&1; then ss -ltn | awk -v p=":$1" '$4 ~ p"$" {found=1} END {exit !found}'
    else return 1; fi
}
check_ports() {
    # 两个端口来自 .env，其余为当前 Compose 固定映射。
    nginx=$(config_value EXPOSE_NGINX_PORT)
    redis=$(config_value EXPOSE_REDIS_PORT)
    for port in "${nginx:-80}" "${redis:-6379}" 33067 17474 17687 8017; do
        owners=$(docker ps --filter "publish=$port" --format '{{.Label "com.docker.compose.project"}}')
        if [ -n "$owners" ]; then
            for owner in $owners; do [ "$owner" = "$PROJECT_NAME" ] || fail "端口 $port 已被其他 Docker 项目占用。"; done
        elif host_listening "$port"; then fail "端口 $port 已被宿主机进程占用。"; fi
    done
    if ! command -v lsof >/dev/null 2>&1 && ! command -v ss >/dev/null 2>&1; then warn '缺少 lsof/ss，未检查宿主机进程端口占用；Docker 容器端口已检查。'; fi
    printf '端口检查通过。\n'
}
preflight() { check_package; check_resources; check_ports; }
check_images() {
    missing=0
    images=$(stack config --images | sort -u)
    [ -n "$images" ] || fail '配置未包含任何镜像。'
    for image in $images; do
        if [ "$OFFLINE" -eq 1 ]; then
            if ! docker image inspect "$image" >/dev/null 2>&1; then
                warn "缺少本地镜像：$image"; missing=1
            fi
        elif ! docker manifest inspect "$image" >/dev/null 2>&1; then
            warn "镜像无法访问（缺失、权限或网络问题）：$image"; missing=1
        else printf '镜像可访问：%s\n' "$image"; fi
    done
    [ "$missing" -eq 0 ] || fail '以上镜像检查失败；请检查标签、仓库登录和网络，或导入离线镜像。'
}
# 按完整镜像名检查本地缓存，仅拉取缺失镜像，包括 latest 标签。
prepare_online_images() {
    images=$(stack config --images | sort -u)
    [ -n "$images" ] || fail '配置未包含任何镜像。'
    for image in $images; do
        if docker image inspect "$image" >/dev/null 2>&1; then
            printf '使用本地镜像：%s\n' "$image"
            continue
        fi
        attempt=1
        until docker pull "$image"; do
            [ "$attempt" -lt 3 ] || fail "镜像拉取失败：$image；请检查仓库权限、网络和 CPU 架构。"
            attempt=$((attempt + 1))
            printf '准备重试拉取 %s（%s/3）...\n' "$image" "$attempt"
            sleep 3
        done
    done
}
start() {
    preflight
    if [ "$OFFLINE" -eq 1 ]; then check_images
    else prepare_online_images
    fi
    # 即使在线也禁止 up 隐式拉取，确保所有镜像在创建容器前已准备好。
    if ! stack up -d --pull never --wait --wait-timeout 600; then
        stack ps -a
        fail '服务未在 600 秒内就绪，请运行 logs 检查；容器和数据已保留。'
    fi
    port=$(config_value EXPOSE_NGINX_PORT)
    printf '容器启动检查通过，访问：http://localhost:%s\n使用 status 查看状态，logs 查看日志。\n' "${port:-80}"
}
main() {
    action=${1:-help}; [ $# -eq 0 ] || shift
    case "$action" in help|-h|--help) [ $# -eq 0 ] || fail 'help 不接受参数。'; usage; return;;
        start|doctor) for option in "$@"; do
            case "$option" in --offline) [ "$action" = start ] || fail '仅 start 支持 --offline。'; OFFLINE=1;; --force) FORCE=1;; *) fail "未知参数：$option";; esac
        done;;
        check-images|status|logs|stop|restart|uninstall) [ $# -eq 0 ] || fail "$action 不接受额外参数。";;
        *) usage; fail "未知命令：$action";; esac
    init
    case "$action" in
        start) start;;
        doctor) preflight; printf '环境诊断通过（未访问镜像仓库），可运行 start。\n';;
        check-images) check_images;;
        status) stack ps -a;;
        logs) stack logs --tail 200;;
        stop) stack stop;;
        restart) preflight; stack restart; printf '已执行重启，请使用 status 和 logs 检查应用状态。\n';;
        uninstall)
            warn '将删除该项目全部容器和网络；保留数据卷及绑定目录，请先备份数据库。'
            printf '请输入 DELETE QONTOLOGY 确认：'
            IFS= read -r confirmation || fail '未收到确认，已取消。'
            [ "$confirmation" = 'DELETE QONTOLOGY' ] || fail '输入不匹配，已取消。'
            stack down;;
    esac
}
main "$@"
