import React, { useState } from 'react';
import Post from './Post';

const Feed = ({ posts }) => {
  const [postsState, setPostsState] = useState(posts);

  const handleLike = (postId) => {
    setPostsState(postsState.map(post =>
      post.id === postId ? { ...post, likes: post.likes + 1 } : post
    ));
  };

  const handleComment = (postId) => {
    const commentText = prompt('Enter your comment:');
    if (commentText) {
      setPostsState(postsState.map(post =>
        post.id === postId
          ? { ...post, comments: [...post.comments, { id: Date.now(), author: 'User', text: commentText }] }
          : post
      ));
    }
  };

  return (
    <div className="feed">
      {postsState.map(post => (
        <Post key={post.id} post={post} onLike={handleLike} onComment={handleComment} />
      ))}
    </div>
  );
};

export default Feed;
