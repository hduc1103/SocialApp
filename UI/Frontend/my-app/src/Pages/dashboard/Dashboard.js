import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BASE_URL, showRedNotification } from '../../config';
import Post from '../../components/post/Post';
import './dashboard.scss';

const Dashboard = () => {
  const [friendPosts, setFriendPosts] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    if (!token) {
      navigate('/login');
      showRedNotification('You must log in to view your dashboard');
      return;
    }
    retrieveFriendsPosts();
  }, []);

  const retrieveFriendsPosts = async () => {
    try {
      const response = await fetch(`${BASE_URL}/post/retrieve-friends-posts?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.status === 401) {
        const errorMessage = await response.text();
        if (errorMessage === 'Token has expired') {
          showRedNotification(errorMessage);
        } else {
          showRedNotification('Unauthorized access, please log in again');
        }
        navigate('/login');
        return;
      }else if(response.status === 403){
        navigate('/login');
        showRedNotification('Session expired, please log in again');
        return;
      }

      if (!response.ok) {
        const errorData = await response.text()
        throw new Error(errorData || 'Failed to retrieve friends posts');
      }

      const posts = await response.json();
      const postsWithLikes = await fetchPostLikeCounts(posts);
      setFriendPosts(postsWithLikes);
    } catch (error) {
      console.error('Error fetching friends posts:', error);
      setError('Failed to fetch posts. Please try again later.');
      showRedNotification('Error fetching friends posts');
    }
  };

  const fetchPostLikeCounts = async (posts) => {
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
            throw new Error('Failed to fetch like count');
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

  return (
    <div className="dashboard">
      <h2>Friends' Recent Posts</h2>
      {error && <p className="error-message">{error}</p>}
      <div className="posts-container">
        {friendPosts.length > 0 ? (
          friendPosts.map((post) => <Post key={post.id} post={post} />)
        ) : (
          <p>No recent posts from friends.</p>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
