import { useState, useEffect } from 'react';
import { BASE_URL, PUBLIC_URL } from '../../config';
import { useNavigate } from 'react-router-dom';
import { FaEllipsisH } from 'react-icons/fa';
import { BiSolidLike } from "react-icons/bi";

import './post.scss';

const Post = ({ post }) => {
  const [likes, setLikes] = useState(post.likeCount || 0);
  const [comments, setComments] = useState(post.comments || []);
  const [comment, setComment] = useState('');
  const [commentUsernames, setCommentUsernames] = useState({});
  const [author, setAuthor] = useState('');
  const [authorImgUrl, setAuthorImgUrl] = useState('')
  const [cmtImg, setcmtImg] = useState({})
  const [showPostOptions, setShowPostOptions] = useState(false);
  const [commentOptions, setCommentOptions] = useState({});
  const [isEditing, setIsEditing] = useState(false);
  const [updatedContent, setUpdatedContent] = useState(post.content);

  const navigate = useNavigate();
  const userId = localStorage.getItem('userId');

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
      const response = await fetch(`${BASE_URL}/user/getUsername?userId=${userId}`, {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to fetch post author');
      }

      const data = await response.json();
      setAuthor(data.username);
      setAuthorImgUrl(data.imgUrl);
    } catch (error) {
      console.error('Error fetching post author:', error);
    }
  };

  const fetchUsernamesForComments = async (comments) => {
    const token = localStorage.getItem('token');
    try {
      const usernames = {};
      const img_url = {};
      for (const comment of comments) {
        const response = await fetch(`${BASE_URL}/user/getUsername?userId=${comment.user_id}`, {
          method: 'GET',
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        if (response.ok) {
          const data = await response.json();
          usernames[comment.id] = data.username;
          img_url[comment.id] = data.imgUrl;
          console.log(img_url[comment.id])
        }
      }
      setCommentUsernames(usernames);
      setcmtImg(img_url)
    } catch (error) {
      console.error('Error fetching usernames:', error);
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
        const updatedComments = [...comments, newComment];
        setComments(updatedComments);
        setComment('');

        const responseUsername = await fetch(`${BASE_URL}/user/getUsername?userId=${newComment.user_id}`, {
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

  const handleUpdatePost = async () => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`${BASE_URL}/post/updatePost?postId=${post.id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(updatedContent),
      });

      if (!response.ok) {
        throw new Error('Failed to update post');
      }

      setIsEditing(false);
      console.log('Post updated successfully');
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
          src={authorImgUrl ? `${PUBLIC_URL}/profile_img_upload/${authorImgUrl}` : "https://via.placeholder.com/150"}
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
        <p className="post-content">{post.content}</p>
      )}
      {post.userId === parseInt(userId) && (
        <div className="post-options">
          <button className="three-dot-button" onClick={() => setShowPostOptions(!showPostOptions)}>
            <FaEllipsisH />
          </button>
          {showPostOptions && (
            <div className="dropdown-menu">
              <button onClick={handleDeletePost}>Delete Post</button>
              <button onClick={() => setIsEditing(true)}>Update Post</button>
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
              <div
                className="comment-author-info"
                onClick={() => navigate(`/userprofile/${comment.user_id}`)}
                style={{ cursor: 'pointer', display: 'flex', alignItems: 'center' }}
              >
                <img
                  src={cmtImg[comment.id] ? `${PUBLIC_URL}/profile_img_upload/${cmtImg[comment.id]}` : "https://via.placeholder.com/150"}
                  alt="Author Profile"
                  className="post-author-img"
                />
                <span className="comment-username">{commentUsernames[comment.id] || 'Loading...'}:</span>
              </div>
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
