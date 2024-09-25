import { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config';
import { useNavigate } from 'react-router-dom';
import './post.scss';

const PostComponent = ({ post }) => {
  const [likes, setLikes] = useState(post.likeCount || 0); 
  const [comments, setComments] = useState(post.comments || []);
  const [comment, setComment] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    }
  }, [navigate]);

  const handleLike = async () => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/interact/like?postId=${post.id}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 409) {
        const dislikeResponse = await fetch(`${BASE_URL}/interact/dislike?postId=${post.id}`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!dislikeResponse.ok) {
          throw new Error('Failed to dislike the post');
        }
        setLikes(likes - 1);
      } else if (!response.ok) {
        throw new Error('Failed to like the post');
      } else {
        setLikes(likes + 1); 
      }
    } catch (error) {
      console.error('Error handling like/dislike:', error);
    }
  };

  const handleComment = async (e) => {
    e.preventDefault();
    if (comment.trim()) {
      const token = localStorage.getItem('token');
      try {
        const response = await fetch(`${BASE_URL}/interact/addComment?postId=${post.id}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ text: comment }),
        });

        if (!response.ok) {
          throw new Error('Failed to add comment');
        }

        setComments([...comments, { text: comment }]); 
        setComment('');
      } catch (error) {
        console.error('Error adding comment:', error);
      }
    }
  };

  return (
    <div className="post-card">
      <p className="post-content">{post.content}</p>
      <div className="post-actions">
        <button className="like-button" onClick={handleLike}>
          Like ({likes}) {/* Use the likes state here */}
        </button>
        <form className="comment-form" onSubmit={handleComment}>
          <input
            type="text"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Add a comment"
            className="comment-input"
          />
          <button className="comment-button" type="submit">Comment</button>
        </form>
      </div>
      <div className="post-comments">
        <h4>Comments:</h4>
        <ul>
          {comments.map((comment, index) => (
            <li key={index}>{comment.text}</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default PostComponent;
