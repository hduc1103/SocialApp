import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { TiDeleteOutline } from "react-icons/ti";
import './searchresult.scss';

const SearchResult = ({ userResults, postResults, onClose, handleNotificationNavigation }) => {
  const navigate = useNavigate();

  const handleUserClick = (userId) => {
    navigate(`/userprofile/${userId}`);
  };

  const handlePostClick = (postId) => {
    handleNotificationNavigation(postId); 
  };

  return (
    <div className="search-result-container">
      <div className="search-result-header">
        <button className="search-close-button" onClick={onClose}>
          <TiDeleteOutline size={24} />
        </button>
      </div>

      {userResults.length > 0 && (
        <div className="result-section">
          <h3>User Results:</h3>
          <ul>
            {userResults.map((user) => (
              <li
                key={user.id}
                className="result-item"
                onClick={() => handleUserClick(user.id)}
              >
                <span className="result-name">{user.name}</span>
                <br />
                <span className="result-email">{user.email}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {postResults.length > 0 && (
        <div className="result-section">
          <h3>Post Results:</h3>
          <ul>
            {postResults.map((post) => (
              <li
                key={post.id}
                className="result-item"
                onClick={() => handlePostClick(post.id)}
              >
                <span className="result-post-content">{post.content}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {userResults.length === 0 && postResults.length === 0 && (
        <div className="no-results">
          <p>No results found.</p>
        </div>
      )}
    </div>
  );
};

export default SearchResult;
