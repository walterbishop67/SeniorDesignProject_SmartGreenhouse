import React from 'react';
import './DataTable.css';

/**
 * Yeniden kullanılabilir veri tablosu bileşeni
 * 
 * @param {Object} props
 * @param {Array} props.columns - Tablo sütunlarını tanımlayan nesneler dizisi
 *   @param {string} props.columns[].key - Veri nesnesindeki alan adı
 *   @param {string} props.columns[].header - Sütun başlığı
 *   @param {function} [props.columns[].render] - Özel render fonksiyonu (isteğe bağlı)
 * @param {Array} props.data - Görüntülenecek veri nesneleri dizisi
 * @param {string} [props.className] - İsteğe bağlı ek CSS sınıfı
 * @param {boolean} [props.isLoading] - Yükleme durumu
 * @param {string} [props.loadingText] - Yükleme metni (varsayılan: "Loading data...")
 * @param {string} [props.emptyText] - Veri yoksa gösterilecek metin (varsayılan: "No data available.")
 * @param {function} [props.onRowClick] - Satıra tıklandığında çağrılacak işlev
 * @param {string} [props.keyField] - Benzersiz satır anahtarı için alan adı (varsayılan: "id")
 */
const DataTable = ({
  columns,
  data,
  className = '',
  isLoading = false,
  loadingText = 'Loading data...',
  emptyText = 'No data available.',
  onRowClick,
  keyField = 'id'
}) => {
  // Tablo satırı için click handler
  const handleRowClick = (item) => {
    if (onRowClick) {
      onRowClick(item);
    }
  };

  // Satır için key oluştur
  const getRowKey = (item, index) => {
    return item[keyField] ? item[keyField] : `row-${index}`;
  };

  // Hücre değerini oluştur
  const renderCell = (item, column) => {
    if (column.render) {
      return column.render(item);
    }
    
    const value = item[column.key];
    return value !== undefined ? value : '';
  };

  return (
    <div className={`data-table-container ${className}`}>
      {isLoading ? (
        <p className="data-table-loading">{loadingText}</p>
      ) : data.length === 0 ? (
        <p className="data-table-empty">{emptyText}</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              {columns.map((column, index) => (
                <th key={`header-${index}`} className={column.className || ''}>
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {data.map((item, rowIndex) => (
              <tr 
                key={getRowKey(item, rowIndex)}
                onClick={() => handleRowClick(item)}
                className={onRowClick ? 'clickable' : ''}
              >
                {columns.map((column, colIndex) => (
                  <td key={`cell-${rowIndex}-${colIndex}`} className={column.className || ''}>
                    {renderCell(item, column)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default DataTable;