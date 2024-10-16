import React, { useEffect, useState } from 'react';
import { BASE_URL, showRedNotification } from '../../config';
import { useNavigate } from 'react-router-dom';
import './adminticketpage.scss';
import ViewTicketSupportModal from '../../components/viewticketsupportmodal/ViewTicketSupportModal';

const AdminTicketPage = () => {
  const [tickets, setTickets] = useState([]);
  const [usernames, setUsernames] = useState({});
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

      const usernamesTemp = {};
      await Promise.all(
        data.map(async (ticket) => {
          const ticketUserResponse = await fetch(`${BASE_URL}/admin/get-username?userId=${ticket.userId}`, {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (ticketUserResponse.ok) {
            const username = await ticketUserResponse.text();
            usernamesTemp[ticket.userId] = username;
          }
        })
      );

      setUsernames(usernamesTemp);
    } catch (error) {
      console.error('Error fetching tickets:', error);
      showRedNotification('Error fetching tickets');
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
                Posted by: <strong>{usernames[ticket.userId] || 'Loading...'}</strong>
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
          isOpen={isModalOpen}
          onClose={handleModalClose}
          ticket={selectedTicket} 
        />
      )}
    </div>
  );
};

export default AdminTicketPage;
