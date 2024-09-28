import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Post from '../../components/post/Post';
import FloatingButton from '../../components/floatingbutton/FloatingButton';
import AddNewPost from '../../components/addpostmodal/AddNewPost';
import UpdateProfileModal from '../../components/updateprofilemodal/UpdateProfileModal';
import { BASE_URL } from '../../service/config';
import './userprofile.scss';
import Footer from '../../components/footer/footer';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [error, setError] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [updatedDetails, setUpdatedDetails] = useState({
    email: '',
    address: '',
    bio: '',
  });

  const navigate = useNavigate();

  const handleNewPost = (content) => {
    const token = localStorage.getItem('token');
    fetch(`${BASE_URL}/post/createPost`, {
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

  const handleUpdateProfile = async (updatedDetails) => {
    const token = localStorage.getItem('token');
    try {
      const formData = new FormData();
      formData.append('email', updatedDetails.email);
      formData.append('address', updatedDetails.address);
      formData.append('bio', updatedDetails.bio);
      if (updatedDetails.profilePicture) {
        formData.append('profilePicture', updatedDetails.profilePicture);
      }
  
      const response = await fetch(`${BASE_URL}/user/updateUser`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });
  
      if (!response.ok) {
        throw new Error('Failed to update profile');
      }
  
      const data = await response.json();
      setUserDetails(data);
      setIsUpdateModalOpen(false);
    } catch (error) {
      setError(error.message);
    }
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
        setUpdatedDetails({
          email: data.email,
          address: data.address,
          bio: data.bio,
        });
      } catch (error) {
        setError(error.message);
      }
    };
    const handleUpdateProfile = async () => {
      const token = localStorage.getItem('token');
      try {
        const formData = new FormData();
        formData.append('email', updatedDetails.email);
        formData.append('address', updatedDetails.address);
        formData.append('bio', updatedDetails.bio);
        if (updatedDetails.profilePicture) {
          formData.append('profilePicture', updatedDetails.profilePicture);
        }
  
        const response = await fetch(`${BASE_URL}/user/updateUser`, {
          method: 'PUT',
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body: formData,
        });
  
        if (!response.ok) {
          throw new Error('Failed to update profile');
        }
  
        const data = await response.json();
        setUserDetails(data);
        setIsUpdateModalOpen(false);
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
            <p>{userDetails ? `Address: ${userDetails.address}` : 'Loading user details...'}</p>
            <p>{userDetails ? `Bio: ${userDetails.bio}` : 'Loading user details...'}</p>
          </div>
        </div>
        <button className="update-profile-button" onClick={() => setIsUpdateModalOpen(true)}>
          Update Profile
        </button>
        {error && <p className="error">{error}</p>}
      </div>

      {isUpdateModalOpen && (
  <UpdateProfileModal
    isOpen={isUpdateModalOpen}
    onClose={() => setIsUpdateModalOpen(false)}
    onSubmit={handleUpdateProfile}
    currentDetails={userDetails}
  />
)}
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
      <Footer />
    </div>
  );
};

export default UserProfile;
