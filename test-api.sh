#!/bin/bash

# Test API endpoints

BASE_URL="http://localhost:8080/api"

echo "🧪 Testing Multi-Tenant SaaS API"
echo "================================"

# Test health endpoint
echo "1. Testing health endpoint..."
curl -s "$BASE_URL/actuator/health" | grep -q "UP" && echo "✅ Health check passed" || echo "❌ Health check failed"

# Test authentication (this will fail until app is running, but shows the endpoint)
echo ""
echo "2. Available endpoints to test once app is running:"
echo "   POST $BASE_URL/auth/login"
echo "   GET  $BASE_URL/users"
echo "   GET  $BASE_URL/tenants"
echo "   GET  $BASE_URL/swagger-ui.html"

echo ""
echo "3. Sample login credentials:"
echo "   System Admin: admin@system.com / password123"
echo "   ACME Admin: john.smith@acme-corp.com / password123"
echo "   TechStart Admin: sarah.johnson@techstart.com / password123"

echo ""
echo "4. Database access:"
echo "   Adminer: http://localhost:8081"
echo "   Server: postgres"
echo "   Username: postgres"
echo "   Password: postgres123"
echo "   Database: saas_framework"
