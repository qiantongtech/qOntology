@echo off
REM =============================================================================
REM qOntology All-Services Docker Launcher
REM
REM Purpose:
REM   Validates the local Docker environment and deployment package, initializes
REM   the configured database, and starts the complete qOntology service stack.
REM
REM Usage:
REM   Run start-all.bat from Command Prompt or double-click it in File Explorer.
REM
REM Requirements:
REM   Windows with Docker Desktop in Linux Containers mode and Docker Compose v2.
REM =============================================================================

REM Initialize the command environment and resolve all relative paths from this file.
setlocal EnableExtensions
chcp 65001 >nul
cd /d "%~dp0"
if errorlevel 1 exit /b 1

REM Define the all-services profile, project identity, and default configuration files.
set "QONTOLOGY_MODE=all"
set "QONTOLOGY_PROJECT=qontology"
set "QONTOLOGY_ENV=.env"
set "QONTOLOGY_COMPOSE=docker-compose.yml"
set "QONTOLOGY_EXIT_CODE=0"

REM Display the launcher banner before beginning the main workflow.
echo ========================================
echo qOntology 一键启动
echo ========================================

REM Run all preflight checks before pulling images or creating containers.
call :prepare
if errorlevel 1 goto :failed

REM Prepare only the missing images required by the deployment profile.
call :pull_images
if errorlevel 1 goto :failed

REM 首次启动时从完整 neo4j.dump 初始化数据库；已有数据库不会重复导入。
call :init_neo4j_dump
if errorlevel 1 goto :failed

REM Start the complete qOntology application stack.
echo [信息] 正在启动 qOntology 全部服务...
docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" up -d --pull never --wait --wait-timeout 1200
if errorlevel 1 (
echo [错误] qOntology 全部服务启动失败。
    goto :failed
)

REM 启动完成后检查实际节点数，避免残留空目录导致错误跳过 dump 导入。
call :verify_neo4j_data
if errorlevel 1 goto :failed

REM Report container status and print the configured web address.
docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" ps
echo.
echo [成功] qOntology 全部服务已启动。
set "QONTOLOGY_WEB_PORT=8897"
for /f "tokens=1,* delims==" %%A in ('findstr /B "EXPOSE_NGINX_PORT=" "%QONTOLOGY_ENV%"') do set "QONTOLOGY_WEB_PORT=%%B"
echo 访问地址：http://localhost:%QONTOLOGY_WEB_PORT%
goto :finished

REM Prepare the host and deployment configuration for an all-services startup.
:prepare
REM Confirm that Docker is installed and its daemon is reachable.
where docker >nul 2>&1
if errorlevel 1 (
echo [错误] 未安装 Docker，请先安装 Docker Desktop。
    exit /b 1
)

docker info >nul 2>&1
if errorlevel 1 (
echo [错误] Docker Desktop 未启动或当前无法访问。
    exit /b 1
)

REM Require Linux Containers mode because all qOntology images target Linux.
set "QONTOLOGY_OS_TYPE="
for /f "usebackq delims=" %%O in (`docker info --format "{{.OSType}}" 2^>nul`) do set "QONTOLOGY_OS_TYPE=%%O"
if /i not "%QONTOLOGY_OS_TYPE%"=="linux" (
echo [错误] Docker Desktop 必须切换到 Linux Containers 模式。
    exit /b 1
)

REM Require the Docker Compose v2 command used by this deployment package.
docker compose version >nul 2>&1
if errorlevel 1 (
echo [错误] 需要安装 Docker Compose v2。
    exit /b 1
)

REM Verify that Docker Desktop has enough CPU and memory for all services.
call :check_resources
if errorlevel 1 exit /b 1

REM Use the existing deployment environment; do not generate new configuration.
if not exist "%QONTOLOGY_ENV%" (
    echo [错误] 缺少 .env 文件。
    exit /b 1
)

REM Check the files mounted by qOntology before Docker can create empty directories.
for %%F in (
    "qOntology\nginx\dist\index.html"
    "qOntology\nginx\nginx.conf"
    "qOntology\nginx\sites\qontology.conf"
    "qOntology\server\application.yml"
    "qOntology\server\application-prod.yml"
    "qOntology\mysql\conf.d\my.cnf"
    "qOntology\mysql\init\init-system.sql"
    "qOntology\mysql\init\init-show.sql"
    "qOntology\neo4j\neo4j.dump"
) do if not exist "%%~F" (
    echo [错误] 缺少 %%~F，部署包不完整。
    exit /b 1
)

