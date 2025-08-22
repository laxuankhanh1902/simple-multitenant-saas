# Multi-Tenant SaaS Framework

## Project Overview
A full-stack multi-tenant SaaS application built with Spring Boot backend and React TypeScript frontend, featuring Kafka management capabilities, user authentication, and tenant isolation.

## Architecture
- **Backend**: Spring Boot 3.x with Java 17, PostgreSQL, Flyway migrations
- **Frontend**: React 18 with TypeScript, Vite, Tailwind CSS
- **Authentication**: JWT-based with multi-tenant context
- **Database**: PostgreSQL with tenant isolation
- **Message Broker**: Kafka integration for audit logs and events

## Key Features
- Multi-tenant architecture with tenant isolation
- JWT authentication with role-based access control
- Kafka cluster management and monitoring
- User and tenant management
- Audit logging for all operations
- Professional dark theme UI with glass morphism
- Real-time dashboard with metrics

## Development Setup

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.9+

### Backend Setup
```bash
cd /Users/franklx/IdeaProjects/simple-multitenant-saas
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend Setup
```bash
cd /Users/franklx/IdeaProjects/simple-multitenant-saas/simple-multitenant-saas-web
npm install
npm run dev
```

## Key Commands

### Backend
- **Development**: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
- **Production**: `mvn spring-boot:run -Dspring-boot.run.profiles=prod`
- **Tests**: `mvn test`
- **Build**: `mvn clean package`

### Frontend
- **Development**: `npm run dev`
- **Build**: `npm run build`
- **Preview**: `npm run preview`
- **Lint**: `npm run lint`
- **Type Check**: `npm run typecheck`

## Database Configuration
- **URL**: `jdbc:postgresql://localhost:5432/saas_framework`
- **Username**: `postgres`
- **Password**: `postgres`
- **Migrations**: Managed by Flyway in `src/main/resources/db/migration/`

## Authentication
- JWT tokens with 24-hour expiration
- Multi-tenant context in JWT payload
- Role-based access control (USER, ADMIN, SUPER_ADMIN)
- Tenant isolation at database level

## API Endpoints
- **Auth**: `/api/auth/login`, `/api/auth/register`
- **Users**: `/api/users`
- **Tenants**: `/api/tenants`
- **Kafka Clusters**: `/api/kafka/clusters`
- **Kafka Topics**: `/api/kafka/topics`
- **Audit Logs**: `/api/kafka/audit-logs`

## Recent Fixes
- Fixed JWT authentication filter database transaction issues
- Resolved autoCommit configuration conflicts
- Implemented proper transaction management for multi-tenant context
- Updated UI with professional dark theme using Tailwind CSS
- Fixed Heroicons import issues and layout overlapping problems

## Configuration Notes
- HikariCP connection pool with `auto-commit: false`
- Hibernate with `provider_disables_autocommit: false`
- CORS enabled for frontend development
- JWT secret key configurable via application properties

## Troubleshooting
- **403 Errors**: Ensure JWT token is valid and not expired
- **Database Issues**: Check PostgreSQL connection and credentials
- **Frontend Build**: Clear node_modules and reinstall if needed
- **CORS Issues**: Verify allowed origins in application.yml

## Development Tips
- Use `@Transactional(readOnly = true)` for read-only database operations
- Tenant context is automatically set via JWT filter
- All API endpoints require authentication except auth endpoints
- Frontend uses React Router for navigation with protected routes