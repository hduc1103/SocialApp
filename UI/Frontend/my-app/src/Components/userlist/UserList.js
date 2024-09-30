import React from 'react';

const UserList = ({ users }) => (
  <div className="user-list">
    {users.length > 0 &&
      users.map((user) => (
        <div key={user.id} className="user-item">
          <p>ID: {user.id}</p>
          <p>Username: {user.username}</p>
          <p>Email: {user.email}</p>
          <p>Bio: {user.bio}</p>
          <p>Address: {user.address}</p>
        </div>
      ))}
  </div>
);

export default UserList;
