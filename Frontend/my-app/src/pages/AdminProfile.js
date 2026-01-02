import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminProfile.css';
import ChangePasswordModal from '../components/common_components/ChangePasswordModal';

const AdminProfile = () => {
  const navigate = useNavigate();
  const [userData, setUserData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    userName: '',
    password: '********'
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  });
  const [passwordError, setPasswordError] = useState(null);
  const [passwordSuccess, setPasswordSuccess] = useState(null);

  useEffect(() => {
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

  const handleLogout = () => {
    setShowLogoutModal(true);
  };

  const confirmLogout = () => {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    navigate('/login');
  };

  const cancelLogout = () => {
    setShowLogoutModal(false);
  };

  const handlePasswordInputChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({
      ...passwordData,
      [name]: value
    });
  };

  const handleChangePassword = () => {
    setShowPasswordModal(true);
  };

  const closePasswordModal = () => {
    setShowPasswordModal(false);
    setPasswordError(null);
    setPasswordSuccess(null);
    setPasswordData({
      currentPassword: '',
      newPassword: '',
      confirmNewPassword: ''
    });
  };

  const submitPasswordChange = async () => {
    setPasswordError(null);
    setPasswordSuccess(null);
    try {
      const token = sessionStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }
      const response = await fetch('https://localhost:9001/api/Account/change-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          currentPassword: passwordData.currentPassword,
          newPassword: passwordData.newPassword,
          confirmNewPassword: passwordData.confirmNewPassword
        })
      });
      const responseText = await response.text();
      let responseData;
      try {
        responseData = JSON.parse(responseText);
      } catch (e) {
        responseData = responseText;
      }
      if (!response.ok) {
        let errorMessage = '';
        if (responseData && typeof responseData === 'object') {
          if (responseData.errors && typeof responseData.errors === 'object') {
            const errorMessages = [];
            for (const key in responseData.errors) {
              if (Array.isArray(responseData.errors[key])) {
                errorMessages.push(...responseData.errors[key]);
              }
            }
            if (errorMessages.length > 0) {
              errorMessage = errorMessages.join(', ');
            }
          } else if (responseData.message) {
            errorMessage = responseData.message;
          } else if (responseData.Message) {
            errorMessage = responseData.Message;
          } else if (responseData.title) {
            errorMessage = responseData.title;
            if (responseData.detail) {
              errorMessage += `: ${responseData.detail}`;
            }
          } else if (typeof responseData === 'string') {
            errorMessage = responseData;
          } else if (Array.isArray(responseData)) {
            errorMessage = responseData.join(', ');
          } else {
            errorMessage = `Şifre değiştirme başarısız (${response.status})`;
          }
        } else {
          errorMessage = responseData || `Şifre değiştirme başarısız (${response.status})`;
        }
        setPasswordError(errorMessage);
        return;
      }
      let successMessage = '';
      if (responseData && typeof responseData === 'object') {
        if (responseData.message) {
          successMessage = responseData.message;
        } else if (responseData.Message) {
          successMessage = responseData.Message;
        } else if (typeof responseData === 'string') {
          successMessage = responseData;
        } else {
          successMessage = 'Şifre başarıyla değiştirildi';
        }
      } else {
        successMessage = responseData || 'Şifre başarıyla değiştirildi';
      }
      setPasswordSuccess(successMessage);
      setTimeout(() => {
        closePasswordModal();
      }, 2000);
    } catch (err) {
      setPasswordError(err.message || 'Şifre değiştirilirken bir hata oluştu');
    }
  };

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
    <div className="admin-profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <h1>Profil Bilgileri</h1>
        </div>
        
        <div className="profile-content">
          <div className="profile-section">
            <h2>Kişisel Bilgiler</h2>
            <div className="info-group">
              <label>Ad:</label>
              <span>{userData.firstName}</span>
            </div>
            <div className="info-group">
              <label>Soyad:</label>
              <span>{userData.lastName}</span>
            </div>
            <div className="info-group">
              <label>E-posta:</label>
              <span>{userData.email}</span>
            </div>
            <div className="info-group">
              <label>Kullanıcı Adı:</label>
              <span>{userData.userName}</span>
            </div>
          </div>
          <div className="profile-section account-section">
            <h2>Hesap İşlemleri</h2>
            <div className="account-actions">
              <button className="change-password-btn" onClick={() => setShowPasswordModal(true)}>
                Şifre Değiştir
              </button>
              <button className="logout-btn" onClick={handleLogout}>
                Çıkış Yap
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Logout Modal */}
      {showLogoutModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>Çıkış Yap</h2>
            <p>Hesabınızdan çıkış yapmak istediğinizden emin misiniz?</p>
            <div className="modal-buttons">
              <button className="confirm-btn" onClick={confirmLogout}>
                Evet, Çıkış Yap
              </button>
              <button className="cancel-btn" onClick={cancelLogout}>
                İptal
              </button>
            </div>
          </div>
        </div>
      )}

      {showPasswordModal && (
        <ChangePasswordModal open={showPasswordModal} onClose={() => setShowPasswordModal(false)} />
      )}
    </div>
  );
};

export default AdminProfile;