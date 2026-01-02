import React, { useState, useEffect } from 'react';
import DataTable from '../common_components/DataTable';
import './UserDetail.css';

function UserDetail({ user, onBack }) {
  const [userDevices, setUserDevices] = useState([]);
  const [loadingDevices, setLoadingDevices] = useState(false);
  const [deviceError, setDeviceError] = useState(null);
  const [addingDevice, setAddingDevice] = useState(false);
  const [addDeviceMessage, setAddDeviceMessage] = useState({ type: '', text: '' });
  const [activeTab, setActiveTab] = useState('details');

  useEffect(() => {
    if (user) {
      fetchUserDevices(user.id);
    }
  }, [user]);

  // Fetch devices for the selected user
  const fetchUserDevices = async (userId) => {
    setLoadingDevices(true);
    setDeviceError(null);
    try {
      const response = await fetch(`https://localhost:9001/api/v1/AdminPanel/electronic-card/by-user-id/all?userId=${userId}`, {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });

      if (!response.ok) {
        throw new Error(`Failed to fetch devices: ${response.status}`);
      }

      const data = await response.json();
      setUserDevices(Array.isArray(data) ? data : [data]);
    } catch (error) {
      console.error('Device fetch error:', error);
      setDeviceError('Failed to load electronic cards. Please try again later.');
      setUserDevices([]);
    } finally {
      setLoadingDevices(false);
    }
  };

  // Add a new device to the selected user
  const addDeviceToUser = async () => {
    setAddingDevice(true);
    setAddDeviceMessage({ type: '', text: '' });
    
    try {
      const response = await fetch(`https://localhost:9001/api/v1/AdminPanel/electronic-card/add-card-by-user-id?userId=${user.id}`, {
        method: 'POST',
        headers: { 
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
          'Content-Type': 'application/json',
          'Accept': '*/*'
        }
      });

      const data = await response.json();
      
      if (response.ok) {
        setAddDeviceMessage({ type: 'success', text: 'Device added successfully!' });
        // Refresh the device list
        fetchUserDevices(user.id);
      } else {
        setAddDeviceMessage({ 
          type: 'error', 
          text: data.message || 'Error while adding electronic card'
        });
      }
    } catch (error) {
      console.error('Add device error:', error);
      setAddDeviceMessage({ 
        type: 'error', 
        text: 'Failed to add device. Please try again later.'
      });
    } finally {
      setAddingDevice(false);
    }
  };

  // Device table column definitions
  const deviceColumns = [
    { key: 'greenHouseId', header: 'Greenhouse ID' },
    { key: 'productName', header: 'Product Name', render: (device) => device.productName || 'N/A' },
    { 
      key: 'status', 
      header: 'Status',
      render: (device) => (
        <span className={`status-badge ${device.status === 'Unavailable' ? 'unavailable' : 'available'}`}>
          {device.status || 'Unknown'}
        </span>
      )
    },
    { key: 'temperature', header: 'Temperature', render: (device) => device.temperature || 'N/A' },
    { key: 'humidity', header: 'Humidity', render: (device) => device.humidity || 'N/A' },
    { key: 'lastDataTime', header: 'Last Data Time', render: (device) => device.lastDataTime || 'N/A' },
    { key: 'created', header: 'Created', render: (device) => device.created || 'N/A' }
  ];

  if (!user) return null;

  return (
    <div className="user-detail-panel animate-slide-in wide-panel">
      <div className="user-info-header">
        <div className="user-avatar">
          <span>{user.firstName?.charAt(0) || ''}{user.lastName?.charAt(0) || ''}</span>
        </div>
        <div className="user-title">
          <h3>{user.firstName} {user.lastName}</h3>
          <span className="user-subtitle">{user.roles && user.roles.join(", ")}</span>
        </div>
        <div className="actions-top-right">
          <button className="action-button" title="Edit User">
            <i className="fas fa-edit"></i>
          </button>
          <button className="back-button" onClick={onBack}>
            <i className="fas fa-arrow-left" style={{ marginRight: '8px' }}></i>
            Back to User List
          </button>
        </div>
      </div>
      
      <div className="user-tabs">
        <button 
          className={`tab-button ${activeTab === 'details' ? 'active' : ''}`}
          onClick={() => setActiveTab('details')}
        >
          <i className="fas fa-user"></i>
          User Details
        </button>
        <button 
          className={`tab-button ${activeTab === 'devices' ? 'active' : ''}`}
          onClick={() => setActiveTab('devices')}
        >
          <i className="fas fa-microchip"></i>
          Devices
          <span className="tab-badge">{userDevices.length}</span>
        </button>
      </div>
      <div className="tab-content">
        {activeTab === 'details' && (
          <div className="details-tab">
            <div className="panel-section">
              <h4 className="section-title">
                <i className="fas fa-id-card"></i>
                Basic Information
              </h4>
              <div className="user-info-grid">
                <div className="info-item">
                  <span className="info-label">Username</span>
                  <span className="info-value">{user.userName}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">Email</span>
                  <span className="info-value">{user.email}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">User ID</span>
                  <span className="info-value">{user.id}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">Status</span>
                  <span className="info-value">
                    <span className="status-badge available">Active</span>
                  </span>
                </div>
              </div>
            </div>
            
            <div className="panel-section">
              <h4 className="section-title">
                <i className="fas fa-shield-alt"></i>
                Permissions & Roles
              </h4>
              <div className="roles-container">
                {user.roles && user.roles.map((role, index) => (
                  <div key={index} className="role-card">
                    <div className="role-icon">
                      <i className={`fas ${
                        role.toLowerCase().includes('admin') ? 'fa-user-shield' : 
                        role.toLowerCase().includes('manager') ? 'fa-user-tie' : 'fa-user'
                      }`}></i>
                    </div>
                    <div className="role-name">{role}</div>
                  </div>
                ))}
              </div>
            </div>
            
            <div className="panel-section">
              <h4 className="section-title">
                <i className="fas fa-history"></i>
                Last Login Activity
              </h4>
              <div className="activity-list">
                <div className="activity-item">
                  <div className="activity-icon">
                    <i className="fas fa-sign-in-alt"></i>
                  </div>
                  <div className="activity-details">
                    <div className="activity-title">Last Login</div>
                    <div className="activity-date">Today, 10:30 AM</div>
                  </div>
                  <div className="activity-status">
                    <span className="status-badge available">Successful</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
        
        {activeTab === 'devices' && (
          <div className="devices-tab">
            <div className="panel-section">
              <div className="section-header">
                <h4 className="section-title">
                  <i className="fas fa-microchip"></i>
                  Electronic Cards
                </h4>
                <div className="header-actions">
                  <button 
                    className="add-device-button"
                    onClick={addDeviceToUser}
                    disabled={addingDevice}
                  >
                    <i className="fas fa-plus"></i>
                    {addingDevice ? 'Adding...' : 'Add New Device'}
                  </button>
                </div>
              </div>
              
              {addDeviceMessage.text && (
                <div className={`message ${addDeviceMessage.type}`}>
                  <i className={`fas ${addDeviceMessage.type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}`}></i>
                  {addDeviceMessage.text}
                </div>
              )}
              
              {loadingDevices ? (
                <div className="loading-spinner">
                  <i className="fas fa-spinner fa-spin"></i>
                  Loading devices...
                </div>
              ) : deviceError ? (
                <div className="error-message">
                  <i className="fas fa-exclamation-triangle"></i>
                  {deviceError}
                </div>
              ) : userDevices.length > 0 ? (
                <div className="devices-table-container">
                  <DataTable
                    columns={deviceColumns}
                    data={userDevices}
                    isLoading={false}
                    emptyText="No devices found"
                    keyField="id"
                    className="devices-table"
                  />
                </div>
              ) : (
                <div className="no-devices">
                  <i className="fas fa-info-circle"></i>
                  No electronic cards found for this user.
                  <button 
                    className="add-device-button mt-10"
                    onClick={addDeviceToUser}
                    disabled={addingDevice}
                  >
                    <i className="fas fa-plus"></i>
                    Add First Device
                  </button>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default UserDetail;