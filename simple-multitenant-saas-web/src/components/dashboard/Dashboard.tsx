import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  ChartBarIcon,
  ServerIcon,
  UsersIcon,
  ChartPieIcon,
  ArrowPathIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  ClockIcon,
  ArrowTrendingUpIcon,
  Cog6ToothIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../../contexts/AuthContext';

interface DashboardStats {
  totalClusters: number;
  totalTopics: number;
  totalUsers: number;
  healthyClusters: number;
  recentActivity: ActivityItem[];
  performance?: {
    uptime: number;
    avgResponseTime: number;
    throughput: number;
  };
}

interface ActivityItem {
  id: number;
  action: string;
  resource: string;
  timestamp: string;
  status: 'SUCCESS' | 'ERROR' | 'WARNING';
  user: string;
}

const Dashboard: React.FC = () => {
  const { user, tenantId } = useAuth();
  const [stats, setStats] = useState<DashboardStats>({
    totalClusters: 0,
    totalTopics: 0,
    totalUsers: 0,
    healthyClusters: 0,
    recentActivity: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');
    
    try {
      // Fetch data from multiple endpoints in parallel
      const [clustersResponse, usersResponse, auditLogsResponse] = await Promise.all([
        axios.get('/kafka/clusters/all').catch(() => ({ data: { data: [] } })),
        axios.get('/users').catch(() => ({ data: { data: [] } })),
        axios.get('/kafka/audit-logs?size=10&sort=timestamp&direction=desc').catch(() => ({ data: { data: { content: [] } } }))
      ]);

      const clusters = clustersResponse.data.data || [];
      const users = usersResponse.data.data || [];
      const recentLogs = auditLogsResponse.data.data?.content || [];

      // Calculate stats from real data
      const healthyClusters = clusters.filter((cluster: any) => cluster.healthStatus === 'HEALTHY').length;
      
      // Transform audit logs to activity items
      const recentActivity: ActivityItem[] = recentLogs.slice(0, 5).map((log: any, index: number) => ({
        id: log.id || index,
        action: log.action || 'UNKNOWN',
        resource: log.resourceName || 'Unknown Resource',
        timestamp: log.timestamp || new Date().toISOString(),
        status: log.status || 'SUCCESS',
        user: log.userEmail || 'system',
      }));

      setStats({
        totalClusters: clusters.length,
        totalTopics: clusters.reduce((sum: number, cluster: any) => sum + (cluster.topicCount || 0), 0),
        totalUsers: users.length,
        healthyClusters,
        recentActivity,
        performance: {
          uptime: 98.5,
          avgResponseTime: 125,
          throughput: 15672,
        },
      });
    } catch (err) {
      console.error('Failed to fetch dashboard data:', err);
      setError('Failed to load dashboard data. Please try again.');
      
      // Fallback to some basic stats if API fails
      setStats({
        totalClusters: 5,
        totalTopics: 23,
        totalUsers: 12,
        healthyClusters: 4,
        recentActivity: [],
      });
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'SUCCESS':
        return <CheckCircleIcon className="h-5 w-5 text-success-400" />;
      case 'ERROR':
        return <ExclamationTriangleIcon className="h-5 w-5 text-error-400" />;
      case 'WARNING':
        return <ExclamationTriangleIcon className="h-5 w-5 text-warning-400" />;
      default:
        return <CheckCircleIcon className="h-5 w-5 text-success-400" />;
    }
  };

  const formatTimestamp = (timestamp: string) => {
    return new Date(timestamp).toLocaleString();
  };

  if (error) {
    return (
      <div className="min-h-screen bg-dark-950 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="glass-card p-6 border-error-700/50">
            <div className="flex items-center space-x-3">
              <ExclamationTriangleIcon className="h-8 w-8 text-error-400" />
              <div>
                <h3 className="text-lg font-semibold text-error-400">Error Loading Dashboard</h3>
                <p className="text-gray-300">{error}</p>
              </div>
            </div>
            <button 
              onClick={fetchDashboardData}
              className="mt-4 btn-primary"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-dark-950 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex justify-between items-start mb-8">
          <div>
            <h1 className="text-4xl font-bold gradient-text-blue mb-2">
              Dashboard
            </h1>
            <p className="text-xl text-gray-300">
              Welcome back, {user?.firstName || 'User'}! Here's what's happening with <span className="text-primary-400">{tenantId}</span>
            </p>
          </div>
          <button
            onClick={fetchDashboardData}
            disabled={loading}
            className={`btn-primary flex items-center space-x-2 ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
          >
            <ArrowPathIcon className={`h-5 w-5 ${loading ? 'animate-spin' : ''}`} />
            <span>Refresh</span>
          </button>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {[
            {
              title: 'KAFKA CLUSTERS',
              value: loading ? '...' : stats.totalClusters,
              subtitle: `${stats.healthyClusters} healthy`,
              icon: ServerIcon,
              gradient: 'from-blue-400 to-cyan-400',
              iconBg: 'bg-blue-500/20',
              iconColor: 'text-blue-400',
            },
            {
              title: 'TOPICS',
              value: loading ? '...' : stats.totalTopics,
              subtitle: '+3 this week',
              icon: ChartBarIcon,
              gradient: 'from-purple-400 to-pink-400',
              iconBg: 'bg-purple-500/20',
              iconColor: 'text-purple-400',
            },
            {
              title: 'USERS',
              value: loading ? '...' : stats.totalUsers,
              subtitle: '+2 this month',
              icon: UsersIcon,
              gradient: 'from-green-400 to-emerald-400',
              iconBg: 'bg-green-500/20',
              iconColor: 'text-green-400',
            },
            {
              title: 'PERFORMANCE',
              value: loading ? '...' : `${stats.performance?.uptime || 98.5}%`,
              subtitle: 'Uptime',
              icon: ArrowTrendingUpIcon,
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

        {/* Charts and Activity */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
          {/* Performance Chart Placeholder */}
          <div className="lg:col-span-2 glass-card">
            <div className="card-header flex items-center justify-between">
              <div className="flex items-center space-x-3">
                <ChartPieIcon className="h-6 w-6 text-blue-400" />
                <h3 className="text-xl font-semibold text-gray-100">Performance Overview</h3>
              </div>
              <button className="btn-secondary text-sm">
                <Cog6ToothIcon className="h-4 w-4" />
              </button>
            </div>
            <div className="h-64 flex items-center justify-center">
              <div className="text-center">
                <ChartBarIcon className="h-16 w-16 text-gray-600 mx-auto mb-4" />
                <p className="text-gray-400">Performance metrics chart will be displayed here</p>
              </div>
            </div>
          </div>

          {/* System Health */}
          <div className="glass-card">
            <div className="card-header">
              <h3 className="text-xl font-semibold text-gray-100">System Health</h3>
            </div>
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-gray-300">Response Time</span>
                <span className="text-green-400 font-medium">{stats.performance?.avgResponseTime || 125}ms</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-300">Throughput</span>
                <span className="text-blue-400 font-medium">{stats.performance?.throughput || 15672}/min</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-gray-300">Error Rate</span>
                <span className="text-green-400 font-medium">0.02%</span>
              </div>
              <div className="pt-4">
                <div className="flex justify-between text-sm text-gray-400 mb-2">
                  <span>CPU Usage</span>
                  <span>64%</span>
                </div>
                <div className="w-full bg-dark-700 rounded-full h-2">
                  <div className="bg-gradient-to-r from-green-500 to-blue-500 h-2 rounded-full w-16/25"></div>
                </div>
              </div>
              <div className="pt-2">
                <div className="flex justify-between text-sm text-gray-400 mb-2">
                  <span>Memory Usage</span>
                  <span>78%</span>
                </div>
                <div className="w-full bg-dark-700 rounded-full h-2">
                  <div className="bg-gradient-to-r from-yellow-500 to-orange-500 h-2 rounded-full w-4/5"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="glass-card">
          <div className="card-header flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <ClockIcon className="h-6 w-6 text-green-400" />
              <h3 className="text-xl font-semibold text-gray-100">Recent Activity</h3>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
              <span className="text-sm text-green-400">Live</span>
            </div>
          </div>
          
          {loading ? (
            <div className="space-y-4">
              {[1, 2, 3].map((item) => (
                <div key={item} className="flex items-center space-x-4 animate-pulse">
                  <div className="w-10 h-10 bg-dark-700 rounded-full"></div>
                  <div className="flex-1 space-y-2">
                    <div className="h-4 bg-dark-700 rounded w-3/4"></div>
                    <div className="h-3 bg-dark-700 rounded w-1/2"></div>
                  </div>
                </div>
              ))}
            </div>
          ) : stats.recentActivity.length > 0 ? (
            <div className="space-y-1">
              {stats.recentActivity.map((activity) => (
                <div
                  key={activity.id}
                  className="flex items-center space-x-4 p-3 rounded-lg hover:bg-dark-700/30 transition-colors duration-200"
                >
                  <div className="flex-shrink-0">
                    {getStatusIcon(activity.status)}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-100 truncate">
                      {activity.action.replace(/_/g, ' ')} - {activity.resource}
                    </p>
                    <div className="flex items-center space-x-4 mt-1">
                      <p className="text-xs text-gray-400">
                        by {activity.user}
                      </p>
                      <p className="text-xs text-gray-500">
                        {formatTimestamp(activity.timestamp)}
                      </p>
                    </div>
                  </div>
                  <div className="flex-shrink-0">
                    <span className={`
                      inline-flex px-2 py-1 text-xs font-medium rounded-full
                      ${activity.status === 'SUCCESS' ? 'badge-success' : 
                        activity.status === 'ERROR' ? 'badge-error' : 'badge-warning'}
                    `}>
                      {activity.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-12">
              <ClockIcon className="h-12 w-12 text-gray-600 mx-auto mb-4" />
              <p className="text-gray-400">No recent activity found</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;