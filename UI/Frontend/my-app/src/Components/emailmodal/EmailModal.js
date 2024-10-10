import React, { useState } from 'react';
import './emailmodal.scss'

const EmailModal = ({ show, onClose, onSubmit }) => {
  const [email, setEmail] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(email);
  };

  if (!show) return null;

  return (
    <div className="email-modal">
      <div className="email-modal-content">
        <h3 className="email-modal-title">Enter your email</h3>
        <form onSubmit={handleSubmit}>
          <div className="email-input-group">
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="email-input"
              placeholder="Email"
              required
            />
          </div>
          <button type="submit" className="email-modal-submit">Submit</button>
          <button type="button" onClick={onClose} className="email-modal-close">Close</button>
        </form>
      </div>
    </div>
  );
};

export default EmailModal;
