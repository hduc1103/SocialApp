import React from 'react';
import { MdOutlineEdit } from "react-icons/md";
import { RiDeleteBin6Line } from "react-icons/ri";
import { MdPageview } from "react-icons/md";
import './userlist.scss'
const UserList = ({ users, onEditUser, onDeleteUser, onViewPosts }) => (
  <div className="user-list">
    {users.length > 0 &&
      users.map((user) => (
        <div key={user.id} className="user-item">
          <p>ID: {user.id}</p>
          <p>Username: {user.username}</p>
          <p>Email: {user.email}</p>
          <p>Bio: {user.bio}</p>
          <p>Address: {user.address}</p>

          <div className="user-actions">
            <button onClick={() => onEditUser(user)}><MdOutlineEdit size={15}/></button>
            <button onClick={() => onDeleteUser(user.id)}><RiDeleteBin6Line size={15}/></button>
            <button onClick={() => onViewPosts(user.id)}><MdPageview size={15}/></button>
          </div>
        </div>
      ))}
  </div>
);

export default UserList;
