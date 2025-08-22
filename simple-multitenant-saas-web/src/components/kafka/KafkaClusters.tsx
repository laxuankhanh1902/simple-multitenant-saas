import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  ServerIcon,
  PlusIcon,
  ArrowPathIcon,
  PencilIcon,
  TrashIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  EllipsisVerticalIcon,
  CloudIcon,
  CpuChipIcon,
} from '@heroicons/react/24/outline';

interface KafkaCluster {
  id: number;
  name: string;
  description: string;
  bootstrapServers: string;
  status: 'ACTIVE' | 'INACTIVE' | 'MAINTENANCE';
  healthStatus: 'HEALTHY' | 'WARNING' | 'CRITICAL' | 'UNKNOWN';
  kafkaVersion: string;
  brokerCount: number;
  topicCount: number;
  createdAt: string;
  lastHealthCheck?: string;
}

const KafkaClusters: React.FC = () => {
  const [clusters, setClusters] = useState<KafkaCluster[]>([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedCluster, setSelectedCluster] = useState<KafkaCluster | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    bootstrapServers: '',
    kafkaVersion: '3.4.0',
  });
  const [error, setError] = useState('');

  // Mock data for demonstration
  const mockClusters: KafkaCluster[] = [
    {
      id: 1,
      name: 'kafka-prod-01',
      description: 'Production Kafka cluster for main workloads',
      bootstrapServers: 'kafka-prod-01:9092,kafka-prod-02:9092,kafka-prod-03:9092',
      status: 'ACTIVE',
      healthStatus: 'HEALTHY',
      kafkaVersion: '3.4.0',
      brokerCount: 3,
      topicCount: 15,
      createdAt: '2025-01-15T10:00:00Z',
      lastHealthCheck: '2025-08-22T11:45:00Z',
    },
    {
      id: 2,
      name: 'kafka-staging',
      description: 'Staging environment for testing',
      bootstrapServers: 'kafka-staging:9092',
      status: 'ACTIVE',
      healthStatus: 'HEALTHY',
      kafkaVersion: '3.4.0',
      brokerCount: 1,
      topicCount: 8,
      createdAt: '2025-02-01T14:30:00Z',
      lastHealthCheck: '2025-08-22T11:40:00Z',
    },
    {
      id: 3,
      name: 'kafka-dev',
      description: 'Development cluster',
      bootstrapServers: 'kafka-dev:9092',
      status: 'ACTIVE',
      healthStatus: 'WARNING',
      kafkaVersion: '3.3.2',
      brokerCount: 1,
      topicCount: 5,
      createdAt: '2025-02-10T09:15:00Z',
      lastHealthCheck: '2025-08-22T11:30:00Z',
    },
    {
      id: 4,
      name: 'kafka-analytics',
      description: 'Analytics and data processing cluster',
      bootstrapServers: 'kafka-analytics-01:9092,kafka-analytics-02:9092',
      status: 'MAINTENANCE',
      healthStatus: 'UNKNOWN',
      kafkaVersion: '3.4.0',
      brokerCount: 2,
      topicCount: 12,
      createdAt: '2025-03-01T16:20:00Z',
      lastHealthCheck: '2025-08-22T10:00:00Z',
    },
  ];

  useEffect(() => {
    fetchClusters();
  }, []);

  const fetchClusters = async () => {
    setLoading(true);
    setError('');
    
    try {
      const response = await axios.get('/kafka/clusters/all');
      if (response.data.success && response.data.data) {
        setClusters(response.data.data);
      } else {
        throw new Error('Invalid API response format');
      }
    } catch (err) {
      console.error('Failed to fetch clusters:', err);
      setError('Failed to load clusters. Using sample data.');
      // Fallback to mock data if API fails
      setClusters(mockClusters);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDialog = (cluster?: KafkaCluster) => {
    if (cluster) {
      setSelectedCluster(cluster);
      setFormData({
        name: cluster.name,
        description: cluster.description,
        bootstrapServers: cluster.bootstrapServers,
        kafkaVersion: cluster.kafkaVersion,
      });
    } else {
      setSelectedCluster(null);
      setFormData({
        name: '',
        description: '',
        bootstrapServers: '',
        kafkaVersion: '3.4.0',
      });
    }
    setOpenDialog(true);
    setError('');
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedCluster(null);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      if (selectedCluster) {
        setClusters(prev => prev.map(cluster => 
          cluster.id === selectedCluster.id 
            ? { ...cluster, ...formData }
            : cluster
        ));
      } else {
        const newCluster: KafkaCluster = {
          id: Date.now(),
          ...formData,
          status: 'ACTIVE',
          healthStatus: 'UNKNOWN',
          brokerCount: formData.bootstrapServers.split(',').length,
          topicCount: 0,
          createdAt: new Date().toISOString(),
        };
        setClusters(prev => [...prev, newCluster]);
      }
      
      handleCloseDialog();
    } catch (error) {
      setError('Failed to save cluster');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      setLoading(true);
      await new Promise(resolve => setTimeout(resolve, 500));
      setClusters(prev => prev.filter(cluster => cluster.id !== id));
    } catch (error) {
      setError('Failed to delete cluster');
    } finally {
      setLoading(false);
    }
  };

  const getHealthIcon = (health: string) => {
    switch (health) {
      case 'HEALTHY':
        return <CheckCircleIcon className="h-5 w-5 text-success-400" />;
      case 'WARNING':
        return <ExclamationTriangleIcon className="h-5 w-5 text-warning-400" />;
      case 'CRITICAL':
        return <XCircleIcon className="h-5 w-5 text-error-400" />;
      default:
        return <ExclamationTriangleIcon className="h-5 w-5 text-gray-400" />;
    }
  };

  const getStatusBadge = (status: string) => {
    const baseClasses = "inline-flex px-2 py-1 text-xs font-medium rounded-full";
    switch (status) {
      case 'ACTIVE':
        return `${baseClasses} badge-success`;
      case 'INACTIVE':
        return `${baseClasses} badge-error`;
      case 'MAINTENANCE':
        return `${baseClasses} badge-warning`;
      default:
        return `${baseClasses} badge-info`;
    }
  };

  if (error && clusters.length === 0) {
    return (
      <div className="min-h-screen bg-dark-950 p-6">
        <div className="max-w-7xl mx-auto">
          <div className="glass-card border-error-700/50">
            <div className="flex items-center space-x-3">
              <ExclamationTriangleIcon className="h-8 w-8 text-error-400" />
              <div>
                <h3 className="text-lg font-semibold text-error-400">Error Loading Clusters</h3>
                <p className="text-gray-300">{error}</p>
              </div>
            </div>
            <button 
              onClick={fetchClusters}
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
            <h1 className="text-4xl font-bold gradient-text-purple mb-2 flex items-center space-x-3">
              <ServerIcon className="h-10 w-10 text-purple-400" />
              <span>Kafka Clusters</span>
            </h1>
            <p className="text-xl text-gray-300">
              Manage your Kafka clusters and monitor their health
            </p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={fetchClusters}
              disabled={loading}
              className={`btn-secondary flex items-center space-x-2 ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              <ArrowPathIcon className={`h-5 w-5 ${loading ? 'animate-spin' : ''}`} />
              <span>Refresh</span>
            </button>
            <button
              onClick={() => handleOpenDialog()}
              className="btn-primary flex items-center space-x-2"
            >
              <PlusIcon className="h-5 w-5" />
              <span>Add Cluster</span>
            </button>
          </div>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-warning-900/30 border border-warning-700/50 rounded-lg">
            <div className="flex items-center space-x-2">
              <ExclamationTriangleIcon className="h-5 w-5 text-warning-400" />
              <p className="text-warning-300">{error}</p>
            </div>
          </div>
        )}

        {/* Loading State */}
        {loading && clusters.length === 0 ? (
          <div className="space-y-4">
            {[1, 2, 3].map((item) => (
              <div key={item} className="glass-card animate-pulse">
                <div className="flex items-center space-x-4">
                  <div className="w-12 h-12 bg-dark-700 rounded-full"></div>
                  <div className="flex-1 space-y-2">
                    <div className="h-4 bg-dark-700 rounded w-1/4"></div>
                    <div className="h-3 bg-dark-700 rounded w-3/4"></div>
                  </div>
                  <div className="w-20 h-8 bg-dark-700 rounded"></div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
            {clusters.map((cluster) => (
              <div key={cluster.id} className="glass-card group hover:scale-105 transition-all duration-300">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="p-3 bg-purple-500/20 rounded-xl">
                      <ServerIcon className="h-6 w-6 text-purple-400" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-100">{cluster.name}</h3>
                      <p className="text-sm text-gray-400">{cluster.kafkaVersion}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleOpenDialog(cluster)}
                      className="p-2 text-gray-400 hover:text-purple-400 hover:bg-purple-500/20 rounded-lg transition-colors duration-200"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(cluster.id)}
                      className="p-2 text-gray-400 hover:text-error-400 hover:bg-error-500/20 rounded-lg transition-colors duration-200"
                    >
                      <TrashIcon className="h-4 w-4" />
                    </button>
                  </div>
                </div>

                <p className="text-sm text-gray-300 mb-4 line-clamp-2">
                  {cluster.description}
                </p>

                <div className="flex items-center justify-between mb-4">
                  <span className={getStatusBadge(cluster.status)}>
                    {cluster.status}
                  </span>
                  <div className="flex items-center space-x-2">
                    {getHealthIcon(cluster.healthStatus)}
                    <span className="text-sm text-gray-300">{cluster.healthStatus}</span>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div className="text-center">
                    <div className="flex items-center justify-center space-x-1">
                      <CpuChipIcon className="h-4 w-4 text-blue-400" />
                      <span className="text-lg font-semibold text-blue-400">{cluster.brokerCount}</span>
                    </div>
                    <p className="text-xs text-gray-400">Brokers</p>
                  </div>
                  <div className="text-center">
                    <div className="flex items-center justify-center space-x-1">
                      <CloudIcon className="h-4 w-4 text-green-400" />
                      <span className="text-lg font-semibold text-green-400">{cluster.topicCount}</span>
                    </div>
                    <p className="text-xs text-gray-400">Topics</p>
                  </div>
                </div>

                <div className="pt-4 border-t border-dark-700/50">
                  <div className="flex justify-between text-xs text-gray-400">
                    <span>Created</span>
                    <span>{new Date(cluster.createdAt).toLocaleDateString()}</span>
                  </div>
                  {cluster.lastHealthCheck && (
                    <div className="flex justify-between text-xs text-gray-400 mt-1">
                      <span>Last Check</span>
                      <span>{new Date(cluster.lastHealthCheck).toLocaleTimeString()}</span>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Add/Edit Dialog */}
        {openDialog && (
          <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div className="glass-card w-full max-w-2xl">
              <div className="card-header">
                <h2 className="text-2xl font-bold text-gray-100">
                  {selectedCluster ? 'Edit Cluster' : 'Add New Cluster'}
                </h2>
                <p className="text-gray-400">
                  {selectedCluster ? 'Modify cluster configuration' : 'Configure your new Kafka cluster'}
                </p>
              </div>

              {error && (
                <div className="mb-4 p-3 bg-error-900/30 border border-error-700/50 rounded-lg">
                  <p className="text-error-300">{error}</p>
                </div>
              )}

              <form onSubmit={handleSubmit} className="space-y-6">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Cluster Name *
                    </label>
                    <input
                      type="text"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      className="input-dark w-full"
                      placeholder="kafka-prod-01"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Kafka Version *
                    </label>
                    <input
                      type="text"
                      value={formData.kafkaVersion}
                      onChange={(e) => setFormData({ ...formData, kafkaVersion: e.target.value })}
                      className="input-dark w-full"
                      placeholder="3.4.0"
                      required
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Description
                  </label>
                  <textarea
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    className="input-dark w-full h-20"
                    placeholder="Describe the purpose of this cluster"
                    rows={3}
                  />
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Bootstrap Servers *
                  </label>
                  <input
                    type="text"
                    value={formData.bootstrapServers}
                    onChange={(e) => setFormData({ ...formData, bootstrapServers: e.target.value })}
                    className="input-dark w-full"
                    placeholder="broker1:9092,broker2:9092,broker3:9092"
                    required
                  />
                  <p className="text-xs text-gray-400 mt-1">
                    Comma-separated list of broker addresses
                  </p>
                </div>

                <div className="flex justify-end space-x-3 pt-6 border-t border-dark-700/50">
                  <button
                    type="button"
                    onClick={handleCloseDialog}
                    className="btn-secondary"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={!formData.name || !formData.bootstrapServers || loading}
                    className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {selectedCluster ? 'Update' : 'Create'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default KafkaClusters;