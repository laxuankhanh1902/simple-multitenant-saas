import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  BuildingOfficeIcon,
  UsersIcon,
  ServerIcon,
  ArrowTrendingUpIcon,
  Cog6ToothIcon,
  DocumentArrowDownIcon,
  KeyIcon,
  CreditCardIcon,
  DocumentTextIcon,
  PencilSquareIcon,
  ExclamationTriangleIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../../contexts/AuthContext';

const TenantManagement: React.FC = () => {
  const { user, tenantId } = useAuth();
  const [tenantData, setTenantData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Mock tenant data fallback
  const mockTenantData = {
    name: 'Enterprise Corp',
    subdomain: tenantId || 'enterprise-corp',
    status: 'ACTIVE',
    plan: 'Professional',
    adminEmail: user?.email || 'admin@enterprise.com',
    adminName: user?.fullName || 'Admin User',
    createdAt: '2025-01-15T10:00:00Z',
    stats: {
      totalUsers: 12,
      totalClusters: 5,
      totalTopics: 23,
      storageUsed: 2.4, // GB
      storageLimit: 100, // GB
    },
    limits: {
      maxUsers: 50,
      maxClusters: 10,
      apiRateLimit: 1000, // requests per minute
      storageLimit: 100, // GB
    },
  };

  useEffect(() => {
    fetchTenantData();
  }, []);

  const fetchTenantData = async () => {
    setLoading(true);
    setError('');
    
    try {
      const response = await axios.get('/tenant/current');
      if (response.data.success && response.data.data) {
        setTenantData(response.data.data);
      } else {
        throw new Error('Invalid API response format');
      }
    } catch (err) {
      console.error('Failed to fetch tenant data:', err);
      setError('Failed to load tenant data. Using sample data.');
      // Fallback to mock data if API fails
      setTenantData(mockTenantData);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-dark-950 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="animate-pulse">
            <div className="h-12 bg-dark-700 rounded w-96 mb-2"></div>
            <div className="h-6 bg-dark-700 rounded w-128 mb-8"></div>
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6 mb-6">
              {[1, 2, 3, 4].map((item) => (
                <div key={item} className="glass-card">
                  <div className="h-32 bg-dark-700 rounded"></div>
                </div>
              ))}
            </div>
            <div className="glass-card">
              <div className="h-96 bg-dark-700 rounded"></div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-dark-950 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="glass-card p-6 border-error-700/50">
            <div className="flex items-center space-x-3">
              <ExclamationTriangleIcon className="h-8 w-8 text-error-400" />
              <div>
                <h3 className="text-lg font-semibold text-error-400">Error Loading Tenant Data</h3>
                <p className="text-gray-300">{error}</p>
              </div>
            </div>
            <button 
              onClick={fetchTenantData}
              className="mt-4 btn-primary"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  if (!tenantData) {
    return (
      <div className="min-h-screen bg-dark-950 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="glass-card p-6 border-warning-700/50">
            <div className="flex items-center space-x-3">
              <ExclamationTriangleIcon className="h-8 w-8 text-warning-400" />
              <div>
                <h3 className="text-lg font-semibold text-warning-400">No Tenant Data</h3>
                <p className="text-gray-300">No tenant data available</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-dark-950 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold gradient-text-green mb-2 flex items-center space-x-3">
            <BuildingOfficeIcon className="h-10 w-10 text-green-400" />
            <span>Tenant Management</span>
          </h1>
          <p className="text-xl text-gray-300">
            Manage your organization settings and monitor resource usage
          </p>
        </div>

        {/* Tenant Overview */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          <div className="lg:col-span-2">
            <div className="glass-card">
              <div className="card-header">
                <div className="flex items-center space-x-4">
                  <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-emerald-500 rounded-xl flex items-center justify-center">
                    <BuildingOfficeIcon className="h-8 w-8 text-white" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-bold text-gray-100">
                      {tenantData.name}
                    </h2>
                    <p className="text-gray-400">
                      Subdomain: {tenantData.subdomain}
                    </p>
                    <div className="flex space-x-2 mt-2">
                      <span className="badge-success">
                        {tenantData.status}
                      </span>
                      <span className="badge-primary">
                        {tenantData.plan}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-1">
                    Admin Email
                  </label>
                  <p className="text-gray-100 font-medium">
                    {tenantData.adminEmail}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-1">
                    Admin Name
                  </label>
                  <p className="text-gray-100 font-medium">
                    {tenantData.adminName}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-1">
                    Created
                  </label>
                  <p className="text-gray-100 font-medium">
                    {new Date(tenantData.createdAt).toLocaleDateString()}
                  </p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-400 mb-1">
                    Plan
                  </label>
                  <p className="text-gray-100 font-medium">
                    {tenantData.plan}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            {/* Users Card */}
            <div className="glass-card group hover:scale-105 transition-all duration-300">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <p className="text-sm font-medium text-gray-400 tracking-wider uppercase">
                    Total Users
                  </p>
                  <h3 className="text-3xl font-bold bg-gradient-to-r from-blue-400 to-cyan-400 bg-clip-text text-transparent">
                    {tenantData.stats.totalUsers}
                  </h3>
                  <p className="text-sm text-gray-400">
                    of {tenantData.limits.maxUsers} limit
                  </p>
                </div>
                <div className="p-3 rounded-xl bg-blue-500/20 group-hover:scale-110 transition-transform duration-300">
                  <UsersIcon className="h-6 w-6 text-blue-400" />
                </div>
              </div>
              <div className="w-full bg-dark-700 rounded-full h-2">
                <div 
                  className="bg-gradient-to-r from-blue-500 to-cyan-500 h-2 rounded-full transition-all duration-500"
                  style={{ width: `${(tenantData.stats.totalUsers / tenantData.limits.maxUsers) * 100}%` }}
                ></div>
              </div>
            </div>

            {/* Storage Card */}
            <div className="glass-card group hover:scale-105 transition-all duration-300">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <p className="text-sm font-medium text-gray-400 tracking-wider uppercase">
                    Storage Used
                  </p>
                  <h3 className="text-3xl font-bold bg-gradient-to-r from-orange-400 to-red-400 bg-clip-text text-transparent">
                    {tenantData.stats.storageUsed}GB
                  </h3>
                  <p className="text-sm text-gray-400">
                    of {tenantData.limits.storageLimit}GB limit
                  </p>
                </div>
                <div className="p-3 rounded-xl bg-orange-500/20 group-hover:scale-110 transition-transform duration-300">
                  <ServerIcon className="h-6 w-6 text-orange-400" />
                </div>
              </div>
              <div className="w-full bg-dark-700 rounded-full h-2">
                <div 
                  className="bg-gradient-to-r from-orange-500 to-red-500 h-2 rounded-full transition-all duration-500"
                  style={{ width: `${(tenantData.stats.storageUsed / tenantData.limits.storageLimit) * 100}%` }}
                ></div>
              </div>
            </div>
          </div>
        </div>

        {/* Usage Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {[
            {
              title: 'KAFKA CLUSTERS',
              value: tenantData.stats.totalClusters,
              subtitle: `of ${tenantData.limits.maxClusters} limit`,
              icon: ServerIcon,
              gradient: 'from-purple-400 to-pink-400',
              iconBg: 'bg-purple-500/20',
              iconColor: 'text-purple-400',
            },
            {
              title: 'TOTAL TOPICS',
              value: tenantData.stats.totalTopics,
              subtitle: '+3 this week',
              icon: ArrowTrendingUpIcon,
              gradient: 'from-green-400 to-emerald-400',
              iconBg: 'bg-green-500/20',
              iconColor: 'text-green-400',
            },
            {
              title: 'API RATE LIMIT',
              value: tenantData.limits.apiRateLimit,
              subtitle: 'requests/min',
              icon: Cog6ToothIcon,
              gradient: 'from-blue-400 to-cyan-400',
              iconBg: 'bg-blue-500/20',
              iconColor: 'text-blue-400',
            },
            {
              title: 'PLAN STATUS',
              value: tenantData.plan,
              subtitle: 'Active',
              icon: BuildingOfficeIcon,
              gradient: 'from-orange-400 to-red-400',
              iconBg: 'bg-orange-500/20',
              iconColor: 'text-orange-400',
            }
          ].map((item, index) => (
            <div key={index} className="glass-card group hover:scale-105 transition-all duration-300">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <p className="text-sm font-medium text-gray-400 tracking-wider">
                    {item.title}
                  </p>
                </div>
                <div className={`p-3 rounded-xl ${item.iconBg} group-hover:scale-110 transition-transform duration-300`}>
                  <item.icon className={`h-6 w-6 ${item.iconColor}`} />
                </div>
              </div>
              <div className="space-y-2">
                <h3 className={`text-3xl font-bold bg-gradient-to-r ${item.gradient} bg-clip-text text-transparent`}>
                  {item.value}
                </h3>
                <p className="text-sm text-gray-400">
                  {item.subtitle}
                </p>
              </div>
            </div>
          ))}
        </div>

        {/* Actions */}
        <div className="glass-card">
          <div className="card-header">
            <div className="flex items-center space-x-3">
              <Cog6ToothIcon className="h-6 w-6 text-green-400" />
              <h3 className="text-xl font-semibold text-gray-100">Tenant Actions</h3>
            </div>
          </div>
          <div className="flex flex-wrap gap-3">
            <button className="btn-secondary flex items-center space-x-2">
              <PencilSquareIcon className="h-4 w-4" />
              <span>Edit Settings</span>
            </button>
            <button className="btn-secondary flex items-center space-x-2">
              <CreditCardIcon className="h-4 w-4" />
              <span>Manage Billing</span>
            </button>
            <button className="btn-secondary flex items-center space-x-2">
              <DocumentTextIcon className="h-4 w-4" />
              <span>View Audit Logs</span>
            </button>
            <button className="btn-secondary flex items-center space-x-2">
              <DocumentArrowDownIcon className="h-4 w-4" />
              <span>Download Reports</span>
            </button>
            <button className="btn-warning flex items-center space-x-2">
              <KeyIcon className="h-4 w-4" />
              <span>Reset API Keys</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TenantManagement;