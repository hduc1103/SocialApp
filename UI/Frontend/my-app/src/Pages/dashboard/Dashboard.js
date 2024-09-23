import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.scss';

const Dashboard = () => {
  const [posts, setPosts] = useState([]);
  const [error, setError] = useState(null);
  const [friendsPosts, setFriendsPosts] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');

    console.log("token ne    " + token);

    if (!token) {
      setError('No token found');
      return;
    }

    fetch('http://localhost:8080/api/posts/user', {
      method: 'GET',
      headers: { 
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
    })
    .then((response) => {
      if (!response.ok) {
        throw new Error('Failed to fetch posts, status: ' + response.status);
      }
      return response.json();
    })
    .then((data) => {
      setPosts(data);
      setError(null); 
    })
    .catch((error) => {
      console.error('Error fetching posts:', error);
      setError(error.message);
    });
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
