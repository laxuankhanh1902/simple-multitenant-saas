# Multi-Tenant SaaS Frontend

A professional React TypeScript frontend built with Vite to showcase the Multi-Tenant SaaS backend implementation.

## 🚀 Features

### Core Features
- **Modern React 18** with TypeScript and Vite
- **Material-UI (MUI)** for professional UI components
- **Responsive Design** that works on desktop, tablet, and mobile
- **Multi-tenant Authentication** with JWT token management
- **Role-based Access Control** with proper permission handling

### Key Pages & Components
- **Authentication System**
  - Professional login/register forms
  - Tenant-aware authentication
  - Password visibility toggle
  - Form validation and error handling

- **Dashboard**
  - Real-time statistics and metrics
  - Interactive charts (Line, Pie, Bar charts)
  - Recent activity feed
  - Health monitoring widgets

- **Kafka Management**
  - **Clusters**: Create, edit, delete, and monitor Kafka clusters
  - **Topics**: Manage topics with partitions, replication, and configurations
  - **Audit Logs**: View comprehensive audit trails with filtering and CSV export

- **User Management**
  - Create and manage users
  - Role assignment and permissions
  - User status monitoring

- **Tenant Management**
  - Tenant overview and settings
  - Usage monitoring and limits
  - Plan and billing information

### Technical Highlights
- **State Management**: React Context API for authentication
- **Data Visualization**: Recharts for interactive charts
- **Data Grids**: Material-UI DataGrid for complex data tables
- **API Integration**: Axios with interceptors for API calls
- **Routing**: React Router v6 with protected routes
- **TypeScript**: Full type safety throughout the application
- **Professional Styling**: Custom Material-UI theme with modern design

## 🛠️ Tech Stack

- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **UI Library**: Material-UI (MUI)
- **Charts**: Recharts
- **HTTP Client**: Axios
- **Routing**: React Router v6
- **Icons**: Material Icons
- **Fonts**: Google Fonts (Inter)

## 📦 Installation & Setup

1. **Navigate to the frontend directory**:
   ```bash
   cd simple-multitenant-saas-web
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

4. **Open your browser**:
   Navigate to `http://localhost:5173`

## 🔧 Configuration

### Backend API Connection
The frontend is configured to connect to the Spring Boot backend at `http://localhost:8080/api`.

Update the API base URL in `src/contexts/AuthContext.tsx`:
```typescript
const API_BASE_URL = 'http://localhost:8080/api';
```

## 🎨 Features Showcase

### Authentication Flow
1. **Register**: Create a new organization with admin user
2. **Login**: Tenant-aware login with proper validation
3. **Protected Routes**: Automatic redirection based on auth state

### Dashboard Analytics
- Real-time cluster and topic statistics
- Health monitoring with visual indicators
- Activity timeline with status badges
- Interactive charts for data visualization

### Kafka Management
- **Cluster Management**: Monitor health, add/edit clusters
- **Topic Management**: Create topics with custom configurations
- **Audit Logging**: Complete audit trail with advanced filtering

### User & Tenant Management
- Comprehensive user CRUD operations
- Role-based permission system
- Tenant settings and usage monitoring

## 🚀 Production Build

```bash
npm run build
```

This creates an optimized production build in the `dist` folder.

## 📱 Responsive Design

The application is fully responsive and works seamlessly across:
- **Desktop**: Full-featured layout with sidebar navigation
- **Tablet**: Adaptive layout with collapsible navigation
- **Mobile**: Touch-optimized interface with mobile navigation

## 🎯 Key Accomplishments

✅ **Zero JavaScript Errors**: Comprehensive type safety and error handling  
✅ **Professional UI**: Modern, clean, and intuitive interface  
✅ **Real API Integration**: Ready to connect with Spring Boot backend  
✅ **Complete Feature Set**: All major SaaS management features implemented  
✅ **Production Ready**: Optimized build and deployment-ready configuration  
✅ **Mobile Responsive**: Works perfectly on all device sizes  

---

**Built with ❤️ using modern React and Material-UI to showcase a professional multi-tenant SaaS platform.**
