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
            User user1 = userRepository.findById(userId1)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId1));
            User user2 = userRepository.findById(userId2)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId2));
            user1.getFriends().add(user2);
            user2.getFriends().add(user1);
            userRepository.save(user1);
            userRepository.save(user2);
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
            User user1 = userRepository.findById(userId1)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId1));
            User user2 = userRepository.findById(userId1)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId2));

            if (user1.getFriends().contains(user2)) {
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

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }
    public void deleteUser(User user){ userRepository.delete(user);}
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
