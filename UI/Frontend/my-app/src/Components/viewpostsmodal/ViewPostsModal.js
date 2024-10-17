import React, { useEffect, useState } from 'react';
import Post from '../../components/post/Post';
import './viewpostsmodal.scss'
const ViewPostsModal = ({ isOpen, onClose, userId, fetchUserPosts }) => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    if (userId) {
      fetchUserPosts(userId).then(setPosts);
    }
  }, [userId]);

  if (!isOpen) return null;

  return (
    <div className="view-posts-modal-overlay">
      <div className="view-posts-modal-content">
        <button className="view-posts-modal-close-button" onClick={onClose}>
          &times;
        </button>
        <h2>User Posts</h2>
        <div className="view-posts-modal-posts-container">
          {posts.length > 0 ? (
            posts.slice().reverse().map((post) => <Post key={post.id} post={post} />)
          ) : (
            <p>No posts found.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default ViewPostsModal;
