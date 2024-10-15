import React, { useState } from 'react';
import { BASE_URL, showGreenNotification, showRedNotification } from '../../config';
import CreateUser from '../../components/createuser/CreateUser';
import UserList from '../../components/userlist/UserList';
import UpdateProfileModal from '../../components/updateprofilemodal/UpdateProfileModal';
import ViewPostsModal from '../../components/viewpostsmodal/ViewPostsModal'; 

import './adminpanel.scss';

const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null); 
  const [selectedUserId, setSelectedUserId] = useState(''); 
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false); 
  const [isPostsModalOpen, setIsPostsModalOpen] = useState(false); 
  const [newUser, setNewUser] = useState({
    username: '',
    email: '',
    password: '',
    address: '',
    bio: '',
    img_url: '',
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

  const deleteUser = async (userId) => {
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
        return; 
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
        return; 
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
      getAllUsers(); 
    } catch (error) {
      showRedNotification('Error creating user');
    }
  };

  const updateUser = async (updateData) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/admin/update-user?userId=${selectedUser.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updateData),
      });
  
      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to update user');
        return; 
      }
  
      showGreenNotification('User updated successfully');
      getAllUsers();
    } catch (error) {
      showRedNotification('Error updating user');
    }
  };

  const fetchUserPosts = async (userId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/post/get-user-post?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to fetch user posts');
        return [];
      }

      return await response.json();
    } catch (error) {
      console.error('Error fetching user posts:', error);
      showRedNotification('Error fetching user posts');
      return [];
    }
  };

  const handleEditUser = (user) => {
    setSelectedUser(user);
    setIsUpdateModalOpen(true);
  };

  const handleViewPosts = (userId) => {
    setSelectedUserId(userId);
    setIsPostsModalOpen(true);
  };

  return (
    <div className="admin-panel">
      <h1>Admin Panel</h1>
      <button onClick={getAllUsers}>Get All Users</button>
      <UserList users={users} onEditUser={handleEditUser} onDeleteUser={deleteUser} onViewPosts={handleViewPosts} />

      <UpdateProfileModal
        isOpen={isUpdateModalOpen}
        onClose={() => setIsUpdateModalOpen(false)}
        user={selectedUser}
        onUpdate={updateUser}
      />

      <ViewPostsModal
        isOpen={isPostsModalOpen}
        onClose={() => setIsPostsModalOpen(false)}
        userId={selectedUserId}
        fetchUserPosts={fetchUserPosts}
      />

      <CreateUser newUser={newUser} setNewUser={setNewUser} createUser={createUser} />
    </div>
  );
};

export default AdminPanel;
