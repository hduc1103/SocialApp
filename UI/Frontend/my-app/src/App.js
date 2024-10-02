import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/login/login'; 
import Header from './components/header/header'; 
import AdminPanel from './pages/adminpanel/AdminPanel';  
import UserProfile from './pages/userprofile/UserProfile'; 
import SupportTicketPage from './pages/supportticketpage/SupportTicketPage';
import AdminSupportTicketPage from './pages/adminsupportticket/AdminTicketPage';

const App = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/adminpanel" element={<AdminPanel/>}/>
        <Route path="/userprofile/:userId" element={<UserProfile />} />
        <Route path="/ticketsupport" element={<SupportTicketPage/>} />
        <Route path="/admin/ticketsupport" element={<AdminSupportTicketPage/>}/>
      </Routes>
    </Router>
  );
};

export default App;
