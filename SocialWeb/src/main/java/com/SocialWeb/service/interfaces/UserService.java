package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    long getUserId(String username);

    String addFriend(Long userId1, Long userId2);

    UserEntity findUserbyUsername(String username);

    UserEntity findUserbyEmail(String email);
    UserResponse updateUser(Long userId, Map<String, String> updateData);

    String decodeFileName(String encodedFileName);

    void updateProfileImage(String username, MultipartFile profilePicture) throws IOException;

    String checkFriendStatus(Long userId1, Long userId2);

    void unfriend(Long userId1, Long userId2);

    void deleteRelationship(long userId);

    List<UserResponse> getAllFriends(long userId);

    Optional<UserEntity> getUserByUsername(String username);

    Optional<UserEntity> getUserById(Long userId);

    String getImageUrl(long userId);

    void saveUser(UserEntity userEntity);

    void deleteUser(UserEntity userEntity);

    boolean existsByUsername(String username);

    boolean existByEmail(String email);

    List<UserEntity> getAllUsers();

    List<UserEntity> searchUserByName(String keyword);

    String getUserName(long userId);

    boolean userExistByEmail(String email);
}
