import React, { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config';
import './supportticketpage.scss';

const SupportTicketPage = ({ userId }) => {
  const [title, setTitle] = useState('');
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [error, setError] = useState('');
  const [usernames, setUsernames] = useState({});
  const [showCommentBox, setShowCommentBox] = useState({});

  const token = localStorage.getItem('token');

  const ticketOptions = [
    'Cannot Change Password',
    'Delete My Account',
    'Create Me A New Account',
    'I Need to Update My Information',
    'Send Me All My Information',
  ];

  useEffect(() => {
    getAllUserTicket();
  }, []);

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
      const content = selectedOptions.join('; ');

      const response = await fetch(`${BASE_URL}/user/createSupportTicket`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: title,
          content: content,
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to submit ticket');
      }

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

      const userIds = Array.from(
        new Set(data.flatMap(ticket => ticket.comments.map(comment => comment.userId)))
      );

      const usernamesMap = {};
      await Promise.all(
        userIds.map(async (userId) => {
          const username = await fetchUsername(userId);
          usernamesMap[userId] = username;
        })
      );

      setUsernames(usernamesMap);
    } catch (error) {
      console.error('Error fetching user tickets:', error);
      setError('Failed to fetch user tickets');
    }
  };

  const fetchUsername = async (userId) => {
    try {
      const response = await fetch(`${BASE_URL}/user/getUsername?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch username');
      }

      const data = await response.json();
      return data.username;
    } catch (error) {
      console.error('Error fetching username:', error);
      return 'Unknown User';
    }
  };

  const handleAddComment = async (ticketId) => {
    if (newComment.trim() === '') {
      setError('Comment cannot be empty');
      return;
    }

    try {
      const response = await fetch(`${BASE_URL}/user/addTicketComment?ticket_id=${ticketId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(newComment),
      });

      if (!response.ok) {
        throw new Error('Failed to add comment');
      }

      await getAllUserTicket();
      setNewComment('');
      setError('');
      setShowCommentBox({ ...showCommentBox, [ticketId]: false });
    } catch (err) {
      console.error(err);
      setError('Failed to add comment');
    }
  };

  const handleCloseTicket = async (ticketId) => {
    try {
      const response = await fetch(`${BASE_URL}/user/${ticketId}/close`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error('Failed to close ticket');
      }

      await getAllUserTicket();
    } catch (err) {
      console.error(err);
      setError('Failed to close ticket');
    }
  };

  const toggleCommentBox = (ticketId) => {
    setShowCommentBox((prev) => ({
      ...prev,
      [ticketId]: !prev[ticketId],
    }));
  };

  return (
    <div className="support-ticket-page">
      <h2 className='support-ticket-page-h2'>Create a Support Ticket</h2>
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
              <ul className="ticket-content-list">
                {ticket.content.split(';').map((item, index) => (
                  <li key={index}>{item.trim()}</li>
                ))}
              </ul>
              <h5>Comments:</h5>
              <div className="comments-section">
                {ticket.comments.map((comment) => (
                  <div key={comment.id} className="user-comment-item">
                    <p>
                      <strong>
                        {usernames[comment.userId] || 'Unknown User'}:
                      </strong>{' '}
                      {comment.text}
                    </p>
                    <p className="comment-date">
                      Posted on: {new Date(comment.createdAt).toLocaleString()}
                    </p>
                  </div>
                ))}
                {!ticket.closed && (
                  <>
                    <button onClick={() => toggleCommentBox(ticket.id)}>Add Comment</button>
                    {showCommentBox[ticket.id] && (
                      <div className="add-comment-box">
                        <textarea
                          value={newComment}
                          onChange={(e) => setNewComment(e.target.value)}
                          placeholder="Add a comment..."
                        />
                        <button onClick={() => handleAddComment(ticket.id)}>Submit Comment</button>
                      </div>
                    )}
                  </>
                )}
              </div>
              {!ticket.closed && (
                <button onClick={() => handleCloseTicket(ticket.id)}>Close Ticket</button>
              )}
            </div>
          ))
        ) : (
          <p>No tickets yet.</p>
        )}
      </div>
    </div>
  );
};

export default SupportTicketPage;
