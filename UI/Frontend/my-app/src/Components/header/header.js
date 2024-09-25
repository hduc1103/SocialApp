import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaHome, FaSearch, FaBell, FaUserCircle } from 'react-icons/fa';
import { BASE_URL } from '../../service/config';
import { IoIosLogOut, IoIosLogIn } from "react-icons/io";
import SearchResult from '../../components/searchresult/SearchResult';

import './header.scss';

const Header = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [userResults, setUserResults] = useState([]);
  const [postResults, setPostResults] = useState([]);
  const [isLoggedin, setIsLoggedin] = useState(false);
  const [searchPerformed, setSearchPerformed] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setIsLoggedin(true);
    } else {
      setIsLoggedin(false);
    }
  }, []);

  const handleSearch = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${BASE_URL}/user/search/result?keyword=${searchTerm}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch search results');
      }

      const data = await response.json();
      setUserResults(data.users);
      setPostResults(data.posts);
      setSearchPerformed(true);
    } catch (error) {
      console.error('Error during search:', error);
    }
  };

  const handleLogout = async () => {
    localStorage.removeItem('token');
    setIsLoggedin(false);
    navigate('/login');
  }

  return (
    <>
      <header className="header">
        <div className="header-container">
          <div className="header-logo" onClick={() => navigate('/home')}>
            Thread
          </div>
          <nav className="header-nav">
            <div className="nav-item" onClick={() => navigate('/home')}>
              <FaHome size={24} />
              <span>Home</span>
            </div>
            <div className="nav-item" onClick={() => navigate('/notifications')}>
              <FaBell size={24} />
              <span>Notifications</span>
            </div>
            <div className="nav-item" onClick={() => navigate('/')}>
              <FaUserCircle size={24} />
              <span>Profile</span>
            </div>
          </nav>
          <div className="search-bar">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search"
            />
            <div className="nav-item search-nav">
              <button onClick={() => {
                navigate('/search');
                handleSearch();
              }}>
                <FaSearch size={24} /> Search
              </button>
            </div>

          </div>
          <div className="nav-item" onClick={isLoggedin ? handleLogout : () => navigate('/login')}>
            {isLoggedin ? (
              <>
                <IoIosLogOut size={24} />
                <span>Log out</span>
              </>
            ) : (
              <>
                <IoIosLogIn size={24} />
                <span>Log in</span>
              </>
            )}
          </div>
        </div>
      </header>
      {searchPerformed && (
        <SearchResult userResults={userResults} postResults={postResults} />
      )}
    </>
  );
};

export default Header;
