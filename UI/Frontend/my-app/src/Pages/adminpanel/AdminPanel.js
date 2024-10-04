import React, { useState } from 'react';
import { BASE_URL } from '../../config';
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
      const response = await fetch(`${BASE_URL}/admin/allUsers`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`, 
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        throw new Error('Failed to fetch users');
      }
  
      const data = await response.json();
      setUsers(data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };
  
  
  const getOneUser = async () => {
    const token = localStorage.getItem('token'); 
  
    try {
      const response = await fetch(`${BASE_URL}/admin/oneUser?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`, 
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        throw new Error('Failed to fetch user details');
      }
  
      const data = await response.json();
      setUserDetails(data);
    } catch (error) {
      console.error('Error fetching user:', error);
    }
  };
  
  const deleteUser = async () => {
    const token = localStorage.getItem('token'); 
  
    try {
      const response = await fetch(`${BASE_URL}/admin/deleteUser?userId=${userId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`, 
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        throw new Error('Failed to delete user');
      }
  
      alert('User deleted successfully');
      getAllUsers();
    } catch (error) {
      console.error('Error deleting user:', error);
    }
  };
  
  const createUser = async () => {
    const token = localStorage.getItem('token'); 
  
    try {
      const response = await fetch(`${BASE_URL}/admin/createUser`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`, 
        },
        body: JSON.stringify(newUser),
      });
  
      if (!response.ok) {
        throw new Error('Failed to create user');
      }
  
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
  
      const response = await fetch(`${BASE_URL}/admin/updateUser?userId=${updateData.userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updateDataPayload),
      });
  
      if (!response.ok) {
        throw new Error('Failed to update user');
      }
  
      alert('User updated successfully');
      setUpdateData({
        userId: '',
        new_name: '',
        new_username: '',
        new_email: '',
        new_address: '',
        new_bio: '',
      });
    } catch (error) {
      console.error('Error updating user:', error);
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