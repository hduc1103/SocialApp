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
  const [commentUsernames, setCommentUsernames] = useState({});
  const [author, setAuthor] = useState('');
  const [authorImgUrl, setAuthorImgUrl] = useState('');
  const [cmtImg, setcmtImg] = useState({});
  const [showPostOptions, setShowPostOptions] = useState(false);
  const [commentOptions, setCommentOptions] = useState({});
  const [isEditing, setIsEditing] = useState(false);
  const [updatedContent, setUpdatedContent] = useState(post.content);
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [updatedComment, setUpdatedComment] = useState('');

  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
    } else {
      fetchPostAuthor(post.userId);
      fetchUsernamesForComments(comments);
    }
  }, [navigate, comments, post.userId]);

  const fetchPostAuthor = async (userId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/user/get-username?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        let errorMessage = 'Failed to fetch post author';
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch (e) {
          console.error('Error parsing response:', e);
        }
        showRedNotification(errorMessage);
        return;
      }

      const data = await response.json();
      setAuthor(data.username);
      setAuthorImgUrl(data.imgUrl);
    } catch (error) {
      console.error('Error fetching post author:', error);
      showRedNotification('An error occurred while fetching the post author.');
    }
  };

  const fetchUsernamesForComments = async (comments) => {
    const token = localStorage.getItem('token');
    try {
      const usernames = {};
      const img_url = {};
      for (const comment of comments) {
        const response = await fetch(`${BASE_URL}/user/get-username?userId=${comment.user_id}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (response.ok) {
          const data = await response.json();
          usernames[comment.id] = data.username;
          img_url[comment.id] = data.imgUrl;
        } else {
          const errorData = await response.json();
          showRedNotification(errorData.message || 'Failed to fetch username');
          return;
        }
      }
      setCommentUsernames(usernames);
      setcmtImg(img_url);
    } catch (error) {
      console.error('Error fetching usernames:', error);
    }
  };

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
        newComment = { ...newComment, user_id: parseInt(userId, 10) };
        setComments([...comments, newComment]);
        setComment('');
        fetchUsernamesForComments([newComment]);
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
    <div className="post-card">
      <div
        className="post-author-info"
        onClick={() => navigate(`/userprofile/${post.userId}`)}
        style={{ cursor: 'pointer' }}
      >
        <img
          src={authorImgUrl ? `data:image/png;base64,${authorImgUrl}` : "https://via.placeholder.com/150"}
          alt="Author Profile"
          className="post-author-img"
        />
        <h3 className="post-author">{author}</h3>
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
          {post.isDeleted && (
            <>
              <button className="three-dot-button" onClick={() => setShowPostOptions(!showPostOptions)}>
                <FaEllipsisH />
              </button>
              {showPostOptions && (
                <div className="dropdown-menu">
                  <button onClick={() => setIsEditing(true)}>Update Post</button>
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
                      src={cmtImg[comment.id] ? `data:image/png;base64,${cmtImg[comment.id]}` : "https://via.placeholder.com/150"}
                      alt="Author Profile"
                      className="post-author-img"
                    />
                    <span className="comment-username">{commentUsernames[comment.id] || 'Loading...'}:</span>
                  </div>
                  <span className="comment-text">
                    {comment.isDeleted ? <em>(Deleted)</em> : comment.text}
                  </span>
                  <br></br>
                  <p className="comment-updated-time">{formatDate(comment.updatedAt)}</p>
                  {(comment.user_id === parseInt(userId) || role === 'ADMIN') && (
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
