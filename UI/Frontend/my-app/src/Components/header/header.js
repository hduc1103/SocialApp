import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaHome, FaSearch, FaBell, FaUserCircle } from 'react-icons/fa';
import { BASE_URL } from '../../service/config';
import './header.scss';

const Header = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [userResults, setUserResults] = useState([]);
  const [postResults, setPostResults] = useState([]);
  const navigate = useNavigate();

  const handleSearch = async () => {
    if (searchTerm.trim() !== '') {
      try {
        const response = await fetch(`${BASE_URL}/search/combined?keyword=${searchTerm}`);
        const data = await response.json();
        setUserResults(data.users);
        setPostResults(data.posts);
      } catch (error) {
        console.error('Error during search:', error);
      }
    }
  };

  return (
    <header className="header">
      <div className="header-container">
        <div className="header-logo" onClick={() => navigate('/home')}>
          MyApp
        </div>
        <nav className="header-nav">
          <div className="nav-item" onClick={() => navigate('/home')}>
            <FaHome size={24} />
            <span>Home</span>
          </div>
          <div className="nav-item" onClick={() => navigate('/search')}>
            <FaSearch size={24} />
            <span>Search</span>
          </div>
          <div className="nav-item" onClick={() => navigate('/notifications')}>
            <FaBell size={24} />
            <span>Notifications</span>
          </div>
          <div className="nav-item" onClick={() => navigate('/profile')}>
            <FaUserCircle size={24} />
            <span>Profile</span>
          </div>
        </nav>
        <div className="search-bar">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search users or posts..."
          />
          <button onClick={handleSearch}>Search</button>
        </div>
      </div>
      {userResults.length > 0 && (
        <div className="search-results">
          <h4>User Results:</h4>
          <ul>
            {userResults.map((user) => (
              <li key={user.id}>{user.username}</li>
            ))}
          </ul>
        </div>
      )}

      {postResults.length > 0 && (
        <div className="search-results">
          <h4>Post Results:</h4>
          <ul>
            {postResults.map((post) => (
              <li key={post.id}>{post.content}</li>
            ))}
          </ul>
        </div>
      )}
    </header>
  );
};

export default Header;
