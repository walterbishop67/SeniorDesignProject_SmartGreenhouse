import React from 'react';
import './ChangePasswordButtonGroup.css';

const ChangePasswordButtonGroup = ({ onChangePassword, onLogout, showLogout = false }) => (
  <div className="change-password-btn-group">
    <button className="change-password-btn" onClick={onChangePassword}>
      Şifre Değiştir
    </button>
    {showLogout && (
      <button className="logout-btn" onClick={onLogout}>
        Çıkış Yap
      </button>
    )}
  </div>
);

export default ChangePasswordButtonGroup; 