import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
  const [posts, setPosts] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPosts = async () => {
      const token = localStorage.getItem('token');
      if (!token) {
        setError('No token found, please login again.');
        navigate('/login');
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/posts', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (response.status === 200) {
          const data = await response.json();
          setPosts(data);
        } else if (response.status === 401) {
          setError('Unauthorized, please login again.');
          localStorage.removeItem('token');
          navigate('/login');
        } else {
          setError('Error fetching posts.');
        }
      } catch (error) {
        setError('Error fetching posts.');
      }
    };

    fetchPosts();
  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div>
      <h1>Welcome to your dashboard!</h1>
      {error && <p>{error}</p>}
      <button onClick={handleLogout}>Logout</button>

      <div>
        <h2>Your Posts</h2>
        {posts.length > 0 ? (
          <ul>
            {posts.map((post) => (
              <li key={post.id}>{post.content}</li>
            ))}
          </ul>
        ) : (
          <p>No posts available.</p>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
