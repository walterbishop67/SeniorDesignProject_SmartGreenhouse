import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './SettingsPage.css';
import Navbar from '../components/user_components/Navbar';
import Notifications from '../components/user_components/Notifications';
import AccountSettings from '../components/user_components/AccountSettings';
import { FaSignOutAlt } from 'react-icons/fa';

const SettingsPage = () => {
  const navigate = useNavigate();
  const [activeSection, setActiveSection] = useState('Account Settings');
  const [showLogoutModal, setShowLogoutModal] = useState(false);
  
  // Password change states
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  });
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [passwordError, setPasswordError] = useState(null);
  const [passwordSuccess, setPasswordSuccess] = useState(null);
  
  // Support form states
  const [supportForm, setSupportForm] = useState({
    subject: '',
    messageContent: ''
  });
  const [supportLoading, setSupportLoading] = useState(false);
  const [supportError, setSupportError] = useState(null);
  const [supportSuccess, setSupportSuccess] = useState(null);

  // Password changing functions
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
    // Validation
    if (passwordData.newPassword !== passwordData.confirmNewPassword) {
      setPasswordError('Yeni şifreler eşleşmiyor');
      return;
    }
    
    setPasswordError(null);
    
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
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Şifre değiştirme başarısız oldu');
      }
      
      setPasswordSuccess('Şifre başarıyla değiştirildi');
      setTimeout(() => {
        closePasswordModal();
      }, 2000);
      
    } catch (err) {
      console.error('Şifre değiştirme hatası:', err);
      setPasswordError(err.message || 'Şifre değiştirilirken bir hata oluştu');
    }
  };

  // Support form functions
  const handleSupportInputChange = (e) => {
    const { id, value } = e.target;
    setSupportForm({
      ...supportForm,
      [id === 'supportTitle' ? 'subject' : 'messageContent']: value
    });
  };

  const submitSupportTicket = async (e) => {
    e.preventDefault();
    
    // Validation
    if (!supportForm.subject.trim() || !supportForm.messageContent.trim()) {
      setSupportError('Lütfen başlık ve mesaj alanlarını doldurunuz');
      return;
    }
    
    setSupportError(null);
    setSupportLoading(true);
    
    try {
      const token = sessionStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }
      
      const currentDate = new Date().toISOString();
      
      const response = await fetch('https://localhost:9001/api/v1/UserSupportMessage', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          subject: supportForm.subject,
          messageContent: supportForm.messageContent,
          sentAt: currentDate
        })
      });
      
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Destek mesajı gönderimi başarısız oldu');
      }
      
      const responseData = await response.json();
      console.log('Support ticket created with ID:', responseData.id);
      
      setSupportSuccess('Destek talebiniz başarıyla gönderildi');
      // Form temizleme
      setSupportForm({
        subject: '',
        messageContent: ''
      });
      
      // 3 saniye sonra başarı mesajını temizle
      setTimeout(() => {
        setSupportSuccess(null);
      }, 3000);
      
    } catch (err) {
      console.error('Destek talebi gönderme hatası:', err);
      setSupportError(err.message || 'Destek talebi gönderilirken bir hata oluştu');
    } finally {
      setSupportLoading(false);
    }
  };

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

  const renderSettingsContent = () => {
    switch(activeSection) {
      case 'Account Settings':
        return <AccountSettings onChangePassword={handleChangePassword} />;
      case 'Notifications':
        return (
          <div className="settings-content">
            <h2>Notifications Settings</h2>
            <div className="settings-section">
              <Notifications />
            </div>
          </div>
        );
      case 'Supports':
        return (
          <div className="settings-content">
            <h2>Support Center</h2>
            <div className="settings-section">
              <h3>Create Support Ticket</h3>
              
              {supportError && <div className="error-message">{supportError}</div>}
              {supportSuccess && <div className="success-message">{supportSuccess}</div>}
              
              <form className="support-form" onSubmit={submitSupportTicket}>
                <div className="form-group">
                  <label htmlFor="supportTitle">Title:</label>
                  <input 
                    type="text" 
                    id="supportTitle" 
                    placeholder="Enter a brief title for your issue"
                    className="support-input"
                    value={supportForm.subject}
                    onChange={handleSupportInputChange}
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="supportDescription">Description:</label>
                  <textarea 
                    id="supportDescription" 
                    placeholder="Please describe your issue in detail..."
                    className="support-textarea"
                    rows="6"
                    value={supportForm.messageContent}
                    onChange={handleSupportInputChange}
                  ></textarea>
                </div>
                
                <button 
                  type="submit" 
                  className="support-submit-btn"
                  disabled={supportLoading}
                >
                  {supportLoading ? 'Submitting...' : 'Submit Ticket'}
                </button>
              </form>
            </div>
            
            <div className="settings-section">
              <h3>Contact Information</h3>
              <p>Email: support@example.com</p>
              <p>Phone: +90 212 123 4567</p>
              <p>Working hours: 09:00 - 18:00 (Monday - Friday)</p>
            </div>
          </div>
        );
      case 'About':
        return (
          <div className="settings-content">
            <h2>About</h2>
            <div className="settings-section">
              <div className="about-header">
                <h3>Akıllı Sera Sistemi</h3>
                <p className="about-intro">
                  Modern tarımı desteklemek ve sera yönetimini dijitalleştirerek çiftçilere zaman kazandırmak amacıyla geliştirilmiş bütünleşik bir platformdur. Projemiz, geleneksel sera yönetiminin zorluklarını azaltmak ve verimi artırmak için yapay zeka, IoT (Nesnelerin İnterneti) ve mobil teknolojileri bir araya getirerek yenilikçi bir çözüm sunar.
                </p>
              </div>
              
              <div className="about-features">
                <h3>Sistem Özellikleri</h3>
                <ul className="feature-list">
                  <li><span className="feature-highlight">Sıcaklık ve nem değerleri</span> anlık olarak izlenebilir</li>
                  <li><span className="feature-highlight">Yapay zeka destekli bitki hastalık tespiti</span> yapılabilir</li>
                  <li><span className="feature-highlight">Otomatik bildirim sistemi</span> sayesinde olumsuz durumlar anında fark edilir</li>
                  <li><span className="feature-highlight">Pazar fiyat listeleri</span> günlük olarak görüntülenebilir</li>
                  <li><span className="feature-highlight">Takvim ve görev planlayıcı</span> ile seradaki işlemler kolayca takip edilebilir</li>
                  <li>İsteğe bağlı olarak <span className="feature-highlight">kamera ile görsel takip</span> yapılabilir</li>
                  <li>Uygulama içi <span className="feature-highlight">çiftçi forumu</span> ile bilgi alışverişi sağlanabilir</li>
                </ul>
              </div>
              
              <div className="about-platforms">
                <p>
                  Mobil ve web platformları üzerinden erişilebilen bu sistem, yalnızca ölçüm yapan bir uygulama olmanın ötesine geçerek, çiftçilere karar destek mekanizması sunar. Aynı zamanda kullanıcı dostu arayüzü ile hem küçük ölçekli üreticilere hem de profesyonel sera yöneticilerine hitap eder.
                </p>
              </div>
              
              <div className="about-mission">
                <div className="mission-item">
                  <h4>Vizyonumuz</h4>
                  <p>Sürdürülebilir tarım için teknolojiyi herkesin erişebileceği şekilde sunmak</p>
                </div>
                <div className="mission-item">
                  <h4>Misyonumuz</h4>
                  <p>Çiftçilerin işlerini kolaylaştırmak ve daha sağlıklı, verimli üretim yapmalarını sağlamaktır</p>
                </div>
              </div>
              
              <div className="about-version">
                <h4>Versiyon</h4>
                <p>v1.2.3</p>
                <p className="release-date">Son Güncelleme: 15 Nisan 2025</p>
              </div>
            </div>
          </div>
        );
      default:
        return (
          <div className="settings-content">
            <h2>Select a settings option</h2>
          </div>
        );
    }
  };

  return (
    <div className="settings-page">
      <Navbar />
      <div className="settings-container">
        <div className="settings-sidebar">
          <h3>Settings</h3>
          <ul className="settings-menu">
            <li 
              className={activeSection === 'Account Settings' ? 'active' : ''}
              onClick={() => setActiveSection('Account Settings')}
            >
              Account Settings
            </li>
            <li 
              className={activeSection === 'Notifications' ? 'active' : ''}
              onClick={() => setActiveSection('Notifications')}
            >
              Notifications
            </li>
            <li 
              className={activeSection === 'Supports' ? 'active' : ''}
              onClick={() => setActiveSection('Supports')}
            >
              Supports
            </li>
            <li 
              className={activeSection === 'About' ? 'active' : ''}
              onClick={() => setActiveSection('About')}
            >
              About
            </li>
            <li className="logout-item" onClick={handleLogout}>
              <FaSignOutAlt className="logout-icon" />
              Çıkış Yap
            </li>
          </ul>
        </div>
        {renderSettingsContent()}
      </div>
      
      {/* Logout Modal */}
      {showLogoutModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>Çıkış Yap</h2>
            <p>Hesabınızdan çıkış yapmak istediğinizden emin misiniz?</p>
            <div className="modal-buttons">
              <button className="confirm-btn" onClick={confirmLogout}>Evet, Çıkış Yap</button>
              <button className="cancel-btn" onClick={cancelLogout}>İptal</button>
            </div>
          </div>
        </div>
      )}

      {/* Password Change Modal */}
      {showPasswordModal && (
        <div className="password-modal-overlay">
          <div className="password-modal">
            <h2>Şifre Değiştir</h2>
            {passwordError && <div className="error-message">{passwordError}</div>}
            {passwordSuccess && <div className="success-message">{passwordSuccess}</div>}
            <div className="password-form">
              <div className="form-group">
                <label htmlFor="currentPassword">Mevcut Şifre:</label>
                <input
                  type="password"
                  id="currentPassword"
                  name="currentPassword"
                  value={passwordData.currentPassword}
                  onChange={handlePasswordInputChange}
                  className="password-input"
                />
              </div>
              <div className="form-group">
                <label htmlFor="newPassword">Yeni Şifre:</label>
                <input
                  type="password"
                  id="newPassword"
                  name="newPassword"
                  value={passwordData.newPassword}
                  onChange={handlePasswordInputChange}
                  className="password-input"
                />
              </div>
              <div className="form-group">
                <label htmlFor="confirmNewPassword">Yeni Şifre (Tekrar):</label>
                <input
                  type="password"
                  id="confirmNewPassword"
                  name="confirmNewPassword"
                  value={passwordData.confirmNewPassword}
                  onChange={handlePasswordInputChange}
                  className="password-input"
                />
              </div>
              <div className="password-modal-buttons">
                <button onClick={submitPasswordChange} className="submit-btn">Şifre Değiştir</button>
                <button onClick={closePasswordModal} className="cancel-btn">İptal</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SettingsPage;