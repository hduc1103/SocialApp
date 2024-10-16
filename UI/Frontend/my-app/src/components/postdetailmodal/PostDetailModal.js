import React, { useState, useEffect } from 'react';
import { BASE_URL, showRedNotification, showGreenNotification } from '../../config';
import { TiDeleteOutline } from "react-icons/ti";
import './postdetailmodal.scss';

const PostDetailModal = ({ postId, onClose }) => {
  const [post, setPost] = useState(null);
  const [likeCount, setLikeCount] = useState(0);
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');
  const [comments, setComments] = useState([]);
  const [comment, setComment] = useState('');
  const [commentUsernames, setCommentUsernames] = useState({});

  useEffect(() => {
    const fetchPostDetail = async () => {
      try {
        const response = await fetch(`${BASE_URL}/post/get-post-by-id?postId=${postId}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          const errorData = await response.json();
          showRedNotification(errorData.message || 'Failed to fetch post details');
          return;
        }

        const data = await response.json();
        setPost(data);
        setComments(data.comments || []);
        showGreenNotification('Post details fetched successfully');

        const likeResponse = await fetch(`${BASE_URL}/post/number-of-likes?postId=${postId}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!likeResponse.ok) {
          const likeErrorData = await likeResponse.json();
          showRedNotification(likeErrorData.message || 'Failed to fetch like count');
          throw new Error(likeErrorData.message || 'Failed to fetch like count');
        }

        const likeData = await likeResponse.json();
        setLikeCount(likeData);
      } catch (error) {
        setError(error.message);
      }
    };

    fetchPostDetail();
  }, [postId, token]);

  const handleComment = async () => {
    try {
      const response = await fetch(`${BASE_URL}/interact/add-comment?postId=${postId}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ text: comment }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to add comment');
        return;
      }

      const newComment = await response.json();
      setComments((prevComments) => [...prevComments, newComment]);
      setComment('');
      showGreenNotification('Comment added successfully');

      const responseUsername = await fetch(`${BASE_URL}/user/get-username?userId=${newComment.user_id}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (responseUsername.ok) {
        const usernameData = await responseUsername.json();
        setCommentUsernames((prevUsernames) => ({
          ...prevUsernames,
          [newComment.id]: usernameData.username,
        }));
      }
    } catch (error) {
      console.error('Error adding comment:', error);
    }
  };

  const handleLike = async () => {
    try {
      const response = await fetch(`${BASE_URL}/interact/like?postId=${postId}`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 409) {
        const dislikeResponse = await fetch(`${BASE_URL}/interact/dislike?postId=${postId}`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!dislikeResponse.ok) {
          return;
        }
        setLikeCount((prev) => prev - 1);
      } else if (!response.ok) {
        const likeErrorData = await response.json();
        showRedNotification(likeErrorData.message || 'Failed to like the post');
        return;
      } else {
        setLikeCount((prev) => prev + 1);
      }
    } catch (error) {
      console.error('Error handling like/dislike:', error);
    }
  };

  if (error) {
    return (
      <div className="post-detail-modal-backdrop">
        <div className="post-detail-modal-content">
          <p>Error: {error}</p>
          <button onClick={onClose} className="post-detail-close-button">Close</button>
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="post-detail-modal-backdrop">
        <div className="post-detail-modal-content">
          <p>Loading post details...</p>
          <button onClick={onClose} className="post-detail-close-button">
            <TiDeleteOutline size={24} />
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="post-detail-modal-backdrop">
      <div className="post-detail-modal-content">
        <button onClick={onClose} className="post-detail-close-button">
          <TiDeleteOutline size={24} />
        </button>
        <h1>{post.title ? post.title : "Post Details"}</h1>
        <p>{post.content}</p>
        <div className="post-detail-like-section">
          <button onClick={handleLike} className="post-detail-like-button">Like ({likeCount})</button>
        </div>
        <div className="post-detail-comments-section">
          <h3>Comments:</h3>
          {comments.length > 0 ? (
            <ul>
              {comments.map((comment) => (
                <li key={comment.id} className="post-detail-comment-item">
                  <p><strong>{commentUsernames[comment.id] || 'User'}:</strong> {comment.text}</p>
                  <span className="post-detail-comment-date">{new Date(comment.createdAt).toLocaleString()}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p>No comments available.</p>
          )}
        </div>
        <div className="post-detail-add-comment-section">
          <textarea
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Add a comment..."
          />
          <button onClick={handleComment} className="post-detail-add-comment-button">
            Add Comment
          </button>
        </div>
      </div>
    </div>
  );
};

export default PostDetailModal;
