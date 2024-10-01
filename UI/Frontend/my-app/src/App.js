import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/login/login'; 
import Dashboard from './pages/dashboard/Dashboard';
import PrivateRoute from './components/PrivateRoute';
import Header from './components/header/header'; 
import AdminPanel from './pages/adminpanel/AdminPanel';  
import UserProfile from './pages/userprofile/UserProfile'; 

const App = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/adminpanel" element={<AdminPanel/>}/>
        <Route path="/userprofile/:userId" element={<UserProfile />} />
      </Routes>
    </Router>
  );
};

export default App;
