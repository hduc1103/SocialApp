import React from 'react';
import { MdOutlineEdit } from "react-icons/md";
import { RiDeleteBin6Line } from "react-icons/ri";
import { MdPageview } from "react-icons/md";

const GetUser = ({ userId, setUserId, getOneUser, userDetails, onEditUser, onDeleteUser, onViewPosts }) => (
  <div className="get-one-user">
    <input
      type="text"
      placeholder="Enter User ID"
      value={userId}  
      onChange={(e) => setUserId(e.target.value)}  
    />
    <button onClick={getOneUser}>Get User Details</button>
    {userDetails && (
      <div className="user-details">
        <p>ID: {userDetails.id}</p>
        <p>Username: {userDetails.username}</p>
        <p>Name: {userDetails.name}</p>
        <p>Email: {userDetails.email}</p>
        <p>Bio: {userDetails.bio}</p>
        <p>Address: {userDetails.address}</p>
        <p>{userDetails.deleted === true ? 'Deleted' : 'Available'}</p>
        <div className="user-actions">
          <button onClick={() => onEditUser(userDetails)}><MdOutlineEdit size={15} /></button>
          <button onClick={() => onDeleteUser(userDetails.id)}><RiDeleteBin6Line size={15} /></button>
          <button onClick={() => onViewPosts(userDetails.id)}><MdPageview size={15} /></button>
        </div>
      </div>
    )}
  </div>
);

export default GetUser;
