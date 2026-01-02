import React, { useState, useEffect } from 'react';
import './Notifications.css';
import { FaSyncAlt } from 'react-icons/fa';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [expandedIds, setExpandedIds] = useState([]);

  useEffect(() => {
    fetchUserSupportMessages();
  }, []);

  const fetchUserSupportMessages = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const token = sessionStorage.getItem('token');
      if (!token) {
        throw new Error('Authentication token not found');
      }

      const response = await fetch('https://localhost:9001/api/v1/UserSupportMessage/user', {
        method: 'GET',
        headers: {
          'Accept': '*/*',
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error('Failed to fetch notifications');
      }

      const data = await response.json();

      // Mevcut bildirimler ile yeni gelen verileri karşılaştır
      if (JSON.stringify(notifications) !== JSON.stringify(data)) {
        setNotifications(data); // Sadece veriler farklıysa state'i güncelle
      }
    } catch (err) {
      console.error('Error fetching notifications:', err);
      setError('Failed to load notifications. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const toggleExpand = (id) => {
    setExpandedIds(prevIds => {
      if (prevIds.includes(id)) {
        return prevIds.filter(expandedId => expandedId !== id);
      } else {
        return [...prevIds, id];
      }
    });
  };

  const isExpanded = (id) => {
    return expandedIds.includes(id);
  };

  if (isLoading) {
    return <div className="notifications-loading">Loading notifications...</div>;
  }

  if (error) {
    return <div className="notifications-error">{error}</div>;
  }

  return (
    <div className="notifications-container">
      <div className="notifications-header">
        <h3>Your Support Messages</h3>
        <button className="refresh-button" onClick={fetchUserSupportMessages}>
          Refresh
        </button>
      </div>

      {notifications.length === 0 ? (
        <div className="no-notifications">
          <p>You don't have any support messages yet.</p>
        </div>
      ) : (
        <div className="notifications-list">
          {notifications.map((notification) => (
            <div 
              key={notification.id} 
              className={`notification-card ${notification.isResponsed ? 'responded' : 'pending'} ${isExpanded(notification.id) ? 'expanded' : ''}`}
              onClick={() => toggleExpand(notification.id)}
            >
              <div className="notification-header">
                <h4 className="notification-subject">{notification.subject}</h4>
                <span className={`notification-status ${notification.isResponsed ? 'status-responded' : 'status-pending'}`}>
                  {notification.isResponsed ? 'Cevaplandı' : 'Beklemede'}
                </span>
              </div>
              
              <div className="notification-content">
                <p>{notification.messageContent || "NULL değer gönderilmiş."}</p>
              </div>
              
              <div className="notification-footer">
                <div className="notification-dates">
                  <span className="notification-sent">
                    <strong>Sent:</strong> {formatDate(notification.sentAt)}
                  </span>
                </div>
                {notification.isResponsed && (
                  <div className="expand-indicator">
                    {isExpanded(notification.id) ? 'Daralt ▲' : 'Yanıtı Görüntüle ▼'}
                  </div>
                )}
              </div>
              
              {notification.isResponsed && isExpanded(notification.id) && (
                <div className="notification-response-container">
                  <div className="notification-response">
                    <h5>Yanıt:</h5>
                    <p>{notification.messageResponse}</p>
                    <span className="notification-responded-date">
                      <strong>Responded:</strong> {formatDate(notification.lastModified)}
                    </span>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Notifications;