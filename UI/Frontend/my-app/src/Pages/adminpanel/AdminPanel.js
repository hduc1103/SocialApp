import React, { useState } from 'react';
import { BASE_URL } from '../../service/config';
import './adminpanel.scss';

const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [userId, setUserId] = useState('');
  const [userDetails, setUserDetails] = useState(null);
  const [newUser, setNewUser] = useState({
    username: '',
    email: '',
    password: '',
    address: '',
    bio: '',
    img_url: '',
  });
  const [updateData, setUpdateData] = useState({
    userId: '',
    new_username: '',
    email: '',
    address: '',
    bio: '',
  });

  // Function to get all users
  const getAllUsers = async () => {
    try {
      const response = await fetch(`${BASE_URL}/admin/allUsers`);
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  // Function to get one user by ID
  const getOneUser = async () => {
    try {
      const response = await fetch(`${BASE_URL}/admin/oneUser?userId=${userId}`);
      const data = await response.json();
      setUserDetails(data);
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  };

  // Function to delete a user
  const deleteUser = async () => {
    try {
      await fetch(`${BASE_URL}/admin/deleteUser?userId=${userId}`, {
        method: 'DELETE',
      });
      alert('User deleted successfully');
      getAllUsers(); // Refresh user list
    } catch (error) {
      console.error('Error deleting user:', error);
    }
  };

  // Function to create a new user
  const createUser = async () => {
    try {
      await fetch(`${BASE_URL}/admin/createUser`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newUser),
      });
      alert('User created successfully');
      setNewUser({
        username: '',
        email: '',
        password: '',
        address: '',
        bio: '',
        img_url: '',
      });
    } catch (error) {
      console.error('Error creating user:', error);
    }
  };

  // Function to update a user
  const updateUser = async () => {
    try {
      await fetch(`${BASE_URL}/admin/updateUser?userId=${updateData.userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateData),
      });
      alert('User updated successfully');
      setUpdateData({
        userId: '',
        new_username: '',
        email: '',
        address: '',
        bio: '',
      });
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  return (
    <div className="admin-panel">
      <h1>Admin Panel</h1>

      {/* Section to get all users */}
      <button onClick={getAllUsers}>Get All Users</button>
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

      {/* Section to get one user by ID */}
      <div className="get-one-user">
        <input
          type="text"
          placeholder="Enter User ID"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
        <button onClick={getOneUser}>Get User Details</button>
        {userDetails && (
          <div className="user-details">
            <p>ID: {userDetails.id}</p>
            <p>Username: {userDetails.username}</p>
            <p>Email: {userDetails.email}</p>
            <p>Bio: {userDetails.bio}</p>
            <p>Address: {userDetails.address}</p>
          </div>
        )}
      </div>

      {/* Section to create a new user */}
      <div className="create-user">
        <h2>Create User</h2>
        <input
          type="text"
          placeholder="Username"
          value={newUser.username}
          onChange={(e) => setNewUser({ ...newUser, username: e.target.value })}
        />
        <input
          type="email"
          placeholder="Email"
          value={newUser.email}
          onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
        />
        <input
          type="password"
          placeholder="Password"
          value={newUser.password}
          onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
        />
        <input
          type="text"
          placeholder="Address"
          value={newUser.address}
          onChange={(e) => setNewUser({ ...newUser, address: e.target.value })}
        />
        <input
          type="text"
          placeholder="Bio"
          value={newUser.bio}
          onChange={(e) => setNewUser({ ...newUser, bio: e.target.value })}
        />
        <input
          type="text"
          placeholder="Image URL"
          value={newUser.img_url}
          onChange={(e) => setNewUser({ ...newUser, img_url: e.target.value })}
        />
        <button onClick={createUser}>Create User</button>
      </div>

      {/* Section to update a user */}
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

      {/* Section to delete a user */}
      <div className="delete-user">
        <input
          type="text"
          placeholder="Enter User ID to Delete"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
        <button onClick={deleteUser}>Delete User</button>
      </div>
    </div>
  );
};

export default AdminPanel;
