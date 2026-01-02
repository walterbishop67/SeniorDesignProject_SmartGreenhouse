import React from 'react';
import './Navbar.css';

const Navbar = () => {
  const handleLogoClick = (e) => {
    e.preventDefault();
    window.location.href = '/home'; // Sayfa yenileme ve ana dizine gitme
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <a href="/" onClick={handleLogoClick} className="logo-link">
          <div className="logo">SMART GREENHOUSE</div>
        </a>
      </div>
      <div className="navbar-center">
        <ul className="nav-links">
          <li><a href="/home">Home</a></li>
          <li><a href="/ai-tracker">AI Tracker</a></li>
          <li><a href="/calculate-price">Calculate Price</a></li>
          <li><a href="/price-estimate">Price Estimate</a></li>
          <li><a href="/current-price">Current Price</a></li>
          <li><a href="/settings">Settings</a></li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;