import React, { useState } from 'react';
import './updateprofilemodal.scss';

const UpdateProfileModal = ({ isOpen, onClose, onSubmit, currentDetails }) => {
  const [updatedDetails, setUpdatedDetails] = useState({
    email: currentDetails?.email || '',
    address: currentDetails?.address || '',
    bio: currentDetails?.bio || '',
    profilePicture: null,
  });

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
            value={updatedDetails.name}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_name: e.target.value })}
          />
          <input
            type="text"
            placeholder="UserName"
            value={updatedDetails.username}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_username: e.target.value })}
          />
          <input
            type="text"
            placeholder="Email"
            value={updatedDetails.email}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_email: e.target.value })}
          />
          <input
            type="text"
            placeholder="Address"
            value={updatedDetails.address}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_address: e.target.value })}
          />
          <textarea
            placeholder="Bio"
            value={updatedDetails.bio}
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_bio: e.target.value })}
          />
          <input
            type="file"
            onChange={(e) => setUpdatedDetails({ ...updatedDetails, new_profilePicture: e.target.files[0] })}
          />
          <button type="submit">Save Changes</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default UpdateProfileModal;
