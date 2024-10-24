import React, { useState, useEffect } from 'react';
import { BASE_URL, showGreenNotification, showRedNotification } from '../../config';
import CreateUser from '../../components/createuser/CreateUser';
import UserList from '../../components/userlist/UserList';
import ViewPostsModal from '../../components/viewpostsmodal/ViewPostsModal';
import SendGlobal from '../../components/sendglobal/SendGlobal';
import SendToUser from '../../components/sendtouser/SendToUser';
import GetUser from '../../components/getuser/GetUser';
import UpdateUser from '../../components/updateuser/UpdateUser';
import DeleteUser from '../../components/deleteuser/DeleteUser';
import { useNavigate } from 'react-router-dom';
import AdminUpdateUserModal from '../../components/adminupdateusermodal/AdminUpdateUserModal';

import './adminpanel.scss';

const AdminPanel = () => {
  const [users, setUsers] = useState([]);
  const [selectedUserId, setSelectedUserId] = useState('');
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [isPostsModalOpen, setIsPostsModalOpen] = useState(false);
  const [isDataFetched, setIsDataFetched] = useState(false);
  const navigate = useNavigate();
  const [newUser, setNewUser] = useState({
    username: '',
    name: '',
    email: '',
    password: '',
    address: '',
    bio: ''
  });
  const [updateData, setUpdateData] = useState({
    userId: '',
    new_username: '',
    new_name:'',
    new_email: '',
    new_address: '',
    new_bio: '',
  });
  const [userId, setUserId] = useState('');
  const [userDetails, setUserDetails] = useState(null);
  const [isUserListVisible, setIsUserListVisible] = useState(true);

  const handleCloseUserList = () => {
    setIsUserListVisible(false); 
  };
  const handleShowUserList = () => {
    setIsUserListVisible(true); 
  };
  useEffect(() => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');
    if (!token) {
      navigate('/login');
      showRedNotification('You must log in to view your dashboard');
      return;
    } else if(token && role !== 'ADMIN') {
      navigate('/');
      showRedNotification('You must be an admin to view this page');
    }
  }, [navigate]);

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
      setIsDataFetched(true);
      setIsUserListVisible(true);
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
      setUserId('');
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

      console.log(updateData);
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
        new_name:'',
        new_email: '',
        new_address: '',
        new_bio: '',
      });
      getAllUsers();
      setIsUpdateModalOpen(false);
    } catch (error) {
      showRedNotification('Error updating user');
    }
  };

  const fetchUserPosts = async (userId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/admin/get-user-post?userId=${userId}`, {
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
      console.log(posts);
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

  const handleEditUser = (userid) => {
    setUpdateData({
      userId: userid,  
      new_username: '',
      name: '',
      email: '',
      address: '',
      bio: '',
    });
    setIsUpdateModalOpen(true);
  };

  const handleViewPosts = (userId) => {
    setSelectedUserId(userId);
    setIsPostsModalOpen(true);
  };

  return (
    <div className="admin-panel">
      <h1>Admin Panel</h1>
      <SendToUser />
      <SendGlobal />
      <button onClick={getAllUsers}>Get All Users</button>
      {isUserListVisible && isDataFetched ? (
        <UserList 
          users={users} 
          onEditUser={handleEditUser} 
          onDeleteUser={deleteUser} 
          onViewPosts={handleViewPosts} 
          onCloseList={handleCloseUserList} 
        />
      ) : (
        isDataFetched && <button onClick={handleShowUserList}>Show User List</button>
      )}
      <GetUser
        userId={userId}
        setUserId={setUserId}
        getOneUser={getOneUser}
        userDetails={userDetails}
        onEditUser={handleEditUser}
        onDeleteUser={() => { }}
        onViewPosts={handleViewPosts}
      />

      <AdminUpdateUserModal
        isOpen={isUpdateModalOpen}
        onClose={() => setIsUpdateModalOpen(false)}
        updateUser={updateUser}
        updateData={updateData}
        setUpdateData={setUpdateData}
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
