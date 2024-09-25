import React, { useState } from 'react';
import './addnewpost.scss';

const AddNewPost = ({ isOpen, onClose, onSubmit }) => {
  const [postContent, setPostContent] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (postContent.trim()) {
      onSubmit(postContent);
      setPostContent(''); 
      onClose(); 
    }
  };

  if (!isOpen) {
    return null; 
  }

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>Add New Post</h2>
          <button className="close-button" onClick={onClose}>X</button>
        </div>
        <form onSubmit={handleSubmit}>
          <textarea
            placeholder="What's on your mind?"
            value={postContent}
            onChange={(e) => setPostContent(e.target.value)}
            required
          />
          <button type="submit" className="submit-button">Post</button>
        </form>
      </div>
    </div>
  );
};

export default AddNewPost;
