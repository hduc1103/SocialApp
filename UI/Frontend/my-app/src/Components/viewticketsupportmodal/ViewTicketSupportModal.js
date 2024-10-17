import React, { useState } from 'react';
import './viewticketsupportmodal.scss';

const ViewTicketSupportModal = ({ username, isOpen, onClose, ticket, onCommentSubmit }) => {
  const [comment, setComment] = useState('');

  if (!isOpen || !ticket) {
    return null;
  }

  const parseCommentText = (text) => {
    try {
      const parsed = JSON.parse(text);
      if (parsed.text || parsed.comment) {
        return parsed.text || parsed.comment;
      }
    } catch (e) {
      return text;
    }
    return text;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (ticket.status !== 'CLOSED') {
      onCommentSubmit(ticket.id, comment);
      setComment('');
    } else {
      alert('Comments cannot be added to closed tickets.');
    }
  };

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

        <h3>Comments</h3>
        <ul>
          {ticket.comments && ticket.comments.length > 0 ? (
            ticket.comments.map((comment, index) => (
              <li key={index}>
                <strong> {comment.name}:</strong> {comment.text} <span style={{ color: 'gray', fontSize: '0.9em', marginLeft: '10px' }}>
                  (Posted on: {new Date(comment.createdAt).toLocaleDateString()})
                </span>
              </li>
            ))
          ) : (
            <li>No comments yet.</li>
          )}
        </ul>

        {ticket.status !== 'Closed' ? (
          <form onSubmit={handleSubmit}>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Add a comment"
              required
            />
            <button type="submit">Submit Comment</button>
          </form>
        ) : (
          <p>This ticket is closed. Comments are disabled, but you can still view existing comments.</p>
        )}
      </div>
    </div>
  );
};

export default ViewTicketSupportModal;
