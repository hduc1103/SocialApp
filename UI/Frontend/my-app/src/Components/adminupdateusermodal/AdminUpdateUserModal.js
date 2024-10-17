import React from 'react';
import './adminupdateusermodal.scss';

const AdminUpdateUserModal = ({ isOpen, onClose, updateUser, updateData, setUpdateData }) => {
    const handleSubmit = (e) => {
      e.preventDefault();
      updateUser();
    };
  
    if (!isOpen) return null;
  
    return (
      <div className="admin-update-modal">
        <div className="admin-update-modal-content">
          <h2>Update User</h2>
          <form onSubmit={handleSubmit}>
            <input
              type="hidden"
              value={updateData.userId}
            />
            <input
              type="text"
              placeholder="Name"
              value= {updateData.new_name}
              onChange={(e) => setUpdateData({ ...updateData, new_name: e.target.value })}
            />
            <input
              type="text"
              placeholder="Username"
              value= {updateData.new_username}
              onChange={(e) => setUpdateData({ ...updateData, new_username: e.target.value })}
            />
            <input
              type="email"
              placeholder="Email"
              value= {updateData.new_email}
              onChange={(e) => setUpdateData({ ...updateData, new_email: e.target.value })}
            />
            <input
              type="text"
              placeholder="Address"
              value= {updateData.new_address}
              onChange={(e) => setUpdateData({ ...updateData, new_address: e.target.value })}
            />
            <textarea
              placeholder="Bio"
              value={updateData.new_bio}
              onChange={(e) => setUpdateData({ ...updateData, new_bio: e.target.value })}
            />
            <button type="submit">Save Changes</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </form>
        </div>
      </div>
    );
  };
  

export default AdminUpdateUserModal;
