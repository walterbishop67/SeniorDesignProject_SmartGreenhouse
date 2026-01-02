import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import HomePage from './pages/HomePage';
import AdminPage from './pages/AdminPage';
import SettingsPage from './pages/SettingsPage';
import AdminProfile from './pages/AdminProfile';
import MunicipalityPriceTracker from './pages/MunicipalityPriceTracker';
import CalculatePricePage from './pages/CalculatePricePage';
import ProtectedRoute from './components/ProtectedRoute';
import UsersSection from './components/admin_components/UsersSection';
import DevicesSection from './components/admin_components/DevicesSection';
import SupportSection from './components/admin_components/SupportSection';

function Dashboard() {
  return <div style={{padding: 24}}><h2>Dashboard</h2></div>;
}

function About() {
  return <div style={{padding: 24}}><h2>About System</h2></div>;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        
        {/* Normal kullanıcı sayfası */}
        <Route path="/home" element={
          <ProtectedRoute>
            <HomePage />
          </ProtectedRoute>
        } />
        
        {/* Settings sayfası */}
        <Route path="/settings" element={
          <ProtectedRoute>
            <SettingsPage />
          </ProtectedRoute>
        } />
        
        {/* Admin sayfası - admin rolü gerektirir */}
        <Route path="/admin" element={
          <ProtectedRoute requireAdmin={true}>
            <AdminPage />
          </ProtectedRoute>
        }>
          <Route index element={<Dashboard />} />
          <Route path="users" element={<UsersSection />} />
          <Route path="devices" element={<DevicesSection />} />
          <Route path="supports" element={<SupportSection />} />
          <Route path="about" element={<About />} />
          <Route path="profile" element={<AdminProfile />} />
        </Route>
        
        {/* AI Tracker sayfası */}
        <Route path="/ai-tracker" element={
          <ProtectedRoute>
            <div>AI Tracker Page - Coming Soon</div>
          </ProtectedRoute>
        } />
        
        {/* Calculate Price sayfası */}
        <Route path="/calculate-price" element={
          <ProtectedRoute>
            <CalculatePricePage />
          </ProtectedRoute>
        } />
        
        {/* Price Estimate sayfası */}
        <Route path="/price-estimate" element={
          <ProtectedRoute>
            <div>Price Estimate Page - Coming Soon</div>
          </ProtectedRoute>
        } />
        
        {/* Current Price sayfası - Belediye fiyat takip sayfası */}
        <Route path="/current-price" element={
          <ProtectedRoute>
            <MunicipalityPriceTracker />
          </ProtectedRoute>
        } />
        
        {/* Uygulama ilk açıldığında login sayfasına yönlendir */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        
        {/* Tanımsız yollar için login'e yönlendir */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;