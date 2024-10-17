import React from 'react';
import { FaUpload } from "react-icons/fa6";
import { LiaUserEditSolid } from "react-icons/lia";
import { PiPasswordBold } from "react-icons/pi";
import { IoPersonAddSharp, IoPersonRemoveSharp } from "react-icons/io5";
import { MdPendingActions } from "react-icons/md";
import { BsCheckCircle } from "react-icons/bs";

const ProfileHeader = ({
  userDetails,
  userId,
  loggedInUserId,
  friendshipStatus,
  handleAddFriend,
  handleUnfriend,
  handleAcceptFriendRequest,
  handleUpdateProfileImage,
  setIsUpdateModalOpen,
  setIsPasswordModalOpen,
}) => {

  const renderFriendshipButton = () => {
    switch (friendshipStatus) {
      case 'NOT_FRIENDS':
        return (
          <button className="add-friend-button" onClick={handleAddFriend}>
            <IoPersonAddSharp size={20} /> Add Friend
          </button>
        );
      case 'REQUEST_SENT':
        return (
          <button className="cancel-request-button">
            <MdPendingActions size={20} /> Request Sent
          </button>
        );
      case 'REQUEST_RECEIVED':
        return (
          <button className="accept-request-button" onClick={handleAcceptFriendRequest}>
            <BsCheckCircle size={20} /> Accept Request
          </button>
        );
      case 'FRIENDS':
        return (
          <button className="unfriend-button" onClick={handleUnfriend}>
            <IoPersonRemoveSharp size={20} /> Unfriend
          </button>
        );
      default:
        return null;
    }
  };

  return (
    <div className="profile-header">
      <div className="profile-details">
        <div className="profile-picture-wrapper">
          <img
            src={userDetails?.img_url ? `data:image/png;base64,${userDetails.img_url}` : "https://via.placeholder.com/150"}
            alt="Profile"
            className="profile-picture"
          />
          {userId === loggedInUserId && (
            <>
              <input
                type="file"
                id="profileImageInput"
                style={{ display: 'none' }}
                onChange={(e) => {
                  if (e.target.files && e.target.files[0]) {
                    handleUpdateProfileImage(e.target.files[0]);
                  }
                }}
              />
              <button
                className="edit-profile-picture-button"
                onClick={() => document.getElementById('profileImageInput').click()}
              >
                <FaUpload size={15} />
              </button>
            </>
          )}
        </div>

        <div className="user-info">
          <h1>{userDetails ? `${userDetails.name}` : 'User Profile'}</h1>
          <p>{userDetails ? `Email: ${userDetails.email}` : 'Loading user details...'}</p>
          <p>{userDetails ? `Address: ${userDetails.address}` : 'Loading user details...'}</p>
          <p>{userDetails ? `Bio: ${userDetails.bio}` : 'Loading user details...'}</p>
        </div>
      </div>

      {userId === loggedInUserId ? (
        <div className="profile-actions">
          <button className="update-profile-button" onClick={() => setIsUpdateModalOpen(true)}>
            <LiaUserEditSolid size={20} />
          </button>
          <button className="change-password-button" onClick={() => setIsPasswordModalOpen(true)}>
            <PiPasswordBold size={20} />
          </button>
        </div>
      ) : (
        renderFriendshipButton()
      )}
    </div>
  );
};

export default ProfileHeader;
