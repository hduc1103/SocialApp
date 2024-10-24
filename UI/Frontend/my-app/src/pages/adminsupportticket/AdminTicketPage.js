import React, { useEffect, useState } from 'react';
import { BASE_URL, showRedNotification } from '../../config';
import { useNavigate } from 'react-router-dom';
import './adminticketpage.scss';
import ViewTicketSupportModal from '../../components/viewticketsupportmodal/ViewTicketSupportModal';

const AdminTicketPage = () => {
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null); 
  const [isModalOpen, setIsModalOpen] = useState(false); 
  const token = localStorage.getItem('token');

  const navigate = useNavigate();

  useEffect(() => {
    fetchTickets();
  }, []);

const fetchTickets = async () => {
  try {
    const response = await fetch(`${BASE_URL}/admin/get-all-support-ticket`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      if (response.status === 401) {
        navigate('/login');
      } else {
        const errorData = await response.text();
        showRedNotification(errorData.message || 'Failed to fetch support tickets');
      }
      return;
    }

    const data = await response.json();
    setTickets(data); 

  } catch (error) {
    console.error('Error fetching tickets:', error);
    showRedNotification('Error fetching tickets');
  }
};

  const handleCommentTicket = async (ticketId, comment) => {
  try {
    const response = await fetch(`${BASE_URL}/admin/add-ticket-comment?ticket_id=${ticketId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'text/plain',
        Authorization: `Bearer ${token}`,
      },
      body: comment,
    });

    if (!response.ok) {
      if (response.status === 401) {
        navigate('/login');
      } else {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to add comment');
      }
      return;
    }
    fetchTickets(); 
  } catch (error) {
    console.error('Error adding comment:', error);
    showRedNotification('Error adding comment');
  }
};

  const handleTicketClick = (ticket) => {
    setSelectedTicket(ticket); 
    setIsModalOpen(true); 
  };

  const handleModalClose = () => {
    setIsModalOpen(false); 
    setSelectedTicket(null); 
  };

  return (
    <div className="admin-page">
      <h2 className="support-tickets-header">Support Tickets Management</h2>
      <div className="support-ticket-container">
        {tickets.length > 0 ? (
          tickets.slice().reverse().map((ticket) => (
            <div key={ticket.id} className="support-ticket-item">
              <h3 className="admin-ticket-title" onClick={() => handleTicketClick(ticket)}>
                {ticket.title}
              </h3>
              <p className="ticket-user">
                Posted by: <strong>{ticket.author || 'Loading...'}</strong>
              </p>
              <div className="ticket-status">
                <strong>Status:</strong> {ticket.status}
              </div>
            </div>
          ))
        ) : (
          <p>No tickets available.</p>
        )}
      </div>

      {selectedTicket && (
        <ViewTicketSupportModal
        onCommentSubmit={handleCommentTicket}
          isOpen={isModalOpen}
          onClose={handleModalClose}
          ticket={selectedTicket} 
        />
      )}
    </div>
  );
};

export default AdminTicketPage;
