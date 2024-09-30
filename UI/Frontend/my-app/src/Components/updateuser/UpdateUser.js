import React from 'react';

const UpdateUser = ({ updateData, setUpdateData, updateUser }) => (
  <div className="update-user">
    <h2>Update User</h2>
    <input
      type="text"
      placeholder="User ID"
      value={updateData.userId}
      onChange={(e) => setUpdateData({ ...updateData, userId: e.target.value })}
    />
    <input
      type="text"
      placeholder="New Username"
      value={updateData.new_username}
      onChange={(e) => setUpdateData({ ...updateData, new_username: e.target.value })}
    />
    <input
      type="email"
      placeholder="Email"
      value={updateData.email}
      onChange={(e) => setUpdateData({ ...updateData, email: e.target.value })}
    />
    <input
      type="text"
      placeholder="Address"
      value={updateData.address}
      onChange={(e) => setUpdateData({ ...updateData, address: e.target.value })}
    />
    <input
      type="text"
      placeholder="Bio"
      value={updateData.bio}
      onChange={(e) => setUpdateData({ ...updateData, bio: e.target.value })}
    />
    <button onClick={updateUser}>Update User</button>
  </div>
);

export default UpdateUser;
