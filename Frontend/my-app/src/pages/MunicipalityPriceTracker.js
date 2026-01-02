import React, { useState, useEffect } from 'react';
import Navbar from '../components/user_components/Navbar';
import DataTable from '../components/common_components/DataTable';
import Pagination from '../components/common_components/Pagination';
import './MunicipalityPriceTracker.css';

const MunicipalityPriceTracker = () => {
  const [selectedMunicipality, setSelectedMunicipality] = useState(null);
  const [priceList, setPriceList] = useState([]);
  const [municipalities, setMunicipalities] = useState([]);
  const [isLoadingMunicipalities, setIsLoadingMunicipalities] = useState(true);
  const [isLoadingPrices, setIsLoadingPrices] = useState(false);
  const [municipalityError, setMunicipalityError] = useState(null);
  const [priceError, setPriceError] = useState(null);
  const [token, setToken] = useState(null);
  
  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalItems, setTotalItems] = useState(0);

  // API URL
  const API_BASE_URL = 'https://localhost:9001';

  // Tablo sütunları tanımı
  const priceColumns = [
    { key: 'item', header: 'Product Name' },
    { 
      key: 'price', 
      header: 'Price', 
      className: 'column-price',
      render: (item) => `${item.price} TL` 
    },
    { key: 'unit', header: 'Unit', className: 'text-center' }
  ];

  // Get token from sessionStorage
  useEffect(() => {
    const storedToken = sessionStorage.getItem('token');
    if (storedToken) {
      setToken(storedToken);
    } else {
      console.error('No token found in session storage');
    }
  }, []);

  // Fetch municipalities when token is available
  useEffect(() => {
    if (token) {
      fetchMunicipalities();
    }
  }, [token]);

  const fetchMunicipalities = async () => {
    setIsLoadingMunicipalities(true);
    setMunicipalityError(null);
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
      
      // Handle different response formats
      const municipalitiesList = Array.isArray(data) ? data : 
                                (data.data ? data.data : 
                                (data.municipalities ? data.municipalities : []));
      
      setMunicipalities(municipalitiesList);
      setIsLoadingMunicipalities(false);
    } catch (err) {
      console.error('Error fetching municipalities:', err);
      setMunicipalityError(`Failed to load municipalities: ${err.message}`);
      setIsLoadingMunicipalities(false);
      
      // Show sample data in case of error
      setMunicipalities([
        { id: 1, name: 'Test Municipality 1' },
        { id: 2, name: 'Test Municipality 2' },
        { id: 3, name: 'Test Municipality 3' }
      ]);
    }
  };

  // Fetch agricultural product prices when municipality or pagination changes
  useEffect(() => {
    if (selectedMunicipality && token) {
      fetchAgriProductPrices(selectedMunicipality, currentPage, pageSize);
    }
  }, [selectedMunicipality, token, currentPage, pageSize]);

  const fetchAgriProductPrices = async (municipalityId, page, size) => {
    setIsLoadingPrices(true);
    setPriceError(null);
    try {
      console.log(`Fetching agri-product prices for municipality ID: ${municipalityId}, page: ${page}, size: ${size}`);
      
      // Using the new API endpoint according to the screenshot
      const response = await fetch(`${API_BASE_URL}/api/v1/AgriProductsPrices/prices?municipalityId=${municipalityId}&pageNumber=${page}&pageSize=${size}&version=1`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Price data received:', data);
      
      // Extract pagination information
      if (data.pageNumber) setCurrentPage(data.pageNumber);
      if (data.totalPages) setTotalPages(data.totalPages);
      if (data.totalCount) setTotalItems(data.totalCount);
      
      // Extract the product data from the response
      let productData = [];
      if (data.data && Array.isArray(data.data)) {
        productData = data.data.map(item => ({
          id: item.id,
          item: item.agriProductName,
          price: item.agriProductPrice,
          unit: item.unit // Include the unit field
        }));
      }
      
      setPriceList(productData);
      setIsLoadingPrices(false);
    } catch (err) {
      console.error('Error fetching agricultural price data:', err);
      setPriceError(`Failed to load price data: ${err.message}`);
      setIsLoadingPrices(false);
      
      // Show sample data in case of error
      setPriceList([
        { id: 1, item: 'Tomatoes', price: 25.50, unit: 'kg' },
        { id: 2, item: 'Potatoes', price: 12.75, unit: 'kg' },
        { id: 3, item: 'Cucumbers', price: 38.20, unit: 'kg' }
      ]);
    }
  };

  const handleMunicipalitySelect = (municipality) => {
    console.log('Selected municipality:', municipality);
    setSelectedMunicipality(municipality.id);
    setCurrentPage(1); // Reset to first page when selecting a new municipality
  };

  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
  };

  return (
    <div className="price-tracker-page">
      <Navbar />
      <div className="price-tracker-content">
        <div className="municipality-sidebar">
          <div className="sidebar-header">Choose Municipality</div>
          <div className="municipality-list">
            {!token ? (
              <p className="error-text">No authentication token found. Please log in again.</p>
            ) : isLoadingMunicipalities ? (
              <p className="loading-text">Loading municipalities...</p>
            ) : municipalityError ? (
              <div>
                <p className="error-text">{municipalityError}</p>
                {municipalities.length > 0 && (
                  <p className="note-text">Showing test data instead</p>
                )}
              </div>
            ) : municipalities.length > 0 ? (
              municipalities.map(municipality => (
                <div
                  key={municipality.id || municipality._id}
                  className={`municipality-item ${selectedMunicipality === (municipality.id || municipality._id) ? 'selected' : ''}`}
                  onClick={() => handleMunicipalitySelect(municipality)}
                >
                  {municipality.name || municipality.municipalityName || municipality.title || "Unnamed Municipality"}
                </div>
              ))
            ) : (
              <p className="no-data-text">No municipalities available.</p>
            )}
          </div>
        </div>
        
        <div className="price-content">
          <div className="price-list-container">
            <h3 className="price-list-title">Agricultural Products Price List</h3>
            
            {!token ? (
              <p className="error-text">No authentication token found. Please log in again.</p>
            ) : priceError ? (
              <div>
                <p className="error-text">{priceError}</p>
                {priceList.length > 0 && (
                  <p className="note-text">Showing test data instead</p>
                )}
              </div>
            ) : !selectedMunicipality ? (
              <p className="no-data-text">Please select a municipality to view prices.</p>
            ) : (
              <div className="price-data-container">
                <DataTable 
                  columns={priceColumns}
                  data={priceList}
                  isLoading={isLoadingPrices}
                  loadingText="Loading price data..."
                  emptyText="No price data available for this municipality."
                  keyField="id"
                />
                
                {priceList.length > 0 && (
                  <Pagination 
                    currentPage={currentPage}
                    totalPages={totalPages}
                    totalItems={totalItems}
                    onPageChange={handlePageChange}
                  />
                )}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MunicipalityPriceTracker;