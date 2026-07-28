#!/bin/bash
set -e

# ============================================
# File Server 一键部署脚本
# 用法: ./deploy.sh [start|stop|restart|logs|status]
# ============================================

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# 默认端口
export PORT="${PORT:-8000}"
# 文件存储目录（默认: 脚本所在目录下的 data 文件夹）
export DATA_DIR="${DATA_DIR:-$SCRIPT_DIR/data}"

# 颜色输出
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    if ! docker info &> /dev/null; then
        error "Docker 守护进程未运行，请先启动 Docker"
        exit 1
    fi
    # 检查 docker compose (v2) 或 docker-compose (v1)
    if docker compose version &> /dev/null; then
        COMPOSE_CMD="docker compose"
    elif command -v docker-compose &> /dev/null; then
        COMPOSE_CMD="docker-compose"
    else
        error "Docker Compose 未安装"
        exit 1
    fi
}

do_build() {
    info "开始构建 File Server (前端 + 后端)..."
    $COMPOSE_CMD build --no-cache
    info "构建完成！"
}

do_start() {
    info "启动 File Server..."
    mkdir -p "$DATA_DIR"
    $COMPOSE_CMD up -d
    info "========================================="
    info " File Server 已启动！"
    info " 访问地址: http://localhost:${PORT}"
    info " 存储目录: ${DATA_DIR}"
    info "========================================="
}

do_stop() {
    info "停止 File Server..."
    $COMPOSE_CMD down
    info "已停止"
}

do_restart() {
    do_stop
    do_start
}

do_logs() {
    $COMPOSE_CMD logs -f --tail=100
}

do_status() {
    $COMPOSE_CMD ps
}

do_rebuild() {
    info "重新构建并启动..."
    $COMPOSE_CMD down
    $COMPOSE_CMD build --no-cache
    $COMPOSE_CMD up -d
    info "========================================="
    info " File Server 已重新部署！"
    info " 访问地址: http://localhost:${PORT}"
    info "========================================="
}

# 主逻辑
check_docker

case "${1:-start}" in
    build)
        do_build
        ;;
    start)
        do_start
        ;;
    stop)
        do_stop
        ;;
    restart)
        do_restart
        ;;
    rebuild)
        do_rebuild
        ;;
    logs)
        do_logs
        ;;
    status)
        do_status
        ;;
    *)
        echo "用法: $0 {build|start|stop|restart|rebuild|logs|status}"
        echo ""
        echo "  build   - 仅构建镜像"
        echo "  start   - 启动服务 (默认)"
        echo "  stop    - 停止服务"
        echo "  restart - 重启服务"
        echo "  rebuild - 重新构建并启动"
        echo "  logs    - 查看日志"
        echo "  status  - 查看服务状态"
        echo ""
        echo "环境变量:"
        echo "  PORT     - 服务端口 (默认: 8000)"
        echo "  DATA_DIR - 文件存储目录 (默认: ./data)"
        exit 1
        ;;
esac
