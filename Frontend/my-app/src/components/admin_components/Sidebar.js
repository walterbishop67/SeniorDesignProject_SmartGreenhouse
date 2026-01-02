import React, { useState } from 'react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';
import './Sidebar.css';
import { useLocation } from 'react-router-dom';

function Sidebar({ menuItems, onMenuChange, title = 'Admin Panel' }) {
  const [open, setOpen] = useState(true);
  const location = useLocation();

  // Aktif menüyü path'e göre bul
  const activeMenuId = menuItems.find(item =>
    item.path === '/admin'
      ? location.pathname === '/admin' || location.pathname === '/admin/'
      : location.pathname.startsWith(item.path)
  )?.id;

  return (
    <aside className={`sidebar${open ? ' open' : ' closed'}`}>
      <div className="sidebar-header">
        {open && <span className="sidebar-title">{title}</span>}
        <div className="sidebar-toggle" onClick={() => setOpen(!open)}>
          {open ? <FaChevronLeft /> : <FaChevronRight />}
        </div>
      </div>
      {open && (
        <nav className="sidebar-nav">
          <ul>
            {menuItems.map((item) => (
              <li key={item.id} className={activeMenuId === item.id ? 'active' : ''}>
                <button onClick={() => onMenuChange(item.id)}>{item.label}</button>
              </li>
            ))}
          </ul>
        </nav>
      )}
    </aside>
  );
}

export default Sidebar; 