#!/bin/bash

# EVCC System Startup Script

echo "🚀 EVCC System Startup"
echo "======================"

echo "📋 Thông tin hệ thống:"
echo "- Database: PostgreSQL (localhost:5432)"
echo "- Backend: Spring Boot (localhost:3000)"
echo "- Frontend: Spring Boot (localhost:8081)"
echo "- Admin Panel: http://localhost:8081/admin"
echo ""

echo "🔑 Admin Account (tự động tạo khi chạy backend):"
echo "- Username: admin"
echo "- Password: admin"
echo "- Quyền: ADMIN, USER"
echo ""

echo "📋 Các bước khởi động:"
echo "1. Khởi động database: docker-compose up -d postgres"
echo "2. Khởi động backend: cd evcc && ./mvnw spring-boot:run"
echo "3. Khởi động frontend: cd fe && ./mvnw spring-boot:run"
echo ""

echo "🔧 Admin có thể:"
echo "- Xác minh người dùng: PUT /api/users/{userId}/verify"
echo "- Thêm xe mới: POST /api/vehicles"
echo "- Quản lý hợp đồng: PUT /api/contracts/{id}/status"
echo "- Xem thống kê: GET /api/users/stats"
echo ""

read -p "Bấm Enter để tiếp tục..."