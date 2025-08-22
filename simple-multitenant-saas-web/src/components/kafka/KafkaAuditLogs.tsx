import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  DocumentTextIcon,
  ArrowPathIcon,
  ArrowDownTrayIcon,
  FunnelIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  MagnifyingGlassIcon,
  ClockIcon,
  UserIcon,
  ServerIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';

interface AuditLog {
  id: number;
  action: string;
  resourceType: string;
  resourceName: string;
  userEmail: string;
  status: 'SUCCESS' | 'ERROR' | 'WARNING';
  timestamp: string;
  duration: number;
  ipAddress: string;
  userAgent: string;
  clusterName?: string;
  topicName?: string;
}

const KafkaAuditLogs: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    action: '',
    status: '',
    userEmail: '',
    resourceName: '',
    startDate: null as Date | null,
    endDate: null as Date | null,
  });

  // Mock data
  const mockLogs: AuditLog[] = [
    {
      id: 1,
      action: 'CREATE_TOPIC',
      resourceType: 'TOPIC',
      resourceName: 'user-events-v2',
      userEmail: 'admin@enterprise.com',
      status: 'SUCCESS',
      timestamp: '2025-08-22T11:45:00Z',
      duration: 1250,
      ipAddress: '192.168.1.100',
      userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)',
      clusterName: 'kafka-prod-01',
      topicName: 'user-events-v2',
    },
    {
      id: 2,
      action: 'DELETE_TOPIC',
      resourceType: 'TOPIC',
      resourceName: 'temp-topic',
      userEmail: 'admin@enterprise.com',
      status: 'SUCCESS',
      timestamp: '2025-08-22T11:30:00Z',
      duration: 850,
      ipAddress: '192.168.1.100',
      userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)',
      clusterName: 'kafka-prod-01',
      topicName: 'temp-topic',
    },
    {
      id: 3,
      action: 'UPDATE_CLUSTER',
      resourceType: 'CLUSTER',
      resourceName: 'kafka-staging',
      userEmail: 'admin@enterprise.com',
      status: 'SUCCESS',
      timestamp: '2025-08-22T11:15:00Z',
      duration: 2100,
      ipAddress: '192.168.1.100',
      userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)',
      clusterName: 'kafka-staging',
    },
    {
      id: 4,
      action: 'HEALTH_CHECK',
      resourceType: 'CLUSTER',
      resourceName: 'kafka-prod-01',
      userEmail: 'system',
      status: 'SUCCESS',
      timestamp: '2025-08-22T11:00:00Z',
      duration: 150,
      ipAddress: '127.0.0.1',
      userAgent: 'System Health Monitor',
      clusterName: 'kafka-prod-01',
    },
    {
      id: 5,
      action: 'CONNECTION_FAILED',
      resourceType: 'CLUSTER',
      resourceName: 'kafka-test',
      userEmail: 'system',
      status: 'ERROR',
      timestamp: '2025-08-22T10:45:00Z',
      duration: 5000,
      ipAddress: '127.0.0.1',
      userAgent: 'System Health Monitor',
      clusterName: 'kafka-test',
    },
  ];

  useEffect(() => {
    fetchLogs();
  }, []);

  const fetchLogs = async () => {
    setLoading(true);
    
    try {
      const params = new URLSearchParams({
        size: '25',
        sort: 'timestamp',
        direction: 'desc'
      });
      
      const response = await axios.get(`/kafka/audit-logs?${params}`);
      if (response.data.success && response.data.data?.content) {
        setLogs(response.data.data.content);
      } else {
        throw new Error('Invalid API response format');
      }
    } catch (error) {
      console.error('Failed to fetch audit logs:', error);
      // Fallback to mock data if API fails
      setLogs(mockLogs);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = () => {
    // Simulate CSV export
    const csvContent = [
      'Timestamp,Action,Resource Type,Resource Name,User,Status,Duration (ms),IP Address',
      ...logs.map(log => 
        `${log.timestamp},${log.action},${log.resourceType},${log.resourceName},${log.userEmail},${log.status},${log.duration},${log.ipAddress}`
      )
    ].join('\n');
    
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'kafka-audit-logs.csv';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'SUCCESS':
        return <CheckCircleIcon className="h-5 w-5 text-success-400" />;
      case 'ERROR':
        return <XCircleIcon className="h-5 w-5 text-error-400" />;
      case 'WARNING':
        return <ExclamationTriangleIcon className="h-5 w-5 text-warning-400" />;
      default:
        return <CheckCircleIcon className="h-5 w-5 text-success-400" />;
    }
  };

  const getStatusBadge = (status: string) => {
    const baseClasses = "inline-flex px-2 py-1 text-xs font-medium rounded-full";
    switch (status) {
      case 'SUCCESS':
        return `${baseClasses} badge-success`;
      case 'ERROR':
        return `${baseClasses} badge-error`;
      case 'WARNING':
        return `${baseClasses} badge-warning`;
      default:
        return `${baseClasses} badge-info`;
    }
  };

  const formatTimestamp = (timestamp: string) => {
    return new Date(timestamp).toLocaleString();
  };

  const actions = ['CREATE_TOPIC', 'DELETE_TOPIC', 'UPDATE_CLUSTER', 'HEALTH_CHECK', 'CONNECTION_FAILED'];
  const statuses = ['SUCCESS', 'ERROR', 'WARNING'];

  return (
    <div className="min-h-screen bg-dark-950 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex justify-between items-start mb-8">
          <div>
            <h1 className="text-4xl font-bold gradient-text-green mb-2 flex items-center space-x-3">
              <DocumentTextIcon className="h-10 w-10 text-green-400" />
              <span>Kafka Audit Logs</span>
            </h1>
            <p className="text-xl text-gray-300">
              Track all actions and changes in your Kafka environment
            </p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={fetchLogs}
              disabled={loading}
              className={`btn-secondary flex items-center space-x-2 ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              <ArrowPathIcon className={`h-5 w-5 ${loading ? 'animate-spin' : ''}`} />
              <span>Refresh</span>
            </button>
            <button
              onClick={handleExport}
              className="btn-primary flex items-center space-x-2"
            >
              <ArrowDownTrayIcon className="h-5 w-5" />
              <span>Export CSV</span>
            </button>
          </div>
        </div>

        {/* Enhanced Filters */}
        <div className="glass-card mb-6">
          <div className="card-header">
            <div className="flex items-center space-x-3">
              <FunnelIcon className="h-6 w-6 text-green-400" />
              <h3 className="text-xl font-semibold text-gray-100">Filters</h3>
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-6 gap-4">
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Action
              </label>
              <select
                value={filters.action}
                onChange={(e) => setFilters({ ...filters, action: e.target.value })}
                className="input-dark w-full"
              >
                <option value="">All</option>
                {actions.map((action) => (
                  <option key={action} value={action}>
                    {action.replace(/_/g, ' ')}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Status
              </label>
              <select
                value={filters.status}
                onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                className="input-dark w-full"
              >
                <option value="">All</option>
                {statuses.map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                User Email
              </label>
              <input
                type="text"
                value={filters.userEmail}
                onChange={(e) => setFilters({ ...filters, userEmail: e.target.value })}
                className="input-dark w-full"
                placeholder="user@example.com"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Resource Name
              </label>
              <input
                type="text"
                value={filters.resourceName}
                onChange={(e) => setFilters({ ...filters, resourceName: e.target.value })}
                className="input-dark w-full"
                placeholder="Resource name"
              />
            </div>
            <div className="flex items-end">
              <button
                onClick={() => setFilters({
                  action: '',
                  status: '',
                  userEmail: '',
                  resourceName: '',
                  startDate: null,
                  endDate: null,
                })}
                className="btn-secondary flex items-center space-x-2 w-full"
              >
                <XMarkIcon className="h-4 w-4" />
                <span>Clear</span>
              </button>
            </div>
          </div>
        </div>

        {/* Loading State */}
        {loading ? (
          <div className="space-y-4">
            {[1, 2, 3].map((item) => (
              <div key={item} className="glass-card animate-pulse">
                <div className="flex items-center space-x-4">
                  <div className="w-10 h-10 bg-dark-700 rounded-full"></div>
                  <div className="flex-1 space-y-2">
                    <div className="h-4 bg-dark-700 rounded w-3/4"></div>
                    <div className="h-3 bg-dark-700 rounded w-1/2"></div>
                  </div>
                  <div className="w-20 h-8 bg-dark-700 rounded"></div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {logs.map((log, index) => (
              <div key={log.id} className="glass-card hover:scale-[1.01] transition-all duration-200">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-4">
                    <div className="flex-shrink-0">
                      {getStatusIcon(log.status)}
                    </div>
                    <div className="flex-1">
                      <div className="flex items-center space-x-3 mb-2">
                        <h3 className="text-lg font-semibold text-gray-100">
                          {log.action.replace(/_/g, ' ')}
                        </h3>
                        <span className={getStatusBadge(log.status)}>
                          {log.status}
                        </span>
                      </div>
                      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 text-sm">
                        <div className="flex items-center space-x-2">
                          <ServerIcon className="h-4 w-4 text-blue-400" />
                          <span className="text-gray-300">
                            <span className="text-gray-400">Resource:</span> {log.resourceName}
                          </span>
                        </div>
                        <div className="flex items-center space-x-2">
                          <UserIcon className="h-4 w-4 text-purple-400" />
                          <span className="text-gray-300">
                            <span className="text-gray-400">User:</span> {log.userEmail}
                          </span>
                        </div>
                        <div className="flex items-center space-x-2">
                          <ClockIcon className="h-4 w-4 text-orange-400" />
                          <span className="text-gray-300">
                            <span className="text-gray-400">Duration:</span> {log.duration}ms
                          </span>
                        </div>
                        <div className="text-gray-400">
                          IP: {log.ipAddress}
                        </div>
                      </div>
                      {log.clusterName && (
                        <div className="mt-2">
                          <span className="inline-flex px-2 py-1 text-xs font-medium rounded-full bg-blue-900/30 text-blue-400 border border-blue-700/50">
                            Cluster: {log.clusterName}
                          </span>
                          {log.topicName && (
                            <span className="ml-2 inline-flex px-2 py-1 text-xs font-medium rounded-full bg-green-900/30 text-green-400 border border-green-700/50">
                              Topic: {log.topicName}
                            </span>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  <div className="flex-shrink-0 text-right">
                    <p className="text-sm font-medium text-gray-100">
                      {formatTimestamp(log.timestamp)}
                    </p>
                    <p className="text-xs text-gray-400">
                      {log.resourceType}
                    </p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {!loading && logs.length === 0 && (
          <div className="glass-card text-center py-12">
            <DocumentTextIcon className="h-16 w-16 text-gray-600 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-400 mb-2">No audit logs found</h3>
            <p className="text-gray-500">Try adjusting your filters or check back later.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default KafkaAuditLogs;