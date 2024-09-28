import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './login.scss';
import { BASE_URL } from '../../service/config';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const loginResponse = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!loginResponse.ok) {
        throw new Error('Invalid credentials');
      }

      const loginData = await loginResponse.json();
      const token = loginData.jwt;
      localStorage.setItem('token', token);
            const userIdResponse = await fetch(`${BASE_URL}/user/getUserId`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!userIdResponse.ok) {
        throw new Error('Failed to fetch user ID');
      }

      const userId = await userIdResponse.json();
      localStorage.setItem('userId', userId);

      console.log('Token stored:', localStorage.getItem('token')); 
      console.log('User ID stored:', localStorage.getItem('userId'));
      navigate('/');
    } catch (error) {
      setError('Invalid credentials');
      console.error('Login error:', error);
    }
  };
  

  return (
    <div className="login-page">
      <div className="login-wrapper">
        <div className="login-box">
          <h2 className="login-title">Welcome Back</h2>
          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <input 
                className="login-input"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Username"
                required
              />
            </div>
            <div className="input-group">
              <input 
                className="login-input"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Password"
                required
              />
            </div>
            <button type="submit" className="login-button">Sign In</button>
            <p className="forgot-password">Forgot password?</p>
          </form>
          {error && <p className="login-error">{error}</p>}
        </div>
      </div>
    </div>
  );
};

export default Login;
