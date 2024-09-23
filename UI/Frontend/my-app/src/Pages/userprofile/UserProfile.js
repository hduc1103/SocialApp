import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './userprofile.scss';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');

    if (!token) {
      navigate('/login');
    }

    // Fetch user details
    const fetchUserProfile = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/profile', {
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

    // Fetch user's posts
    const fetchUserPosts = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/posts/user', {
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
      <div className="profile-header">
        <h1>{userDetails ? `${userDetails.username}'s Profile` : 'User Profile'}</h1>
        <p>{userDetails ? `Email: ${userDetails.email}` : 'Loading user details...'}</p>
        {error && <p className="error">{error}</p>}
      </div>

      <div className="posts-section">
        <h2>Your Posts</h2>
        {userPosts.length > 0 ? (
          <div className="post-list">
            {userPosts.map((post) => (
              <div key={post.id} className="post-card">
                <p>{post.content}</p>
                <span>{new Date(post.createdAt).toLocaleString()}</span>
              </div>
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
