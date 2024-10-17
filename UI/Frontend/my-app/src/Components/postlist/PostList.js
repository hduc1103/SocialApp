import React from 'react';
import Post from '../../components/post/Post';

const PostList = ({ posts, onDeletePost, userId ,Name, loggedInUserId }) => {
  return (
    <div className="posts-section">
      <h2>{userId === loggedInUserId ? 'Your Posts' : `${Name}'s Posts`}</h2>
      {posts.length > 0 ? (
        <div className="post-list">
          {posts.slice().reverse().map((post) => (
            <Post key={post.id} post={post} onDeletePost={onDeletePost} />
          ))}
        </div>
      ) : (
        <p>No posts to display.</p>
      )}
    </div>
  );
};

export default PostList;
