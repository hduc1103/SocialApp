import React from 'react';
import AddNewPost from '../addpostmodal/AddNewPost';
import UpdateProfileModal from '../updateprofilemodal/UpdateProfileModal';
import ChangePasswordModal from '../changepasswordmodal/ChangePasswordModal';

const Modals = ({
  isModalOpen,
  setIsModalOpen,
  handleNewPost,
  isUpdateModalOpen,
  setIsUpdateModalOpen,
  handleUpdateProfile,
  isPasswordModalOpen,
  setIsPasswordModalOpen,
  handlePasswordChange,
  userDetails,
}) => {
  return (
    <>
      <AddNewPost isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleNewPost} />

      {isUpdateModalOpen && (
        <UpdateProfileModal
          isOpen={isUpdateModalOpen}
          onClose={() => setIsUpdateModalOpen(false)}
          onSubmit={handleUpdateProfile}
          currentDetails={userDetails}
        />
      )}

      {isPasswordModalOpen && (
        <ChangePasswordModal
          isOpen={isPasswordModalOpen}
          onClose={() => setIsPasswordModalOpen(false)}
          onSubmit={handlePasswordChange}
        />
      )}
    </>
  );
};

export default Modals;
