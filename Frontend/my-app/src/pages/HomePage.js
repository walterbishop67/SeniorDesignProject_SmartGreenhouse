import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './HomePage.css';
import Navbar from '../components/user_components/Navbar';
import GreenhouseSidebar from '../components/user_components/GreenhouseSidebar';
import SensorCard from '../components/user_components/SensorCard';
import PlantCard from '../components/user_components/PlantCard';
import ConfirmModal from '../components/common_components/ConfirmModal';
import Select from 'react-select';

const HomePage = () => {
  const navigate = useNavigate();
  const [greenhouseList, setGreenhouseList] = useState([]);
  const [selectedGreenhouse, setSelectedGreenhouse] = useState(null);
  const [isAddGreenhouseModalOpen, setIsAddGreenhouseModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [espCards, setEspCards] = useState([]);
  const [currentSensorData, setCurrentSensorData] = useState({
    humidity: "--",
    temperature: "--",
    lastDataTime: "--",
    plants: []
  });
  const [confirmModalOpen, setConfirmModalOpen] = useState(false);
  const [pendingDeleteId, setPendingDeleteId] = useState(null);
  const [confirmAddModalOpen, setConfirmAddModalOpen] = useState(false);
  const [pendingAddData, setPendingAddData] = useState(null);
  const plantOptions = [
    { value: 'Domates', label: 'Domates' },
    { value: 'Biber', label: 'Biber' },
    { value: 'Patlıcan', label: 'Patlıcan' },
    { value: 'Salatalık', label: 'Salatalık' },
    { value: 'Kavun', label: 'Kavun' },
    { value: 'Fasulye', label: 'Fasulye' }
  ];
  const [selectedPlants, setSelectedPlants] = useState([]);
  
  // Bileşen yüklendiğinde sera verilerini getir
  useEffect(() => {
    // Token kontrolü yap
    const token = sessionStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }
    
    fetchGreenhouses();
    fetchEspCards();
  }, [navigate]);

  // Seçilen sera değiştiğinde onun ESP kart verilerini çek
  useEffect(() => {
    if (selectedGreenhouse) {
      fetchGreenhouseSensorData();
      const interval = setInterval(() => {
        fetchGreenhouseSensorData();
      }, 5000); // 5 saniyede bir güncelle
      return () => clearInterval(interval);
    }
  }, [selectedGreenhouse]);

  // API'den sera verilerini getir
  const fetchGreenhouses = async () => {
    setIsLoading(true);
    try {
      const token = sessionStorage.getItem('token');
      
      const response = await fetch('https://localhost:9001/api/v1/Greenhouse', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        if (response.status === 401) {
          // Token geçersiz veya süresi dolmuş, login sayfasına yönlendir
          sessionStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('Sera verileri alınamadı');
      }
      
      // Response içeriğini kontrol et
      const contentType = response.headers.get('content-type');
      if (!contentType || !contentType.includes('application/json')) {
        const text = await response.text();
        console.error('JSON olmayan yanıt alındı:', text);
        throw new Error('Sunucu JSON olmayan bir yanıt döndürdü');
      }
      
      const data = await response.json();
      setGreenhouseList(data);
      
      // İlk serayı varsayılan olarak seç
      if (data.length > 0) {
        setSelectedGreenhouse(data[0].productName || data[0].name);
      }
      
    } catch (err) {
      console.error('Sera verilerini getirme hatası:', err);
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  // API'den ESP kartlarını getir
  const fetchEspCards = async () => {
    try {
      const token = sessionStorage.getItem('token');
      
      const response = await fetch('https://localhost:9001/api/v1/ElectronicCard/electronic-card/get-available-electronic-card-by-user-id', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        if (response.status === 401) {
          // Token geçersiz veya süresi dolmuş
          sessionStorage.removeItem('token');
          navigate('/login');
          return;
        }
        throw new Error('ESP kartları alınamadı');
      }
      
      const data = await response.json();
      
      // Sadece "Available" durumundaki ESP kartlarını filtrele
      const availableCards = data.filter(card => card.status === "Available");
      setEspCards(availableCards);
      
    } catch (err) {
      console.error('ESP kartlarını getirme hatası:', err);
      // Hata durumunda UI'da kullanıcıya bildirilebilir
    }
  };

  // Seçilen seranın sensör verilerini çek
  const fetchGreenhouseSensorData = async () => {
    try {
      const token = sessionStorage.getItem('token');
      
      // Seçilen serayı bul
      const selectedGreenhouseObj = greenhouseList.find(
        gh => (gh.productName || gh.name) === selectedGreenhouse
      );
      
      if (!selectedGreenhouseObj) {
        console.error('Seçilen sera bulunamadı');
        return;
      }
      
      // İlgili ESP kartını bulmak için ElektronicCard API'sine istek gönder
      const response = await fetch('https://localhost:9001/api/v1/ElectronicCard/get-unavailable-electronic-card-by-user-id', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error('ESP kart verileri alınamadı');
      }
      
      const data = await response.json();
      
      // Seçilen seraya bağlı ESP kartını bul
      const linkedEspCard = data.find(card => 
        card.greenHouseId === selectedGreenhouseObj.id || 
        card.greenHouseId === selectedGreenhouseObj.greenhouseId
      );
      
      if (linkedEspCard) {
        // ESP kartı bulundu, sensör verilerini güncelle
        setCurrentSensorData({
          humidity: linkedEspCard.humidity || "--",
          temperature: linkedEspCard.temperature || "--",
          lastDataTime: linkedEspCard.lastDataTime || "--",
          plants: selectedGreenhouseObj.ProductType || ["Varsayılan Bitki"]
        });
      } else {
        // Bu seraya bağlı bir ESP kartı bulunamadı
        setCurrentSensorData({
          humidity: "--",
          temperature: "--",
          lastDataTime: "Bağlı ESP kartı bulunamadı",
          plants: selectedGreenhouseObj.productType || ["Varsayılan Bitki"]
        });
      }
      
    } catch (err) {
      console.error('Sensör verilerini getirme hatası:', err);
      setCurrentSensorData({
        humidity: "--",
        temperature: "--",
        lastDataTime: "Veri çekilirken hata oluştu",
        plants: ["Varsayılan Bitki"]
      });
    }
  };

  // Sera silme işlemi
  const deleteGreenhouse = (greenhouseId) => {
    if (!greenhouseId) {
      console.error('Sera ID\'si bulunamadı');
      return;
    }
    setPendingDeleteId(greenhouseId);
    setConfirmModalOpen(true);
  };

  const handleConfirmDelete = async () => {
    if (!pendingDeleteId) return;
    try {
      const token = sessionStorage.getItem('token');
      const response = await fetch(`https://localhost:9001/api/v1/Greenhouse/${pendingDeleteId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!response.ok) {
        console.error('Sera silinemedi');
        return;
      }
      setSelectedGreenhouse(null);
      window.location.reload();
    } catch (err) {
      console.error('Sera silme hatası:', err);
    } finally {
      setConfirmModalOpen(false);
      setPendingDeleteId(null);
    }
  };

  const handleCancelDelete = () => {
    setConfirmModalOpen(false);
    setPendingDeleteId(null);
  };

  const openAddGreenhouseModal = () => {
    setIsAddGreenhouseModalOpen(true);
  };

  const closeAddGreenhouseModal = () => {
    setIsAddGreenhouseModalOpen(false);
  };

  // Sera ekleme işlemi - API'ye göre güncellenmiş parametre isimleri
  const handleAddGreenhouse = (e) => {
    e.preventDefault();
    const greenhouseName = document.getElementById('greenhouse-name').value;
    const greenhouseArea = document.getElementById('greenhouse-area').value || '1';
    const espCardId = document.getElementById('esp-select').value;
    const greenhouseType = document.getElementById('greenhouse-type').value;
    if (!greenhouseName) {
      console.error('Lütfen bir sera adı girin');
      return;
    }
    setPendingAddData({ greenhouseName, greenhouseType, greenhouseArea, espCardId });
    closeAddGreenhouseModal();
    setConfirmAddModalOpen(true);
  };

  const handleConfirmAdd = async () => {
    if (!pendingAddData) return;
    try {
      const token = sessionStorage.getItem('token');
      const response = await fetch('https://localhost:9001/api/v1/Greenhouse', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          name: pendingAddData.greenhouseName,
          productType: pendingAddData.greenhouseType,
          area: pendingAddData.greenhouseArea,
          code: pendingAddData.espCardId
        })
      });
      if (!response.ok) {
        console.error('Sera eklenemedi');
        return;
      }
      closeAddGreenhouseModal();
      window.location.reload();
    } catch (err) {
      console.error('Sera ekleme hatası:', err);
    } finally {
      setConfirmAddModalOpen(false);
      setPendingAddData(null);
    }
  };

  const handleCancelAdd = () => {
    setConfirmAddModalOpen(false);
    setPendingAddData(null);
  };

  // Manuel sera seçimi işleyicisi - güncel sensör verileri için
  const handleGreenhouseSelect = (name) => {
    setSelectedGreenhouse(name);
  };

  // Render edilecek içerik için modal tanımı
  const renderAddGreenhouseModal = () => {
    if (!isAddGreenhouseModalOpen) return null;
    
    return (
      <div className="add-greenhouse-modal-overlay">
        <div className="add-greenhouse-modal">
          <h3>Yeni Sera Ekle</h3>
          <form onSubmit={handleAddGreenhouse}>
            <div>
              <label htmlFor="greenhouse-name">Sera Adı:</label>
              <input type="text" id="greenhouse-name" placeholder="Sera adını girin" required />
            </div>
            <div>
              <label htmlFor="greenhouse-type">Sera Tipi:</label>
              <input type="text" id="greenhouse-type" placeholder="Sera tipi" />
            </div>
            <div>
              <label htmlFor="greenhouse-area">Sera Alanı:</label>
              <input type="text" id="greenhouse-area" placeholder="Sera alanı" defaultValue="1" />
            </div>
            <div>
              <label htmlFor="esp-select">ESP Kart Seçin:</label>
              <select id="esp-select">
                {espCards.length > 0 ? (
                  espCards.map((card) => (
                    <option key={card.id} value={card.id}>
                      {card.productName} (ID: {card.id})
                    </option>
                  ))
                ) : (
                  <option value="">Kullanılabilir ESP kartı bulunamadı</option>
                )}
              </select>
            </div>
            <div className="modal-buttons">
              <button type="button" onClick={closeAddGreenhouseModal}>İptal</button>
              <button type="submit">Sera Ekle</button>
            </div>
          </form>
        </div>
      </div>
    );
  };

  // Yükleme durumunu göster
  if (isLoading && greenhouseList.length === 0) {
    return (
      <div className="home-page">
        <Navbar />
        <div className="loading-container">
          <p>Sera verileri yükleniyor...</p>
        </div>
      </div>
    );
  }

  // Hata durumunu göster
  if (error && greenhouseList.length === 0) {
    return (
      <div className="home-page">
        <Navbar />
        <div className="error-container">
          <p>Hata: {error}</p>
          <button onClick={fetchGreenhouses}>Tekrar Dene</button>
        </div>
      </div>
    );
  }

  const selectedGreenhouseObj = greenhouseList.find(
    gh => (gh.productName || gh.name) === selectedGreenhouse
  );

  return (
    <div className="home-page">
      <Navbar />
      <div className="main-content">
        <GreenhouseSidebar
          greenhouses={greenhouseList.map(gh => gh.productName || gh.name)}
          greenhouseObjects={greenhouseList}
          selected={selectedGreenhouse}
          onSelect={handleGreenhouseSelect}
          onAddGreenhouse={openAddGreenhouseModal}
          onDeleteGreenhouse={deleteGreenhouse}
        />
        
        <div className="dashboard">
          {greenhouseList.length === 0 ? (
            // Sera yoksa bu kısmı göster
            <div className="no-greenhouse-container">
              <div className="no-greenhouse">
                <h3>Sera Bulunamadı</h3>
                <p>Sistemde kayıtlı sera bulunmamaktadır. Seralara veri eklemek için önce bir sera oluşturun.</p>
                <button className="add-first-greenhouse-btn" onClick={openAddGreenhouseModal}>
                  Yeni Sera Ekle
                </button>
              </div>
            </div>
          ) : (
            // Sera varsa normal dashboard içeriğini göster
            <>
              <div className="sensor-cards">
                <SensorCard type="Humidity" value={`${currentSensorData.humidity}%`} />
                <SensorCard type="Temperature" value={`${currentSensorData.temperature}°C`} />
              </div>
              <div className="description">
                <p>Seçili sera: {selectedGreenhouse}</p>
                <p>
                  Seralar, bitkilerin iklim koşullarından bağımsız olarak yetiştirilebildiği, sıcaklık, nem ve ışık gibi çevresel faktörlerin kontrol altında tutulduğu modern tarım alanlarıdır. Akıllı sera sistemleri sayesinde üretim verimliliği artar, kaynak kullanımı optimize edilir ve yıl boyunca sağlıklı ürünler elde edilebilir.
                </p>
              </div>
              <div className="last-data-time">
                <p>Son veri zamanı: {currentSensorData.lastDataTime}</p>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Sera ekleme modalı - kod tekrarını önlemek için tek yerde tanımlandı */}
      {renderAddGreenhouseModal()}

      <ConfirmModal
        open={confirmModalOpen}
        title="Sera Sil"
        message="Bu serayı silmek istediğinizden emin misiniz?"
        onConfirm={handleConfirmDelete}
        onCancel={handleCancelDelete}
      />

      <ConfirmModal
        open={confirmAddModalOpen}
        title="Sera Ekle"
        message="Bu serayı eklemek istediğinizden emin misiniz?"
        onConfirm={handleConfirmAdd}
        onCancel={handleCancelAdd}
        confirmText="Evet, Ekle"
        cancelText="İptal"
      />
    </div>
  );
};

export default HomePage;