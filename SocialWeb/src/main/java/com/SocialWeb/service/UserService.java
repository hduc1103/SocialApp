package com.SocialWeb.service;

import com.SocialWeb.entity.*;
import com.SocialWeb.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        } catch (NoSuchElementException e) {
            System.err.println(ERROR_MSG + e.getMessage());
            return ERROR_MSG + e.getMessage();
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
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
        } catch (NoSuchElementException e) {
            System.err.println(ERROR_MSG + e.getMessage());
            return ERROR_MSG + e.getMessage();
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return UNEXPECTED_ERROR + e.getMessage();
        }
        return N_FRIEND;
    }

    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void createUser(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
    public void deleteUser(UserEntity userEntity){ userRepository.delete(userEntity);}
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
