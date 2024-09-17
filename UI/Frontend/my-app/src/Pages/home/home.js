import React from 'react';
import Feed from '../../Components/feed/feed';

const Home = () => {
  const samplePosts = [
    { id: 1, author: 'User1', content: 'This is a post', likes: 0, comments: [] },
    // Add more sample posts
  ];

  return (
    <div className="home">
      <Feed posts={samplePosts} />
    </div>
  );
};

export default Home;
