import { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config';
import { useNavigate } from 'react-router-dom';
import { FaEllipsisH } from 'react-icons/fa';
import { BiSolidLike } from "react-icons/bi";

import './post.scss';

const Post = ({ post }) => {
  const [likes, setLikes] = useState(post.likeCount || 0);
  const [comments, setComments] = useState(post.comments || []);
  const [comment, setComment] = useState('');
  const [commentUsernames, setCommentUsernames] = useState({});
  const [showPostOptions, setShowPostOptions] = useState(false);
  const [commentOptions, setCommentOptions] = useState({});

  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    } else {
      fetchUsernamesForComments(comments);
    }
  }, [navigate, comments]);

  const fetchUsernamesForComments = async (comments) => {
    const token = localStorage.getItem('token');
    try {
      const usernames = {};
      for (const comment of comments) {
        const response = await fetch(`${BASE_URL}/interact/getCommentUser/${comment.id}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (response.ok) {
          const username = await response.text();
          usernames[comment.id] = username;
        }
      }
      setCommentUsernames(usernames);
    } catch (error) {
      console.error('Error fetching usernames:', error);
    }
  };

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

        const newComment = await response.json();
        setComments([...comments, newComment]);
        setComment('');
      } catch (error) {
        console.error('Error adding comment:', error);
      }
    }
  };

  const handleDeletePost = async () => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/post/deletePost?postId=${post.id}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete post');
      }
      console.log('Post deleted');
    } catch (error) {
      console.error('Error deleting post:', error);
    }
  };

  const handleDeleteComment = async (commentId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/interact/deleteComment?cmtId=${commentId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete comment');
      }
      setComments(comments.filter((c) => c.id !== commentId));
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const handleUpdateComment = async (commentId, comment) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/interact/updateComment?cmtId=${commentId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ comment }),
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }

      const result = await response.json();
      console.log('Comment updated successfully:', result);
    } catch (error) {
      console.error('Failed to update comment:', error);
    }
  };

  return (
    <div className="post-card">
      <p className="post-content">{post.content}</p>

      {post.user_id === parseInt(userId) && (
        <div className="post-options">
          <button className="three-dot-button" onClick={() => setShowPostOptions(!showPostOptions)}>
            <FaEllipsisH />
          </button>
          {showPostOptions && (
            <div className="dropdown-menu">
              <button onClick={handleDeletePost}>Delete Post</button>
            </div>
          )}
        </div>
      )}

      <div className="post-actions">
        <button className="like-button" onClick={handleLike}>
          <BiSolidLike /> {likes}
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
          {comments.map((comment) => (
            <li key={comment.id} className="comment-item-post">
              <span className="comment-username">{commentUsernames[comment.id] || 'Loading...'}:</span>
              <span className="comment-text">{comment.text}</span>
              {comment.user_id === parseInt(userId) && (
                <div className="comment-options">
                  <button
                    className="three-dot-button"
                    onClick={() =>
                      setCommentOptions((prev) => ({
                        ...prev,
                        [comment.id]: !prev[comment.id],
                      }))
                    }
                  >
                    <FaEllipsisH />
                  </button>
                  {commentOptions[comment.id] && (
                    <div className="dropdown-menu">
                      <button onClick={() => handleDeleteComment(comment.id)}>Delete Comment</button>
                      <button onClick={() => handleUpdateComment(comment.id)}>Update Comment</button>
                    </div>
                  )}
                </div>
              )}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Post;
