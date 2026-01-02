import React, { useState, useEffect } from 'react';
import Navbar from '../components/user_components/Navbar';
import './CalculatePricePage.css';

const CalculatePricePage = () => {
  const [municipalities, setMunicipalities] = useState([]);
  const [selectedMunicipality, setSelectedMunicipality] = useState(null);
  const [plants, setPlants] = useState([]);
  const [selectedPlants, setSelectedPlants] = useState([]);
  // Yeni state: Her ürün için miktar saklamak için
  const [quantities, setQuantities] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [token, setToken] = useState(null);

  // API URL
  const API_BASE_URL = 'https://localhost:9001';

  // Token alınması
  useEffect(() => {
    const storedToken = sessionStorage.getItem('token');
    if (storedToken) {
      setToken(storedToken);
    } else {
      console.error('No token found in session storage');
    }
  }, []);

  // Token hazır olduğunda belediyeleri çek
  useEffect(() => {
    if (token) {
      fetchMunicipalities();
    }
  }, [token]);

  // Seçilen belediyeye göre tarımsal ürünleri çek
  useEffect(() => {
    if (selectedMunicipality && token) {
      fetchAgriProducts(selectedMunicipality);
    }
  }, [selectedMunicipality, token]);

  const fetchMunicipalities = async () => {
    setIsLoading(true);
    try {
      console.log('Fetching municipalities from API...');
      
      const response = await fetch(`${API_BASE_URL}/api/Municipality`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      
      console.log('API Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Municipalities data received:', data);
      
      // Farklı API yanıt formatları için kontrol
      const municipalitiesList = Array.isArray(data) ? data : 
                                (data.data ? data.data : 
                                (data.municipalities ? data.municipalities : []));
      
      setMunicipalities(municipalitiesList);
      
      if (municipalitiesList.length > 0) {
        setSelectedMunicipality(municipalitiesList[0].id || municipalitiesList[0]._id);
      }
      
      setIsLoading(false);
    } catch (err) {
      console.error('Error fetching municipalities:', err);
      setError(`Failed to load municipalities: ${err.message}`);
      setIsLoading(false);
      
      // Hata durumunda örnek veri göster
      setMunicipalities([
        { id: 1, municipalityName: 'Test Municipality 1' },
        { id: 2, municipalityName: 'Test Municipality 2' },
        { id: 3, municipalityName: 'Test Municipality 3' }
      ]);
    }
  };

  const fetchAgriProducts = async (municipalityId) => {
    setIsLoading(true);
    try {
      console.log(`Fetching agri-product prices for municipality ID: ${municipalityId}`);
      
      const response = await fetch(`${API_BASE_URL}/api/v1/AgriProductsPrices/prices?municipalityId=${municipalityId}&pageNumber=1&pageSize=100&version=1`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      
      console.log('API Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Price data received:', data);
      
      // API'dan gelen veriyi bizim plant formatımıza dönüştür
      let formattedPlants = [];
      if (data.data && Array.isArray(data.data)) {
        formattedPlants = data.data.map(product => ({
          id: product.id,
          name: product.agriProductName,
          basePrice: product.agriProductPrice,
          unit: product.unit || 'kg' // Birim bilgisi eklendi
        }));
      }
      
      setPlants(formattedPlants);
      setSelectedPlants([]);
      // Belediye değiştiğinde miktarları sıfırla
      setQuantities({});
      setIsLoading(false);
    } catch (err) {
      console.error('Error fetching agricultural products:', err);
      setError(`Failed to load agricultural products: ${err.message}`);
      setIsLoading(false);
      
      // Hata durumunda örnek veri göster
      setPlants([
        { id: 1, name: 'Tomatoes', basePrice: 15.50, unit: 'kg' },
        { id: 2, name: 'Cucumbers', basePrice: 12.75, unit: 'kg' },
        { id: 3, name: 'Pepper', basePrice: 18.20, unit: 'kg' }
      ]);
    }
  };

  const handleMunicipalitySelect = (municipalityId) => {
    console.log('Selected municipality ID:', municipalityId);
    setSelectedMunicipality(municipalityId);
    setSelectedPlants([]); // Belediye değiştiğinde seçili bitkileri sıfırla
    setQuantities({}); // Belediye değiştiğinde miktarları sıfırla
  };

  const handlePlantSelect = (plantId) => {
    setSelectedPlants(prevSelected => {
      // Eğer bitki zaten seçiliyse, listeden çıkar
      if (prevSelected.includes(plantId)) {
        // Seçimden kaldırılınca, miktarı ve birimi de kaldır
        const newQuantities = {...quantities};
        const newUnits = {...selectedUnits};
        delete newQuantities[plantId];
        delete newUnits[plantId];
        setQuantities(newQuantities);
        setSelectedUnits(newUnits);
        return prevSelected.filter(id => id !== plantId);
      } 
      // Değilse listeye ekle ve varsayılan miktar 1 ve birim 'kg' olarak ayarla
      else {
        setQuantities(prev => ({...prev, [plantId]: 1}));
        setSelectedUnits(prev => ({...prev, [plantId]: 'kg'}));
        return [...prevSelected, plantId];
      }
    });
  };

  // Yeni state: Birim seçimi için
  const [selectedUnits, setSelectedUnits] = useState({});
  
  // Birim dönüşüm faktörleri
  const unitConversionFactors = {
    'kg': 1,
    'ton': 1000,
    'gr': 0.001
  };
  
  // Miktar değişikliğini işle
  const handleQuantityChange = (plantId, value) => {
    // Negatif değer olmamasını sağla
    const quantity = Math.max(0.1, parseFloat(value) || 0);
    setQuantities(prev => ({...prev, [plantId]: quantity}));
  };
  
  // Birim değişikliğini işle
  const handleUnitChange = (plantId, unit) => {
    setSelectedUnits(prev => ({...prev, [plantId]: unit}));
  };

  const calculatePlantPrice = (plantId) => {
    const plant = plants.find(p => p.id === plantId);
    if (!plant) return 0;
    
    // Seçilen birimi al, yoksa kg varsayılan olsun
    const selectedUnit = selectedUnits[plantId] || 'kg';
    const conversionFactor = unitConversionFactors[selectedUnit];
    
    // Miktar ile çarp ve birim dönüşümünü uygula
    const quantity = quantities[plantId] || 0;
    return (plant.basePrice * quantity * conversionFactor).toFixed(2);
  };

  const calculateTotalPrice = () => {
    if (selectedPlants.length === 0) return '0.00';
    
    const total = selectedPlants.reduce((sum, plantId) => {
      return sum + parseFloat(calculatePlantPrice(plantId));
    }, 0);
    
    return total.toFixed(2);
  };

  return (
    <div className="calculate-price-page">
      <Navbar />
      <div className="calculate-price-content">
        <div className="greenhouse-sidebar">
          <div className="sidebar-header">Belediyeler</div>
          <div className="greenhouse-list">
            {!token ? (
              <p className="error-text">Token bulunamadı. Lütfen tekrar giriş yapın.</p>
            ) : isLoading && municipalities.length === 0 ? (
              <p className="loading-text">Belediyeler yükleniyor...</p>
            ) : error && municipalities.length === 0 ? (
              <div>
                <p className="error-text">{error}</p>
              </div>
            ) : municipalities.length > 0 ? (
              municipalities.map(municipality => (
                <div 
                  key={municipality.id || municipality._id} 
                  className={`greenhouse-item ${selectedMunicipality === (municipality.id || municipality._id) ? 'selected' : ''}`}
                  onClick={() => handleMunicipalitySelect(municipality.id || municipality._id)}
                >
                  {municipality.municipalityName || municipality.name || municipality.title || "İsimsiz Belediye"}
                </div>
              ))
            ) : (
              <p className="no-data-text">Belediye bulunamadı.</p>
            )}
          </div>
        </div>
        
        <div className="calculation-area">
          {!token ? (
            <div className="error-message">Token bulunamadı. Lütfen tekrar giriş yapın.</div>
          ) : isLoading && plants.length === 0 ? (
            <div className="loading-message">Veriler yükleniyor...</div>
          ) : error && plants.length === 0 ? (
            <div className="error-message">{error}</div>
          ) : !selectedMunicipality ? (
            <div className="no-data-text">Lütfen fiyatları görmek için bir belediye seçin.</div>
          ) : (
            <>
              <div className="plant-selection-section">
                <h3>Lütfen Ürünleri Seçin...</h3>
                <div className="plant-options">
                  {plants.map(plant => (
                    <div 
                      key={plant.id} 
                      className={`plant-option ${selectedPlants.includes(plant.id) ? 'selected' : ''}`}
                      onClick={() => handlePlantSelect(plant.id)}
                    >
                      <div className="plant-name">{plant.name}</div>
                    </div>
                  ))}
                </div>
              </div>
              
              {selectedPlants.length > 0 && (
                <div className="price-calculation-section">
                  <h3>Fiyat Hesaplama</h3>
                  
                  {selectedPlants.map(plantId => {
                    const plant = plants.find(p => p.id === plantId);
                    return (
                      <div key={plantId} className="plant-calculation-item">
                        <div className="plant-details">
                          <div className="plant-image-placeholder">
                            <div className="plant-image-x"></div>
                          </div>
                          <div className="plant-info">
                            <div className="plant-name">
                              <strong>Ürün Adı:</strong> {plant?.name}
                            </div>
                            <div className="plant-price">
                              <strong>Birim Fiyatı:</strong> {plant?.basePrice.toFixed(2)} TL/{plant?.unit || 'kg'}
                            </div>
                            <div className="plant-quantity">
                              <strong>Miktar:</strong>
                              <div className="quantity-unit-container">
                                <input 
                                  type="number" 
                                  min="0.1" 
                                  step="0.1"
                                  value={quantities[plantId] || 0}
                                  onChange={(e) => handleQuantityChange(plantId, e.target.value)}
                                  className="quantity-input"
                                />
                                <select 
                                  value={selectedUnits[plantId] || 'kg'} 
                                  onChange={(e) => handleUnitChange(plantId, e.target.value)}
                                  className="unit-select"
                                >
                                  <option value="gr">gr</option>
                                  <option value="kg">kg</option>
                                  <option value="ton">ton</option>
                                </select>
                              </div>
                            </div>
                          </div>
                        </div>
                        
                        <div className="item-total-price">
                          <strong>Ürün Toplam Fiyatı:</strong> <span>{calculatePlantPrice(plantId)} TL</span>
                        </div>
                      </div>
                    );
                  })}
                  
                  <div className="summary-section">
                    <div className="total-price">
                      <strong>Toplam Fiyat:</strong> <span>{calculateTotalPrice()} TL</span>
                    </div>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default CalculatePricePage;