import React, { useState } from 'react';
import './newpasswordmodal.scss'
const NewPasswordModal = ({ show, onClose, onSubmit }) => {
  const [newPassword, setNewPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(newPassword);
  };

  if (!show) return null;

  return (
    <div className="new-password-modal">
      <div className="new-password-modal-content">
        <h3 className="new-password-modal-title">Enter new password</h3>
        <form onSubmit={handleSubmit}>
          <div className="new-password-input-group">
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              className="new-password-input"
              placeholder="New Password"
              required
            />
          </div>
          <button type="submit" className="new-password-modal-submit">Submit</button>
          <button type="button" onClick={onClose} className="new-password-modal-close">Close</button>
        </form>
      </div>
    </div>
  );
};

export default NewPasswordModal;
