import { useState, useEffect } from 'react';
import { BASE_URL, showRedNotification, showGreenNotification } from '../../config';
import { useNavigate } from 'react-router-dom';
import { FaEllipsisH } from 'react-icons/fa';
import { BiSolidLike } from "react-icons/bi";

import './post.scss';

const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleString();
};

const Post = ({ post, onDeletePost }) => {
  const [likes, setLikes] = useState(post.likeCount || 0);
  const [comments, setComments] = useState(post.comments || []);
  const [comment, setComment] = useState('');
  const [showPostOptions, setShowPostOptions] = useState(false);
  const [commentOptions, setCommentOptions] = useState({});
  const [isEditing, setIsEditing] = useState(false);
  const [updatedContent, setUpdatedContent] = useState(post.content);
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [updatedComment, setUpdatedComment] = useState('');

  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');

  const handleDeleteComment = async (commentId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/interact/delete-comment?cmtId=${commentId}`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to delete comment');
        return;
      }
      setComments(comments.filter((c) => c.id !== commentId));
      showGreenNotification('Comment deleted successfully');
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const handleUpdateComment = async (commentId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/interact/update-comment?commentId=${commentId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ text: updatedComment }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to update comment');
        return;
      }

      const updatedComments = comments.map((comment) =>
        comment.id === commentId ? { ...comment, text: updatedComment } : comment
      );

      setComments(updatedComments);
      setEditingCommentId(null);
      setUpdatedComment('');
      showGreenNotification('Comment updated successfully');
    } catch (error) {
      console.error('Failed to update comment:', error);
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
          const errorData = await dislikeResponse.json();
          showRedNotification(errorData.message || 'Failed to dislike the post');
          return;
        }
        setLikes(likes - 1);
      } else if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to like the post');
        return;
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
        const response = await fetch(`${BASE_URL}/interact/add-comment?postId=${post.id}`, {
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
          let newComment = await response.json();
        newComment = {
          ...newComment,
          user_id: parseInt(userId, 10),
          imgUrl: newComment.imgUrl,   
          author: newComment.author   
        };
  
        setComments([...comments, newComment]); 
        setComment(''); 
        showGreenNotification('Comment added successfully');
      } catch (error) {
        console.error('Error adding comment:', error);
      }
    }
  };
  

  const handleUpdatePost = async () => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/post/update-post?postId=${post.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedContent),
      });

      if (!response.ok) {
        const errorData = await response.json();
        showRedNotification(errorData.message || 'Failed to update post');
        return;
      }

      post.content = updatedContent;
      setIsEditing(false);
      showGreenNotification('Post updated successfully');
    } catch (error) {
      console.error('Error updating post:', error);
    }
  };

  return (
    <div className="post-card"  id={`post-${post.id}`}>
      <div
        className="post-author-info"
        onClick={() => navigate(`/userprofile/${post.userId}`)}
        style={{ cursor: 'pointer' }}
      >
        <img
          src={post.imgUrl ? `data:image/png;base64,${post.imgUrl}` : "https://via.placeholder.com/150"}
          alt="Author Profile"
          className="post-author-img"
        />
        <h3 className="post-author">{ post.author}</h3>
      </div>

      {isEditing ? (
        <div className="edit-post-container">
          <textarea
            value={updatedContent}
            onChange={(e) => setUpdatedContent(e.target.value)}
            className="edit-post-input"
          />
          <button className="save-post-button" onClick={handleUpdatePost}>Save</button>
          <button className="cancel-edit-button" onClick={() => setIsEditing(false)}>Cancel</button>
        </div>
      ) : (
        <p className="post-content">
          {post.isDeleted ? <em>(Deleted)</em> : post.content}
        </p>
      )}
      {(post.userId === parseInt(userId) || role === 'ADMIN') && (
        <div className="post-options">
          {!post.deleted && (
            <>
              <button
                className="three-dot-button post-options-button"
                onClick={() => setShowPostOptions(!showPostOptions)}
              >
                <FaEllipsisH />
              </button>
              {showPostOptions && (
                <div className="dropdown-menu">
                  {post.userId === parseInt(userId) && (
                    <button onClick={() => setIsEditing(true)}>Update Post</button>
                  )}
                  <button onClick={() => onDeletePost(post.id)}>Delete Post</button>
                </div>
              )}
            </>
          )}
        </div>
      )}

      <p className="post-updated-time">{formatDate(post.updatedAt)}</p>
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
              {editingCommentId === comment.id ? (
                <>
                  <input
                    type="text"
                    value={updatedComment}
                    onChange={(e) => setUpdatedComment(e.target.value)}
                    placeholder="Update your comment"
                    className="edit-comment-input"
                  />
                  <button onClick={() => handleUpdateComment(comment.id)}>Save</button>
                  <button onClick={() => setEditingCommentId(null)}>Cancel</button>
                </>
              ) : (
                <>
                  <div
                    className="comment-author-info"
                    onClick={() => navigate(`/userprofile/${comment.user_id}`)}
                    style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}
                  >
                    <img
                      src={comment.imgUrl ? `data:image/png;base64,${comment.imgUrl}` : "https://via.placeholder.com/150"}
                      alt="Author Profile"
                      className="post-author-img"
                    />
                    <span className="comment-username">{comment.author || 'Loading...'}:</span>
                  </div>
                  <span className="comment-text">
                    {comment.isDeleted ? <em>(Deleted)</em> : comment.text}
                  </span>
                  <br></br>
                  <p className="comment-updated-time">{formatDate(comment.updatedAt)}</p>
                  {(comment.user_id === parseInt(userId)) && (
                    <div className="comment-options">

                      <button
                        className="three-dot-button comment-options-button"
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
                          <button
                            onClick={() => {
                              setEditingCommentId(comment.id);
                              setUpdatedComment(comment.text);
                            }}
                          >
                            Update Comment
                          </button>
                        </div>
                      )}
                    </div>
                  )}
                </>
              )}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Post;
