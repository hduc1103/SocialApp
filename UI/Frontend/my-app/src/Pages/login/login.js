import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './login.scss';
import { BASE_URL, showRedNotification, showGreenNotification } from '../../config';
import EmailModal from '../../components/emailmodal/EmailModal';
import OtpModal from '../../components/otpmodal/OtpModal';
import NewPasswordModal from '../../components/newpasswordmodal/NewPasswordModal';

const Login = () => {
  const [isSignUp, setIsSignUp] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [address, setAddress] = useState('');
  const [error, setError] = useState('');
  const [showEmailModal, setShowEmailModal] = useState(false);
  const [showOtpModal, setShowOtpModal] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (isSignUp) {
      try {
        const signUpResponse = await fetch(`${BASE_URL}/user/create-user`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ name, username, password, email, address }),
        });

        if (!signUpResponse.ok) {
          const errorData = await signUpResponse.json();
          showRedNotification(errorData.message || 'Failed to sign up');
          return;
        }

        showGreenNotification('Sign up successful! Please log in.');
        setIsSignUp(false);
        setError('');
      } catch (error) {
        showRedNotification('Failed to sign up. Please try again.');
        console.error('Sign-up error:', error);
      }
    } else {
      try {
        const loginResponse = await fetch(`${BASE_URL}/user/login`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ username, password }),
        });

        if (!loginResponse.ok) {
          const errorData = await loginResponse.json();
          showRedNotification(errorData.message || 'Invalid credentials');
          return;
        }

        const loginData = await loginResponse.json();
        const token = loginData.jwt;
        localStorage.setItem('token', token);

        const roleResponse = await fetch(`${BASE_URL}/user/get-user-role`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!roleResponse.ok) {
          showRedNotification('Failed to fetch user role');
          return;
        }

        const role = await roleResponse.text();
        localStorage.setItem('role', role);

        const userIdResponse = await fetch(`${BASE_URL}/user/get-user-id`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!userIdResponse.ok) {
          showRedNotification('Failed to fetch user ID');
          return;
        }

        const userId = await userIdResponse.text();
        localStorage.setItem('userId', userId);

        showGreenNotification('Login successful!');

        if (role === 'ADMIN') {
          navigate('/adminpanel');
        } else {
          navigate(`/userprofile/${userId}`);
        }
      } catch (error) {
        showRedNotification('Invalid credentials');
        console.error('Login error:', error);
      }
    }
  };

  const handleEmailSubmit = async (submittedEmail) => {
    setEmail(submittedEmail);

    try {
      const response = await fetch(`${BASE_URL}/user/forget-password`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: submittedEmail }),
      });

      if (!response.ok) {
        showRedNotification('Email not found');
        return;
      }

      setShowEmailModal(false);
      setShowOtpModal(true);
    } catch (error) {
      showRedNotification('Failed to send OTP. Please try again.');
      console.error('Error sending OTP:', error);
    }
  };

  const handleOtpSubmit = async (otp) => {
    try {
      const response = await fetch(`${BASE_URL}/user/verify-otp`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
        },
        body: otp,
      });

      if (!response.ok) {
        showRedNotification('Invalid OTP');
        return;
      }

      setShowOtpModal(false);
      setShowPasswordModal(true);
    } catch (error) {
      showRedNotification('Invalid OTP. Please try again.');
      console.error('OTP verification error:', error);
    }
  };

  const handleNewPasswordSubmit = async (newPassword) => {
    try {
      const response = await fetch(`${BASE_URL}/user/reset-password`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, new_password: newPassword }),
      });

      if (!response.ok) {
        showRedNotification('Failed to reset password');
        return;
      }

      setShowPasswordModal(false);
      showGreenNotification('Password reset successful! Please log in.');
    } catch (error) {
      showRedNotification('Failed to reset password. Please try again.');
      console.error('Password reset error:', error);
    }
  };

  return (
    <div className="login-page">
      <div className="login-wrapper">
        <div className="login-box">
          <h2 className="login-title">{isSignUp ? 'Create an Account' : 'Welcome Back'}</h2>
          <form onSubmit={handleSubmit}>
            {isSignUp && (
              <>
                <div className="input-group">
                  <input
                    className="login-input"
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Name"
                    required
                  />
                </div>
                <div className="input-group">
                  <input
                    className="login-input"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="Email"
                    required
                  />
                </div>
                <div className="input-group">
                  <input
                    className="login-input"
                    type="text"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    placeholder="Address"
                    required
                  />
                </div>
              </>
            )}
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
            <button type="submit" className="login-button">
              {isSignUp ? 'Sign Up' : 'Sign In'}
            </button>
            <p className="forgot-password" onClick={() => setShowEmailModal(true)}>
              Forgot password?
            </p>
          </form>
          {error && <p className="login-error">{error}</p>}
          <div className="toggle-login-signup" onClick={() => setIsSignUp(!isSignUp)}>
            {isSignUp ? 'Already have an account? Sign In' : 'Don’t have an account? Sign Up'}
          </div>
        </div>
      </div>

      <EmailModal
        show={showEmailModal}
        onClose={() => setShowEmailModal(false)}
        onSubmit={handleEmailSubmit}
      />

      <OtpModal
        show={showOtpModal}
        onClose={() => setShowOtpModal(false)}
        onSubmit={handleOtpSubmit}
      />

      <NewPasswordModal
        show={showPasswordModal}
        onClose={() => setShowPasswordModal(false)}
        onSubmit={handleNewPasswordSubmit}
      />
    </div>
  );
};

export default Login;
