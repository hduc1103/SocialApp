import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/login/login'; 
import Header from './components/header/header'; 
import AdminPanel from './pages/adminpanel/AdminPanel';  
import UserProfile from './pages/userprofile/UserProfile'; 
import SupportTicketPage from './pages/supportticketpage/SupportTicketPage';

const App = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/adminpanel" element={<AdminPanel/>}/>
        <Route path="/userprofile/:userId" element={<UserProfile />} />
        <Route path="/ticketsupport" element={<SupportTicketPage/>} />
      </Routes>
    </Router>
  );
};

export default App;
