import React, { useState, useEffect } from 'react';
import { BASE_URL } from '../../service/config';
import { TiDeleteOutline } from "react-icons/ti";
import './postdetailmodal.scss';

const PostDetailModal = ({ postId, onClose }) => {
  const [post, setPost] = useState(null);
  const [likeCount, setLikeCount] = useState(0);
  const [error, setError] = useState('');
  const token = localStorage.getItem('token');

  useEffect(() => {
    const fetchPostDetail = async () => {
      try {
        const response = await fetch(`${BASE_URL}/post/getPostById?postId=${postId}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error('Failed to fetch post details');
        }

        const data = await response.json();
        setPost(data);
        const likeResponse = await fetch(`${BASE_URL}/post/numberOfLikes?postId=${postId}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!likeResponse.ok) {
          throw new Error('Failed to fetch like count');
        }

        const likeData = await likeResponse.json();
        setLikeCount(likeData);
      } catch (error) {
        setError(error.message);
      }
    };

    fetchPostDetail();
  }, [postId, token]);

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
          throw new Error('Failed to dislike the post');
        }
        setLikeCount((prev) => prev - 1);
      } else if (!response.ok) {
        throw new Error('Failed to like the post');
      } else {
        setLikeCount((prev) => prev + 1);
      }
    } catch (error) {
      console.error('Error handling like/dislike:', error);
    }
  };

  if (error) {
    return (
      <div className="modal-backdrop">
        <div className="modal-content">
          <p>Error: {error}</p>
          <button onClick={onClose} className="close-button">Close</button>
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="modal-backdrop">
        <div className="modal-content">
          <p>Loading post details...</p>
          <button onClick={onClose} className="close-button">
            <TiDeleteOutline size={24} />
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-backdrop">
      <div className="modal-content">
        <button onClick={onClose} className="close-button">
          <TiDeleteOutline size={24} />
        </button>
        <h1>{post.title ? post.title : "Post Details"}</h1>
        <p>{post.content}</p>
        <div className="like-section">
          <button onClick={handleLike} className="like-button">Like ({likeCount})</button>
        </div>
        <div className="comments-section">
          <h3>Comments:</h3>
          {post.comments.length > 0 ? (
            <ul>
              {post.comments.map((comment) => (
                <li key={comment.id} className="comment-item">
                  <p>{comment.text}</p>
                  <span className="comment-date">{new Date(comment.createdAt).toLocaleString()}</span>
                </li>
              ))}
            </ul>
          ) : (
            <p>No comments available.</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default PostDetailModal;
