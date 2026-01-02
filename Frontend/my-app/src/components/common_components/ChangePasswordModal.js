import React, { useState } from 'react';
import './ChangePasswordModal.css';

const ChangePasswordModal = ({ open, onClose }) => {
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  });
  const [passwordError, setPasswordError] = useState(null);
  const [passwordSuccess, setPasswordSuccess] = useState(null);
  const [loading, setLoading] = useState(false);

  if (!open) return null;

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setPasswordData({ ...passwordData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordSuccess(null);
    setLoading(true);
    try {
      const token = sessionStorage.getItem('token');
      if (!token) {
        setPasswordError('Oturum bulunamadı. Lütfen tekrar giriş yapın.');
        setLoading(false);
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
        setLoading(false);
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
        setPasswordSuccess(null);
        setPasswordData({ currentPassword: '', newPassword: '', confirmNewPassword: '' });
        onClose();
      }, 2000);
    } catch (err) {
      setPasswordError(err.message || 'Şifre değiştirilirken bir hata oluştu');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="password-modal-overlay">
      <div className="password-modal">
        <h2>Şifre Değiştir</h2>
        {passwordError && <div className="error-message">{passwordError}</div>}
        {passwordSuccess && <div className="success-message">{passwordSuccess}</div>}
        <form className="password-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="currentPassword">Mevcut Şifre:</label>
            <input
              type="password"
              id="currentPassword"
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={handleInputChange}
              className="password-input"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="newPassword">Yeni Şifre:</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              value={passwordData.newPassword}
              onChange={handleInputChange}
              className="password-input"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="confirmNewPassword">Yeni Şifre (Tekrar):</label>
            <input
              type="password"
              id="confirmNewPassword"
              name="confirmNewPassword"
              value={passwordData.confirmNewPassword}
              onChange={handleInputChange}
              className="password-input"
              required
            />
          </div>
          <div className="password-modal-buttons">
            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? 'Değiştiriliyor...' : 'Şifre Değiştir'}
            </button>
            <button type="button" className="cancel-btn" onClick={onClose} disabled={loading}>
              İptal
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangePasswordModal; 