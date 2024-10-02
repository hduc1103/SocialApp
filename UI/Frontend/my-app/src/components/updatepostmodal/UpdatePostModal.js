import React from 'react';
import './updatepostmodal.scss';

const UpdatePostModal = ({ isOpen, onClose, content, onUpdate, onChangeContent }) => {
  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>Update Post</h2>
        <textarea
          value={content}
          onChange={onChangeContent}
          className="edit-post-input"
          rows="5"
        />
        <div className="modal-actions">
          <button className="save-post-button" onClick={onUpdate}>Save</button>
          <button className="cancel-edit-button" onClick={onClose}>Cancel</button>
        </div>
      </div>
    </div>
  );
};

export default UpdatePostModal;
