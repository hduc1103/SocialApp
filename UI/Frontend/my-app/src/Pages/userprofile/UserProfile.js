import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Post from '../../components/post/Post';
import FloatingButton from '../../components/floatingbutton/FloatingButton';
import AddNewPost from '../../components/addpostmodal/AddNewPost';
import { BASE_URL } from '../../service/config';
import './userprofile.scss';
import Footer from '../../components/footer/footer';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [error, setError] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const navigate = useNavigate();

  const handleNewPost = (content) => {
    const token = localStorage.getItem('token');
    fetch(`${BASE_URL}/post/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ content }),
    })
      .then(response => response.json())
      .then(data => {
        setUserPosts([...userPosts, { ...data, likeCount: 0 }]); 
      })
      .catch(err => console.error(err));
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }

    const fetchUserProfile = async () => {
      try {
        const response = await fetch(`${BASE_URL}/user/getUserData`, {
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
        const response = await fetch(`${BASE_URL}/post/getUserPost`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch user posts');
        }

        const posts = await response.json();
        const postsWithLikes = await fetchPostLikeCounts(posts);
        setUserPosts(postsWithLikes);
      } catch (error) {
        setError(error.message);
      }
    };

    const fetchPostLikeCounts = async (posts) => {
      const token = localStorage.getItem('token');
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

    fetchUserProfile();
    fetchUserPosts();
  }, [navigate]);

  return (
    <div className="user-profile">
      <FloatingButton onClick={() => setIsModalOpen(true)} />
      <AddNewPost isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleNewPost} />
      <div className="profile-header">
        <div className="profile-details">
          <img
            src="https://via.placeholder.com/150"
            alt="Profile"
            className="profile-picture"
          />
          <div className="user-info">
            <h1>{userDetails ? `${userDetails.username}` : 'User Profile'}</h1>
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
      <Footer></Footer>
    </div>
  );
};

export default UserProfile;
