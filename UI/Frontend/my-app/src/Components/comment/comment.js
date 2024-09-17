import React from 'react';

const Comment = ({ comment }) => (
  <div className="comment">
    <p>{comment.author}: {comment.text}</p>
  </div>
);

export default Comment;
