import React, { useState, useEffect } from 'react';
import { BASE_URL, showGreenNotification, showRedNotification } from '../../config';
import CreateUser from '../../components/createuser/CreateUser';
import UserList from '../../components/userlist/UserList';
import UpdateProfileModal from '../../components/updateprofilemodal/UpdateProfileModal';
import ViewPostsModal from '../../components/viewpostsmodal/ViewPostsModal'; 
import GetUser from '../../components/getuser/GetUser';
import UpdateUser from '../../components/updateuser/UpdateUser';
import DeleteUser from '../../components/deleteuser/DeleteUser';
import { useNavigate } from 'react-router-dom';

import './adminpanel.scss';

const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null); 
  const [selectedUserId, setSelectedUserId] = useState(''); 
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false); 
  const [isPostsModalOpen, setIsPostsModalOpen] = useState(false); 
  const navigate = useNavigate();

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
  const [userId, setUserId] = useState('');
  const [userDetails, setUserDetails] = useState(null);
  useEffect(() => {
    const token = localStorage.getItem('token'); 
    if (!token) {
      navigate('/login');
      showRedNotification('You must log in to view your dashboard');
      return;
    }
  }, []);
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
        const errorData = await response.text();
        showRedNotification(errorData || 'Failed to fetch user details');
        return; 
      }
  
      const data = await response.json();
      setUserDetails(data);
      showGreenNotification('User details fetched successfully');
    } catch (error) {
      showRedNotification('Error fetching user');
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
        return; 
      }

      if (!response.ok) {
        const errorData = await response.text();
        showRedNotification(errorData || 'Failed to update user');
        return; 
      }
  
      showGreenNotification('User updated successfully');
      setUpdateData({
        userId: '',
        new_username: '',
        email: '',
        address: '',
        bio: '',
      });
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

      const posts = await response.json();
      return await fetchPostLikeCounts(posts);
    } catch (error) {
      console.error('Error fetching user posts:', error);
      showRedNotification('Error fetching user posts');
      return [];
    }
  };

  const fetchPostLikeCounts = async (posts) => {
    const token = localStorage.getItem('token');
    const updatedPosts = await Promise.all(
      posts.map(async (post) => {
        try {
          const response = await fetch(`${BASE_URL}/post/number-of-likes?postId=${post.id}`, {
            method: 'GET',
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });

          if (!response.ok) {
            const errorData = await response.json();
            showRedNotification(errorData.message || `Failed to fetch like count for post ${post.id}`);
            return { ...post, likeCount: 0 };
          }

          const likeCount = await response.json();
          return { ...post, likeCount };
        } catch (error) {
          console.error(`Error fetching like count for post ${post.id}:`, error);
          return { ...post, likeCount: 0 };
        }
      })
    );
    return updatedPosts;
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
      <GetUser userId={userId} setUserId={setUserId} getOneUser={getOneUser} userDetails={userDetails} />
      
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
      <UpdateUser updateData={updateData} setUpdateData={setUpdateData} updateUser={updateUser} />
      <DeleteUser userId={userId} setUserId={setUserId} deleteUser={deleteUser} />
    </div>
  );
};

export default AdminPanel;
