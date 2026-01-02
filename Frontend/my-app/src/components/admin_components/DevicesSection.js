import React, { useState, useEffect } from 'react';
import DataTable from '../common_components/DataTable'; // Var olan tablo bileşenini import ediyoruz
import Pagination from '../common_components/Pagination'; // Var olan sayfalama bileşenini import ediyoruz
import './DevicesSection.css';

function DevicesSection() {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(10);
  const [totalItems, setTotalItems] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [selectedDevice, setSelectedDevice] = useState(null);
  const [activeFilter, setActiveFilter] = useState('all'); // Yeni: Aktif filtre durumu

  // API endpoint'lerini tanımla
  const API_ENDPOINTS = {
    all: 'get-all-electronic-cards',
    available: 'available',
    unavailable: 'unavailable',
    withError: 'with-error'
  };

  useEffect(() => {
    fetchDevices(activeFilter);
  }, [currentPage, pageSize, activeFilter]); // currentPage değiştiğinde API çağrısı yapılacak

  // Filtreye göre API endpoint'ini değiştir
  const fetchDevices = async (filter = 'all') => {
    setLoading(true);
    try {
      const endpoint = API_ENDPOINTS[filter];
      
      // URL parametrelerini oluştur
      const url = new URL(`https://localhost:9001/api/v1/AdminPanel/electronic-card/${endpoint}`);
      url.searchParams.append('pageNumber', currentPage);
      url.searchParams.append('pageSize', pageSize);
      
      // Oluşturulan URL'yi kullan
      const response = await fetch(url.toString(), {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });
      
      if (!response.ok) {
        throw new Error(`Failed to fetch devices: ${response.status}`);
      }
      
      const data = await response.json();
      console.log("API Response:", data);
      
      // API yanıtını kontrol et ve array'e dönüştür
      let devicesArray = [];
      let totalCount = 0;

      // Backend'in sayfalama meta verilerini kontrol et
      if (data.totalCount !== undefined) {
        totalCount = data.totalCount;
      } else if (data.total !== undefined) {
        totalCount = data.total;
      } else if (data.count !== undefined) {
        totalCount = data.count;
      }
      
      // API yanıtının yapısını kontrol et
      if (Array.isArray(data)) {
        devicesArray = data;
      } else if (data && typeof data === 'object') {
        // Eğer data bir nesne ise ve içinde bir devices array'i veya benzeri varsa
        if (data.items && Array.isArray(data.items)) {
          devicesArray = data.items;
        } else if (data.devices && Array.isArray(data.devices)) {
          devicesArray = data.devices;
        } else if (data.data && Array.isArray(data.data)) {
          devicesArray = data.data;
        } else if (data.results && Array.isArray(data.results)) {
          devicesArray = data.results;
        } else {
          // Hiçbir dizi bulunamadıysa, nesneyi dizi olarak çevirebiliriz
          console.warn("API response is not an array, trying to convert to array:", data);
          // İlk key'i log'la
          const firstKey = Object.keys(data)[0];
          console.log("First key in data:", firstKey);
          
          if (data[firstKey] && Array.isArray(data[firstKey])) {
            devicesArray = data[firstKey];
          } else {
            // Son çare olarak boş bir dizi kullan
            devicesArray = [];
          }
        }
      }
      
      console.log("Processed devices array:", devicesArray);
      setDevices(devicesArray);
      setTotalItems(totalCount);
      setTotalPages(Math.ceil(totalCount / pageSize) || 1);
      setError(null);
    } catch (error) {
      console.error('Device data fetch error:', error);
      setError('Failed to load devices. Please try again later.');
      setDevices([]);
      setTotalItems(0);
      setTotalPages(1);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (filter) => {
    setActiveFilter(filter);
    setCurrentPage(1); // Filtre değiştiğinde ilk sayfaya dön
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage); // Bu değişiklik useEffect ile fetchDevices'i tetikleyecek
    }
  };

  const handleDeviceClick = (device) => {
    setSelectedDevice(device);
  };

  const handleAddDevice = () => {
    // Cihaz ekleme işlevselliği
    console.log('Add device clicked');
  };

  // Tablo sütun tanımları - sizin DataTable yapınıza göre uyarlandı
  const columns = [
    { 
      key: 'id', 
      header: 'Device ID',
      render: (item) => item.id || 'N/A' 
    },
    { 
      key: 'status', 
      header: 'Status',
      render: (item) => (
        <span className={`status-badge ${(item.status || '').toLowerCase()}`}>
          {item.status || 'Unknown'}
        </span>
      )
    },
    { 
      key: 'error', 
      header: 'Error',
      render: (item) => item.error || 'None' 
    },
    { 
      key: 'userId', 
      header: 'User ID',
      render: (item) => item.userId || 'N/A' 
    },
    { 
      key: 'createdBy', 
      header: 'Created By',
      render: (item) => item.createdBy || 'N/A' 
    }
  ];

  return (
    <div className="devices-management">
      <h2>Devices Management</h2>
      
      {selectedDevice ? (
        <div className="device-detail-panel">
          <h3>Device Details</h3>
          <p><strong>Device ID:</strong> {selectedDevice.id || 'N/A'}</p>
          <p><strong>Status:</strong> {selectedDevice.status || 'N/A'}</p>
          <p><strong>Error:</strong> {selectedDevice.error || 'None'}</p>
          <p><strong>User ID:</strong> {selectedDevice.userId || 'N/A'}</p>
          <p><strong>Created By:</strong> {selectedDevice.createdBy || 'N/A'}</p>
          <p><strong>Created:</strong> {selectedDevice.created || 'N/A'}</p>
          
          {selectedDevice.cardId && (
            <p><strong>Card ID:</strong> {selectedDevice.cardId}</p>
          )}
          
          {selectedDevice.temperature && (
            <p><strong>Temperature:</strong> {selectedDevice.temperature}</p>
          )}
          
          {selectedDevice.humidity && (
            <p><strong>Humidity:</strong> {selectedDevice.humidity}</p>
          )}
          
          <button className="back-button" onClick={() => setSelectedDevice(null)}>
            Back to Devices List
          </button>
        </div>
      ) : (
        <>
          <div className="list-header">
            <div className="filter-controls">
              <button 
                className={`filter-button ${activeFilter === 'all' ? 'active' : ''}`}
                onClick={() => handleFilterChange('all')}
              >
                All Devices
              </button>
              <button 
                className={`filter-button ${activeFilter === 'available' ? 'active' : ''}`}
                onClick={() => handleFilterChange('available')}
              >
                Available
              </button>
              <button 
                className={`filter-button ${activeFilter === 'unavailable' ? 'active' : ''}`}
                onClick={() => handleFilterChange('unavailable')}
              >
                Unavailable
              </button>
              <button 
                className={`filter-button ${activeFilter === 'withError' ? 'active' : ''}`}
                onClick={() => handleFilterChange('withError')}
              >
                With Error
              </button>
            </div>
            <div className="list-controls">
              <button className="refresh-button" onClick={fetchDevices}>
                Refresh
              </button>
            </div>
          </div>
          
          {error && <div className="error-message">{error}</div>}
          
          {/* Var olan DataTable bileşeninizi kullan */}
          <DataTable
            columns={columns}
            data={devices}
            isLoading={loading}
            loadingText="Loading devices..."
            emptyText="No devices found"
            onRowClick={handleDeviceClick}
            keyField="id"
          />
          
          {totalItems > 0 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              totalItems={totalItems}
              onPageChange={handlePageChange}
              previousLabel="Previous"
              nextLabel="Next"
            />
          )}
        </>
      )}
    </div>
  );
}

export default DevicesSection;