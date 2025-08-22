# Multi-Tenant SaaS Framework

A production-ready, comprehensive multi-tenant SaaS framework built with Spring Boot 3, providing complete user management, authentication, and REST APIs for rapid SaaS application development.

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker & Docker Compose
- Git

### One-Command Setup

```bash
# Clone the repository (if needed) and run setup
./setup.sh
```

This script will:
- ✅ Start PostgreSQL and Redis containers
- ✅ Create database with sample data
- ✅ Configure the application
- ✅ Provide testing credentials
- ✅ Optionally start the application

## 📁 Project Structure

```
multi-tenant-saas-framework/
├── src/main/java/io/conduktor/saas/
│   ├── SaasApplication.java              # Main application class
│   ├── config/                           # Configuration classes
│   ├── core/                            # Base entities, repositories, services
│   ├── security/                        # JWT, authentication, tenant isolation
│   ├── tenant/                          # Tenant management
│   ├── user/                            # User management
│   └── auth/                            # Authentication controllers
├── src/main/resources/
│   ├── application.yml                  # Main configuration
│   ├── application-dev.yml              # Development settings
│   ├── application-test.yml             # Test settings
│   └── application-prod.yml             # Production settings
├── docker-compose.yml                   # Docker services (auto-generated)
├── setup.sh                            # One-command setup script
├── test-api.sh                         # API testing script
└── README.md                           # This file
```

## 🎯 Core Features

### 🏗️ **Multi-Tenant Architecture**
- Complete tenant isolation at data level
- Automatic tenant context management
- Per-tenant configuration and limits
- Trial management and subscription handling

### 🔐 **Advanced Security**
- JWT-based authentication with refresh tokens
- Role-based access control (ADMIN, TENANT_ADMIN, USER, VIEWER)
- Account security (locking, password policies, email verification)
- CORS configuration for frontend integration

### 👥 **User Management**
- Complete user lifecycle management
- Password management and security
- Role and permission management
- User activity tracking

### 🏢 **Tenant Management**
- Multi-tenant organization support
- Resource limits and quotas
- Trial and subscription management
- Tenant-specific configurations

### 📊 **Rich REST APIs**
- Comprehensive CRUD operations
- Advanced filtering and search
- Pagination and sorting
- CSV/Excel export capabilities
- OpenAPI/Swagger documentation

## 🗃️ **Database Schema**

The framework includes rich sample data:

### Tenants
- **5 sample tenants** with different configurations
- **ACME Corporation** - Large enterprise
- **TechStart Inc** - Startup in trial mode  
- **Global Retail Solutions** - International company
- **HealthCare Plus** - Healthcare provider
- **EduPlatform** - Education platform

### Users  
- **16+ sample users** across all tenants
- **System admin** with cross-tenant access
- **Tenant admins** for each organization
- **Regular users** with various roles

## 🔧 **Configuration**

### Environment Profiles

- **dev** - Development with Docker PostgreSQL
- **test** - Testing with H2 in-memory database  
- **prod** - Production with external PostgreSQL

### Key Settings

```yaml
# Database
spring.datasource.url: jdbc:postgresql://localhost:5432/saas_framework
spring.datasource.username: postgres
spring.datasource.password: postgres123

# JWT
app.jwt.secret: your-secret-key
app.jwt.expiration: 86400000  # 24 hours

# Multi-tenancy
app.tenant.isolation: SCHEMA  # ROW_LEVEL | SCHEMA | DATABASE
```

## 🧪 **Testing & Development**

### Sample Credentials

```bash
# System Administrator (cross-tenant access)
Email: admin@system.com
Password: password123
Roles: ADMIN, TENANT_ADMIN, USER

# Tenant Administrators
ACME Corp: john.smith@acme-corp.com / password123
TechStart: sarah.johnson@techstart.com / password123
Global Retail: michael.brown@globalretail.com / password123
HealthCare: emily.davis@healthcareplus.com / password123
Education: robert.wilson@eduplatform.com / password123
```

### API Testing

```bash
# Run the application
mvn spring-boot:run -Dspring.profiles.active=dev

# Test API endpoints
./test-api.sh

# Access API documentation
http://localhost:8080/api/swagger-ui.html
```

### Available Endpoints

```http
# Authentication
POST /api/auth/login
POST /api/auth/register  
POST /api/auth/refresh
POST /api/auth/logout

# User Management
GET    /api/users
POST   /api/users
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
GET    /api/users/export

# Tenant Management  
GET    /api/tenants
POST   /api/tenants
GET    /api/tenants/{id}
PUT    /api/tenants/{id}
DELETE /api/tenants/{id}
GET    /api/tenants/export

# Health & Monitoring
GET /api/actuator/health
GET /api/actuator/metrics
```

## 🐳 **Docker Services**

The setup script creates these services:

| Service | Port | Purpose | Credentials |
|---------|------|---------|-------------|
| PostgreSQL | 5432 | Main database | postgres/postgres123 |
| Redis | 6379 | Caching & sessions | No auth |
| Adminer | 8081 | Database management | Use DB credentials |

### Database Management

Access Adminer at http://localhost:8081:
- **Server**: postgres
- **Username**: postgres  
- **Password**: postgres123
- **Database**: saas_framework

## 🏭 **Production Deployment**

### Environment Variables

```bash
# Database
DATABASE_URL=jdbc:postgresql://your-db-host:5432/saas_framework
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Security
JWT_SECRET=your-production-secret-key-256-bits-minimum
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# CORS
CORS_ALLOWED_ORIGINS=https://your-frontend.com,https://your-app.com

# Cache
CACHE_TENANT_SIZE=500
CACHE_USER_SIZE=5000
CACHE_TOKEN_SIZE=10000
```

### Production Configuration

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10

app:
  jwt:
    secret: ${JWT_SECRET}
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS}
```

## 🔍 **Architecture Details**

### Multi-Tenancy Pattern

The framework uses **row-level multi-tenancy**:
- Single database with tenant_id columns
- Automatic tenant filtering in all queries
- Tenant context management via filters
- Complete data isolation between tenants

### Security Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway    │    │   Application   │
│                 │───▶│                  │───▶│                 │
│ (React/Angular) │    │ (Load Balancer)  │    │ (Spring Boot)   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                         │
        ┌────────────────────────────────────────────────┼─────────────┐
        │                 Security Layer                  │             │
        │                                                 │             │
        ▼                                                 ▼             ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│ Tenant Filter   │    │ JWT Auth Filter  │    │ Role-Based      │
│                 │    │                  │    │ Authorization   │
│ (Tenant Context)│    │ (Authentication) │    │ (Method Level)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
        │                        │                        │
        └────────────────────────┼────────────────────────┘
                                 ▼
                    ┌─────────────────────┐
                    │    Data Access      │
                    │                     │
                    │ (Tenant-Aware JPA)  │
                    └─────────────────────┘
                                 │
                                 ▼
                    ┌─────────────────────┐
                    │    PostgreSQL       │
                    │                     │
                    │ (Multi-Tenant Data) │
                    └─────────────────────┘
```

## 🚀 **Extending the Framework**

### Adding New Entities

1. **Create Entity** extending BaseEntity:
```java
@Entity
@Table(name = "your_entities")
public class YourEntity extends BaseEntity {
    // Your fields here
}
```

2. **Create Repository** extending BaseRepository:
```java
public interface YourRepository extends BaseRepository<YourEntity> {
    // Your custom queries here
}
```

3. **Create Service** extending BaseService:
```java
@Service
public class YourService extends BaseService<YourEntity> {
    public YourService(YourRepository repository) {
        super(repository, "YourEntity");
    }
    // Your business logic here
}
```

4. **Create Controller**:
```java
@RestController
@RequestMapping("/api/your-entities")
public class YourController {
    // Your REST endpoints here
}
```

### Adding Authentication to Endpoints

```java
@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin-only")
public ResponseEntity<?> adminOnlyEndpoint() {
    // Admin-only logic
}

@PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('ADMIN')")
@GetMapping("/tenant-admin")
public ResponseEntity<?> tenantAdminEndpoint() {
    // Tenant admin logic
}
```

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📄 **License**

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## 📞 **Support**

For questions and support:
- Create an issue in the repository
- Check the API documentation at `/swagger-ui.html`
- Review the sample data and test scripts

---

**🎉 Happy coding with your Multi-Tenant SaaS Framework!**