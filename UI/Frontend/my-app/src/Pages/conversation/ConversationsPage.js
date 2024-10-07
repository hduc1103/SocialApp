import React, { useEffect, useState } from 'react';
import { BASE_URL } from '../../config';
import { useNavigate } from 'react-router-dom';
import './conversationspage.scss';

const ConversationsPage = () => {
  const [conversations, setConversations] = useState([]);
  const [usernames, setUsernames] = useState({});
  const navigate = useNavigate();

  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

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
            const response = await fetch(`${BASE_URL}/user/getUsername?userId=${id}`, {
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
  }, [userId, token]);

  const openChat = (receiverId) => {
    navigate(`/chat/${receiverId}`);
  };

  return (
    <div className="conversations-container">
      <h2>Your Conversations</h2>
      <ul className="conversations-list">
        {conversations.map((receiverId, index) => (
          <li key={index} className="conversation-item" onClick={() => openChat(receiverId)}>
            Conversation with <span className="receiver-id"> {usernames[receiverId]}</span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ConversationsPage;
