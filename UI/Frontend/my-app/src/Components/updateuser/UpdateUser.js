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
      placeholder="Name"
      value={updateData.new_name}
      onChange={(e) => setUpdateData({ ...updateData, new_name: e.target.value })} 
    />
    <input
      type="text"
      placeholder="Username"
      value={updateData.new_username}
      onChange={(e) => setUpdateData({ ...updateData, new_username: e.target.value })}
    />
    <input
      type="email"
      placeholder="Email"
      value={updateData.new_email}
      onChange={(e) => setUpdateData({ ...updateData, new_email: e.target.value })} 
    />
    <input
      type="text"
      placeholder="Address"
      value={updateData.new_address}
      onChange={(e) => setUpdateData({ ...updateData, new_address: e.target.value })} 
    />
    <input
      type="text"
      placeholder="Bio"
      value={updateData.new_bio}
      onChange={(e) => setUpdateData({ ...updateData, new_bio: e.target.value })} 
    />
    <button onClick={updateUser}>Update User</button>
  </div>
);

export default UpdateUser;

