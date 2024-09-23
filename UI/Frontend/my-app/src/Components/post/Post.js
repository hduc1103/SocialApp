import React, { useState } from 'react';
import './post.scss'; 

const Post = ({ post }) => {
  const [likes, setLikes] = useState(0);
  const [comment, setComment] = useState('');
  const [comments, setComments] = useState(post.comments || []);

  const handleLike = () => {
    setLikes(likes + 1);
  };

  const handleComment = (e) => {
    e.preventDefault();
    if (comment.trim()) {
      setComments([...comments, { text: comment }]);
      setComment('');
    }
  };

  return (
    <div className="post-card">
      <p>{post.content}</p>
      <div className="post-actions">
        <button onClick={handleLike}>Like ({likes})</button>
        <form onSubmit={handleComment}>
          <input
            type="text"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Add a comment"
          />
          <button type="submit">Comment</button>
        </form>
      </div>
      <div className="post-comments">
        <h4>Comments:</h4>
        {comments.length > 0 ? (
          <ul>
            {comments.map((comment, index) => (
              <li key={index}>{comment.text}</li>
            ))}
          </ul>
        ) : (
          <p>No comments yet</p>
        )}
      </div>
    </div>
  );
};

export default Post;
