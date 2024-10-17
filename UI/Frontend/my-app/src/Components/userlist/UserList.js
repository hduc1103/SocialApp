import React from 'react';
import { MdOutlineEdit } from "react-icons/md";
import { RiDeleteBin6Line } from "react-icons/ri";
import { MdPageview } from "react-icons/md";
import { IoCloseSharp } from "react-icons/io5";
import './userlist.scss';

const UserList = ({ users, onEditUser, onDeleteUser, onViewPosts, onCloseList }) => (
  <div className="user-list">
    <button className="close-list-btn" onClick={onCloseList}>
      <IoCloseSharp size={20} />
    </button>
    {users.length > 0 &&
      users.map((user) => (
        <div key={user.id} className="user-item">
          <p>ID: {user.id}</p>
          <p>Username: {user.username}</p>
          <p>Name: {user.name}</p>
          <p>Email: {user.email}</p>
          <p>Bio: {user.bio}</p>
          <p>Address: {user.address}</p>
          <div className="user-actions">
            <button onClick={() => onEditUser(user.id)}><MdOutlineEdit size={15} /></button>
            <button onClick={() => onDeleteUser(user.id)}><RiDeleteBin6Line size={15} /></button>
            <button onClick={() => onViewPosts(user.id)}><MdPageview size={15} /></button>
          </div>
        </div>
      ))}
  </div>
);

export default UserList;
