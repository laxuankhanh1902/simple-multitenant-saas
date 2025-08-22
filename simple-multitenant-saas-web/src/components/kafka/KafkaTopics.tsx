import React, { useState, useEffect, useMemo } from 'react';
import axios from 'axios';
import {
  ChartBarIcon,
  PlusIcon,
  ArrowPathIcon,
  PencilIcon,
  TrashIcon,
  FunnelIcon,
  MagnifyingGlassIcon,
  XMarkIcon,
  ServerIcon,
  ClockIcon,
  DocumentTextIcon,
  ExclamationTriangleIcon,
} from '@heroicons/react/24/outline';

interface KafkaTopic {
  id: number;
  name: string;
  description: string;
  clusterId: number;
  clusterName: string;
  partitions: number;
  replicationFactor: number;
  status: 'ACTIVE' | 'INACTIVE' | 'DELETING';
  sizeBytes: number;
  messageCount: number;
  retentionMs: number;
  compressionType: string;
  createdAt: string;
  throughputMBps?: number;
  avgMessageSize?: number;
}

const KafkaTopics: React.FC = () => {
  const [topics, setTopics] = useState<KafkaTopic[]>([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedTopic, setSelectedTopic] = useState<KafkaTopic | null>(null);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [clusterFilter, setClusterFilter] = useState('');
  
  // Enhanced form data
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    clusterId: '',
    partitions: 3,
    replicationFactor: 1,
    retentionMs: 604800000, // 7 days
    compressionType: 'none',
  });

  // Mock data with enhanced information
  const mockTopics: KafkaTopic[] = [
    {
      id: 1,
      name: 'user-events',
      description: 'User activity events and behavior tracking',
      clusterId: 1,
      clusterName: 'kafka-prod-01',
      partitions: 6,
      replicationFactor: 3,
      status: 'ACTIVE',
      sizeBytes: 1024 * 1024 * 512, // 512 MB
      messageCount: 1250000,
      retentionMs: 604800000,
      compressionType: 'gzip',
      createdAt: '2025-01-20T10:00:00Z',
      throughputMBps: 15.2,
      avgMessageSize: 420,
    },
    {
      id: 2,
      name: 'order-processing',
      description: 'E-commerce order events and transaction processing',
      clusterId: 1,
      clusterName: 'kafka-prod-01',
      partitions: 12,
      replicationFactor: 3,
      status: 'ACTIVE',
      sizeBytes: 1024 * 1024 * 256,
      messageCount: 850000,
      retentionMs: 1209600000,
      compressionType: 'snappy',
      createdAt: '2025-02-01T14:30:00Z',
      throughputMBps: 8.7,
      avgMessageSize: 315,
    },
    {
      id: 3,
      name: 'inventory-updates',
      description: 'Real-time inventory and stock level changes',
      clusterId: 2,
      clusterName: 'kafka-staging',
      partitions: 4,
      replicationFactor: 2,
      status: 'ACTIVE',
      sizeBytes: 1024 * 1024 * 128,
      messageCount: 420000,
      retentionMs: 259200000,
      compressionType: 'lz4',
      createdAt: '2025-02-10T09:15:00Z',
      throughputMBps: 3.1,
      avgMessageSize: 280,
    },
    {
      id: 4,
      name: 'notification-service',
      description: 'Push notifications and email alerts',
      clusterId: 1,
      clusterName: 'kafka-prod-01',
      partitions: 3,
      replicationFactor: 3,
      status: 'INACTIVE',
      sizeBytes: 1024 * 1024 * 64,
      messageCount: 180000,
      retentionMs: 86400000,
      compressionType: 'none',
      createdAt: '2025-01-15T16:45:00Z',
      throughputMBps: 0.5,
      avgMessageSize: 380,
    },
  ];

  useEffect(() => {
    fetchTopics();
  }, []);

  const fetchTopics = async () => {
    setLoading(true);
    setError('');
    
    try {
      const response = await axios.get('/kafka/topics');
      if (response.data.success && response.data.data) {
        setTopics(response.data.data);
      } else {
        throw new Error('Invalid API response format');
      }
    } catch (err) {
      console.error('Failed to fetch topics:', err);
      setError('Failed to load topics. Using sample data.');
      // Fallback to mock data if API fails
      setTopics(mockTopics);
    } finally {
      setLoading(false);
    }
  };

  // Filter and search logic
  const filteredTopics = useMemo(() => {
    return topics.filter(topic => {
      const matchesSearch = topic.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          topic.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          topic.clusterName.toLowerCase().includes(searchTerm.toLowerCase());
      const matchesStatus = !statusFilter || topic.status === statusFilter;
      const matchesCluster = !clusterFilter || topic.clusterName === clusterFilter;
      
      return matchesSearch && matchesStatus && matchesCluster;
    });
  }, [topics, searchTerm, statusFilter, clusterFilter]);

  // Get unique cluster names for filter
  const uniqueClusters = useMemo(() => {
    return Array.from(new Set(topics.map(topic => topic.clusterName)));
  }, [topics]);

  const handleOpenDialog = (topic: KafkaTopic | null = null) => {
    setSelectedTopic(topic);
    if (topic) {
      setFormData({
        name: topic.name,
        description: topic.description,
        clusterId: topic.clusterId.toString(),
        partitions: topic.partitions,
        replicationFactor: topic.replicationFactor,
        retentionMs: topic.retentionMs,
        compressionType: topic.compressionType,
      });
    } else {
      setFormData({
        name: '',
        description: '',
        clusterId: '',
        partitions: 3,
        replicationFactor: 1,
        retentionMs: 604800000,
        compressionType: 'none',
      });
    }
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedTopic(null);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.name.trim()) {
      setError('Topic name is required');
      return;
    }

    try {
      if (selectedTopic) {
        // Update existing topic
        setTopics(prev => prev.map(topic => 
          topic.id === selectedTopic.id 
            ? { ...topic, ...formData, clusterId: parseInt(formData.clusterId) }
            : topic
        ));
      } else {
        // Create new topic
        const newTopic: KafkaTopic = {
          id: Math.max(...topics.map(t => t.id)) + 1,
          ...formData,
          clusterId: parseInt(formData.clusterId),
          clusterName: topics.find(t => t.clusterId.toString() === formData.clusterId)?.clusterName || 'Unknown',
          status: 'ACTIVE',
          sizeBytes: 0,
          messageCount: 0,
          createdAt: new Date().toISOString(),
          throughputMBps: 0,
          avgMessageSize: 0,
        };
        setTopics(prev => [...prev, newTopic]);
      }
      handleCloseDialog();
    } catch (err) {
      setError('Failed to save topic. Please try again.');
    }
  };

  const handleDelete = (topicId: number) => {
    setTopics(prev => prev.filter(topic => topic.id !== topicId));
  };

  const clearFilters = () => {
    setSearchTerm('');
    setStatusFilter('');
    setClusterFilter('');
  };

  const formatBytes = (bytes: number): string => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatRetention = (ms: number): string => {
    const days = ms / (1000 * 60 * 60 * 24);
    if (days < 1) {
      const hours = ms / (1000 * 60 * 60);
      return `${Math.round(hours)}h`;
    }
    return `${Math.round(days)}d`;
  };

  const formatNumber = (num: number): string => {
    return new Intl.NumberFormat().format(num);
  };

  const getStatusBadge = (status: string) => {
    const baseClasses = "inline-flex px-2 py-1 text-xs font-medium rounded-full";
    switch (status) {
      case 'ACTIVE':
        return `${baseClasses} badge-success`;
      case 'INACTIVE':
        return `${baseClasses} badge-warning`;
      case 'DELETING':
        return `${baseClasses} badge-error`;
      default:
        return `${baseClasses} badge-info`;
    }
  };

  return (
    <div className="min-h-screen bg-dark-950 p-6">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex justify-between items-start mb-8">
          <div>
            <h1 className="text-4xl font-bold gradient-text-orange mb-2 flex items-center space-x-3">
              <ChartBarIcon className="h-10 w-10 text-orange-400" />
              <span>Kafka Topics</span>
            </h1>
            <p className="text-xl text-gray-300">
              Manage and monitor your Kafka topic configurations
            </p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={fetchTopics}
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
              <span>New Topic</span>
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

        {/* Enhanced Filters */}
        <div className="glass-card mb-6">
          <div className="card-header">
            <div className="flex items-center space-x-3">
              <FunnelIcon className="h-6 w-6 text-orange-400" />
              <h3 className="text-xl font-semibold text-gray-100">Filters & Search</h3>
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-12 gap-4 items-end">
            <div className="md:col-span-4">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Search
              </label>
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                <input
                  type="text"
                  placeholder="Search topics, descriptions, clusters..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="input-dark pl-10 w-full"
                />
              </div>
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Status
              </label>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="input-dark w-full"
              >
                <option value="">All</option>
                <option value="ACTIVE">Active</option>
                <option value="INACTIVE">Inactive</option>
                <option value="DELETING">Deleting</option>
              </select>
            </div>
            <div className="md:col-span-3">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Cluster
              </label>
              <select
                value={clusterFilter}
                onChange={(e) => setClusterFilter(e.target.value)}
                className="input-dark w-full"
              >
                <option value="">All Clusters</option>
                {uniqueClusters.map((cluster) => (
                  <option key={cluster} value={cluster}>
                    {cluster}
                  </option>
                ))}
              </select>
            </div>
            <div className="md:col-span-3 flex space-x-2">
              <button
                onClick={clearFilters}
                className="btn-secondary flex items-center space-x-2"
              >
                <XMarkIcon className="h-4 w-4" />
                <span>Clear</span>
              </button>
              <div className="text-sm text-gray-400 flex items-center">
                Showing {filteredTopics.length} of {topics.length} topics
              </div>
            </div>
          </div>
        </div>

        {/* Loading State */}
        {loading && topics.length === 0 ? (
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
          <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
            {filteredTopics.map((topic) => (
              <div key={topic.id} className="glass-card group hover:scale-105 transition-all duration-300">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="p-3 bg-orange-500/20 rounded-xl">
                      <ChartBarIcon className="h-6 w-6 text-orange-400" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-100">{topic.name}</h3>
                      <p className="text-sm text-gray-400">{topic.clusterName}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleOpenDialog(topic)}
                      className="p-2 text-gray-400 hover:text-orange-400 hover:bg-orange-500/20 rounded-lg transition-colors duration-200"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => handleDelete(topic.id)}
                      className="p-2 text-gray-400 hover:text-error-400 hover:bg-error-500/20 rounded-lg transition-colors duration-200"
                    >
                      <TrashIcon className="h-4 w-4" />
                    </button>
                  </div>
                </div>

                <p className="text-sm text-gray-300 mb-4 line-clamp-2">
                  {topic.description}
                </p>

                <div className="flex items-center justify-between mb-4">
                  <span className={getStatusBadge(topic.status)}>
                    {topic.status}
                  </span>
                  <div className="text-sm text-gray-400">
                    {topic.compressionType}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div className="text-center">
                    <div className="text-lg font-semibold text-blue-400">{topic.partitions}</div>
                    <p className="text-xs text-gray-400">Partitions</p>
                  </div>
                  <div className="text-center">
                    <div className="text-lg font-semibold text-green-400">{topic.replicationFactor}</div>
                    <p className="text-xs text-gray-400">Replication</p>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div className="text-center">
                    <div className="text-sm font-semibold text-purple-400">{formatBytes(topic.sizeBytes)}</div>
                    <p className="text-xs text-gray-400">Size</p>
                  </div>
                  <div className="text-center">
                    <div className="text-sm font-semibold text-cyan-400">{formatNumber(topic.messageCount)}</div>
                    <p className="text-xs text-gray-400">Messages</p>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div className="text-center">
                    <div className="text-sm font-semibold text-yellow-400">{topic.throughputMBps?.toFixed(1) || '0'} MB/s</div>
                    <p className="text-xs text-gray-400">Throughput</p>
                  </div>
                  <div className="text-center">
                    <div className="text-sm font-semibold text-pink-400">{formatRetention(topic.retentionMs)}</div>
                    <p className="text-xs text-gray-400">Retention</p>
                  </div>
                </div>

                <div className="pt-4 border-t border-dark-700/50">
                  <div className="flex justify-between text-xs text-gray-400">
                    <span>Created</span>
                    <span>{new Date(topic.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Add/Edit Dialog */}
        {openDialog && (
          <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50">
            <div className="glass-card w-full max-w-2xl max-h-[90vh] overflow-y-auto">
              <div className="card-header">
                <h2 className="text-2xl font-bold text-gray-100">
                  {selectedTopic ? 'Edit Topic' : 'Create New Topic'}
                </h2>
                <p className="text-gray-400">
                  {selectedTopic ? 'Modify topic configuration' : 'Configure your new Kafka topic'}
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
                      Topic Name *
                    </label>
                    <input
                      type="text"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                      className="input-dark w-full"
                      placeholder="user-events"
                      required
                      disabled={!!selectedTopic}
                    />
                    {selectedTopic && (
                      <p className="text-xs text-gray-400 mt-1">Topic name cannot be changed</p>
                    )}
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Cluster *
                    </label>
                    <select
                      value={formData.clusterId}
                      onChange={(e) => setFormData({ ...formData, clusterId: e.target.value })}
                      className="input-dark w-full"
                      required
                    >
                      <option value="">Select Cluster</option>
                      <option value="1">kafka-prod-01</option>
                      <option value="2">kafka-staging</option>
                      <option value="3">kafka-dev</option>
                    </select>
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
                    placeholder="Describe the purpose and usage of this topic"
                    rows={3}
                  />
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Partitions *
                    </label>
                    <input
                      type="number"
                      min="1"
                      max="50"
                      value={formData.partitions}
                      onChange={(e) => setFormData({ ...formData, partitions: parseInt(e.target.value) })}
                      className="input-dark w-full"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Replication Factor *
                    </label>
                    <input
                      type="number"
                      min="1"
                      max="3"
                      value={formData.replicationFactor}
                      onChange={(e) => setFormData({ ...formData, replicationFactor: parseInt(e.target.value) })}
                      className="input-dark w-full"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Compression
                    </label>
                    <select
                      value={formData.compressionType}
                      onChange={(e) => setFormData({ ...formData, compressionType: e.target.value })}
                      className="input-dark w-full"
                    >
                      <option value="none">None</option>
                      <option value="gzip">GZIP</option>
                      <option value="snappy">Snappy</option>
                      <option value="lz4">LZ4</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Retention Period (ms)
                  </label>
                  <input
                    type="number"
                    value={formData.retentionMs}
                    onChange={(e) => setFormData({ ...formData, retentionMs: parseInt(e.target.value) })}
                    className="input-dark w-full"
                  />
                  <p className="text-xs text-gray-400 mt-1">
                    Current: {formatRetention(formData.retentionMs)}
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
                    disabled={!formData.name || !formData.clusterId || loading}
                    className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {selectedTopic ? 'Update Topic' : 'Create Topic'}
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

export default KafkaTopics;