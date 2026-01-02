import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './LoginPage.css';

function LoginPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  // Password reset states
  const [showForgotPassword, setShowForgotPassword] = useState(false);
  const [showResetPassword, setShowResetPassword] = useState(false);
  const [forgotEmail, setForgotEmail] = useState('');
  const [resetData, setResetData] = useState({
    email: '',
    token: '',
    password: '',
    confirmPassword: ''
  });
  const [forgotLoading, setForgotLoading] = useState(false);
  const [resetLoading, setResetLoading] = useState(false);
  const [forgotError, setForgotError] = useState('');
  const [resetError, setResetError] = useState('');
  const [forgotSuccess, setForgotSuccess] = useState('');
  const [resetSuccess, setResetSuccess] = useState('');

  // Component mount olduğunda token kontrolü yap
  useEffect(() => {
    const token = sessionStorage.getItem('token');
    if (token) {
      // Kullanıcının rolüne göre yönlendirme yap
      const user = JSON.parse(sessionStorage.getItem('user') || '{}');
      if (user.roles && (user.roles.includes('Admin') || user.roles.includes('SuperAdmin'))) {
        navigate('/admin');
      } else {
        navigate('/home');
      }
    }
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleForgotEmailChange = (e) => {
    setForgotEmail(e.target.value);
  };

  const handleResetDataChange = (e) => {
    const { name, value } = e.target;
    setResetData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await fetch('https://localhost:9001/api/Account/authenticate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      console.log("Status Code:", response.status, response.statusText);
      // Response içeriğini text olarak alıyoruz
      const responseText = await response.text();
      console.log("Raw Response:", responseText);
      // Boş yanıt kontrolü
      if (!responseText) {
        console.log("Empty response received");
        setError("Sunucudan yanıt alınamadı.");
        setLoading(false);
        return;
      }
      // JSON parse etmeyi deniyoruz
      let responseData;
      try {
        responseData = JSON.parse(responseText);
        console.log("Parsed Response Data:", responseData);
      } catch (e) {
        console.error("JSON parse error:", e);
        // JSON parse edilemezse text'i direkt kullanıyoruz
        setError(responseText);
        setLoading(false);
        return;
      }
      if (response.ok) {
        console.log('Login başarılı:', responseData);
        // Token'ı sessionStorage'a kaydet
        sessionStorage.setItem('token', responseData.jwToken);
        // Kullanıcı bilgilerini de sakla
        sessionStorage.setItem('user', JSON.stringify({
          id: responseData.id,
          userName: responseData.userName,
          //email: responseData.email,
          roles: responseData.roles,
          isVerified: responseData.isVerified
        }));
        // Kullanıcı rollerini kontrol et ve yönlendirmeyi buna göre yap
        if (responseData.roles && (responseData.roles.includes('Admin') || responseData.roles.includes('SuperAdmin'))) {
          navigate('/admin');
        } else {
          navigate('/home');
        }
      } else {
        // API'den gelen hata mesajını doğrudan kullanıyoruz
        let errorMessage = '';
        // Farklı hata formatlarını kontrol ediyoruz
        if (responseData) {
          // ModelState validation errors
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
          } 
          // Message property
          else if (responseData.message) {
            errorMessage = responseData.message;
          }
          // Message property (pascal case)
          else if (responseData.Message) {
            errorMessage = responseData.Message;
          }
          // ProblemDetails format
          else if (responseData.title) {
            errorMessage = responseData.title;
            if (responseData.detail) {
              errorMessage += `: ${responseData.detail}`;
            }
          }
          // String response
          else if (typeof responseData === 'string') {
            errorMessage = responseData;
          }
          // Array of error messages
          else if (Array.isArray(responseData)) {
            errorMessage = responseData.join(', ');
          }
          // Fallback if no recognizable format
          else {
            errorMessage = `Giriş başarısız (${response.status})`;
          }
        } else {
          errorMessage = `Giriş başarısız (${response.status})`;
        }
        console.log("Error Message:", errorMessage);
        setError(errorMessage);
      }
    } catch (err) {
      console.error('Bağlantı hatası:', err);
      setError(`Sunucu bağlantı hatası: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // handleForgotSubmit fonksiyonunu da benzer şekilde sadeleştirelim
const handleForgotSubmit = async (e) => {
  e.preventDefault();
  setForgotError('');
  setForgotSuccess('');
  setForgotLoading(true);
  
  try {
    const response = await fetch('https://localhost:9001/api/Account/forgot-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: forgotEmail })
    });
    
    const responseText = await response.text();
    console.log("Forgot Password Raw Response:", responseText);
    
    // Boş yanıt kontrolü
    if (!responseText) {
      setForgotError("Sunucudan yanıt alınamadı.");
      setForgotLoading(false);
      return;
    }
    
    // Try to parse as JSON
    let responseData;
    try {
      responseData = JSON.parse(responseText);
    } catch (e) {
      // JSON parse edilemezse text'i direkt kullanıyoruz
      if (response.ok) {
        setForgotSuccess(responseText);
        // Pre-populate email for reset password form
        setResetData(prev => ({ ...prev, email: forgotEmail }));
        // Show reset password form
        setShowResetPassword(true);
      } else {
        setForgotError(responseText);
      }
      setForgotLoading(false);
      return;
    }
    
    // API yanıtını bilinen formata göre işle
    if (response.ok) {
      setForgotSuccess(responseData.Message || "Şifre sıfırlama talimatları e-posta adresinize gönderildi.");
      // Pre-populate email for reset password form
      setResetData(prev => ({ ...prev, email: forgotEmail }));
      // Show reset password form
      setShowResetPassword(true);
    } else {
      // API'den gelen hata mesajını kullan
      setForgotError(responseData.Message || `İşlem başarısız (${response.status})`);
    }
  } catch (err) {
    console.error('Bağlantı hatası:', err);
    setForgotError(`Sunucu bağlantı hatası: ${err.message}`);
  } finally {
    setForgotLoading(false);
  }
};

  // handleResetSubmit fonksiyonundaki hata işleme kısmını sadeleştirelim
const handleResetSubmit = async (e) => {
  e.preventDefault();
  setResetError('');
  setResetSuccess('');
  setResetLoading(true);
  
  // Validate passwords match
  if (resetData.password !== resetData.confirmPassword) {
    setResetError('Şifreler eşleşmiyor.');
    setResetLoading(false);
    return;
  }
  
  try {
    const response = await fetch('https://localhost:9001/api/Account/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(resetData)
    });
    
    const responseText = await response.text();
    console.log("Reset Password Raw Response:", responseText);
    
    // Boş yanıt kontrolü
    if (!responseText) {
      setResetError("Sunucudan yanıt alınamadı.");
      setResetLoading(false);
      return;
    }
    
    // Try to parse as JSON
    let responseData;
    try {
      responseData = JSON.parse(responseText);
    } catch (e) {
      // JSON parse edilemezse text'i direkt kullanıyoruz
      if (response.ok) {
        setResetSuccess(responseText);
      } else {
        setResetError(responseText);
      }
      setResetLoading(false);
      return;
    }
    
    // API yanıtını bilinen formata göre işle
    if (response.ok) {
      setResetSuccess(responseData.Message || "Şifreniz başarıyla sıfırlandı.");
      // Reset forms and close reset dialogs after successful reset
      setTimeout(() => {
        setShowForgotPassword(false);
        setShowResetPassword(false);
        setForgotEmail('');
        setResetData({
          email: '',
          token: '',
          password: '',
          confirmPassword: ''
        });
        setSuccess("Şifreniz başarıyla sıfırlandı. Lütfen yeni şifrenizle giriş yapın.");
      }, 3000);
    } else {
      // API'den gelen hata mesajını kullan
      setResetError(responseData.Message || `İşlem başarısız (${response.status})`);
    }
  } catch (err) {
    console.error('Bağlantı hatası:', err);
    setResetError(`Sunucu bağlantı hatası: ${err.message}`);
  } finally {
    setResetLoading(false);
  }
};

  const handleForgotPasswordClick = (e) => {
    e.preventDefault();
    setShowForgotPassword(true);
  };

  const handleBackToLogin = () => {
    setShowForgotPassword(false);
    setShowResetPassword(false);
    setForgotEmail('');
    setResetData({
      email: '',
      token: '',
      password: '',
      confirmPassword: ''
    });
    setForgotError('');
    setResetError('');
    setForgotSuccess('');
    setResetSuccess('');
  };

  return (
    <div className="login-container">
      <div className="login-box">
        {!showForgotPassword && !showResetPassword && (
          <>
            <h1 className="login-title">Hesabınıza Giriş Yapın</h1>

            {error && <div className="error-message">{error}</div>}
            {success && <div className="success-message">{success}</div>}

            <form onSubmit={handleSubmit} className="login-form" method="post">
              <div className="form-group">
                <label htmlFor="email">E-posta</label>
                <input
                  type="email"
                  name="email"
                  id="email"
                  placeholder="E-posta adresiniz"
                  value={formData.email}
                  onChange={handleChange}
                  disabled={loading}
                  required
                  autoComplete="username"
                />
              </div>

              <div className="form-group">
                <label htmlFor="password">Şifre</label>
                <input
                  type="password"
                  name="password"
                  id="password"
                  placeholder="Şifreniz"
                  value={formData.password}
                  onChange={handleChange}
                  disabled={loading}
                  required
                  autoComplete="new-password"
                />
              </div>

              <div className="forgot-password">
                <a href="#" onClick={handleForgotPasswordClick}>Şifrenizi mi unuttunuz?</a>
              </div>

              <button type="submit" className="login-button" disabled={loading}>
                {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
              </button>
            </form>

            <div className="register-link">
              Hesabınız yok mu? <Link to="/register">Kayıt Olun</Link>
            </div>
          </>
        )}

        {showForgotPassword && !showResetPassword && (
          <>
            <h1 className="login-title">Şifremi Unuttum</h1>
            
            {forgotError && <div className="error-message">{forgotError}</div>}
            {forgotSuccess && <div className="success-message">{forgotSuccess}</div>}
            
            <form onSubmit={handleForgotSubmit} className="login-form">
              <div className="form-group">
                <label htmlFor="forgotEmail">E-posta</label>
                <input
                  type="email"
                  name="forgotEmail"
                  id="forgotEmail"
                  placeholder="E-posta adresiniz"
                  value={forgotEmail}
                  onChange={handleForgotEmailChange}
                  disabled={forgotLoading}
                  required
                  autoComplete="email"
                />
              </div>
              
              <button type="submit" className="login-button" disabled={forgotLoading}>
                {forgotLoading ? 'Gönderiliyor...' : 'Sıfırlama Bağlantısı Gönder'}
              </button>
              
              <button type="button" className="back-button" onClick={handleBackToLogin}>
                Giriş Ekranına Dön
              </button>
            </form>
          </>
        )}

        {showResetPassword && (
          <>
            <h1 className="login-title">Şifre Sıfırlama</h1>
            
            {resetError && <div className="error-message">{resetError}</div>}
            {resetSuccess && <div className="success-message">{resetSuccess}</div>}
            
            <form onSubmit={handleResetSubmit} className="login-form">
              <div className="form-group">
                <label htmlFor="reset-email">E-posta</label>
                <input
                  type="email"
                  name="email"
                  id="reset-email"
                  placeholder="E-posta adresiniz"
                  value={resetData.email}
                  onChange={handleResetDataChange}
                  disabled={resetLoading}
                  required
                  autoComplete="email"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="reset-token">Sıfırlama Kodu</label>
                <input
                  type="text"
                  name="token"
                  id="reset-token"
                  placeholder="E-postanıza gönderilen kod"
                  value={resetData.token}
                  onChange={handleResetDataChange}
                  disabled={resetLoading}
                  required
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="reset-password">Yeni Şifre</label>
                <input
                  type="password"
                  name="password"
                  id="reset-password"
                  placeholder="Yeni şifreniz"
                  value={resetData.password}
                  onChange={handleResetDataChange}
                  disabled={resetLoading}
                  required
                  autoComplete="new-password"
                />
              </div>
              
              <div className="form-group">
                <label htmlFor="reset-confirm-password">Şifre Tekrar</label>
                <input
                  type="password"
                  name="confirmPassword"
                  id="reset-confirm-password"
                  placeholder="Yeni şifrenizi tekrar girin"
                  value={resetData.confirmPassword}
                  onChange={handleResetDataChange}
                  disabled={resetLoading}
                  required
                  autoComplete="new-password"
                />
              </div>
              
              <button type="submit" className="login-button" disabled={resetLoading}>
                {resetLoading ? 'Şifre Sıfırlanıyor...' : 'Şifremi Sıfırla'}
              </button>
              
              <button type="button" className="back-button" onClick={handleBackToLogin}>
                Giriş Ekranına Dön
              </button>
            </form>
          </>
        )}
      </div>
    </div>
  );
}

export default LoginPage;