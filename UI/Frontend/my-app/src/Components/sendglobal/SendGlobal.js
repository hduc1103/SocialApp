import React, { useState } from 'react';
import { BASE_URL } from '../../config';
import './sendglobal.scss'; 

const SendGlobal = () => {
  const [message, setMessage] = useState('');
    const token = localStorage.getItem("token");
  const handleSendGlobal = async () => {
    try {
      const response = await fetch(`${BASE_URL}/admin/global-notification`, {
        method: 'POST',
        headers: {
            Authorization: `Bearer ${token}`          },
        body: message
      });

      if (response.ok) {
        alert('Global message sent!');
        setMessage('');
      } else {
        alert('Failed to send global message.');
      }
    } catch (error) {
      console.error('Error sending global message:', error);
      alert('An error occurred.');
    }
  };

  return (
    <div className="send-global-container">
      <h2>Send Global Message</h2>
      <textarea
        placeholder="Message"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
      />
      <button onClick={handleSendGlobal}>Send to All Users</button>
    </div>
  );
};

export default SendGlobal;
