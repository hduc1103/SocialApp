import React from 'react';

const DeleteUser = ({ userId, setUserId, deleteUser }) => (
  <div className="delete-user">
    <input
      type="text"
      placeholder="Enter User ID to Delete"
      value=" "
      onChange={(e) => setUserId(e.target.value)}
    />
    <button onClick={deleteUser}>Delete User</button>
  </div>
);

export default DeleteUser;
