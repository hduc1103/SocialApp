import React, { useEffect, useState } from 'react';
import { BASE_URL, showRedNotification } from '../../config';
import { useNavigate } from 'react-router-dom';
import './conversationspage.scss';

const ConversationsPage = () => {
  const [conversations, setConversations] = useState([]);
  const [usernames, setUsernames] = useState({});
  const [friends, setFriends] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

  useEffect(() => {
    if (!token) {
      navigate('/login');
      showRedNotification('You must log in first');
    }
  }, [token, navigate]);
  

  useEffect(() => {
    if (userId && token) {
      const fetchConversations = async () => {
        try {
          const response = await fetch(`${BASE_URL}/chat/conversations?userId=${userId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (!response.ok) {
            if(response.status === 401){
              navigate('/login');
              showRedNotification('You must log in first');
              return;
            } else if(response.status === 403){
              navigate('/login');
              return;
            }
            throw new Error('Failed to fetch conversations');
          }
          const data = await response.json();
          setConversations(data);
          fetchUsernames(data);
        } catch (error) {
          console.error('Error fetching conversations:', error);
        }
      };

      const fetchUsernames = async (userIds) => {
        try {
          const usernamesMap = {};
          for (const id of userIds) {
            const response = await fetch(`${BASE_URL}/user/get-username?userId=${id}`, {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });
            if (response.ok) {
              const { username } = await response.json();
              usernamesMap[id] = username;
            }
          }
          setUsernames(usernamesMap);
        } catch (error) {
          console.error('Error fetching usernames:', error);
        }
      };

      fetchConversations();
    }
  }, [userId, token, navigate]);

  const openChat = (receiverId) => {
    navigate(`/chat/${receiverId}`);
  };

  const fetchFriends = async () => {
    if (userId && token) {
      try {
        const response = await fetch(`${BASE_URL}/user/get-all-friends`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (!response.ok) {
          throw new Error('Failed to fetch friends');
        }
        const data = await response.json();
        setFriends(data);
        setShowModal(true);
      } catch (error) {
        console.error('Error fetching friends:', error);
      }
    }
  };

  const startNewConversation = (friendId) => {
    setShowModal(false);
    navigate(`/chat/${friendId}`);
  };

  return (
    <div className="conversations-container">
      <h2>Your Conversations</h2>
      <button onClick={fetchFriends} className="add-conversation-button">Add Conversation</button>
      <ul className="conversations-list">
        {conversations.map((receiverId, index) => (
          <li key={index} className="conversation-item" onClick={() => openChat(receiverId)}>
            Conversation with <span className="receiver-id">{usernames[receiverId]}</span>
          </li>
        ))}
      </ul>
      {showModal && (
        <div className="modal">
          <div className="modal-content">
          <h3>Select a Friend to Start a New Conversation</h3>
            <ul className="friend-list">
              {friends.length > 0 ? (
                friends.map((friend) => (
                  <li key={friend.userId} className="friend-item" onClick={() => startNewConversation(friend.id)}>
                    <img src={`data:image/png;base64,${friend.img_url}`} alt={`${friend.username}'s avatar`} className="friend-avatar" />
                    <span className="friend-username">{friend.name}</span>
                  </li>
                ))
              ) : (
                <p className="no-friends-message">Add a friend to start chatting!</p>
              )}
            </ul>
            <button onClick={() => setShowModal(false)} className="close-modal-button">Close</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ConversationsPage;
