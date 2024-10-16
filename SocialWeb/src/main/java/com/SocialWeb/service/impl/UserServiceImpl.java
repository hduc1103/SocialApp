package com.SocialWeb.service.impl;

import com.SocialWeb.domain.response.PostResponse;
import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.entity.WebFriendEntity;
import com.SocialWeb.repository.UserRepository;
import com.SocialWeb.repository.WebFriendRepository;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.security.UserDetail;
import com.SocialWeb.service.interfaces.MessageService;
import com.SocialWeb.service.interfaces.PostService;
import com.SocialWeb.service.interfaces.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.SocialWeb.Message.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WebFriendRepository webFriendRepository;
    private final MessageService messageService;
    private final PostService postService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final UserDetail userDetail;

    public UserServiceImpl(UserRepository userRepository, WebFriendRepository webFriendRepository, MessageService messageService, PostService postService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil, JavaMailSender mailSender, UserDetail userDetail) {
        this.webFriendRepository = webFriendRepository;
        this.messageService = messageService;
        this.userRepository = userRepository;
        this.postService = postService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
        this.userDetail = userDetail;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @Override
    public Long getUserIdByToken(String token) {
        String username = extractUsername(token);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        return user.getId();
    }

    @Override
    public String getUserRoleByToken(String token) {
        String username = extractUsername(token);
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));
        return user.getRoles().getFirst();
    }

    @Override
    public List<UserResponse> getAllUserResponses() {
        List<UserEntity> userEntities = getAllUsers();
        List<UserResponse> result = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            result.add(new UserResponse(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getName(),
                    userEntity.getEmail(),
                    userEntity.getImg_url(),
                    userEntity.getBio(),
                    userEntity.getAddress()
            ));
        }
        return result;
    }

    @Override
    public void changeUserPassword(String token, Map<String, String> passwordData) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        String oldPassword = passwordData.get("old-password");
        String newPassword = passwordData.get("new-password");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, oldPassword));

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
    }

    private String serverOtp = null;

    @Override
    public void sendOtpForPasswordReset(String email) {
        boolean userExists = userRepository.existsByEmail(email);

        if (!userExists) {
            throw new NoSuchElementException("Email not found");
        }

        String otp = generateOtp();
        serverOtp = otp;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + otp);
        message.setFrom("your-email@gmail.com");

        mailSender.send(message);
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    @Override
    public boolean verifyOtp(String otp) {
        if (serverOtp != null && serverOtp.equals(otp)) {
            serverOtp = null;
            return true;
        }
        return false;
    }

    @Override
    public void resetUserPassword(String email, String newPassword) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new NoSuchElementException("User not found");
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userEntity.getUsername(), newPassword)
        );
    }

    @Override
    public String createNewUser(Map<String, String> newAccount) {
        if (existsByUsername(newAccount.get("username"))) {
            throw new IllegalArgumentException(USERNAME_ALREADY_EXIST);
        }
        if (existByEmail(newAccount.get("email"))) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXIST);
        }

        String rawPassword = newAccount.get("password");
        UserEntity userEntity = UserEntity.builder()
                .password(passwordEncoder.encode(rawPassword))
                .name(newAccount.get("name"))
                .username(newAccount.get("username"))
                .email(newAccount.get("email"))
                .bio(newAccount.get("bio"))
                .address(newAccount.get("address"))
                .img_url(newAccount.get("img_url"))
                .roles(new ArrayList<>(List.of("USER")))
                .build();

        saveUser(userEntity);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userEntity.getUsername(), rawPassword)
        );

        final UserDetails userDetails = userDetail.loadUserByUsername(userEntity.getUsername());
        return jwtUtil.generateToken(userDetails);
    }

    @Override
    public UserResponse getUserResponseById(Long userId) {
        try {
            UserEntity userEntity = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

            return new UserResponse(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getName(),
                    userEntity.getEmail(),
                    userEntity.getImg_url(),
                    userEntity.getBio(),
                    userEntity.getAddress()
            );
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching user: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while fetching user details", e);
        }
    }

    @Override
    public void deleteUserById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        messageService.deleteAllUserMessage(userId);
        deleteRelationship(userId);
        deleteUser(userEntity);
    }

    public String addFriend(String token, Long userId2) {
        try {
            String username = extractUsername(token);

            UserEntity userEntity1 = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

            UserEntity userEntity2 = userRepository.findById(userId2)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + userId2));

            boolean friendshipExists = webFriendRepository.existsByUserId1AndUserId2(userEntity1.getId(), userEntity2.getId());
            if (friendshipExists) {
                return "Friendship request already sent.";
            }

            WebFriendEntity webFriend = WebFriendEntity.builder()
                    .userId1(userEntity1.getId())
                    .userId2(userEntity2.getId())
                    .user1Accepted(1L)
                    .user2Accepted(0L)
                    .build();
            webFriendRepository.save(webFriend);

            return "Friend request sent successfully!";
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return "Unexpected error: " + e.getMessage();
        }
    }

    @Override
    public UserResponse updateUser(Long userId, Map<String, String> updateData) {
        if (updateData.containsKey("new_username") && existsByUsername(updateData.get("new_username"))) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (updateData.containsKey("new_email") && existByEmail(updateData.get("new_email"))) {
            throw new IllegalArgumentException("Email already exists");
        }

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

        return UserResponse.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .img_url(userEntity.getImg_url())
                .bio(userEntity.getBio())
                .address(userEntity.getAddress())
                .build();
    }

    @Override
    public UserResponse updateUserByToken(String token, Map<String, String> updateData) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + username));

        if (updateData.containsKey("new_username") && existsByUsername(updateData.get("new_username"))) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (updateData.containsKey("new_email") && existByEmail(updateData.get("new_email"))) {
            throw new IllegalArgumentException("Email already exists");
        }
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
    public UserResponse getUserInfo(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));

        return new UserResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getImg_url(),
                userEntity.getBio(),
                userEntity.getAddress()
        );
    }

    @Override
    public Map<String, String> updateProfileImage(String token, MultipartFile profilePicture) throws IOException {
        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new IllegalArgumentException("Profile picture is required");
        }

        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + username));

        String base64Image = Base64.getEncoder().encodeToString(profilePicture.getBytes());
        userEntity.setImg_url(base64Image);
        userRepository.save(userEntity);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Profile image updated successfully");
        responseBody.put("img_url", base64Image);

        return responseBody;
    }

    @Override
    public boolean checkFriendStatus(String token, Long userId2) {
        try {
            String username = extractUsername(token);

            UserEntity userEntity1 = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + username));

            Optional<WebFriendEntity> friendship = webFriendRepository.findByUserId1AndUserId2(userEntity1.getId(), userId2)
                    .or(() -> webFriendRepository.findByUserId1AndUserId2(userId2, userEntity1.getId()));

            return friendship.isPresent()
                    && friendship.get().getUser1Accepted() == 1L
                    && friendship.get().getUser2Accepted() == 1L;

        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
            return false;
        }
    }

    @Override
    public void unfriend(String token, Long userId2) {
        try {
            String username = extractUsername(token);
            UserEntity userEntity1 = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + username));

            webFriendRepository.unfriend(userEntity1.getId(), userId2);
        } catch (Exception e) {
            System.err.println(UNEXPECTED_ERROR + e.getMessage());
        }
    }

    @Override
    public void deleteUserByToken(String token) {
        String username = extractUsername(token);
        UserEntity userEntity = getUserByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        deleteRelationship(userEntity.getId());
        deleteUser(userEntity);
    }

    @Override
    public void deleteRelationship(long userId) {
        webFriendRepository.deleteRelationship(userId);
    }

    @Override
    public List<UserResponse> getAllFriends(String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + username));

        Long userId = userEntity.getId();
        return getFriends(userId);
    }

    private List<UserResponse> getFriends(long userId) {
        List<Long> friendIds = webFriendRepository.findFriendsByUserId(userId);

        return friendIds.stream()
                .map(friendId -> {
                    Optional<UserEntity> friendEntity = userRepository.findById(friendId);
                    if (friendEntity.isPresent()) {
                        UserEntity friend = friendEntity.get();

                        return UserResponse.builder()
                                .id(friend.getId())
                                .name(friend.getName())
                                .username(friend.getUsername())
                                .img_url(friend.getImg_url())
                                .build();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> searchCombined(String keyword) {
        List<UserEntity> userEntities = userRepository.searchUsersByUsername(keyword);
        List<PostEntity> postEntities = postService.searchPostsByKeyWord(keyword);

        List<UserResponse> userResponses = userEntities.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());

        List<PostResponse> postResponses = postEntities.stream()
                .map(post -> PostResponse.builder()
                        .id(post.getId())
                        .content(post.getContent())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("users", userResponses);
        result.put("posts", postResponses);

        return result;
    }

    @Override
    public Optional<UserEntity> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void createUser(Map<String, String> newAccount) {
        if (existsByUsername(newAccount.get("username"))) {
            throw new IllegalArgumentException(USERNAME_ALREADY_EXIST);
        }
        if (existByEmail(newAccount.get("email"))) {
            throw new IllegalArgumentException(EMAIL_ALREADY_EXIST);
        }

        String rawPassword = newAccount.get("password");
        UserEntity userEntity = UserEntity.builder()
                .name(newAccount.get("name"))
                .password(passwordEncoder.encode(rawPassword))
                .username(newAccount.get("username"))
                .email(newAccount.get("email"))
                .bio(newAccount.get("bio"))
                .address(newAccount.get("address"))
                .img_url(newAccount.get("img_url"))
                .roles(new ArrayList<>(List.of("USER")))
                .build();

        saveUser(userEntity);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userEntity.getUsername(), rawPassword));
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
    public Map<String, String> getUsernameAndImage(long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND + userId));

        Map<String, String> response = new HashMap<>();
        response.put("username", userEntity.getName());

        String base64Image = null;
        if (userEntity.getImg_url() != null) {
            base64Image = userEntity.getImg_url();
        }
        response.put("imgUrl", base64Image);

        return response;
    }

}

