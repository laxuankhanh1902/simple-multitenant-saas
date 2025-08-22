import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import Dashboard from './components/dashboard/Dashboard';
import KafkaClusters from './components/kafka/KafkaClusters';
import KafkaTopics from './components/kafka/KafkaTopics';
import KafkaAuditLogs from './components/kafka/KafkaAuditLogs';
import UserManagement from './components/users/UserManagement';
import TenantManagement from './components/tenants/TenantManagement';
import Layout from './components/layout/Layout';


function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { token } = useAuth();
  return token ? <>{children}</> : <Navigate to="/login" replace />;
}

function PublicRoute({ children }: { children: React.ReactNode }) {
  const { token } = useAuth();
  return !token ? <>{children}</> : <Navigate to="/dashboard" replace />;
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          } />
          <Route path="/register" element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          } />
          
          {/* Protected routes */}
          <Route path="/" element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="kafka">
              <Route path="clusters" element={<KafkaClusters />} />
              <Route path="topics" element={<KafkaTopics />} />
              <Route path="audit-logs" element={<KafkaAuditLogs />} />
            </Route>
            <Route path="users" element={<UserManagement />} />
            <Route path="tenants" element={<TenantManagement />} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
