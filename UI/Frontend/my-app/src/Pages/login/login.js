import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './login.scss';
import { BASE_URL } from '../../config';
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
        const signUpResponse = await fetch(`${BASE_URL}/user/createUser`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ name, username, password, email, address }),
        });

        if (!signUpResponse.ok) {
          throw new Error('Failed to sign up');
        }

        setIsSignUp(false);
        setError('');
      } catch (error) {
        setError('Failed to sign up. Please try again.');
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
          throw new Error('Invalid credentials');
        }

        const loginData = await loginResponse.json();
        const token = loginData.jwt;
        localStorage.setItem('token', token);

        const roleResponse = await fetch(`${BASE_URL}/user/getUserRole`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!roleResponse.ok) {
          throw new Error('Failed to fetch user role');
        }

        const role = await roleResponse.text();
        localStorage.setItem('role', role);

        const userIdResponse = await fetch(`${BASE_URL}/user/getUserId`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        const userId = await userIdResponse.text();
        localStorage.setItem('userId', userId);

        if (role === 'ADMIN') {
          navigate('/adminpanel');
        } else {
          navigate(`/userprofile/${userId}`);
        }
      } catch (error) {
        setError('Invalid credentials');
        console.error('Login error:', error);
      }
    }
  };

  const handleEmailSubmit = async (submittedEmail) => {
    setEmail(submittedEmail);  

    try {
      const response = await fetch(`${BASE_URL}/user/forgetPassword`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email: submittedEmail }),
      });

      if (!response.ok) {
        throw new Error('Email not found');
      }

      setShowEmailModal(false);
      setShowOtpModal(true);  
    } catch (error) {
      setError('Failed to send OTP. Please try again.');
    }
  };

  const handleOtpSubmit = async (otp) => {
    try {
      const response = await fetch(`${BASE_URL}/user/verifyOtp`, {
        method: 'POST',
        headers: {
          'Content-Type': 'text/plain',
        },
        body: otp,
      });

      if (!response.ok) {
        throw new Error('Invalid OTP');
      }

      setShowOtpModal(false);
      setShowPasswordModal(true);  
    } catch (error) {
      setError('Invalid OTP. Please try again.');
    }
  };

  const handleNewPasswordSubmit = async (newPassword) => {
    try {
      const response = await fetch(`${BASE_URL}/user/resetPassword`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, new_password: newPassword }),  
      });

      if (!response.ok) {
        throw new Error('Failed to reset password');
      }

      setShowPasswordModal(false);
      setError('');
    } catch (error) {
      setError('Failed to reset password. Please try again.');
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

      {/* Email Modal */}
      <EmailModal
        show={showEmailModal}
        onClose={() => setShowEmailModal(false)}
        onSubmit={handleEmailSubmit}
      />

      {/* OTP Modal */}
      <OtpModal
        show={showOtpModal}
        onClose={() => setShowOtpModal(false)}
        onSubmit={handleOtpSubmit}
      />

      {/* New Password Modal */}
      <NewPasswordModal
        show={showPasswordModal}
        onClose={() => setShowPasswordModal(false)}
        onSubmit={handleNewPasswordSubmit}
      />
    </div>
  );
};

export default Login;
