import React, { useState, useRef, useEffect } from 'react';
import './GreenhouseSidebar.css';
import { FaChevronLeft, FaChevronRight, FaEllipsisV } from 'react-icons/fa';

const GreenhouseSidebar = ({ greenhouses, greenhouseObjects, selected, onSelect, onAddGreenhouse, onDeleteGreenhouse }) => {
  const [isOpen, setIsOpen] = useState(true);
  const [menuOpen, setMenuOpen] = useState(null);
  const menuRef = useRef(null);
  
  const toggleSidebar = () => setIsOpen(!isOpen);
  
  const toggleMenu = (name, event) => {
    event.stopPropagation();
    if (menuOpen === name) {
      setMenuOpen(null);
    } else {
      setMenuOpen(name);
    }
  };

  // Sera silme işlemi için yeni fonksiyon
  const handleDelete = (name, event) => {
    event.stopPropagation();
    
    // Seçilen seranın tam objesini bul
    const selectedGreenhouse = greenhouseObjects.find(gh => (gh.productName || gh.name) === name);
    
    if (selectedGreenhouse) {
      // Sera ID'sini al
      const greenhouseId = selectedGreenhouse.id || selectedGreenhouse.greenhouseId;
      
      if (greenhouseId) {
        // Silme fonksiyonunu çağır
        onDeleteGreenhouse(greenhouseId);
        // Menüyü kapat
        setMenuOpen(null);
      } else {
        console.error('Sera ID\'si bulunamadı:', selectedGreenhouse);
        alert('Sera ID\'si bulunamadı. Silme işlemi gerçekleştirilemedi.');
      }
    }
  };

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(null);
      }
    };
    
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  return (
    <div className={`sidebar ${isOpen ? 'open' : 'closed'}`}>
      <div className="toggle-button" onClick={toggleSidebar}>
        {isOpen ? <FaChevronLeft /> : <FaChevronRight />}
      </div>
      {isOpen && (
        <>
          <h3 className="sidebar-title">My Greenhouses</h3>
          <div className="greenhouse-list">
            {greenhouses && greenhouses.length > 0 ? (
              // If there are greenhouses, display them
              greenhouses.map((name) => (
                <div
                  key={name}
                  className={`greenhouse-item ${selected === name ? 'selected' : ''}`}
                  onClick={() => onSelect(name)}
                >
                  <span className="greenhouse-name">{name}</span>
                  <div className="menu-container">
                    <button 
                      className="menu-button" 
                      onClick={(e) => toggleMenu(name, e)}
                    >
                      <FaEllipsisV />
                    </button>
                    {menuOpen === name && (
                      <div className="dropdown-menu" ref={menuRef}>
                        <div className="menu-item" onClick={(e) => handleDelete(name, e)}>Delete</div>
                      </div>
                    )}
                  </div>
                </div>
              ))
            ) : (
              // If no greenhouses, display message
              <div className="empty-greenhouse-message">
                No greenhouses available
              </div>
            )}
          </div>
          <div className="add-greenhouse">
            <button onClick={onAddGreenhouse}>+ Add Greenhouse</button>
          </div>
        </>
      )}
    </div>
  );
};

export default GreenhouseSidebar;