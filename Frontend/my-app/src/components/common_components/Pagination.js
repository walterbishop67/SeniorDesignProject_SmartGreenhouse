import React from 'react';
import './Pagination.css';

/**
 * Yeniden kullanılabilir sayfalama bileşeni
 * 
 * @param {Object} props
 * @param {number} props.currentPage - Aktif sayfa numarası
 * @param {number} props.totalPages - Toplam sayfa sayısı
 * @param {number} [props.totalItems] - Toplam öğe sayısı (isteğe bağlı)
 * @param {function} props.onPageChange - Sayfa değişimi için callback function
 * @param {boolean} [props.showPageSizeSelector] - Sayfa boyutu seçici göster/gizle
 * @param {number} [props.pageSize] - Mevcut sayfa boyutu
 * @param {function} [props.onPageSizeChange] - Sayfa boyutu değişimi için callback
 * @param {Array} [props.pageSizeOptions] - Sayfa boyutu seçenekleri [5, 10, 20, 50]
 * @param {string} [props.className] - İsteğe bağlı ek CSS sınıfı
 * @param {string} [props.previousLabel] - Önceki buton etiketi (varsayılan: "Previous")
 * @param {string} [props.nextLabel] - Sonraki buton etiketi (varsayılan: "Next")
 */
const Pagination = ({
  currentPage,
  totalPages,
  totalItems,
  onPageChange,
  showPageSizeSelector = false,
  pageSize,
  onPageSizeChange,
  pageSizeOptions = [5, 10, 20, 50],
  className = '',
  previousLabel = 'Previous',
  nextLabel = 'Next'
}) => {
  // Sayfa değişimi işleyici
  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      onPageChange(newPage);
    }
  };

  // Sayfa boyutu değişimi işleyici
  const handlePageSizeChange = (e) => {
    if (onPageSizeChange) {
      onPageSizeChange(Number(e.target.value));
    }
  };

  return (
    <div className={`pagination-controls ${className}`}>
      <button 
        className="pagination-button"
        onClick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage === 1}
      >
        {previousLabel}
      </button>
      
      <span className="pagination-info">
        Page {currentPage} of {totalPages}
        {totalItems !== undefined && ` (${totalItems} items)`}
      </span>
      
      <button 
        className="pagination-button"
        onClick={() => handlePageChange(currentPage + 1)}
        disabled={currentPage === totalPages}
      >
        {nextLabel}
      </button>
      
      {showPageSizeSelector && pageSize && onPageSizeChange && (
        <div className="page-size-selector">
          <label htmlFor="pageSize">Items per page:</label>
          <select 
            id="pageSize" 
            value={pageSize} 
            onChange={handlePageSizeChange}
          >
            {pageSizeOptions.map(size => (
              <option key={size} value={size}>{size}</option>
            ))}
          </select>
        </div>
      )}
    </div>
  );
};

export default Pagination;