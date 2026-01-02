import React, { useState, useEffect } from 'react';
import './AccountSettings.css';
import ChangePasswordModal from '../common_components/ChangePasswordModal';

const AccountSettings = () => {
  const [userData, setUserData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '********'
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showPasswordModal, setShowPasswordModal] = useState(false);

  useEffect(() => {
    // Fetch user data from the API
    const fetchUserData = async () => {
      setIsLoading(true);
      try {
        const token = sessionStorage.getItem('token');
        if (!token) {
          throw new Error('No authentication token found');
        }
        const response = await fetch('https://localhost:9001/api/Account/get-user-basic-info', {
          method: 'GET',
          headers: {
            'Accept': '*/*',
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch user data');
        }
        const data = await response.json();
        setUserData({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          email: data.email || '',
          userName: data.userName || '',
          password: '********'
        });
        setIsLoading(false);
      } catch (err) {
        console.error('Could not fetch user data:', err);
        setError('User information could not be loaded.');
        setIsLoading(false);
      }
    };
    fetchUserData();
  }, []);

  if (isLoading) {
    return (
      <div className="loading-container">
        <p>Loading user data...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p>Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="settings-content">
      <h2>Basic Info</h2>
      <div className="settings-section">
        <div className="settings-field">
          <span className="field-label">First Name :</span>
          <span className="field-value">{userData.firstName}</span>
        </div>
        <div className="settings-field">
          <span className="field-label">Last Name :</span>
          <span className="field-value">{userData.lastName}</span>
        </div>
      </div>
      <h2>Account Info</h2>
      <div className="settings-section">
        <div className="settings-field">
          <span className="field-label">Email :</span>
          <span className="field-value">{userData.email}</span>
        </div>
        <div className="settings-field">
          <span className="field-label">Password :</span>
          <span className="field-value">{userData.password}</span>
          <div className="account-actions">
            <button className="change-password-btn" onClick={() => setShowPasswordModal(true)}>
              Şifre Değiştir
            </button>
          </div>
        </div>
      </div>
      {showPasswordModal && (
        <ChangePasswordModal open={showPasswordModal} onClose={() => setShowPasswordModal(false)} />
      )}
    </div>
  );
};

export default AccountSettings;