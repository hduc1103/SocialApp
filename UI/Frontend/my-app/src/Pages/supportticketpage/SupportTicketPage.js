import React, { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config';
import './supportticketpage.scss';

const SupportTicketPage = ({ userId }) => {
  const [title, setTitle] = useState('');
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [selectedTicketId, setSelectedTicketId] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [error, setError] = useState('');

  const token = localStorage.getItem('token');

  const ticketOptions = [
    'Cannot Change Password',
    'Delete My Account',
    'Create Me A New Account',
    'I Need to Update My Information',
    'Send Me All My Information',
  ];

  // Fetch tickets on component mount
  useEffect(() => {
    getAllUserTicket();
  }, []);

  // Fetch comments when a ticket is selected
  useEffect(() => {
    if (selectedTicketId) {
      fetchComments(selectedTicketId);
    }
  }, [selectedTicketId]);

  const handleOptionChange = (option) => {
    if (selectedOptions.includes(option)) {
      setSelectedOptions(selectedOptions.filter((opt) => opt !== option));
    } else {
      setSelectedOptions([...selectedOptions, option]);
    }
  };

  const handleSubmitTicket = async () => {
    if (title.trim() === '') {
      setError('Title cannot be empty');
      return;
    }

    if (selectedOptions.length === 0) {
      setError('Please select at least one option');
      return;
    }

    try {
      const response = await fetch(`${BASE_URL}/user/createSupportTicket`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: title,
          content: selectedOptions,
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to submit ticket');
      }

      // Re-fetch tickets after successful ticket creation
      await getAllUserTicket();
      setTitle('');
      setSelectedOptions([]);
      setError('');
    } catch (err) {
      console.error(err);
      setError('Failed to submit ticket');
    }
  };

  const getAllUserTicket = async () => {
    try {
      const response = await fetch(`${BASE_URL}/user/getAllUserTicket`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch user tickets');
      }

      const data = await response.json();
      setTickets(data);
    } catch (error) {
      console.error('Error fetching user tickets:', error);
      setError('Failed to fetch user tickets');
    }
  };

  const fetchComments = async (ticketId) => {
    try {
      const response = await fetch(`${BASE_URL}/support/${ticketId}/comments`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to load comments');
      }

      const data = await response.json();
      setComments(data);
    } catch (err) {
      console.error(err);
      setError('Failed to load comments');
    }
  };

  const handleAddComment = async () => {
    if (newComment.trim() === '') {
      setError('Comment cannot be empty');
      return;
    }

    try {
      const response = await fetch(`${BASE_URL}/support/${selectedTicketId}/comment`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          text: newComment,
          userId: userId,
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to add comment');
      }

      const data = await response.json();
      setComments([...comments, data]);
      setNewComment('');
      setError('');
    } catch (err) {
      console.error(err);
      setError('Failed to add comment');
    }
  };

  const handleCloseTicket = async (ticketId) => {
    try {
      const response = await fetch(`${BASE_URL}/support/${ticketId}/close`, {
        method: 'PATCH',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to close ticket');
      }

      // Re-fetch tickets after closing one
      await getAllUserTicket();
      setSelectedTicketId(null);
      setComments([]);
    } catch (err) {
      console.error(err);
      setError('Failed to close ticket');
    }
  };

  return (
    <div className="support-ticket-page">
      <h2>Create a Support Ticket</h2>
      <div className="ticket-form">
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="Enter ticket title"
        />
        <div className="ticket-options">
          <h4>Select Issue(s):</h4>
          {ticketOptions.map((option, index) => (
            <label key={index}>
              <input
                type="checkbox"
                value={option}
                checked={selectedOptions.includes(option)}
                onChange={() => handleOptionChange(option)}
              />
              {option}
            </label>
          ))}
        </div>
        <button onClick={handleSubmitTicket}>Submit Ticket</button>
        {error && <p className="error">{error}</p>}
      </div>

      <div className="ticket-list">
        <h3>Your Tickets</h3>
        {tickets.length > 0 ? (
          tickets.map((ticket) => (
            <div key={ticket.id} className={`ticket-item ${ticket.closed ? 'closed' : ''}`}>
              <h4>{ticket.title}</h4>
              <p>{ticket.content.join(', ')}</p>
              {!ticket.closed && (
                <button onClick={() => setSelectedTicketId(ticket.id)}>View Comments</button>
              )}
              {!ticket.closed && (
                <button onClick={() => handleCloseTicket(ticket.id)}>Close Ticket</button>
              )}
            </div>
          ))
        ) : (
          <p>No tickets yet.</p>
        )}
      </div>

      {selectedTicketId && (
        <div className="comments-section">
          <h3>Comments</h3>
          {comments.map((comment) => (
            <div key={comment.id} className="comment-item">
              <p>{comment.text}</p>
            </div>
          ))}
          <div className="add-comment">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Add a comment..."
            />
            <button onClick={handleAddComment}>Add Comment</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default SupportTicketPage;
