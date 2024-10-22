import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import FloatingButton from '../../components/floatingbutton/FloatingButton';
import ProfileHeader from '../../components/profileheader/ProfileHeader';
import PostList from '../../components/postlist/PostList';
import Modals from '../../components/profilemodals/ProfileModals';
import { BASE_URL, showRedNotification, showGreenNotification } from '../../config';
import './userprofile.scss';
import Footer from '../../components/footer/footer';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [friendshipStatus, setFriendshipStatus] = useState('NOT_FRIENDS'); 
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [isPasswordModalOpen, setIsPasswordModalOpen] = useState(false);

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
      return data;
    } catch (error) {
      console.error('Error fetching post author:', error);
      showRedNotification('Error fetching post author');
      return null;
    }
  };

  const handleNewPost = async (content) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/post/create-post`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ content }),
      });

      if (!response.ok) {
        const errorData = await response.text();
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
    } catch (err) {
      console.error('Error creating post:', err);
      showRedNotification('Error creating post');
    }
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

      if (Object.keys(updateData).length === 0) return;

      const response = await fetch(`${BASE_URL}/user/update-user`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateData),
      });

      if (!response.ok) {
        const errorData = await response.text();
        if (response.status === 409) {
          showRedNotification(errorData);
        } else {
          showRedNotification(errorData || 'Failed to update profile');
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
    
    if (!profilePicture) {
      showRedNotification('Please select an image to upload');
      return;
    }
  
    if (!profilePicture.type.startsWith('image/')) {
      showRedNotification('Please upload a valid image file');
      return;
    }
  
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
        const errorData = await response.text();
        showRedNotification(errorData || 'Failed to check friendship status');
        return;
      }

      const friendshipStatus = await response.text();
      setFriendshipStatus(friendshipStatus);
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

      setFriendshipStatus('REQUEST_SENT');
      showGreenNotification('Friend request sent');
    } catch (error) {
      console.error('Error adding friend:', error);
      showRedNotification('Error adding friend');
    }
  };

  const hanldeCancelFriendRequest = async () => {
    const token = localStorage.getItem("token");
    try{
      const response = await fetch(`${BASE_URL}/user/cancel-friend-request?userId2=${userId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if(!response.ok){
        const error = await response.text();
        showRedNotification(error);
        return;
      }
      setFriendshipStatus('NOT_FRIENDS');
      showGreenNotification("Friend request cancelled");
    }catch (error){
      console.log(error);
    }
  };

  const handleAcceptFriendRequest = async () => {
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
        showRedNotification(errorData.message || 'Failed to accept friend request');
        return;
      }

      setFriendshipStatus('FRIENDS');
      showGreenNotification('Friend request accepted');
    } catch (error) {
      console.error('Error accepting friend request:', error);
      showRedNotification('Error accepting friend request');
    }
  };

  const handleUnfriend = async () => {
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

      setFriendshipStatus('NOT_FRIENDS');
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

      if (response.status === 401) {
        const errorMessage = await response.text();
        if (errorMessage === 'Token has expired') {
          showRedNotification(errorMessage);
        } else {
          showRedNotification('Unauthorized access, please log in again');
        }
        navigate('/login');
        return;
      } else if(response.status === 403){
        navigate('/login');
        return;
      }

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

  const handleDeletePost = async (postId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/post/delete-post?postId=${postId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to delete post');
        return;
      }
      setUserPosts(userPosts.filter((post) => post.id !== postId));
      showGreenNotification('Post deleted successfully');
    } catch (error) {
      console.error('Error deleting post:', error);
      showRedNotification('Error deleting post');
    }
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
      {userId === loggedInUserId && <FloatingButton onClick={() => setIsModalOpen(true)} />}
      <Modals
        isModalOpen={isModalOpen}
        setIsModalOpen={setIsModalOpen}
        handleNewPost={handleNewPost}
        isUpdateModalOpen={isUpdateModalOpen}
        setIsUpdateModalOpen={setIsUpdateModalOpen}
        handleUpdateProfile={handleUpdateProfile}
        isPasswordModalOpen={isPasswordModalOpen}
        setIsPasswordModalOpen={setIsPasswordModalOpen}
        handlePasswordChange={handlePasswordChange}
        userDetails={userDetails}
      />

      <ProfileHeader
        userDetails={userDetails}
        userId={userId}
        loggedInUserId={loggedInUserId}
        friendshipStatus={friendshipStatus}
        handleAddFriend={handleAddFriend}
        hanldeCancelFriendRequest={hanldeCancelFriendRequest}
        handleAcceptFriendRequest={handleAcceptFriendRequest}
        handleUnfriend={handleUnfriend}
        handleUpdateProfileImage={handleUpdateProfileImage}
        setIsUpdateModalOpen={setIsUpdateModalOpen}
        setIsPasswordModalOpen={setIsPasswordModalOpen}
      />

      <PostList posts={userPosts} onDeletePost={handleDeletePost} userId={userId} Name={userDetails?.name} loggedInUserId={loggedInUserId} />

      <Footer />
    </div>
  );
};

export default UserProfile;
