import React from 'react';
import { useNavigate } from 'react-router-dom';
import { TiDeleteOutline } from "react-icons/ti";
import { BASE_URL } from '../../config';
import './searchresult.scss';

const SearchResult = ({ userResults, postResults, onClose, handleSearchNavigation }) => {
  const navigate = useNavigate();
  const handleUserClick = (userId) => {
    navigate(`/userprofile/${userId}`);
  };
  const handleFetchUserId = async (postId) => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${BASE_URL}/post/get-userId-by-postId?postId=${postId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
  
      if (response.ok) {
        const userId = await response.text(); 
        return userId; 
      } else {
        console.error('Failed to fetch post ID:', response.statusText);
        return null; 
      }
    } catch (error) {
      console.error('Error fetching post ID:', error);
      return null; 
    }
  };
  
  const handlePostClick = async(postId) => {
    const userId = await handleFetchUserId(postId);
    handleSearchNavigation(userId, postId); 
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
