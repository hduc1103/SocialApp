package com.SocialWeb.service.interfaces;

import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Long getUserIdByToken(String token);

    String addFriend(String token, Long userId2);

    UserResponse updateUser(Long userId, Map<String, String> updateData);

    UserResponse updateUserByToken(String token, Map<String, String> updateData);

    UserResponse getUserInfo(long userId);

    Map<String, String> updateProfileImage(String username, MultipartFile profilePicture) throws IOException;

    String getUserRoleByToken(String token);

    List<UserResponse> getAllUserResponses();

    void changeUserPassword(String token, Map<String, String> passwordData);

    void sendOtpForPasswordReset(String email);

    boolean verifyOtp(String otp);

    void resetUserPassword(String email, String newPassword);

    String createNewUser(Map<String, String> newAccount);

    UserResponse getUserResponseById(Long userId);

    void deleteUserById(Long userId);

    boolean checkFriendStatus(String token, Long userId2);

    void unfriend(String token, Long userId2);

    void deleteUserByToken(String token);

    void deleteRelationship(long userId);

    List<UserResponse> getAllFriends(String token);

    Map<String, Object> searchCombined(String keyword);

    Optional<UserEntity> getUserByUsername(String username);

    void saveUser(UserEntity userEntity);

    void deleteUser(UserEntity userEntity);

    boolean existsByUsername(String username);

    boolean existByEmail(String email);

    List<UserEntity> getAllUsers();

    List<UserEntity> searchUserByName(String keyword);

    String getUserName(long userId);

    void createUser(Map<String, String> newAccount);

    Map<String, String> getUsernameAndImage(long userId);
}
