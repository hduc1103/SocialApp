import React, { useState, useEffect, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BASE_URL } from '../../config';
import './chatcomponent.scss';

const ChatComponent = ({ senderId }) => {
  const [receiverId, setReceiverId] = useState('');
  const [message, setMessage] = useState('');
  const [chatMessages, setChatMessages] = useState([]);
  const stompClient = useRef(null);

  useEffect(() => {
    // Create a new SockJS connection
    const socket = new SockJS(`${BASE_URL}/ws`);
    // Create a STOMP client using the SockJS connection
    stompClient.current = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: onConnected,
      onStompError: onError,
    });

    // Activate the STOMP client
    stompClient.current.activate();

    return () => {
      // Disconnect on component unmount
      if (stompClient.current) {
        stompClient.current.deactivate();
      }
    };
  }, []);

  const onConnected = () => {
    console.log('Connected to WebSocket');
    stompClient.current.subscribe(`/user/${senderId}/private`, onMessageReceived);
  };

  const onError = (error) => {
    console.error('WebSocket error:', error);
  };

  const onMessageReceived = (payload) => {
    const receivedMessage = JSON.parse(payload.body);
    setChatMessages((prevMessages) => [...prevMessages, receivedMessage]);
  };

  const sendMessage = () => {
    if (stompClient.current && message.trim() !== '' && receiverId.trim() !== '') {
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
      <div className="user-selection">
        <input
          type="text"
          placeholder="Receiver User ID"
          value={receiverId}
          onChange={(e) => setReceiverId(e.target.value)}
          className="receiver-input"
        />
      </div>
      <div className="chat-box">
        <div className="messages-container">
          {chatMessages.map((msg, index) => (
            <div
              key={index}
              className={`message ${msg.senderId === senderId ? 'sent' : 'received'}`}
            >
              <strong>{msg.senderId === senderId ? 'You' : msg.senderId}:</strong> {msg.content}
            </div>
          ))}
        </div>
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

export default ChatComponent;
