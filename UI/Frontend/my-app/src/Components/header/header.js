import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaHome, FaSearch, FaBell, FaUserCircle } from 'react-icons/fa';
import { RiAdminFill } from 'react-icons/ri';
import { MdSupportAgent } from "react-icons/md";
import { BASE_URL } from '../../service/config';
import { IoIosLogOut, IoIosLogIn } from "react-icons/io";
import SearchResult from '../../components/searchresult/SearchResult';
import './header.scss';

const Header = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [userResults, setUserResults] = useState([]);
  const [postResults, setPostResults] = useState([]);
  const [isLoggedin, setIsLoggedin] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [searchPerformed, setSearchPerformed] = useState(false);
  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const token = localStorage.getItem('token');
    const role = localStorage.getItem('role');

    if (token) {
      setIsLoggedin(true);
      if (role === 'ADMIN') {
        setIsAdmin(true);
      } else {
        setIsAdmin(false);
      }
    } else {
      setIsLoggedin(false);
      setIsAdmin(false);
    }
    setSearchPerformed(false);
    setUserResults([]);
    setPostResults([]);
  }, [navigate]);

  const handleSearch = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${BASE_URL}/user/search?keyword=${searchTerm}`, {
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

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    setIsLoggedin(false);
    setIsAdmin(false);
    navigate('/login');
  };

  const handleCloseSearchResult = () => {
    setSearchPerformed(false);
    setUserResults([]);
    setPostResults([]);
  };

  return (
    <>
      <header className="header">
        <div className="header-container">
          <div className="header-logo" onClick={() => navigate('/')}>
            Thread
          </div>
          <nav className="header-nav">
            <div className="nav-item" onClick={() => navigate('/')}>
              <FaHome size={24} />
              <span>Home</span>
            </div>
            <div className="nav-item" onClick={() => navigate('/notifications')}>
              <FaBell size={24} />
              <span>Notifications</span>
            </div>
            <div className="nav-item" onClick={() => navigate(`/userprofile/${userId}`)}>
              <FaUserCircle size={24} />
              <span>Profile</span>
            </div>
            <div className = "nav-item" onClick={() => navigate(`/`)} >
            <MdSupportAgent size = {24}/>
            <span>Support</span>
            </div>
            {isAdmin && (
              <div className="nav-item" onClick={() => navigate('/admin')}>
                <RiAdminFill size={24} />
                <span>Admin</span>
              </div>
            )}
          </nav>
          <div className="search-bar nav-search">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search"
            />
            <div className="nav-item search-nav">
              <button onClick={handleSearch}>
                <FaSearch size={24} /> Search
              </button>
            </div>
          </div>
          <div className="nav-item nav-logout" onClick={isLoggedin ? handleLogout : () => navigate('/login')}>
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
        <SearchResult
          userResults={userResults}
          postResults={postResults}
          onClose={handleCloseSearchResult}
        />
      )}
    </>
  );
};

export default Header;
