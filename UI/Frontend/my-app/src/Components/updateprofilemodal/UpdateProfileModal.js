import React, { useState, useEffect } from 'react';
import './updateprofilemodal.scss';

const UpdateProfileModal = ({ isOpen, onClose, onSubmit, currentDetails }) => {
  const [updatedDetails, setUpdatedDetails] = useState({
    new_name: '',
    new_username: '',
    new_email: '',
    new_address: '',
    new_bio: '',
  });

  useEffect(() => {
    if (currentDetails) {
      setUpdatedDetails({
        new_name: currentDetails.name || '',
        new_username: currentDetails.username || '',
        new_email: currentDetails.email || '',
        new_address: currentDetails.address || '',
        new_bio: currentDetails.bio || '',
      });
    }
  }, [currentDetails]);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(updatedDetails);
  };

  if (!isOpen) return null;

  return (
    <div className="update-modal">
      <div className="update-modal-content">
        <h2>Update Profile</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Name"
            value={updatedDetails.new_name}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_name: e.target.value })}
          />
          <input
            type="text"
            placeholder="Username"
            value={updatedDetails.new_username}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_username: e.target.value })}
          />
          <input
            type="email"
            placeholder="Email"
            value={updatedDetails.new_email}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_email: e.target.value })}
          />
          <input
            type="text"
            placeholder="Address"
            value={updatedDetails.new_address}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_address: e.target.value })}
          />
          <textarea
            placeholder="Bio"
            value={updatedDetails.new_bio}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_bio: e.target.value })}
          />
          <button type="submit">Save Changes</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default UpdateProfileModal;