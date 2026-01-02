import React, { useState, useEffect } from 'react';
import './SupportSection.css';
import DataTable from '../common_components/DataTable';
import Pagination from '../common_components/Pagination';
import TicketDetail from './TicketDetail';
import ConfirmModal from '../common_components/ConfirmModal';

function SupportSection() {
  const [supportTickets, setSupportTickets] = useState([]);
  const [allTickets, setAllTickets] = useState([]); // Store all fetched tickets
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [responseText, setResponseText] = useState('');
  const [responding, setResponding] = useState(false);
  const [onlyUnopened, setOnlyUnopened] = useState(false); // false = all tickets, true = only unopened tickets
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  
  useEffect(() => {
    fetchSupportTickets(currentPage);
  }, [currentPage, onlyUnopened]);

  const fetchSupportTickets = async (page) => {
    try {
      setLoading(true);
      const response = await fetch(
        `https://localhost:9001/api/v1/UserSupportMessage/all?PageNumber=${page}&PageSize=${pageSize}&OnlyUnopened=${onlyUnopened}`, 
        {
          headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
        }
      );
      const data = await response.json();
      
      const tickets = data.data || [];
      setAllTickets(tickets); // Store all tickets
      setSupportTickets(tickets); // Set tickets directly from API
      
      setTotalPages(Math.ceil((data.total || tickets.length) / pageSize));
      setCurrentPage(data.pageNumber || 1);
    } catch (error) {
      console.error('Support tickets fetch error:', error);
      setAllTickets([]);
      setSupportTickets([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchTicketDetails = async (ticketId) => {
    try {
      setLoading(true);
      const response = await fetch(`https://localhost:9001/api/v1/UserSupportMessage/${ticketId}`, {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });
      
      if (!response.ok) {
        throw new Error(`Error fetching ticket details: ${response.status}`);
      }
      
      const data = await response.json();
      setSelectedTicket(data);
      // Reset response text when viewing a new ticket
      setResponseText(data.messageResponse || '');
    } catch (error) {
      console.error('Error fetching ticket details:', error);
      alert('Failed to load ticket details. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleTicketClick = (ticket) => {
    fetchTicketDetails(ticket.id);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  const toggleFilter = (showOnlyUnopened) => {
    setOnlyUnopened(showOnlyUnopened);
    // Reset to page 1 when filter changes
    setCurrentPage(1);
  };

  const handleResponseChange = (text) => {
    setResponseText(text);
  };

  const submitResponse = async () => {
    if (!responseText.trim()) {
      setErrorMessage('Lütfen bir yanıt mesajı girin.');
      setTimeout(() => setErrorMessage(''), 3000);
      return;
    }

    try {
      setResponding(true);
      const response = await fetch(`https://localhost:9001/api/v1/UserSupportMessage/${selectedTicket.id}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: selectedTicket.id,
          messageResponse: responseText
        })
      });

      if (!response.ok) {
        throw new Error(`Error submitting response: ${response.status}`);
      }

      // Refresh ticket details to show the new response
      await fetchTicketDetails(selectedTicket.id);
      // Update the ticket in the allTickets array
      const updatedTickets = allTickets.map(ticket => 
        ticket.id === selectedTicket.id 
          ? {...ticket, isResponsed: true} 
          : ticket
      );
      setAllTickets(updatedTickets);
      setSupportTickets(updatedTickets);
      
      setSuccessMessage('Yanıt başarıyla gönderildi!');
      setShowSuccessModal(true);
      setTimeout(() => setShowSuccessModal(false), 2000);
    } catch (error) {
      console.error('Error submitting response:', error);
      setErrorMessage('Yanıt gönderilirken bir hata oluştu. Lütfen tekrar deneyin.');
      setTimeout(() => setErrorMessage(''), 3000);
    } finally {
      setResponding(false);
    }
  };

  // DataTable column definitions
  const columns = [
    { 
      key: 'id', 
      header: 'ID',
      className: 'id-column'
    },
    { 
      key: 'subject', 
      header: 'Subject',
      className: 'subject-column'
    },
    { 
      key: 'createdBy', 
      header: 'Created By',
      className: 'created-by-column'
    },
    { 
      key: 'status', 
      header: 'Status',
      className: 'status-column',
      render: (item) => (
        <span className={item.isResponsed ? 'status-badge answered' : 'status-badge pending'}>
          {item.isResponsed ? 'Answered' : 'Pending'}
        </span>
      )
    }
  ];

  return (
    <div className="support-management">
      <h2>Support Tickets</h2>
      {loading && <div className="loading">Loading...</div>}
      
      {selectedTicket ? (
        <TicketDetail
          ticket={selectedTicket}
          responseText={responseText}
          onResponseChange={handleResponseChange}
          onSubmitResponse={submitResponse}
          responding={responding}
          onBack={() => setSelectedTicket(null)}
        />
      ) : (
        <>
          <div className="list-header">
            <h3>Support Requests</h3>
            <div className="filter-buttons">
              <button 
                className={`filter-button ${!onlyUnopened ? 'active' : ''}`}
                onClick={() => toggleFilter(false)}
              >
                All Tickets
              </button>
              <button 
                className={`filter-button ${onlyUnopened ? 'active' : ''}`}
                onClick={() => toggleFilter(true)}
              >
                Pending
              </button>
              <button className="refresh-button" onClick={() => fetchSupportTickets(currentPage)}>
                Refresh
              </button>
            </div>
          </div>
          
          <DataTable
            columns={columns}
            data={supportTickets}
            isLoading={loading}
            loadingText="Loading tickets..."
            emptyText="No support tickets found."
            onRowClick={handleTicketClick}
            keyField="id"
            className="support-tickets-table"
          />
          
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
            className="support-pagination"
          />
        </>
      )}
      <ConfirmModal
        open={showSuccessModal}
        title="Başarılı"
        message={successMessage}
        onConfirm={() => setShowSuccessModal(false)}
        hideButtons={true}
      />
      {errorMessage && (
        <div className="error-message">{errorMessage}</div>
      )}
    </div>
  );
}

export default SupportSection;