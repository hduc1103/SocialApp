import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Post from '../../components/post/Post';
import FloatingButton from '../../components/floatingbutton/FloatingButton';
import AddNewPost from '../../components/addpostmodal/AddNewPost';
import UpdateProfileModal from '../../components/updateprofilemodal/UpdateProfileModal';
import { LiaUserEditSolid } from "react-icons/lia";
import { FaUpload } from "react-icons/fa6";
import { IoPersonAddSharp, IoPersonRemoveSharp } from "react-icons/io5";
import { BASE_URL, PUBLIC_URL, showRedNotification, showGreenNotification } from '../../config';
import './userprofile.scss';
import Footer from '../../components/footer/footer';

const UserProfile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [userPosts, setUserPosts] = useState([]);
  const [isFriend, setIsFriend] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [author, setAuthor] = useState('');
  const [authorImgUrl, setAuthorImgUrl] = useState('');

  const navigate = useNavigate();
  const { userId } = useParams();

  const loggedInUserId = localStorage.getItem('userId');
  const fetchPostAuthor = async (new_post_userId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/user/getUsername?userId=${new_post_userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch post author');
      }

      const data = await response.json();
      setAuthor(data.username);
      setAuthorImgUrl(data.imgUrl);
      return data;
    } catch (error) {
      console.error('Error fetching post author:', error);
    }
  };

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
        fetchPostAuthor(data.userId).then((authorData) => {
          const newPost = {
            ...data,
            likeCount: 0,
            author: authorData.username,
            authorImgUrl: authorData.imgUrl
          };
          setUserPosts([...userPosts, newPost]);
        });
      })
      .catch(err => showRedNotification(err));
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

      const response = await fetch(`${BASE_URL}/user/updateUser`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(updateData),
      });

      if (response.status === 409) {
        showRedNotification('Username or email already exists');
      }

      const data = await response.json();
      showGreenNotification("Profile updated")
      setUserDetails(data);
      setIsUpdateModalOpen(false);
    } catch (error) {
      console.log(error);
    }
  };

  const handleUpdateProfileImage = async (profilePicture) => {
    const token = localStorage.getItem('token');
    try {
      const formData = new FormData();
      formData.append('profilePicture', profilePicture);

      const response = await fetch(`${BASE_URL}/user/updateProfileImage`, {
        method: 'PUT',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (!response.ok) {
        showRedNotification('Failed to update profile image');
      }

      const updatedData = await response.json();
      showGreenNotification("Profile image updated")
      setUserDetails((prevDetails) => ({
        ...prevDetails,
        img_url: updatedData.img_url,
      }));
    } catch (error) {
      showRedNotification(error);
    }
  };

  const checkFriendshipStatus = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${BASE_URL}/user/checkFriendStatus?userId2=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to check friendship status');
      }

      const isFriendStatus = await response.json();
      setIsFriend(isFriendStatus);
    } catch (error) {
      console.error('Error checking friendship status:', error);
    }
  };

  const handleAddFriend = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${BASE_URL}/user/addFriend?userId2=${userId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        throw new Error('Failed to add friend');
      }
      setIsFriend(1);
    } catch (error) {
      console.error('Error: ', error);
    }
  }
  const Handleunfriend = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${BASE_URL}/user/unfriend?userId2=${userId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (!response.ok) {
        throw new Error('Failed to unfriend');
      }
      setIsFriend(0);
    } catch (error) {
      console.log(error);
    }
  }
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }

    const fetchUserProfile = async () => {
      try {
        const response = await fetch(`${BASE_URL}/user/getUserData?userId=${userId}`, {
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
        console.error(error);
      }
    };

    const fetchUserPosts = async () => {
      try {
        const response = await fetch(`${BASE_URL}/post/getUserPost?userId=${userId}`, {
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
        console.error(error);
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
          <button className="update-profile-button" onClick={() => setIsUpdateModalOpen(true)}>
            <LiaUserEditSolid size={20} />
          </button>
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