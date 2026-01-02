import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom'; // Link'i buraya ekledik
import './RegisterPage.css';

function RegisterPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    userName: '',
    password: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(''); // Hata mesajını sıfırlıyoruz.
    try {
      const response = await fetch('https://localhost:9001/api/Account/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });
      // HTTP yanıt durum kodunu ve statusText'i konsola yazdırıyoruz
      console.log("Status Code:", response.status, response.statusText);
      // JSON parse etmeyi deniyoruz
      const responseText = await response.text();
console.log("Raw Response Text:", responseText);

// JSON ise parse etmeye çalış, değilse raw olarak ele al
let responseData;
try {
  responseData = JSON.parse(responseText);
} catch (e) {
  responseData = { message: responseText }; // JSON değilse string olarak al
}
      console.log("Complete Response Data:", responseData);
      if (response.ok) {
        navigate('/login', { state: { registrationSuccess: true } });
      } else {
        // Hata durumlarını kontrol ediyoruz
        let errorMessage = 'Kayıt başarısız.';
        // API'nin döndürdüğü farklı hata alanlarını kontrol ediyoruz
        if (responseData.errors) {
          // ASP.NET ModelState hatalarını işleme
          const errorMessages = [];
          for (const key in responseData.errors) {
            if (Array.isArray(responseData.errors[key])) {
              errorMessages.push(...responseData.errors[key]);
            }
          }
          if (errorMessages.length > 0) {
            errorMessage = errorMessages.join(', ');
          }
        } else if (responseData.Message) {
          errorMessage = responseData.Message;
        } else if (responseData.message) {
          errorMessage = responseData.message;
        } else if (responseData.title) {
          errorMessage = responseData.title;
          if (responseData.detail) {
            errorMessage += `: ${responseData.detail}`;
          }
        }
        console.log("Final Error Message:", errorMessage);
        setError(errorMessage);
      }
    } catch (error) {
      console.error("Fetch Error:", error);
      setError('Bir hata oluştu: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h1 className="login-title">Yeni Hesap Oluştur</h1>
        
        {error && <div className="error-message">{error}</div>}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="form-group">
            <label>Ad</label>
            <input
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>
          
          <div className="form-group">
            <label>Soyad</label>
            <input
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>
          
          <div className="form-group">
            <label>E-posta</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>

          <div className="form-group">
            <label>Kullanıcı Adı</label>
            <input
              name="userName"
              value={formData.userName}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>
          
          <div className="form-group">
            <label>Şifre</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              disabled={loading}
              required
            />
          </div>
          
          <div className="form-group">
            <label>Şifre Tekrar</label>
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              disabled={loading}
            />
          </div>
          
          <button type="submit" className="login-button" disabled={loading}>
            {loading ? 'İşleniyor...' : 'Kayıt Ol'}
          </button>
        </form>

        <div className="register-link">
          Zaten hesabınız var mı? <Link to="/login">Giriş Yapın</Link>
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;
