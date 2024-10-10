import React, { useState } from 'react';
import './otpmodal.scss'
const OtpModal = ({ show, onClose, onSubmit }) => {
  const [otp, setOtp] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(otp); 
  };

  if (!show) return null;

  return (
    <div className="otp-modal">
      <div className="otp-modal-content">
        <h3 className="otp-modal-title">Enter OTP</h3>
        <form onSubmit={handleSubmit}>
          <div className="otp-input-group">
            <input
              type="text"
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
              className="otp-input"
              placeholder="OTP"
              required
            />
          </div>
          <button type="submit" className="otp-modal-submit">Submit</button>
          <button type="button" onClick={onClose} className="otp-modal-close">Close</button>
        </form>
      </div>
    </div>
  );
};

export default OtpModal;
