import React, { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config';
import './viewticketsupportmodal.scss';

const ViewTicketSupportModal = ({ isOpen, onClose }) => {
  const [tickets, setTickets] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isOpen) {
      const fetchSupportTickets = async () => {
        const token = localStorage.getItem('token');
        try {
          const response = await fetch(`${BASE_URL}/user/supportTickets`, {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
              'Content-Type': 'application/json',
            },
          });

          if (!response.ok) {
            throw new Error('Failed to fetch support tickets');
          }

          const data = await response.json();
          setTickets(data);
        } catch (error) {
          setError(error.message);
        }
      };

      fetchSupportTickets();
    }
  }, [isOpen]);

  if (!isOpen) {
    return null;
  }

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <button className="close-button" onClick={onClose}>
          &times;
        </button>
        <h1>My Support Tickets</h1>
        {error && <p className="error-message">{error}</p>}
        {tickets.length > 0 ? (
          <div className="tickets-list">
            {tickets.map((ticket) => (
              <div key={ticket.id} className="ticket-item">
                <p><strong>Ticket ID:</strong> {ticket.id}</p>
                <p><strong>Status:</strong> {ticket.status}</p>
                <p><strong>Created At:</strong> {new Date(ticket.createdAt).toLocaleDateString()}</p>
                <p><strong>Content:</strong></p>
                <ul>
                  {ticket.content.map((item, index) => (
                    <li key={index}>{item}</li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
        ) : (
          <p>No support tickets found.</p>
        )}
      </div>
    </div>
  );
};

export default ViewTicketSupportModal;
