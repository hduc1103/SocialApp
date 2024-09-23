import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.scss';

const Dashboard = () => {
  const [posts, setPosts] = useState([]);
  const [friendsPosts, setFriendsPosts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    // Fetch user's posts
    fetch('http://localhost:8080/api/posts', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    .then((response) => response.json())
    .then((data) => setPosts(data))
    .catch((error) => console.error('Error fetching posts:', error));

    // Fetch friends' latest posts
    fetch('http://localhost:8080/api/friends-posts', {
      headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
    })
    .then((response) => response.json())
    .then((data) => setFriendsPosts(data))
    .catch((error) => console.error('Error fetching friends posts:', error));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1>Welcome to your Dashboard!</h1>
        <button className="logout-button" onClick={handleLogout}>Logout</button>
      </div>

      <div className="posts-section">
        <div className="your-posts">
          <h2>Your Posts</h2>
          {posts.map((post) => (
            <div className="post-card" key={post.id}>
              <p>{post.content}</p>
            </div>
          ))}
        </div>

        <div className="friends-posts">
          <h2>Friends' Latest Posts</h2>
          {friendsPosts.map((post) => (
            <div className="post-card" key={post.id} onClick={() => navigate(`/profile/${post.userId}`)}>
              <p><strong>{post.user.username}'s Post:</strong></p>
              <p>{post.content}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
