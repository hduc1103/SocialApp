import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Post from '../../components/post/Post';
import FloatingButton from '../../components/floatingbutton/FloatingButton';
import AddNewPost from '../../components/addpostmodal/AddNewPost';
import UpdateProfileModal from '../../components/updateprofilemodal/UpdateProfileModal';
import ChangePasswordModal from '../../components/changepasswordmodal/ChangePasswordModal';
import { LiaUserEditSolid } from "react-icons/lia";
import { FaUpload } from "react-icons/fa6";
import { IoPersonAddSharp, IoPersonRemoveSharp } from "react-icons/io5";
import { PiPasswordBold } from "react-icons/pi";
import { BASE_URL, PUBLIC_URL, showRedNotification, showGreenNotification } from '../../config';
import './userprofile.scss';
import Footer from '../../components/footer/footer';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [isFriend, setIsFriend] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);
  const [author, setAuthor] = useState('');
  const [authorImgUrl, setAuthorImgUrl] = useState('');

  const navigate = useNavigate();
  const { userId } = useParams();

  const loggedInUserId = localStorage.getItem('userId');

const fetchPostAuthor = async (new_post_userId) => {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch(`${BASE_URL}/user/get-username?userId=${new_post_userId}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to fetch post author');
      return null; 
    }

    const data = await response.json();
    setAuthor(data.username);
    setAuthorImgUrl(data.imgUrl);
    return data;
  } catch (error) {
    console.error('Error fetching post author:', error);
    showRedNotification('Error fetching post author');
    return null;
  }
};

const handleNewPost = (content) => {
  const token = localStorage.getItem('token');
  fetch(`${BASE_URL}/post/create-post`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ content }),
  })
    .then(async (response) => {
      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to create post');
        return;
      }
      const data = await response.json();
      const authorData = await fetchPostAuthor(data.userId);
      if (authorData) {
        const newPost = {
          ...data,
          likeCount: 0,
          author: authorData.username,
          authorImgUrl: authorData.imgUrl,
        };
        setUserPosts((prevPosts) => [...prevPosts, newPost]);
        showGreenNotification('Post created successfully');
      }
    })
    .catch((err) => {
      console.error(err);
      showRedNotification('Error creating post');
    });
};

const handleUpdateProfile = async (updatedDetails) => {
  const token = localStorage.getItem('token');
  try {
    const updateData = Object.keys(updatedDetails).reduce((acc, key) => {
      if (updatedDetails[key]) {
        acc[key] = updatedDetails[key];
      }
      return acc;
    }, {});

    if (Object.keys(updateData).length === 0) {
      return;
    }

    const response = await fetch(`${BASE_URL}/user/update-user`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updateData),
    });

    if (!response.ok) {
      const errorData = await response.json();
      if (response.status === 409) {
        showRedNotification('Username or email already exists');
      } else {
        showRedNotification(errorData.message || 'Failed to update profile');
      }
      return;
    }

    const data = await response.json();
    showGreenNotification('Profile updated successfully');
    setUserDetails(data);
    setIsUpdateModalOpen(false);
  } catch (error) {
    console.error('Error updating profile:', error);
    showRedNotification('Error updating profile');
  }
};

const handleUpdateProfileImage = async (profilePicture) => {
  const token = localStorage.getItem('token');
  try {
    const formData = new FormData();
    formData.append('profilePicture', profilePicture);

    const response = await fetch(`${BASE_URL}/user/update-profile-image`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${token}`,
      },
      body: formData,
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to update profile image');
      return;
    }

    const updatedData = await response.json();
    showGreenNotification('Profile image updated successfully');
    setUserDetails((prevDetails) => ({
      ...prevDetails,
      img_url: updatedData.img_url,
    }));
  } catch (error) {
    console.error('Error updating profile image:', error);
    showRedNotification('Error updating profile image');
  }
};

const handlePasswordChange = (passwordDetails) => {
  const token = localStorage.getItem('token');
  fetch(`${BASE_URL}/user/change-password`, {
    method: 'POST',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      'old-password': passwordDetails.old_password,
      'new-password': passwordDetails.new_password,
    }),
  })
    .then(async (response) => {
      if (!response.ok) {
        const errorData = await response.json();
        if (response.status === 401) {
          showRedNotification('Invalid old password');
        } else {
          showRedNotification(errorData.message || 'Failed to change password');
        }
        return;
      }
      showGreenNotification('Password changed successfully');
      setIsPasswordModalOpen(false);
    })
    .catch((error) => {
      console.error('Error changing password:', error);
      showRedNotification('Failed to change password');
    });
};

