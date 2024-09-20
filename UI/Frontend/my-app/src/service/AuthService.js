import api from './api';

const login = async (username, password) => {
  const response = await api.post('/api/authenticate', { username, password });
  localStorage.setItem('token', response.data.token);
};

const logout = () => {
  localStorage.removeItem('token');
};

const isAuthenticated = () => {
  return !!localStorage.getItem('token');
};

export { login, logout, isAuthenticated };
