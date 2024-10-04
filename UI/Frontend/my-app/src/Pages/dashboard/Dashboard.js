import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { BASE_URL } from '../../config';
import Post from '../../components/post/Post';
import './dashboard.scss';

const Dashboard = () => {
  const [friendPosts, setFriendPosts] = useState([]);
  const [error, setError] = useState('');
  const navigate=useNavigate();
  const token = localStorage.getItem('token');
  const userId =localStorage.getItem('userId')
  console.log(userId)

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }
    retrieveFriendsPosts();
  }, []);

  const retrieveFriendsPosts = async () => {
    try {
      if (!token) {
        throw new Error('User not logged in');
      }

      const response = await fetch(`${BASE_URL}/post/retrieveFriendsPosts?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if(response.status=== 401){
        navigate('/login')
      } else if(response.status=== 403){
        navigate('/login')
        alert("Your token expired, please log in!")
      }

      const posts = await response.json();
      const postsWithLikes = await fetchPostLikeCounts(posts);
      setFriendPosts(postsWithLikes);
    } catch (error) {
      console.error('Error fetching friends posts:', error);
      setError('Failed to fetch posts. Please try again later.');
    }
  };

  const fetchPostLikeCounts = async (posts) => {
    const updatedPosts = await Promise.all(
      posts.map(async (post) => {
        try {
          const response = await fetch(`${BASE_URL}/post/numberOfLikes?postId=${post.id}`, {
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
          friendPosts.map((post) => (
            <Post key={post.id} post={post} /> 
          ))
        ) : (
          <p>No recent posts from friends.</p>
        )}
      </div>
    </div>
  );
};

export default Dashboard;