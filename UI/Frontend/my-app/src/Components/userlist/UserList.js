import React from 'react';

const UserList = ({ users, onEditUser, onDeleteUser, onViewPosts }) => (
  <div className="user-list">
    {users.length > 0 &&
      users.map((user) => (
        <div key={user.id} className="user-item">
          <p>ID: {user.id}</p>
          <p>Username: {user.username}</p>
          <p>Email: {user.email}</p>
          <p>Bio: {user.bio}</p>
          <p>Address: {user.address}</p>

          <div className="user-actions">
            <button onClick={() => onEditUser(user)}>Edit</button>
            <button onClick={() => onDeleteUser(user.id)}>Delete</button>
            <button onClick={() => onViewPosts(user.id)}>View Posts</button>
          </div>
        </div>
      ))}
  </div>
);

export default UserList;
