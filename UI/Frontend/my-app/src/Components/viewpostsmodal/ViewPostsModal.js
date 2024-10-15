import React, { useEffect, useState } from 'react';
import Post from '../../components/post/Post';

const ViewPostsModal = ({ isOpen, onClose, userId, fetchUserPosts }) => {
  const [posts, setPosts] = useState([]);

  useEffect(() => {
    if (userId) {
      fetchUserPosts(userId).then(setPosts);
    }
  }, [userId]);

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>User Posts</h2>
        <div className="posts-container">
          {posts.length > 0 ? (
            posts.map((post) => <Post key={post.id} post={post} />)
          ) : (
            <p>No posts found.</p>
          )}
        </div>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
};

export default ViewPostsModal;