REM Validate the resolved Compose model before changing Docker state.
docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" config --quiet
if errorlevel 1 (
echo [错误] Compose 配置校验失败，建议使用 Docker Compose 2.20.2 或更高版本。
    exit /b 1
)

exit /b 0

:init_neo4j_dump
REM databases\neo4j 不存在时才执行首次导入；空库情况由 verify_neo4j_data 处理。
if exist "qOntology\neo4j\data\databases\neo4j" exit /b 0
echo [信息] 检测到首次启动，正在导入 neo4j.dump...
if not exist "qOntology\neo4j\data" mkdir "qOntology\neo4j\data"
set "QONTOLOGY_NEO4J_IMAGE="
set "QONTOLOGY_HUB_VALUE="
REM 从 .env 读取镜像仓库地址，避免写死私有仓库前缀。
for /f "tokens=1,* delims==" %%A in ('findstr /B /L "QONTOLOGY_HUB=" "%QONTOLOGY_ENV%"') do set "QONTOLOGY_HUB_VALUE=%%B"
if defined QONTOLOGY_HUB_VALUE set "QONTOLOGY_NEO4J_IMAGE=%QONTOLOGY_HUB_VALUE%/neo4j:4.4.40"
if not defined QONTOLOGY_NEO4J_IMAGE (
    echo [错误] 无法从 %QONTOLOGY_ENV% 读取 QONTOLOGY_HUB。
    exit /b 1
)
REM 直接以 Neo4j 用户执行 neo4j-admin，生成的数据目录权限可被正式容器使用。
docker run --rm --user 7474:7474 -v "%CD%\qOntology\neo4j\data:/data" -v "%CD%\qOntology\neo4j\neo4j.dump:/dump/neo4j.dump:ro" --entrypoint neo4j-admin "%QONTOLOGY_NEO4J_IMAGE%" load --database=neo4j --from=/dump/neo4j.dump --force
if errorlevel 1 (
    echo [错误] Neo4j dump 导入失败，未启动服务。
    exit /b 1
)
echo [成功] Neo4j dump 初始化完成。
exit /b 0

:verify_neo4j_data
REM 查询节点数；大于 0 表示 dump 数据已经正确加载。
set "QONTOLOGY_NEO4J_NODE_COUNT="
for /f "usebackq skip=1 delims=" %%N in (`docker exec %QONTOLOGY_PROJECT%-neo4j-1 cypher-shell -u neo4j -p InC3tmU4bijT4vkl "MATCH (n) RETURN count(n)" 2^>nul`) do set "QONTOLOGY_NEO4J_NODE_COUNT=%%N"
if not defined QONTOLOGY_NEO4J_NODE_COUNT (
    echo [错误] 无法读取 Neo4j 节点数量。
    exit /b 1
)
if not "%QONTOLOGY_NEO4J_NODE_COUNT%"=="0" (
    echo [信息] Neo4j 数据校验通过：%QONTOLOGY_NEO4J_NODE_COUNT% 个节点。
    exit /b 0
)

echo [警告] 检测到 Neo4j 数据目录存在但节点数为 0，正在自动重新导入 dump...
REM 停止依赖 Neo4j 的服务，再将空库目录移动为带时间戳的备份。
docker stop %QONTOLOGY_PROJECT%-nginx-1 %QONTOLOGY_PROJECT%-qontology-api-1 %QONTOLOGY_PROJECT%-neo4j-1 >nul
powershell -NoProfile -Command "$data = Join-Path (Get-Location) 'qOntology\neo4j\data'; $stamp = Get-Date -Format 'yyyyMMdd-HHmmss'; if (Test-Path -LiteralPath $data) { Move-Item -LiteralPath $data -Destination ($data + '-empty-backup-' + $stamp) }; New-Item -ItemType Directory -Path $data | Out-Null"
if errorlevel 1 (
    echo [错误] 备份空 Neo4j 数据目录失败。
    exit /b 1
)
call :init_neo4j_dump
if errorlevel 1 exit /b 1
docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" up -d --pull never --wait --wait-timeout 1200
if errorlevel 1 exit /b 1
set "QONTOLOGY_NEO4J_NODE_COUNT="
REM 重启后再次校验，仍为 0 时明确失败，不允许带着空库继续运行。
for /f "usebackq skip=1 delims=" %%N in (`docker exec %QONTOLOGY_PROJECT%-neo4j-1 cypher-shell -u neo4j -p InC3tmU4bijT4vkl "MATCH (n) RETURN count(n)" 2^>nul`) do set "QONTOLOGY_NEO4J_NODE_COUNT=%%N"
if not defined QONTOLOGY_NEO4J_NODE_COUNT exit /b 1
if "%QONTOLOGY_NEO4J_NODE_COUNT%"=="0" (
    echo [错误] 重新导入后 Neo4j 仍然没有节点。
    exit /b 1
)
echo [成功] Neo4j dump 已重新导入：%QONTOLOGY_NEO4J_NODE_COUNT% 个节点。
exit /b 0

