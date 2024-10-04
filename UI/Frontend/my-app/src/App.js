import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/login/login'; 
import Header from './components/header/header'; 
import AdminPanel from './pages/adminpanel/AdminPanel';  
import UserProfile from './pages/userprofile/UserProfile'; 
import SupportTicketPage from './pages/supportticketpage/SupportTicketPage';
import AdminSupportTicketPage from './pages/adminsupportticket/AdminTicketPage';
import Dashboard from './pages/dashboard/Dashboard';
import ChatComponent from './components/chatcomponent/ChatComponent';
const App = () => {
  return (
    <Router>
      <Header />
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path='/' element={< Dashboard />}/>
        <Route path="/adminpanel" element={<AdminPanel/>}/>
        <Route path="/userprofile/:userId" element={<UserProfile />} />
        <Route path="/ticketsupport" element={<SupportTicketPage/>} />
        <Route path="/admin/ticketsupport" element={<AdminSupportTicketPage/>}/>
        <Route path="/chat" element={<ChatComponent/>}/>
      </Routes>
    </Router>
  );
};

export default App;

