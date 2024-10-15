import React from 'react';
import './viewticketsupportmodal.scss';

const ViewTicketSupportModal = ({ isOpen, onClose, ticket }) => {
  if (!isOpen || !ticket) {
    return null;
  }

  return (
    <div className="view-ticket-support-modal-overlay">
     <div className="view-ticket-support-modal-content">
        <button className="close-button" onClick={onClose}>
          &times;
        </button>
        <h1>Ticket Details</h1>
        <p><strong>Ticket ID:</strong> {ticket.id}</p>
        <p><strong>Title:</strong> {ticket.title}</p>
        <p><strong>Status:</strong> {ticket.status}</p>
        <p><strong>Created At:</strong> {new Date(ticket.createdAt).toLocaleDateString()}</p>
        <p><strong>Content:</strong></p>
        <ul>
          {ticket.content.split(';').map((item, index) => (
            <li key={index}>{item}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default ViewTicketSupportModal;
