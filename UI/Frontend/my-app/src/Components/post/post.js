import React, { useState } from 'react';
import Comment from './Comment';

const Post = ({ post, onLike, onComment }) => {
  const [showComments, setShowComments] = useState(false);

  return (
    <div className="post">
      <h2>{post.author}</h2>
      <p>{post.content}</p>
      <button onClick={() => onLike(post.id)}>Like</button>
      <button onClick={() => setShowComments(!showComments)}>
        {showComments ? 'Hide Comments' : 'Show Comments'}
      </button>
      {showComments && (
        <div>
          {post.comments.map(comment => (
            <Comment key={comment.id} comment={comment} />
          ))}
          <button onClick={() => onComment(post.id)}>Add Comment</button>
        </div>
      )}
    </div>
  );
};

export default Post;
