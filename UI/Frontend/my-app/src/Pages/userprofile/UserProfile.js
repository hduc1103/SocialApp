import React from 'react';
import Feed from '../components/Feed';

const UserProfile = () => {
  const userPosts = [
    { id: 1, author: 'User', content: 'My first post', likes: 5, comments: [] },
  ];

  return (
    <div className="user-profile">
      <h1>User Profile</h1>
      <Feed posts={userPosts} />
    </div>
  );
};

export default UserProfile;


