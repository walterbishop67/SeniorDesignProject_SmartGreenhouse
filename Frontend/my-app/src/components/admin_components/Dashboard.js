import React, { useState, useEffect } from 'react';
import '../../pages/AdminPage.css';
import { FaUsers, FaUser, FaUserShield, FaMobileAlt, FaCheckCircle, FaTimesCircle, FaExclamationTriangle } from 'react-icons/fa';

function Dashboard() {
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
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      const userStatsResponse = await fetch('https://localhost:9001/api/v1/AdminPanel/users/user-stats-count', {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });
      const userStatsData = await userStatsResponse.json();
      const adminCount = userStatsData.roleCounts &&
        (userStatsData.roleCounts.SuperAdmin || 0) +
        (userStatsData.roleCounts.Admin || 0);

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

  return (
    <div className="dashboard-management">
      <h2>Dashboard</h2>
      <div className="dashboard-cards">
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#e3f2fd' }}>
              <FaUsers size={24} color="#1976d2" />
            </div>
            <span>Total Users</span>
          </div>
          <div className="card-value">{dashboardData.totalUsers}</div>
        </div>
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#f1f8e9' }}>
              <FaUser size={24} color="#43a047" />
            </div>
            <span>Basic Users</span>
          </div>
          <div className="card-value">{dashboardData.basicUsers}</div>
        </div>
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#fff3e0' }}>
              <FaUserShield size={24} color="#ff9800" />
            </div>
            <span>Admin Users</span>
          </div>
          <div className="card-value">{dashboardData.adminUsers}</div>
        </div>
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#e0f7fa' }}>
              <FaMobileAlt size={24} color="#00bcd4" />
            </div>
            <span>Total Devices</span>
          </div>
          <div className="card-value">{dashboardData.totalDevices}</div>
        </div>
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#e8f5e9' }}>
              <FaCheckCircle size={24} color="#43a047" />
            </div>
            <span>Available Devices</span>
          </div>
          <div className="card-value">{dashboardData.availableDevices}</div>
        </div>
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#ffebee' }}>
              <FaTimesCircle size={24} color="#e53935" />
            </div>
            <span>Unavailable Devices</span>
          </div>
          <div className="card-value">{dashboardData.unavailableDevices}</div>
        </div>
        <div className="dashboard-card">
          <div className="card-icon-title">
            <div className="card-icon" style={{ background: '#fffde7' }}>
              <FaExclamationTriangle size={24} color="#fbc02d" />
            </div>
            <span>Error Devices</span>
          </div>
          <div className="card-value">{dashboardData.errorDevices}</div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard; 