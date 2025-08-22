import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
  UsersIcon,
  PlusIcon,
  ArrowPathIcon,
  PencilIcon,
  TrashIcon,
  UserIcon,
  EnvelopeIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  ShieldCheckIcon,
  ClockIcon,
} from '@heroicons/react/24/outline';

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  status: 'ACTIVE' | 'INACTIVE' | 'SUSPENDED' | 'PENDING_VERIFICATION';
  enabled: boolean;
  emailVerified: boolean;
  createdAt: string;
  lastLogin?: string;
  loginCount: number;
}

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [openDialog, setOpenDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    roles: ['USER'],
    status: 'ACTIVE',
    enabled: true,
  });
  const [error, setError] = useState('');

  // Mock data
  const mockUsers: User[] = [
    {
      id: 1,
      username: 'admin@enterprise.com',
      email: 'admin@enterprise.com',
      firstName: 'Admin',
      lastName: 'User',
      roles: ['TENANT_ADMIN'],
      status: 'ACTIVE',
      enabled: true,
      emailVerified: true,
      createdAt: '2025-01-15T10:00:00Z',
      lastLogin: '2025-08-22T11:45:00Z',
      loginCount: 156,
    },
    {
      id: 2,
      username: 'john.doe@enterprise.com',
      email: 'john.doe@enterprise.com',
      firstName: 'John',
      lastName: 'Doe',
      roles: ['USER'],
      status: 'ACTIVE',
      enabled: true,
      emailVerified: true,
      createdAt: '2025-02-01T14:30:00Z',
      lastLogin: '2025-08-22T09:15:00Z',
      loginCount: 89,
    },
    {
      id: 3,
      username: 'jane.smith@enterprise.com',
      email: 'jane.smith@enterprise.com',
      firstName: 'Jane',
      lastName: 'Smith',
      roles: ['USER'],
      status: 'PENDING_VERIFICATION',
      enabled: true,
      emailVerified: false,
      createdAt: '2025-08-20T16:20:00Z',
      loginCount: 0,
    },
  ];

  const roles = ['ADMIN', 'TENANT_ADMIN', 'USER', 'VIEWER'];
  const statuses = ['ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION'];

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    setError('');
    
    try {
      const response = await axios.get('/users');
      if (response.data.success && Array.isArray(response.data.data)) {
        setUsers(response.data.data);
      } else if (Array.isArray(response.data)) {
        setUsers(response.data);
      } else {
        throw new Error('Invalid API response format - expected array');
      }
    } catch (err) {
      console.error('Failed to fetch users:', err);
      setError('Failed to load users. Using sample data.');
      // Fallback to mock data if API fails
      setUsers(mockUsers);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenDialog = (user?: User) => {
    if (user) {
      setSelectedUser(user);
      setFormData({
        username: user.username,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        roles: user.roles,
        status: user.status,
        enabled: user.enabled,
      });
    } else {
      setSelectedUser(null);
      setFormData({
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        roles: ['USER'],
        status: 'ACTIVE',
        enabled: true,
      });
    }
    setOpenDialog(true);
    setError('');
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setSelectedUser(null);
    setError('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setLoading(true);
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      if (selectedUser) {
        setUsers(prev => (prev || []).map(user => 
          user.id === selectedUser.id 
            ? { ...user, ...formData }
            : user
        ));
      } else {
        const newUser: User = {
          id: Date.now(),
          ...formData,
          emailVerified: false,
          createdAt: new Date().toISOString(),
          loginCount: 0,
        };
        setUsers(prev => [...(prev || []), newUser]);
      }
      
      handleCloseDialog();
    } catch (error) {
      setError('Failed to save user');
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ACTIVE':
        return <CheckCircleIcon className="h-5 w-5 text-success-400" />;
      case 'SUSPENDED':
        return <XCircleIcon className="h-5 w-5 text-error-400" />;
      case 'PENDING_VERIFICATION':
        return <ExclamationTriangleIcon className="h-5 w-5 text-warning-400" />;
      default:
        return <ExclamationTriangleIcon className="h-5 w-5 text-gray-400" />;
    }
  };

  const getStatusBadge = (status: string) => {
    const baseClasses = "inline-flex px-2 py-1 text-xs font-medium rounded-full";
    switch (status) {
      case 'ACTIVE':
        return `${baseClasses} badge-success`;
      case 'SUSPENDED':
        return `${baseClasses} badge-error`;
      case 'PENDING_VERIFICATION':
        return `${baseClasses} badge-warning`;
      default:
        return `${baseClasses} badge-info`;
    }
  };

  const getRoleBadge = (role: string) => {
    const baseClasses = "inline-flex px-2 py-1 text-xs font-medium rounded-full mr-1 mb-1";
    switch (role) {
      case 'ADMIN':
        return `${baseClasses} bg-red-900/30 text-red-400 border border-red-700/50`;
      case 'TENANT_ADMIN':
        return `${baseClasses} bg-purple-900/30 text-purple-400 border border-purple-700/50`;
      case 'USER':
        return `${baseClasses} bg-blue-900/30 text-blue-400 border border-blue-700/50`;
      case 'VIEWER':
        return `${baseClasses} bg-gray-700/30 text-gray-400 border border-gray-600/50`;
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
            <h1 className="text-4xl font-bold gradient-text-blue mb-2 flex items-center space-x-3">
              <UsersIcon className="h-10 w-10 text-blue-400" />
              <span>User Management</span>
            </h1>
            <p className="text-xl text-gray-300">
              Manage users and their permissions across your organization
            </p>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={fetchUsers}
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
              <span>Add User</span>
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
        {loading && (users || []).length === 0 ? (
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
            {(users || []).map((user) => (
              <div key={user.id} className="glass-card group hover:scale-105 transition-all duration-300">
                <div className="flex items-start justify-between mb-4">
                  <div className="flex items-center space-x-3">
                    <div className="p-3 bg-blue-500/20 rounded-xl">
                      <UserIcon className="h-6 w-6 text-blue-400" />
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-100">
                        {user.firstName} {user.lastName}
                      </h3>
                      <div className="flex items-center space-x-2">
                        <EnvelopeIcon className="h-4 w-4 text-gray-400" />
                        <p className="text-sm text-gray-400">{user.email}</p>
                      </div>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    <button
                      onClick={() => handleOpenDialog(user)}
                      className="p-2 text-gray-400 hover:text-blue-400 hover:bg-blue-500/20 rounded-lg transition-colors duration-200"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>
                    <button
                      onClick={() => setUsers(prev => (prev || []).filter(u => u.id !== user.id))}
                      className="p-2 text-gray-400 hover:text-error-400 hover:bg-error-500/20 rounded-lg transition-colors duration-200"
                    >
                      <TrashIcon className="h-4 w-4" />
                    </button>
                  </div>
                </div>

                <div className="flex items-center justify-between mb-4">
                  <span className={getStatusBadge(user.status)}>
                    {user.status.replace(/_/g, ' ')}
                  </span>
                  <div className="flex items-center space-x-2">
                    {user.emailVerified ? (
                      <>
                        <CheckCircleIcon className="h-4 w-4 text-success-400" />
                        <span className="text-xs text-success-400">Verified</span>
                      </>
                    ) : (
                      <>
                        <ExclamationTriangleIcon className="h-4 w-4 text-warning-400" />
                        <span className="text-xs text-warning-400">Pending</span>
                      </>
                    )}
                  </div>
                </div>

                <div className="mb-4">
                  <p className="text-xs text-gray-400 mb-2">Roles</p>
                  <div className="flex flex-wrap">
                    {user.roles.map((role) => (
                      <span key={role} className={getRoleBadge(role)}>
                        {role.replace(/_/g, ' ')}
                      </span>
                    ))}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div className="text-center">
                    <div className="flex items-center justify-center space-x-1">
                      <ClockIcon className="h-4 w-4 text-purple-400" />
                      <span className="text-lg font-semibold text-purple-400">{user.loginCount}</span>
                    </div>
                    <p className="text-xs text-gray-400">Logins</p>
                  </div>
                  <div className="text-center">
                    <div className="flex items-center justify-center space-x-1">
                      <ShieldCheckIcon className="h-4 w-4 text-green-400" />
                      <span className="text-sm font-semibold text-green-400">
                        {user.enabled ? 'Enabled' : 'Disabled'}
                      </span>
                    </div>
                    <p className="text-xs text-gray-400">Account</p>
                  </div>
                </div>

                <div className="pt-4 border-t border-dark-700/50">
                  <div className="flex justify-between text-xs text-gray-400 mb-1">
                    <span>Created</span>
                    <span>{new Date(user.createdAt).toLocaleDateString()}</span>
                  </div>
                  {user.lastLogin && (
                    <div className="flex justify-between text-xs text-gray-400">
                      <span>Last Login</span>
                      <span>{new Date(user.lastLogin).toLocaleDateString()}</span>
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
                  {selectedUser ? 'Edit User' : 'Add New User'}
                </h2>
                <p className="text-gray-400">
                  {selectedUser ? 'Modify user information and permissions' : 'Create a new user account'}
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
                      First Name *
                    </label>
                    <input
                      type="text"
                      value={formData.firstName}
                      onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                      className="input-dark w-full"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Last Name *
                    </label>
                    <input
                      type="text"
                      value={formData.lastName}
                      onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                      className="input-dark w-full"
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Email *
                    </label>
                    <input
                      type="email"
                      value={formData.email}
                      onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                      className="input-dark w-full"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Username *
                    </label>
                    <input
                      type="text"
                      value={formData.username}
                      onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                      className="input-dark w-full"
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Status *
                    </label>
                    <select
                      value={formData.status}
                      onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                      className="input-dark w-full"
                      required
                    >
                      {statuses.map((status) => (
                        <option key={status} value={status}>
                          {status.replace(/_/g, ' ')}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-300 mb-2">
                      Roles *
                    </label>
                    <select
                      multiple
                      value={formData.roles}
                      onChange={(e) => setFormData({ 
                        ...formData, 
                        roles: Array.from(e.target.selectedOptions, option => option.value)
                      })}
                      className="input-dark w-full h-24"
                      required
                    >
                      {roles.map((role) => (
                        <option key={role} value={role}>
                          {role.replace(/_/g, ' ')}
                        </option>
                      ))}
                    </select>
                    <p className="text-xs text-gray-400 mt-1">
                      Hold Ctrl/Cmd to select multiple roles
                    </p>
                  </div>
                </div>

                <div className="flex items-center space-x-3">
                  <input
                    type="checkbox"
                    id="enabled"
                    checked={formData.enabled}
                    onChange={(e) => setFormData({ ...formData, enabled: e.target.checked })}
                    className="rounded border-dark-600 text-primary-500 focus:ring-primary-500 bg-dark-700"
                  />
                  <label htmlFor="enabled" className="text-sm font-medium text-gray-300">
                    Account Enabled
                  </label>
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
                    disabled={!formData.firstName || !formData.lastName || !formData.email || loading}
                    className="btn-primary disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {selectedUser ? 'Update' : 'Create'}
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

export default UserManagement;