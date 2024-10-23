import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { FaHome, FaUserCircle } from 'react-icons/fa';
import { RiAdminFill } from 'react-icons/ri';
import { MdSupportAgent } from "react-icons/md";
import { IoIosLogOut, IoIosLogIn, IoIosNotifications } from "react-icons/io";
import { AiFillMessage } from "react-icons/ai";
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { BASE_URL, showBlueNotification } from '../../config';
import SearchResult from '../../components/searchresult/SearchResult';
import './header.scss';

const Header = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [userResults, setUserResults] = useState([]);
  const [postResults, setPostResults] = useState([]);
  const [isLoggedin, setIsLoggedin] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const [searchPerformed, setSearchPerformed] = useState(false);
  const stompClient = useRef(null);
  const [notifications, setNotifications] = useState([]);
  const [newNotificationCount, setNewNotificationCount] = useState(0);
  const [showNotifications, setShowNotifications] = useState(false);

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
      connectToWebSocket();
    } else {
      setIsLoggedin(false);
      setIsAdmin(false);
    }

    setSearchPerformed(false);
    setUserResults([]);
    setPostResults([]);
  }, [navigate]);

  const connectToWebSocket = () => {
    const socket = new SockJS(`${BASE_URL}/ws`);
  
    stompClient.current = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
    });
    stompClient.current.onConnect = (frame) => {
      console.log("WebSocket connected: ", frame);
      stompClient.current.subscribe(`/user/${userId}/queue/notifications`, (message) => {
        try {
          const notification = JSON.parse(message.body);
          console.log("Notification received as JSON:", notification);
          showBlueNotification(notification);
        } catch (error) {
          console.log("Notification received as plain text:", message.body);
          showBlueNotification(message.body);
        }
        setNewNotificationCount((prevCount) => prevCount + 1);
      });

    };
  
    stompClient.current.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };
  
    stompClient.current.activate();
  };  

  const handleNotificationClick = async () => {
    setShowNotifications(!showNotifications);
    setNewNotificationCount(0);

    const token = localStorage.getItem('token');
    const response = await fetch(`${BASE_URL}/user/get-notification`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      setNotifications(data);
    } else {
      console.error('Failed to fetch notifications');
    }
  };

  const handleSupportNavigation = () => {
    if (isAdmin) {
      navigate('/admin/ticketsupport');
    } else {
      navigate('/ticketsupport');
    }
  };

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
      setSearchTerm('');
    } catch (error) {
      console.error('Error during search:', error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    setIsLoggedin(false);
    setIsAdmin(false);
    if (stompClient.current) {
      stompClient.current.deactivate();
    }
    navigate('/login');
  };
  const handleNotificationNavigation = (notification) => {
    if (notification.type === 'post') {
      navigate(`/userprofile/${notification.userId}`, { state: { scrollToPostId: notification.relatedId } });
    } else if (notification.type === 'comment') {
      navigate(`/userprofile/${notification.userId}`, { state: { scrollToCommentId: notification.relatedId } });
    }
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
              <span>Dashboard</span>
            </div>
            <div className="nav-item" onClick={() => navigate(`/userprofile/${userId}`)}>
              <FaUserCircle size={24} />
              <span>Profile</span>
            </div>
            <div className="nav-item" onClick={() => navigate(`/conversation`)}>
              <AiFillMessage size={24} />
              <span>Chat</span>
            </div>
            <div className="search-bar nav-search">
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search"
            />
            <div className="nav-item search-nav">
              <button onClick={handleSearch}>
                Search
              </button>
            </div>
          </div>
            <div className={`nav-item ${showNotifications ? 'show-dropdown' : ''}`} onClick={handleNotificationClick}>
              <IoIosNotifications size={24} />
              {newNotificationCount > 0 && <span className="notification-dot"></span>}
              <span>Notifications</span>
              {showNotifications && (
                <div className="notification-dropdown">
                  {notifications.length > 0 ? (
                    <ul>
                    {notifications.map((notification) => (
                      <li key={notification.id} onClick={() => handleNotificationNavigation(notification)}>
                        <img src={`data:image/png;base64,${notification.imgUrl}`} alt="Sender Avatar" className="sender-avatar" />
                        <div className="notification-content">
                          {notification.content}
                          <span className="notification-time">{new Date(notification.createdAt).toLocaleString()}</span>
                        </div>
                      </li>
                    ))}
                  </ul>
                  ) : (
                    <p>No notifications yet.</p>
                  )}
                </div>
              )}
            </div>

            <div className="nav-item" onClick={handleSupportNavigation}>
              <MdSupportAgent size={24} />
              <span>Support</span>
            </div>
            {isAdmin && (
              <div className="nav-item" onClick={() => navigate('/adminpanel')}>
                <RiAdminFill size={24} />
                <span>Admin</span>
              </div>
            )}
          </nav>
          
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
