import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import axios from 'axios';

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  status: string;
  enabled: boolean;
  tenantAdmin: boolean;
  fullName: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  tenantId: string | null;
  login: (username: string, password: string, tenantId: string) => Promise<boolean>;
  register: (data: RegisterData) => Promise<boolean>;
  logout: () => void;
  loading: boolean;
}

interface RegisterData {
  organizationName: string;
  subdomain: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  phone?: string;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const API_BASE_URL = 'http://localhost:8080/api';

// Configure axios defaults
axios.defaults.baseURL = API_BASE_URL;

// Add request interceptor to include tenant-id and authorization headers
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    const tenantId = localStorage.getItem('tenantId');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    if (tenantId) {
      config.headers['X-Tenant-ID'] = tenantId;
    }
    
    return config;
  },
  (error) => Promise.reject(error)
);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [tenantId, setTenantId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const savedToken = localStorage.getItem('token');
    const savedTenantId = localStorage.getItem('tenantId');
    const savedUser = localStorage.getItem('user');

    if (savedToken && savedTenantId && savedUser) {
      setToken(savedToken);
      setTenantId(savedTenantId);
      setUser(JSON.parse(savedUser));
    }
    setLoading(false);
  }, []);

  const login = async (username: string, password: string, tenantId: string): Promise<boolean> => {
    try {
      const response = await axios.post('/auth/login', 
        { username, password, tenantId }
      );

      if (response.data.success) {
        const { accessToken: newToken, user: userData } = response.data.data;
        
        setToken(newToken);
        setTenantId(tenantId);
        setUser(userData);
        
        localStorage.setItem('token', newToken);
        localStorage.setItem('tenantId', tenantId);
        localStorage.setItem('user', JSON.stringify(userData));
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  const register = async (data: RegisterData): Promise<boolean> => {
    try {
      const response = await axios.post('/auth/register', data);
      
      if (response.data.success) {
        // Auto-login after successful registration
        return await login(data.email, data.password, data.subdomain);
      }
      return false;
    } catch (error) {
      console.error('Registration failed:', error);
      return false;
    }
  };

  const logout = () => {
    setToken(null);
    setTenantId(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('tenantId');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      tenantId,
      login,
      register,
      logout,
      loading
    }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};