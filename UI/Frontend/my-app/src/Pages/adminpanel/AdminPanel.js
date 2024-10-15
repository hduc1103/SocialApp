import React, { useState } from 'react';
import { BASE_URL, showGreenNotification, showRedNotification } from '../../config';
import CreateUser from '../../components/createuser/CreateUser';
import DeleteUser from '../../components/deleteuser/DeleteUser';
import UserList from '../../components/userlist/UserList';
import GetUser from '../../components/getuser/GetUser';
import UpdateUser from '../../components/updateuser/UpdateUser';

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

  const getAllUsers = async () => {
    const token = localStorage.getItem('token'); 
    try {
      const response = await fetch(`${BASE_URL}/admin/all-users`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`, 
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to fetch users');
        return; 
      }
  
      const data = await response.json();
      setUsers(data);
      showGreenNotification('Users fetched successfully');
    } catch (error) {
      showRedNotification('Error fetching users');
    }
  };

  const getOneUser = async () => {
    const token = localStorage.getItem('token'); 
    try {
      const response = await fetch(`${BASE_URL}/admin/one-user?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`, 
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to fetch user details');
        return; 
      }
  
      const data = await response.json();
      setUserDetails(data);
      showGreenNotification('User details fetched successfully');
    } catch (error) {
      showRedNotification('Error fetching user');
    }
  };
  
  const deleteUser = async () => {
    const token = localStorage.getItem('token'); 
    try {
      const response = await fetch(`${BASE_URL}/admin/delete-user?userId=${userId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`, 
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to delete user');
        return; // Stop execution if there's an error
      }
  
      showGreenNotification('User deleted successfully');
      getAllUsers();
    } catch (error) {
      showRedNotification('Error deleting user');
    }
  };

  const createUser = async () => {
    const token = localStorage.getItem('token'); 
    try {
      const response = await fetch(`${BASE_URL}/admin/create-user`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`, 
        },
        body: JSON.stringify(newUser),
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to create user');
        return; // Stop execution if there's an error
      }
  
      showGreenNotification('User created successfully');
      setNewUser({
        username: '',
        name: '',
        email: '',
        password: '',
        address: '',
        bio: '',
        img_url: '',
      });
      getAllUsers(); // Refresh user list after creation
    } catch (error) {
      showRedNotification('Error creating user');
    }
  };

  const updateUser = async () => {
    const token = localStorage.getItem('token');
    try {
      const updateDataPayload = Object.keys(updateData).reduce((acc, key) => {
        if (updateData[key]) {
          acc[key] = updateData[key];
        }
        return acc;
      }, {});
  
      if (Object.keys(updateDataPayload).length === 0) {
        return;
      }
  
      const response = await fetch(`${BASE_URL}/admin/update-user?userId=${updateData.userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updateDataPayload),
      });
  
      if (response.status === 409) {
        showRedNotification('Username or email already exists');
        return; // Stop execution in case of conflict
      }

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to update user');
        return; // Stop execution if there's an error
      }
  
      showGreenNotification('User updated successfully');
      setUpdateData({
        userId: '',
        new_name: '',
        new_username: '',
        new_email: '',
        new_address: '',
        new_bio: '',
      });
      getAllUsers(); 
    } catch (error) {
      showRedNotification('Error updating user');
    }
  };

  return (
    <div className="admin-panel">
      <h1>Admin Panel</h1>
      <button onClick={getAllUsers}>Get All Users</button>
      <UserList users={users} />
      <GetUser userId={userId} setUserId={setUserId} getOneUser={getOneUser} userDetails={userDetails} />
      <CreateUser newUser={newUser} setNewUser={setNewUser} createUser={createUser} />
      <UpdateUser updateData={updateData} setUpdateData={setUpdateData} updateUser={updateUser} />
      <DeleteUser userId={userId} setUserId={setUserId} deleteUser={deleteUser} />
    </div>
  );
};

export default AdminPanel;
