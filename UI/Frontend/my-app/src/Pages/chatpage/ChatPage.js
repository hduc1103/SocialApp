import React, { useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useParams } from 'react-router-dom';
import { BASE_URL, PUBLIC_URL, showGreenNotification } from '../../config';
import './chatpage.scss';

const ChatPage = () => {
  const senderId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');
  const { receiverId } = useParams();

  const [message, setMessage] = useState('');
  const [chatMessages, setChatMessages] = useState([]);
  const [userDetails, setUserDetails] = useState({});
  const [loading, setLoading] = useState(true);
  const stompClient = useRef(null);
  
  useEffect(() => {
    if (senderId && receiverId && token) {
      const fetchChatHistory = async () => {
        try {
          const response = await fetch(`${BASE_URL}/chat/conversation?senderId=${senderId}&receiverId=${receiverId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          if (!response.ok) {
            throw new Error('Failed to fetch chat history');
          }
          const data = await response.json();
          setChatMessages(data);
          const oponents = new Set(data.map(msg => msg.senderId));
          fetchUserDetails(oponents);
        } catch (error) {
          console.error('Failed to load chat history:', error);
        } finally {
          setLoading(false);
        }
      };

      const fetchUserDetails = async (userIds) => {
        try {
          const detailsMap = {};
          for (const id of userIds) {
            if (id !== senderId) {
              const response = await fetch(`${BASE_URL}/user/get-username?userId=${id}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              });
              if (response.ok) {
                const data = await response.json();
                detailsMap[id] = {
                  username: data.username,
                  imgUrl: data.imgUrl,
                };
              }
            }
          }
          setUserDetails(detailsMap);
        } catch (error) {
          console.error('Error fetching user details:', error);
        }
      };

      fetchChatHistory();
      const socket = new SockJS(`${BASE_URL}/ws`);
      stompClient.current = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        onConnect: onConnected,
        onStompError: onError,
      });

      stompClient.current.activate();

      return () => {
        if (stompClient.current) {
          stompClient.current.deactivate();
        }
      };
    } else {
      console.error('Missing senderId, receiverId or token');
      setLoading(false);
    }
  }, [senderId, receiverId, token]);

  const onConnected = () => {
    showGreenNotification('Connected to WebSocket');
    stompClient.current.subscribe(`/user/${senderId}/private`, onMessageReceived);
  };

  const onError = (error) => {
    alert(error)
  };

  const onMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body);
    if (
      (receivedMessage.senderId === senderId && receivedMessage.receiverId === receiverId) ||
      (receivedMessage.senderId === receiverId && receivedMessage.receiverId === senderId)
    ) {
      setChatMessages((prevMessages) => [...prevMessages, receivedMessage]);
    }
  };

  const sendMessage = () => {
    if (stompClient.current && message.trim() !== '') {
      const chatMessage = {
        senderId: senderId,
        receiverId: receiverId,
        content: message,
        status: 'MESSAGE',
      };
      stompClient.current.publish({
        destination: '/app/private-message',
        body: JSON.stringify(chatMessage),
      });
      setChatMessages((prevMessages) => [...prevMessages, chatMessage]);
      setMessage('');
    }
  };

  return (
    <div className="chat-container">
      <div className="chat-box">
        {loading ? (
          <div className="loading-message">Loading...</div>
        ) : (
          <div className="messages-container">
            {chatMessages.length > 0 ? (
              chatMessages.map((msg, index) => (
                <div
                  key={index}
                  className={`message ${msg.senderId === senderId ? 'sent' : 'received'}`}
                >
                  {msg.senderId !== senderId && userDetails[msg.senderId] && (
                    <>
                      <img
                        src={userDetails[msg.senderId]?.imgUrl
                          ? `${PUBLIC_URL}/profile_img_upload/${userDetails[msg.senderId].imgUrl}`
                          : 'https://via.placeholder.com/40'}

                        className="user-avatar-chat"
                      />
                      <strong>{userDetails[msg.senderId].username || 'Loading...'}:</strong>
                    </>
                  )}
                  {msg.senderId === senderId ? (
                    <strong>You:</strong>
                  ) : null}
                  <span> {msg.content}</span>
                </div>
              ))
            ) : (
              <div className="no-messages">No messages yet</div>
            )}
          </div>
        )}
        <div className="send-message">
          <input
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Type a message..."
            className="message-input"
          />
          <button onClick={sendMessage} className="send-button">
            Send
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChatPage;
