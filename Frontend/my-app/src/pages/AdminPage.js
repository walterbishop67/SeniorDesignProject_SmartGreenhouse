import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation, Routes, Route, Navigate } from 'react-router-dom';
import SupportSection from '../components/admin_components/SupportSection';
import DevicesSection from '../components/admin_components/DevicesSection';
import UsersSection from '../components/admin_components/UsersSection';
import Sidebar from '../components/admin_components/Sidebar';
import AdminNavbar from '../components/admin_components/AdminNavbar';
import AdminProfile from './AdminProfile';
import AboutSection from '../components/common_components/AboutSection';
import './AdminPage.css';
import Dashboard from '../components/admin_components/Dashboard';

const menuItems = [
  { id: 'Dashboard', label: 'Dashboard', path: '/admin' },
  { id: 'Users', label: 'Users', path: '/admin/users' },
  { id: 'Devices', label: 'Devices', path: '/admin/devices' },
  { id: 'Supports', label: 'Supports', path: '/admin/supports' },
  { id: 'About', label: 'About', path: '/admin/about' }
];

function AdminPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [activeMenu, setActiveMenu] = useState('Dashboard');
  const [dashboardData, setDashboardData] = useState({
    totalUsers: 0,
    basicUsers: 0,
    adminUsers: 0,
    totalDevices: 0,
    availableDevices: 0,
    unavailableDevices: 0,
    errorDevices: 0
  });

  useEffect(() => {
    if (activeMenu === 'Dashboard') {
      fetchDashboardData();
    }
  }, [activeMenu]);

  const fetchDashboardData = async () => {
    try {
      // Fetch user stats data
      const userStatsResponse = await fetch('https://localhost:9001/api/v1/AdminPanel/users/user-stats-count', {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });
      const userStatsData = await userStatsResponse.json();
      
      // Calculate admin count from roleCount
      const adminCount = userStatsData.roleCounts && 
                        (userStatsData.roleCounts.SuperAdmin || 0) + 
                        (userStatsData.roleCounts.Admin || 0);
      
      // Fetch electronic card counts
      const cardResponse = await fetch('https://localhost:9001/api/v1/AdminPanel/electronic-card/counts', {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });
      const cardData = await cardResponse.json();
      
      setDashboardData({
        totalUsers: userStatsData.totalUserCount || 0,
        basicUsers: userStatsData.roleCounts?.Basic || 0,
        adminUsers: adminCount || 0,
        totalDevices: cardData.totalCount || 0,
        availableDevices: cardData.availableCount || 0,
        unavailableDevices: cardData.unavailableCount || 0,
        errorDevices: cardData.errorCount || 0
      });
    } catch (error) {
      console.error('Dashboard data fetch error:', error);
      setDashboardData({
        totalUsers: 0,
        basicUsers: 0,
        adminUsers: 0,
        totalDevices: 0,
        availableDevices: 0,
        unavailableDevices: 0,
        errorDevices: 0
      });
    }
  };

  const handleLogout = () => {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    navigate('/login');
  };

  const handleMenuChange = (id) => {
    const item = menuItems.find((m) => m.id === id);
    if (item) {
      navigate(item.path);
    }
  };

  return (
    <div className="admin-container">
      <AdminNavbar onProfileClick={handleLogout} />
      <div className="admin-content">
        <Sidebar menuItems={menuItems} onMenuChange={handleMenuChange} />
        <main className="admin-main">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/users" element={<UsersSection />} />
            <Route path="/devices" element={<DevicesSection />} />
            <Route path="/supports" element={<SupportSection />} />
            <Route path="/about" element={<AboutSection />} />
            <Route path="/profile" element={<AdminProfile />} />
            <Route path="*" element={<Navigate to="/admin" />} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

export default AdminPage;