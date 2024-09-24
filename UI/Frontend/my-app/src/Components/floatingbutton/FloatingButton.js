import React from 'react';
import { FaPlus } from 'react-icons/fa';
import './floatingButton.scss';

const FloatingButton = ({ onClick }) => {
  return (
    <div className="floating-button" onClick={onClick}>
      <FaPlus size={24} color="#fff" />
    </div>
  );
};

export default FloatingButton;
