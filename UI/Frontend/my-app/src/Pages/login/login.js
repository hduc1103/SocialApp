import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './login.scss';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/api/authenticate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        throw new Error('Invalid credentials');
      }

      const data = await response.json();
      localStorage.setItem('token', data.token);
      navigate('/dashboard');
    } catch (error) {
      setError('Invalid credentials');
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
