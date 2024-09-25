import React from 'react';
import './searchresult.scss';

const SearchResult = ({ userResults, postResults }) => {
  return (
    <div className="search-result-container">
      {userResults.length > 0 && (
        <div className="result-section">
          <h3>User Results</h3>
          <ul>
            {userResults.map((user) => (
              <li key={user.id} className="result-item">
                <span className="result-name">{user.username}</span>
                <span className="result-email">{user.email}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {postResults.length > 0 && (
        <div className="result-section">
          <h3>Post Results</h3>
          <ul>
            {postResults.map((post) => (
              <li key={post.id} className="result-item">
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
