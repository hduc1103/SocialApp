import { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config'
import { useNavigate } from 'react-router-dom'; 

const PostComponent = ({ post }) => {
  const [likes, setLikes] = useState(0);
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
    console.log(token);
  
    try {
      const response = await fetch(`${BASE_URL}/like/add_like?postId=${post.id}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
  
      if (response.status === 409) {
        console.log("User has already liked the post. Performing dislike...");
  
        const dislikeResponse = await fetch(`${BASE_URL}/like/remove_like?postId=${post.id}`, {
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
      const newComment = { text: comment };
      setComments([...comments, newComment]);
      setComment('');
      
      const token = localStorage.getItem('token');
      try {
        const response = await fetch(`${BASE_URL}/comment/add_comment?postId=${post.id}`, {
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
  
        const responseData = await response.json();
        console.log('Comment added:', responseData);
      } catch (error) {
        console.error('Error adding comment:', error);
      }
    }
  };
  
  return (
    <div>
      <p>{post.content}</p>
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

      <div>
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
