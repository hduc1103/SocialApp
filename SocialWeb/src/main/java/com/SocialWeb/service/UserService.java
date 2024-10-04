package com.SocialWeb.service;

import com.SocialWeb.entity.*;
import com.SocialWeb.repository.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.SocialWeb.Message.*;
import static com.SocialWeb.Message.UNEXPECTED_ERROR;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public long getUserId(String username){
        UserEntity userEntity= userRepository.findByUsername(username).orElseThrow();
        return userEntity.getId();
    }
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
    public UserEntity findUserbyUsername(String username){
        return userRepository.findByUsername(username).orElseThrow();
    }
    public String updateUser(Long userId, Map<String, String> updateData){
        UserEntity userEntity= userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));
        if (updateData.containsKey("new_name")) {
            userEntity.setUsername(updateData.get("new_name"));
        }
        if (updateData.containsKey("new_username")) {
            userEntity.setUsername(updateData.get("new_username"));
        }
        if (updateData.containsKey("new_email")) {
            userEntity.setEmail(updateData.get("new_email"));
        }
        if (updateData.containsKey("new_img_url")) {
            userEntity.setImg_url(updateData.get("new_img_url"));
        }
        if (updateData.containsKey("new_bio")) {
            userEntity.setBio(updateData.get("new_bio"));
        }
        if (updateData.containsKey("new_address")) {
            userEntity.setAddress(updateData.get("new_address"));
        }
        userRepository.save(userEntity);
        return Y_UPDATE;
    }
    public String checkFriendStatus(Long userId1, Long userId2) {
        try {
            UserEntity userEntity1 = userRepository.findById(userId1)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId1));
            UserEntity userEntity2 = userRepository.findById(userId1)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId2));

            if (userEntity1.getFriends().contains(userEntity2)) {
                return Y_FRIEND;
            }
        }catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
        return N_FRIEND;
    }

    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public Optional<UserEntity> getUserById(Long userId){return userRepository.findById(userId);}

    public void createUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
    public void deleteUser(UserEntity userEntity){ userRepository.delete(userEntity);}
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    public boolean existByEmail(String email){
        return userRepository.existsByEmail(email);
    }
    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }
    public List<UserEntity> searchUserByName(String keyword){
        return userRepository.searchUsersByUsername(keyword);
    }
    public String getUserName(long userId){
        UserEntity userEntity= userRepository.findById(userId).orElseThrow();
        return userEntity.getName();
    }
}


