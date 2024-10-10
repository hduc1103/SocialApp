import React, { useState } from 'react';
import './changepasswordmodal.scss';

const ChangePasswordModal = ({ isOpen, onClose, onSubmit }) => {
  const [passwordDetails, setPasswordDetails] = useState({
    old_password: '',
    new_password: '',
    confirm_password: '',
  });

  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const handleFormSubmit = (e) => {
    e.preventDefault();
    if (passwordDetails.new_password !== passwordDetails.confirm_password) {
      setErrorMessage('New passwords do not match.');
      return;
    }
    setErrorMessage('');
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

          {errorMessage && <p style={{ color: 'red' }}>{errorMessage}</p>}
          {successMessage && <p style={{ color: 'green' }}>{successMessage}</p>}

          <button type="submit">Change Password</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default ChangePasswordModal;