REM Check Docker resource allocations against the all-services recommendations.
:check_resources
REM Define minimum resources and clear values populated from Docker Desktop.
set "QONTOLOGY_MIN_CPU=4"
set "QONTOLOGY_MIN_MEMORY_GB=6"
set "QONTOLOGY_CPU="
set "QONTOLOGY_MEMORY_GB="

REM Read the CPU count and convert the Docker memory allocation to whole gigabytes.
for /f "usebackq delims=" %%O in (`docker info --format "{{.NCPU}}" 2^>nul`) do set "QONTOLOGY_CPU=%%O"
for /f "usebackq delims=" %%O in (`powershell -NoProfile -Command "[math]::Floor([double](docker info --format '{{.MemTotal}}') / 1GB)" 2^>nul`) do set "QONTOLOGY_MEMORY_GB=%%O"

if not defined QONTOLOGY_CPU (
echo [错误] 无法读取 Docker CPU 资源。
    exit /b 1
)
if not defined QONTOLOGY_MEMORY_GB (
echo [错误] 无法读取 Docker 内存资源。
    exit /b 1
)

REM Compare the detected allocations with the all-services minimums.
set "QONTOLOGY_RESOURCE_INSUFFICIENT=0"
if %QONTOLOGY_CPU% LSS %QONTOLOGY_MIN_CPU% set "QONTOLOGY_RESOURCE_INSUFFICIENT=1"
if %QONTOLOGY_MEMORY_GB% LSS %QONTOLOGY_MIN_MEMORY_GB% set "QONTOLOGY_RESOURCE_INSUFFICIENT=1"

REM Require explicit user confirmation before starting with insufficient resources.
if "%QONTOLOGY_RESOURCE_INSUFFICIENT%"=="1" (
    echo [警告] Docker 可用资源不足：当前 %QONTOLOGY_CPU%CPU/%QONTOLOGY_MEMORY_GB%GB，全部服务最低建议 %QONTOLOGY_MIN_CPU%CPU/%QONTOLOGY_MIN_MEMORY_GB%GB。
    choice /C YN /N /M "是否仍要强制启动？[Y/N] "
    if errorlevel 2 (
        echo [信息] 已取消启动。
        exit /b 1
    )
    echo [警告] 已选择强制启动，请自行承担资源不足导致的运行风险。
)

echo [信息] Docker 可用资源：%QONTOLOGY_CPU%CPU/%QONTOLOGY_MEMORY_GB%GB
exit /b 0

REM Reuse local images and pull only missing references, including latest tags.
:pull_images
REM Compose 输出本次部署使用的全部镜像，逐个检查并仅拉取缺失项。
for /f "usebackq delims=" %%I in (`docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" config --images`) do (
    call :pull_image "%%I"
    if errorlevel 1 exit /b 1
)
exit /b 0

:pull_image
REM 单个镜像最多重试三次，已有本地镜像时直接复用。
set "QONTOLOGY_IMAGE=%~1"
docker image inspect "%QONTOLOGY_IMAGE%" >nul 2>&1
if not errorlevel 1 (
    echo [信息] 使用本地镜像：%QONTOLOGY_IMAGE%
    exit /b 0
)
set "QONTOLOGY_PULL_ATTEMPT=1"
:pull_retry
echo [信息] 正在拉取 %QONTOLOGY_IMAGE%（第 %QONTOLOGY_PULL_ATTEMPT%/3 次）...
docker pull "%QONTOLOGY_IMAGE%"
if not errorlevel 1 exit /b 0
if "%QONTOLOGY_PULL_ATTEMPT%"=="3" (
    echo [错误] 镜像拉取失败：%QONTOLOGY_IMAGE%，请检查仓库权限、网络和 CPU 架构。
    exit /b 1
)
set /a QONTOLOGY_PULL_ATTEMPT+=1
timeout /t 5 /nobreak >nul
goto :pull_retry

REM Convert any workflow failure into a non-zero process exit code.
:failed
set "QONTOLOGY_EXIT_CODE=1"
echo.
echo qOntology 启动失败，请查看上方错误信息。

REM Pause for interactive launchers and return the final workflow result.
:finished
echo.
pause
exit /b %QONTOLOGY_EXIT_CODE%
