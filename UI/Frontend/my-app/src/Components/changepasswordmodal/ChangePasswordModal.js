import React, { useState } from 'react';
import {showRedNotification } from '../../config';
import './changepasswordmodal.scss';

const ChangePasswordModal = ({ isOpen, onClose, onSubmit }) => {
  const [passwordDetails, setPasswordDetails] = useState({
    old_password: '',
    new_password: '',
    confirm_password: '',
  });

  const handleFormSubmit = (e) => {
    e.preventDefault();
    if (passwordDetails.new_password !== passwordDetails.confirm_password) {
      showRedNotification('New passwords do not match.');
      return;
    }
    onSubmit(passwordDetails); 
  };

  if (!isOpen) return null;

  return (
    <div className="update-modal">
      <div className="update-modal-content">
        <h2>Change Password</h2>
        <form onSubmit={handleFormSubmit}>
          <input
            type="password"
            placeholder="Old Password"
            value={passwordDetails.old_password}
            onChange={(e) => setPasswordDetails({ ...passwordDetails, old_password: e.target.value })}
            required
          />
          <input
            type="password"
            placeholder="New Password"
            value={passwordDetails.new_password}
            onChange={(e) => setPasswordDetails({ ...passwordDetails, new_password: e.target.value })}
            required
          />
          <input
            type="password"
            placeholder="Confirm New Password"
            value={passwordDetails.confirm_password}
            onChange={(e) => setPasswordDetails({ ...passwordDetails, confirm_password: e.target.value })}
            required
          />

          <button type="submit">Change Password</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default ChangePasswordModal;
