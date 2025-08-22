import React, { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
  Bars3Icon,
  Squares2X2Icon,
  ServerIcon,
  QueueListIcon,
  DocumentTextIcon,
  UsersIcon,
  BuildingOfficeIcon,
  UserCircleIcon,
  ArrowRightOnRectangleIcon,
  Cog6ToothIcon,
  XMarkIcon,
} from '@heroicons/react/24/outline';
import { useAuth } from '../../contexts/AuthContext';

interface NavigationItem {
  text: string;
  icon: React.ReactElement;
  path: string;
  children?: NavigationItem[];
}

const navigationItems: NavigationItem[] = [
  {
    text: 'Dashboard',
    icon: <Squares2X2Icon className="h-5 w-5" />,
    path: '/dashboard',
  },
  {
    text: 'Kafka Management',
    icon: <ServerIcon className="h-5 w-5" />,
    path: '/kafka',
    children: [
      {
        text: 'Clusters',
        icon: <ServerIcon className="h-4 w-4" />,
        path: '/kafka/clusters',
      },
      {
        text: 'Topics',
        icon: <QueueListIcon className="h-4 w-4" />,
        path: '/kafka/topics',
      },
      {
        text: 'Audit Logs',
        icon: <DocumentTextIcon className="h-4 w-4" />,
        path: '/kafka/audit-logs',
      },
    ],
  },
  {
    text: 'User Management',
    icon: <UsersIcon className="h-5 w-5" />,
    path: '/users',
  },
  {
    text: 'Tenant Management',
    icon: <BuildingOfficeIcon className="h-5 w-5" />,
    path: '/tenants',
  },
];

const Layout: React.FC = () => {
  const { user, logout, tenantId } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [profileMenuOpen, setProfileMenuOpen] = useState(false);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const toggleProfileMenu = () => {
    setProfileMenuOpen(!profileMenuOpen);
  };

  const handleLogout = () => {
    logout();
    setProfileMenuOpen(false);
  };

  const isSelected = (path: string) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  const sidebarContent = (
    <div className="h-full flex flex-col bg-dark-900">
      {/* Logo/Header */}
      <div className="p-4 border-b border-dark-700">
        <h1 className="text-xl font-bold gradient-text-primary">Multi-Tenant SaaS</h1>
        <p className="text-sm text-gray-400">{tenantId}</p>
      </div>
      
      {/* Navigation */}
      <nav className="flex-1 px-2 py-4 space-y-1">
        {navigationItems.map((item) => (
          <div key={item.text}>
            <button
              onClick={() => {
                navigate(item.path);
                setSidebarOpen(false);
              }}
              className={`
                w-full flex items-center px-3 py-2 text-sm font-medium rounded-lg transition-all duration-200
                ${isSelected(item.path) 
                  ? 'bg-primary-600 text-white shadow-lg' 
                  : 'text-gray-300 hover:bg-dark-700 hover:text-white'
                }
              `}
            >
              <span className="mr-3">{item.icon}</span>
              {item.text}
            </button>
            
            {item.children && (
              <div className="ml-4 mt-1 space-y-1">
                {item.children.map((child) => (
                  <button
                    key={child.text}
                    onClick={() => {
                      navigate(child.path);
                      setSidebarOpen(false);
                    }}
                    className={`
                      w-full flex items-center px-3 py-2 text-sm rounded-lg transition-all duration-200
                      ${isSelected(child.path) 
                        ? 'bg-primary-500/20 text-primary-400 border-l-2 border-primary-400' 
                        : 'text-gray-400 hover:bg-dark-700 hover:text-gray-300'
                      }
                    `}
                  >
                    <span className="mr-3 opacity-70">{child.icon}</span>
                    {child.text}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}
      </nav>
    </div>
  );

  return (
    <div className="min-h-screen bg-dark-950 flex w-full overflow-hidden">
      {/* Mobile sidebar overlay */}
      {sidebarOpen && (
        <div 
          className="fixed inset-0 z-40 bg-black bg-opacity-50 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <div className={`
        fixed inset-y-0 left-0 z-50 w-64 transform transition-transform duration-300 ease-in-out
        lg:relative lg:translate-x-0 lg:flex-shrink-0
        ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'}
      `}>
        {sidebarContent}
      </div>

      {/* Main content */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Top header */}
        <header className="bg-dark-800/50 backdrop-blur-xl border-b border-dark-700 sticky top-0 z-30">
          <div className="px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              {/* Mobile menu button */}
              <button
                onClick={toggleSidebar}
                className="lg:hidden p-2 rounded-lg text-gray-400 hover:text-white hover:bg-dark-700 transition-colors"
              >
                <Bars3Icon className="h-6 w-6" />
              </button>

              {/* Spacer */}
              <div className="flex-1"></div>

              {/* User menu */}
              {user && (
                <div className="flex items-center space-x-4">
                  <span className="badge-primary text-xs">
                    {user.roles[0]}
                  </span>
                  <div className="hidden sm:flex sm:flex-col sm:items-end">
                    <p className="text-sm font-medium text-gray-100">{user.fullName}</p>
                    <p className="text-xs text-gray-400">{user.email}</p>
                  </div>
                  <div className="relative">
                    <button
                      onClick={toggleProfileMenu}
                      className="flex items-center p-1 rounded-full hover:bg-dark-700 transition-colors"
                    >
                      <div className="w-8 h-8 bg-gradient-to-r from-primary-500 to-primary-600 rounded-full flex items-center justify-center text-white font-medium text-sm">
                        {user.firstName.charAt(0)}
                      </div>
                    </button>

                    {/* Profile dropdown */}
                    {profileMenuOpen && (
                      <div className="absolute right-0 mt-2 w-56 bg-dark-800 rounded-lg shadow-xl border border-dark-700 py-1 z-50">
                        <button
                          onClick={() => setProfileMenuOpen(false)}
                          className="w-full px-4 py-2 text-left text-sm text-gray-300 hover:bg-dark-700 hover:text-white flex items-center"
                        >
                          <UserCircleIcon className="h-4 w-4 mr-3" />
                          Profile
                        </button>
                        <button
                          onClick={() => setProfileMenuOpen(false)}
                          className="w-full px-4 py-2 text-left text-sm text-gray-300 hover:bg-dark-700 hover:text-white flex items-center"
                        >
                          <Cog6ToothIcon className="h-4 w-4 mr-3" />
                          Settings
                        </button>
                        <hr className="my-1 border-dark-700" />
                        <button
                          onClick={handleLogout}
                          className="w-full px-4 py-2 text-left text-sm text-gray-300 hover:bg-dark-700 hover:text-white flex items-center"
                        >
                          <ArrowRightOnRectangleIcon className="h-4 w-4 mr-3" />
                          Logout
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>

        {/* Page content */}
        <main className="flex-1 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;