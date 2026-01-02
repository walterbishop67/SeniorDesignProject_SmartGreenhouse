import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requireAdmin = false }) => {
  // sessionStorage'dan token ve kullanıcı verilerini al
  const token = sessionStorage.getItem('token');
  const userStr = sessionStorage.getItem('user');
  
  // Token yoksa login sayfasına yönlendir
  if (!token) {
    console.log('Token bulunamadı, login sayfasına yönlendiriliyor');
    return <Navigate to="/login" replace />;
  }
  
  // Admin gerektiren sayfa kontrolü
  if (requireAdmin) {
    try {
      const user = JSON.parse(userStr);
      const isAdmin = user && user.roles && 
        (user.roles.includes('Admin') || user.roles.includes('SuperAdmin'));
      
      if (!isAdmin) {
        console.log('Admin yetkisi gerekli, erişim reddedildi');
        return <Navigate to="/home" replace />;
      }
    } catch (err) {
      console.error('Kullanıcı verisi işlenirken hata oluştu:', err);
      sessionStorage.removeItem('token');
      sessionStorage.removeItem('user');
      return <Navigate to="/login" replace />;
    }
  }
  
  // Gerekli koşullar sağlanıyorsa sayfayı görüntüle
  return children;
};

export default ProtectedRoute;