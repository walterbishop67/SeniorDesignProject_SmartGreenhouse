import React from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminNavbar.css';

const AdminNavbar = () => {
  const navigate = useNavigate();

  const handleProfileClick = () => {
    navigate('/admin/profile');
  };

  return (
    <nav className="admin-navbar">
      <div className="admin-navbar-left">
        <div className="logo-link" onClick={() => navigate('/admin')}>
          <div className="admin-navbar-logo">SMART GREENHOUSE</div>
        </div>
      </div>
      <div className="admin-navbar-right">
        <button className="profile-btn account-btn" onClick={handleProfileClick}>
          MyAccount
        </button>
      </div>
    </nav>
  );
};

export default AdminNavbar; 