const checkFriendshipStatus = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch(`${BASE_URL}/user/check-friend-status?userId2=${userId}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to check friendship status');
      return;
    }

    const isFriendStatus = await response.json();
    setIsFriend(isFriendStatus);
  } catch (error) {
    console.error('Error checking friendship status:', error);
    showRedNotification('Error checking friendship status');
  }
};

const handleAddFriend = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch(`${BASE_URL}/user/add-friend?userId2=${userId}`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to add friend');
      return;
    }

    setIsFriend(1);
    showGreenNotification('Friend added successfully');
  } catch (error) {
    console.error('Error adding friend:', error);
    showRedNotification('Error adding friend');
  }
};

const Handleunfriend = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch(`${BASE_URL}/user/unfriend?userId2=${userId}`, {
      method: 'DELETE',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to unfriend');
      return;
    }

    setIsFriend(0);
    showGreenNotification('Unfriended successfully');
  } catch (error) {
    console.error('Error unfriending:', error);
    showRedNotification('Error unfriending');
  }
};

const fetchUserProfile = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch(`${BASE_URL}/user/get-user-data?userId=${userId}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to fetch user profile');
      return;
    }

    const data = await response.json();
    setUserDetails(data);
  } catch (error) {
    console.error('Error fetching user profile:', error);
    showRedNotification('Error fetching user profile');
  }
};

const fetchUserPosts = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await fetch(`${BASE_URL}/post/get-user-post?userId=${userId}`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!response.ok) {
      const errorData = await response.json();
      showRedNotification(errorData.message || 'Failed to fetch user posts');
      return;
    }

    const posts = await response.json();
    const postsWithLikes = await fetchPostLikeCounts(posts);
    setUserPosts(postsWithLikes);
  } catch (error) {
    console.error('Error fetching user posts:', error);
    showRedNotification('Error fetching user posts');
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

useEffect(() => {
  const token = localStorage.getItem('token');
  if (!token) {
    navigate('/login');
    return;
  }

  fetchUserProfile();
  fetchUserPosts();
  if (userId !== loggedInUserId) {
    checkFriendshipStatus();
  }
}, [navigate, userId]);


  return (
    <div className="user-profile">
      {userId === loggedInUserId && (
        <FloatingButton onClick={() => setIsModalOpen(true)} />
      )}
      <AddNewPost isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleNewPost} />

      <div className="profile-header">
        <div className="profile-details">
          <div className="profile-picture-wrapper">
            <img
              src={userDetails?.img_url ? `${PUBLIC_URL}/profile_img_upload/${userDetails.img_url}` : "https://via.placeholder.com/150"}
              alt="Profile"
              className="profile-picture"
            />
            {userId === loggedInUserId && (
              <>
                <input
                  type="file"
                  id="profileImageInput"
                  style={{ display: 'none' }}
                  onChange={(e) => {
                    if (e.target.files && e.target.files[0]) {
                      handleUpdateProfileImage(e.target.files[0]);
                    }
                  }}
                />
                <button
                  className="edit-profile-picture-button"
                  onClick={() => document.getElementById('profileImageInput').click()}
                >
                  <FaUpload size={15} />
                </button>
              </>
            )}
          </div>
          <div className="user-info">
            <h1>{userDetails ? `${userDetails.name}` : 'User Profile'}</h1>
            <p>{userDetails ? `Email: ${userDetails.email}` : 'Loading user details...'}</p>
            <p>{userDetails ? `Address: ${userDetails.address}` : 'Loading user details...'}</p>
            <p>{userDetails ? `Bio: ${userDetails.bio}` : 'Loading user details...'}</p>
          </div>
        </div>
        {userId === loggedInUserId && (
          <div className="profile-actions">
            <button className="update-profile-button" onClick={() => setIsUpdateModalOpen(true)}>
              <LiaUserEditSolid size={20} />
            </button>
            <button className="change-password-button" onClick={() => setIsPasswordModalOpen(true)}>
              <PiPasswordBold size={20} />
            </button>
          </div>
        )}
        {userId !== loggedInUserId && (
          <button
            className="add-friend-button"
            onClick={() => {
              if (!isFriend) {
                handleAddFriend();
              } else {
                Handleunfriend();
              }
            }}
          >
            {isFriend ? (
              <>
                <IoPersonRemoveSharp size={20} /> Unfriend
              </>
            ) : (
              <>
                <IoPersonAddSharp size={20} /> Add Friend
              </>
            )}
          </button>
        )}
      </div>

      {isUpdateModalOpen && (
        <UpdateProfileModal
          isOpen={isUpdateModalOpen}
          onClose={() => setIsUpdateModalOpen(false)}
          onSubmit={handleUpdateProfile}
          currentDetails={userDetails}
        />
      )}
      {isPasswordModalOpen && (
        <ChangePasswordModal
          isOpen={isPasswordModalOpen}
          onClose={() => setIsPasswordModalOpen(false)}
          onSubmit={handlePasswordChange}
        />
      )}
      <div className="posts-section">
        <h2>{userId === loggedInUserId ? 'Your Posts' : `${userDetails?.name}'s Posts`}</h2>
        {userPosts.length > 0 ? (
          <div className="post-list">
            {userPosts.slice().reverse().map((post) => (
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