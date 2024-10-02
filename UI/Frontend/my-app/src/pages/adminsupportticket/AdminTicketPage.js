import React, { useEffect, useState } from 'react';
import { BASE_URL } from '../../service/config';
import './adminticketpage.scss';

const AdminTicketPage = () => {
  const [tickets, setTickets] = useState([]);
  const [usernames, setUsernames] = useState({});
  const [showCommentBox, setShowCommentBox] = useState(null);
  const [newComment, setNewComment] = useState('');
  const token = localStorage.getItem('token');

  useEffect(() => {
    fetchTickets();
  }, []);

  const fetchTickets = async () => {
    try {
      const response = await fetch(`${BASE_URL}/admin/getAllSupportTicket`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch support tickets');
      }

      const data = await response.json();
      setTickets(data);

      const usernamesTemp = {};
      await Promise.all(
        data.map(async (ticket) => {
          const ticketUserResponse = await fetch(`${BASE_URL}/admin/AdmingetUsername?userId=${ticket.userId}`, {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (ticketUserResponse.ok) {
            const username = await ticketUserResponse.text();
            usernamesTemp[ticket.userId] = username;
          }
          await Promise.all(
            ticket.comments.map(async (comment) => {
              if (!usernamesTemp[comment.userId]) {
                const commentUserResponse = await fetch(`${BASE_URL}/admin/AdmingetUsername?userId=${comment.userId}`, {
                  method: 'GET',
                  headers: {
                    Authorization: `Bearer ${token}`,
                  },
                });
                if (commentUserResponse.ok) {
                  const commentUsername = await commentUserResponse.text();
                  usernamesTemp[comment.userId] = commentUsername;
                }
              }
            })
          );
        })
      );

      setUsernames(usernamesTemp);
    } catch (error) {
      console.error('Error fetching tickets:', error);
    }
  };

  const handleAddComment = async (ticketId) => {
    if (newComment.trim() === '') {
      return;
    }

    try {
      const response = await fetch(`${BASE_URL}/admin/addTicketComment?ticket_id=${ticketId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newComment),
      });

      if (!response.ok) {
        throw new Error('Failed to add comment');
      }

      await fetchTickets(); // Refresh tickets to include the new comment
      setShowCommentBox(null); // Hide the comment box after submitting
      setNewComment(''); // Clear the comment input
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  return (
    <div className="admin-page">
      <h2 className="support-tickets-header">Support Tickets Management</h2>
      <div className="support-ticket-container">
        {tickets.length > 0 ? (
          tickets.map((ticket) => (
            <div key={ticket.id} className="support-ticket-item">
              <h3 className="ticket-title">{ticket.title}</h3>
              <p className="ticket-user">
                Posted by: <strong>{usernames[ticket.userId] || 'Loading...'}</strong>
              </p>
              <div className="ticket-content">
                {ticket.content.split(';').map((issue, index) => (
                  <p key={index} className="ticket-content-item">{issue.trim()}</p>
                ))}
              </div>
              <div className="ticket-status">
                <strong>Status:</strong> {ticket.status}
              </div>
              <h4 className="comments-header">Comments:</h4>
              <div className="admin-comments-section">
                {ticket.comments.length > 0 ? (
                  ticket.comments.map((comment) => (
                    <div key={comment.id} className="comment-item">
                      <p>
                        <strong>{usernames[comment.userId] || 'Loading...'}:</strong> {comment.text}
                      </p>
                      <p className="comment-date">Posted on: {new Date(comment.createdAt).toLocaleString()}</p>
                    </div>
                  ))
                ) : (
                  <p>No comments yet.</p>
                )}
              </div>
              <button
                className="add-comment-button"
                onClick={() => setShowCommentBox(ticket.id === showCommentBox ? null : ticket.id)}
              >
                {showCommentBox === ticket.id ? 'Cancel' : 'Add Comment'}
              </button>
              {showCommentBox === ticket.id && (
                <div className="add-comment-box">
                  <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Add your comment here"
                  />
                  <button
                    className="submit-comment-button"
                    onClick={() => handleAddComment(ticket.id)}
                  >
                    Submit Comment
                  </button>
                </div>
              )}
            </div>
          ))
        ) : (
          <p>No tickets available.</p>
        )}
      </div>
    </div>
  );
};

export default AdminTicketPage;
