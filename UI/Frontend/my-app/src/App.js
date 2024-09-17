import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Components/header/header'; 
import Home from './Pages/home/home'; 
import UserProfile from './Pages/userprofile/UserProfile'; 

const App = () => (
  <Router>
    <Header />
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/profile" element={<UserProfile />} />
    </Routes>
  </Router>
);

export default App;
