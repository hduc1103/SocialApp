package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.SocialWeb.Message.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadDir;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public long getUserId(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow();
        return userEntity.getId();
    }

    @Override
    public String addFriend(Long userId1, Long userId2) {
        try {
            UserEntity userEntity1 = userRepository.findById(userId1)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId1));
            UserEntity userEntity2 = userRepository.findById(userId2)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId2));
            userEntity1.getFriends().add(userEntity2);
            userEntity2.getFriends().add(userEntity1);
            userRepository.save(userEntity1);
            userRepository.save(userEntity2);
            return FRIEND_ADDED;
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
    }

    @Override
    public UserEntity findUserbyUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public UserEntity findUserbyEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserResponse updateUser(Long userId, Map<String, String> updateData) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userId));
        if (updateData.containsKey("new_name")) {
            userEntity.setName(updateData.get("new_name"));
        }
        if (updateData.containsKey("new_username")) {
            userEntity.setUsername(updateData.get("new_username"));
        }
        if (updateData.containsKey("new_email")) {
            userEntity.setEmail(updateData.get("new_email"));
        }
        if (updateData.containsKey("new_bio")) {
            userEntity.setBio(updateData.get("new_bio"));
        }
        if (updateData.containsKey("new_address")) {
            userEntity.setAddress(updateData.get("new_address"));
        }

        userRepository.save(userEntity);

        String decodedImgUrl = null;
        if (userEntity.getImg_url() != null) {
            decodedImgUrl = new String(Base64.getDecoder().decode(userEntity.getImg_url()));
        }

        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .img_url(decodedImgUrl)
                .bio(userEntity.getBio())
                .address(userEntity.getAddress())
                .build();
    }

    @Override
    public String decodeFileName(String encodedFileName) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedFileName);
        return new String(decodedBytes);
    }

    @Override
    public void updateProfileImage(String username, MultipartFile profilePicture) throws IOException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + username));

        if (profilePicture != null && !profilePicture.isEmpty()) {
            String fileName = profilePicture.getOriginalFilename();
            String encodedFileName = Base64.getEncoder().encodeToString(fileName.getBytes());
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, profilePicture.getBytes());
            userEntity.setImg_url(encodedFileName);
            userRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("Profile picture is required");
        }
    }

    @Override
    public String checkFriendStatus(Long userId1, Long userId2) {
        try {
            int count = userRepository.countFriendship(userId1, userId2);

            if (count == 2) {
                return Y_FRIEND;
            }
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
        return N_FRIEND;
    }

    @Override
    public void unfriend(Long userId1, Long userId2) {
        userRepository.unfriend(userId1, userId2);
    }

    @Override
    public void deleteRelationship(long userId) {
        userRepository.deleteRelationship(userId);
    }

    @Override
    public List<UserResponse> getAllFriends(long userId) {
        List<Long> friendIds = userRepository.findFriendsByUserId(userId);
        List<UserResponse> friends = friendIds.stream()
                .map(friendId -> {
                    Optional<UserEntity> friendEntity = userRepository.findById(friendId);
                    if (friendEntity.isPresent()) {
                        UserEntity friend = friendEntity.get();

                        String decodedImgUrl = null;
                        if (friend.getImg_url() != null) {
                            decodedImgUrl = new String(Base64.getDecoder().decode(friend.getImg_url()));
                        }
                        return UserResponse.builder()
                                .id(friend.getId())
                                .name(friend.getName())
                                .username(friend.getUsername())
                                .img_url(decodedImgUrl)
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return friends;
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<UserEntity> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public String getImageUrl(long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();
        return userEntity.getImg_url();
    }

    @Override
    public void saveUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(UserEntity userEntity) {
        userRepository.delete(userEntity);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> searchUserByName(String keyword) {
        return userRepository.searchUsersByUsername(keyword);
    }

    @Override
    public String getUserName(long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow();
        return userEntity.getName();
    }

    @Override
    public boolean userExistByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
