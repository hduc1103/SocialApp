import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { BASE_URL, showRedNotification, showGreenNotification } from '../../config';
import './supportticketpage.scss';

const SupportTicketPage = ({ userId }) => {
  const [title, setTitle] = useState('');
  const [selectedOptions, setSelectedOptions] = useState([]);
  const [tickets, setTickets] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [usernames, setUsernames] = useState({});
  const [showCommentBox, setShowCommentBox] = useState({});
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  
  const ticketOptions = [
    'Cannot Change Password',
    'Delete My Account',
    'Create Me A New Account',
    'I Need to Update My Information',
    'Send Me All My Information',
  ];

  useEffect(() => {
    if (!token) {
      navigate('/login');
      return;
    }
    getAllUserTicket();
  }, []);

  const handleOptionChange = (option) => {
    setSelectedOptions((prevOptions) =>
      prevOptions.includes(option)
        ? prevOptions.filter((opt) => opt !== option)
        : [...prevOptions, option]
    );
  };

  const handleSubmitTicket = async () => {
    if (!title.trim()) {
      showRedNotification('Title cannot be empty');
      return;
    }

    if (selectedOptions.length === 0) {
      showRedNotification('Please select at least one option');
      return;
    }

    try {
      const content = selectedOptions.join('; ');

      const response = await fetch(`${BASE_URL}/user/create-support-ticket`, {
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
        const errorData = await response.text();
        showRedNotification(errorData.message || 'Failed to submit ticket');
        return;
      }

      await getAllUserTicket();
      setTitle('');
      setSelectedOptions([]);
      showGreenNotification('Ticket submitted successfully');
    } catch (err) {
      console.error(err);
      showRedNotification('Failed to submit ticket');
    }
  };

  const getAllUserTicket = async () => {
    try {
      const response = await fetch(`${BASE_URL}/user/get-all-user-ticket`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.text();
        showRedNotification(errorData.message || 'Failed to fetch user tickets');
        if (response.status === 401) {
          navigate('/login');
        }
        return;
      }

      const data = await response.json();
      setTickets(data);

      const userIds = Array.from(
        new Set(data.flatMap((ticket) => ticket.comments.map((comment) => comment.userId)))
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
      showRedNotification('Failed to fetch user tickets');
    }
  };

  const fetchUsername = async (userId) => {
    try {
      const response = await fetch(`${BASE_URL}/user/get-username?userId=${userId}`, {
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
    if (!newComment.trim()) {
      showRedNotification('Comment cannot be empty');
      return;
    }

    try {
      const response = await fetch(`${BASE_URL}/user/add-ticket-comment?ticket_id=${ticketId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ text: newComment }),
      });

      if (!response.ok) {
        const errorData = await response.text();
        showRedNotification(errorData.message || 'Failed to add comment');
        return;
      }

      await getAllUserTicket();
      setNewComment('');
      showGreenNotification('Comment added successfully');
      setShowCommentBox({ ...showCommentBox, [ticketId]: false });
    } catch (err) {
      console.error(err);
      showRedNotification('Failed to add comment');
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
        const errorData = await response.text();
        showRedNotification(errorData.message || 'Failed to close ticket');
        return;
      }

      await getAllUserTicket();
      showGreenNotification('Ticket closed successfully');
    } catch (err) {
      console.error(err);
      showRedNotification('Failed to close ticket');
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
      <h2 className="support-ticket-page-h2">Create a Support Ticket</h2>
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
      </div>

      {tickets.length > 0 ? (
        <div className="ticket-list">
          <h3>Your Tickets</h3>
          {tickets.map((ticket) => (
            <div key={ticket.id} className={`ticket-item ${ticket.closed ? 'closed' : ''}`}>
              <h4>{ticket.title}</h4>
              <ul className="ticket-content-list">
                {ticket.content.split(';').map((item, index) => (
                  <li key={index}>{item.trim()}</li>
                ))}
              </ul>
              <h5 className='commenth5'>Comments:</h5>
              <div className="comments-section">
                {ticket.comments.map((comment) => (
                  <div key={comment.id} className="user-comment-item">
                    <p>
                      <strong>{usernames[comment.userId] || 'Unknown User'}:</strong> {comment.text}
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
          ))}
        </div>
      ) : (
        <p className="no-ticket">No tickets yet.</p>
      )}
    </div>
  );
};

export default SupportTicketPage;
