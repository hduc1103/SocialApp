import React from 'react';

const GetUser = ({ userId, setUserId, getOneUser, userDetails }) => (
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
        <p>Email: {userDetails.email}</p>
        <p>Bio: {userDetails.bio}</p>
        <p>Address: {userDetails.address}</p>
      </div>
    )}
  </div>
);

export default GetUser;
