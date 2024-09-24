import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Post from '../../components/post/Post';
import FloatingButton from '../../components/floatingbutton/FloatingButton';
import './userprofile.scss';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleNewPost = () => {
    navigate('/new-post');
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }
    const fetchUserProfile = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/user/info', {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch user profile');
        }

        const data = await response.json();
        setUserDetails(data);
      } catch (error) {
        setError(error.message);
      }
    };

    const fetchUserPosts = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/post/user', {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch user posts');
        }

        const data = await response.json();
        setUserPosts(data);
      } catch (error) {
        setError(error.message);
      }
    };

    fetchUserProfile();
    fetchUserPosts();
  }, [navigate]);

  return (
    <div className="user-profile">
      <FloatingButton onClick={handleNewPost} />
      <div className="profile-header">
        <div className="profile-details">
          <img
            src="https://via.placeholder.com/150"
            alt="Profile"
            className="profile-picture"
          />
          <div className="user-info">
            <h1>{userDetails ? `${userDetails.username}'s Profile` : 'User Profile'}</h1>
            <p>{userDetails ? `Email: ${userDetails.email}` : 'Loading user details...'}</p>
          </div>
        </div>
        {error && <p className="error">{error}</p>}
      </div>

      <div className="posts-section">
        <h2>Your Posts</h2>
        {userPosts.length > 0 ? (
          <div className="post-list">
            {userPosts.map((post) => (
              <Post key={post.id} post={post} />
            ))}
          </div>
        ) : (
          <p>No posts to display.</p>
        )}
      </div>
    </div>
  );
};

export default UserProfile;
