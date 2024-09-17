import React from 'react';
import { Link } from 'react-router-dom';

const Header = () => (
  <header>
    <h1>Social Media App</h1>
    <nav>
      <Link to="/">Home</Link>
      <Link to="/profile">Profile</Link>
    </nav>
  </header>
);

export default Header;
