import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login'; 
import Dashboard from './pages/Dashboard';
import PrivateRoute from './components/PrivateRoute';
import Header from './components/header/header'; 
import Home from './pages/home/Home';  
import UserProfile from './pages/userprofile/UserProfile'; 

const App = () => {
  return (
    <Router>
      <Header /> {/* Include the header */}
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={
          <PrivateRoute>
            <Dashboard />
          </PrivateRoute>
        } />
        <Route path="/" element={<Home />} />
        <Route path="/profile" element={
          <PrivateRoute>
            <UserProfile />
          </PrivateRoute>
        } />
      </Routes>
    </Router>
  );
};

export default App;
