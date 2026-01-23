#!/bin/bash

# Spring PetClinic 微服务启动脚本
# 此脚本将按正确顺序启动所有微服务

MICROSERVICES_DIR="/Users/xiading/workspace/spring-petclinic/microservices"
LOG_DIR="$MICROSERVICES_DIR/logs"

# 创建日志目录
mkdir -p "$LOG_DIR"

echo "======================================"
echo "启动 Spring PetClinic 微服务"
echo "======================================"
echo ""

# 停止已有的服务
echo "停止现有服务..."
pkill -f "spring-petclinic" 2>/dev/null
sleep 2

# 1. 启动 Config Server
echo "1. 启动 Config Server (端口 8888)..."
cd "$MICROSERVICES_DIR/config-server"
nohup mvn spring-boot:run > "$LOG_DIR/config-server.log" 2>&1 &
echo "   等待 Config Server 启动..."
sleep 20

# 检查 Config Server
if curl -s http://localhost:8888/actuator/health > /dev/null; then
    echo "   ✓ Config Server 启动成功"
else
    echo "   ✗ Config Server 启动失败，请检查日志: $LOG_DIR/config-server.log"
fi

# 2. 启动 Discovery Server
echo ""
echo "2. 启动 Discovery Server (端口 8761)..."
cd "$MICROSERVICES_DIR/discovery-server"
nohup mvn spring-boot:run > "$LOG_DIR/discovery-server.log" 2>&1 &
echo "   等待 Discovery Server 启动..."
sleep 25

# 检查 Discovery Server
if curl -s http://localhost:8761/actuator/health > /dev/null; then
    echo "   ✓ Discovery Server 启动成功"
else
    echo "   ✗ Discovery Server 启动失败，请检查日志: $LOG_DIR/discovery-server.log"
fi

# 3. 启动 API Gateway
echo ""
echo "3. 启动 API Gateway (端口 8080)..."
cd "$MICROSERVICES_DIR/api-gateway"
nohup mvn spring-boot:run > "$LOG_DIR/api-gateway.log" 2>&1 &
echo "   等待 API Gateway 启动..."
sleep 15

# 4. 启动业务服务
echo ""
echo "4. 启动业务服务..."

echo "   - Vet Service (端口 8084)..."
cd "$MICROSERVICES_DIR/vet-service"
nohup mvn spring-boot:run > "$LOG_DIR/vet-service.log" 2>&1 &

echo "   - Owner Service (端口 8081)..."
cd "$MICROSERVICES_DIR/owner-service"
nohup mvn spring-boot:run > "$LOG_DIR/owner-service.log" 2>&1 &

echo "   - Pet Service (端口 8082)..."
cd "$MICROSERVICES_DIR/pet-service"
nohup mvn spring-boot:run > "$LOG_DIR/pet-service.log" 2>&1 &

echo "   - Visit Service (端口 8083)..."
cd "$MICROSERVICES_DIR/visit-service"
nohup mvn spring-boot:run > "$LOG_DIR/visit-service.log" 2>&1 &

echo ""
echo "   等待所有业务服务启动..."
sleep 30

# 检查所有服务状态
echo ""
echo "======================================"
echo "服务状态检查"
echo "======================================"

check_service() {
    local name=$1
    local port=$2
    if curl -s http://localhost:$port/actuator/health > /dev/null 2>&1; then
        echo "✓ $name (端口 $port) - 运行中"
        return 0
    else
        echo "✗ $name (端口 $port) - 未运行"
        return 1
    fi
}

check_service "Config Server" 8888
check_service "Discovery Server" 8761
check_service "API Gateway" 8080
check_service "Owner Service" 8081
check_service "Pet Service" 8082
check_service "Visit Service" 8083
check_service "Vet Service" 8084

echo ""
echo "======================================"
echo "启动完成！"
echo "======================================"
echo ""
echo "访问地址："
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - API Gateway: http://localhost:8080"
echo "  - Owner API: http://localhost:8080/api/owners"
echo ""
echo "查看日志："
echo "  日志目录: $LOG_DIR"
echo "  例如: tail -f $LOG_DIR/api-gateway.log"
echo ""
echo "停止所有服务："
echo "  pkill -f 'spring-petclinic'"
echo ""
