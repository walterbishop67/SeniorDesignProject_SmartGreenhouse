import React, { useState } from 'react';
import './TicketDetail.css';

function TicketDetail({ 
  ticket, 
  responseText, 
  onResponseChange, 
  onSubmitResponse, 
  responding, 
  onBack 
}) {
  const [activeTab, setActiveTab] = useState('details');

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString();
  };

  return (
    <div className="ticket-detail-panel">
      <div className="ticket-header">
        <div className="ticket-title">
          <h3>Ticket #{ticket.id}</h3>
          <p className="ticket-subject">{ticket.subject}</p>
        </div>
        <button className="back-button" onClick={onBack}>
          Back to Ticket List
        </button>
      </div>

      <div className="ticket-tabs">
        <div className="tab-headers">
          <button 
            className={`tab-button ${activeTab === 'details' ? 'active' : ''}`}
            onClick={() => setActiveTab('details')}
          >
            Ticket Details
          </button>
          <button 
            className={`tab-button ${activeTab === 'response' ? 'active' : ''}`}
            onClick={() => setActiveTab('response')}
          >
            Add Response
          </button>
        </div>

        <div className="tab-content">
          {activeTab === 'details' && (
            <div className="details-tab">
              <div className="ticket-info-grid">
                <div className="info-item">
                  <span className="info-label">Created By</span>
                  <span className="info-value">{ticket.createdBy}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">Created</span>
                  <span className="info-value">{formatDate(ticket.created)}</span>
                </div>
                <div className="info-item">
                  <span className="info-label">Status</span>
                  <span className="info-value">
                    <span className={`status-indicator ${ticket.isResponsed ? 'answered' : 'pending'}`}>
                      {ticket.isResponsed ? 'Answered' : 'Pending'}
                    </span>
                  </span>
                </div>
                <div className="info-item">
                  <span className="info-label">Sent To</span>
                  <span className="info-value">{ticket.sentTo || 'N/A'}</span>
                </div>
              </div>

              <div className="message-content">
                <h4>Message</h4>
                <div className="message-text">
                  {ticket.messageContent || 'No message content available.'}
                </div>
              </div>

              {ticket.messageResponse && (
                <div className="response-content">
                  <h4>Previous Response</h4>
                  <div className="response-text">
                    {ticket.messageResponse}
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'response' && (
            <div className="response-tab">
              <div className="response-form">
                <h4>Admin Response</h4>
                <textarea
                  className="response-textarea"
                  value={responseText}
                  onChange={(e) => onResponseChange(e.target.value)}
                  placeholder="Type your response here..."
                  rows={5}
                />
                <div className="response-actions">
                  <button 
                    className={`submit-response ${responding ? 'responding' : ''}`}
                    onClick={onSubmitResponse}
                    disabled={responding}
                  >
                    {responding ? 'Submitting...' : 'Submit Response'}
                  </button>
                </div>
                <div className={`response-status ${ticket.isResponsed ? 'completed' : 'pending'}`}>
                  {ticket.isResponsed ? 'This ticket has been answered' : 'Waiting for response'}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default TicketDetail;