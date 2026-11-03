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

REM Start the complete qOntology application stack.
echo [信息] 正在启动 qOntology 全部服务...
docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" up -d --pull never --wait --wait-timeout 1200
if errorlevel 1 (
echo [错误] qOntology 全部服务启动失败。
    goto :failed
)

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
for /f "usebackq delims=" %%I in (`docker compose --env-file "%QONTOLOGY_ENV%" -p "%QONTOLOGY_PROJECT%" -f "%QONTOLOGY_COMPOSE%" --profile "%QONTOLOGY_MODE%" config --images`) do (
    call :pull_image "%%I"
    if errorlevel 1 exit /b 1
)
exit /b 0

:pull_image
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
