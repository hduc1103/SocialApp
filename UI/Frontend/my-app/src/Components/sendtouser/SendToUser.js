import React, { useState } from 'react';
import { BASE_URL } from '../../config';
import './sendtouser.scss';

const SendToUser = () => {
  const [userId, setUserId] = useState('');
  const [message, setMessage] = useState('');
  const token = localStorage.getItem("token");
  const handleSendToUser = async () => {
    try {
      const response = await fetch(`${BASE_URL}/admin/user-notification?userId=${userId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`
        },
        body:  message,
      });

      if (response.ok) {
        alert('Message sent to user!');
        setUserId('');
        setMessage('');
      } else {
        alert('Failed to send message to user.');
      }
    } catch (error) {
      console.error('Error sending message to user:', error);
      alert('An error occurred.');
    }
  };

  return (
    <div className="send-to-user-container">
      <h2>Send Message to Specific User</h2>
      <input
        type="text"
        placeholder="User ID"
        value={userId}
        onChange={(e) => setUserId(e.target.value)}
      />
      <textarea
      className='specific-user'
        placeholder="Message"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      />
      <button onClick={handleSendToUser}>Send to specific User</button>
    </div>
  );
};

export default SendToUser;
