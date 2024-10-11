package com.SocialWeb.controller;

import com.SocialWeb.domain.request.AuthRequest;
import com.SocialWeb.domain.response.*;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.SupportTicketEntity;
import com.SocialWeb.entity.TicketCommentEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.service.interfaces.PostService;
import com.SocialWeb.service.interfaces.SupportTicketService;
import com.SocialWeb.security.UserDetail;
import com.SocialWeb.service.interfaces.UserService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.SocialWeb.security.JwtUtil;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.SocialWeb.Message.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final SupportTicketService supportTicketService;
    private final UserDetail userDetail;
    private final PostService postService;
    private final JavaMailSender mailSender;

    public UserController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder, SupportTicketService supportTicketService, UserDetail userDetail, PostService postService, JavaMailSender mailSender) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.supportTicketService = supportTicketService;
        this.userDetail = userDetail;
        this.postService = postService;
        this.mailSender = mailSender;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_CREDENTIAL);
        }
        final UserDetails userDetails = userDetail.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @GetMapping("/getUserId")
    public long getUserId(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        return userService.getUserId(username);
    }

    @GetMapping("/getUserRole")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        UserEntity user = userService.getUserByUsername(username).orElseThrow();
        String role = user.getRoles().getFirst();
        return ResponseEntity.ok(role);
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwordData) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();

        String oldPassword = passwordData.get("old-password");
        String newPassword = passwordData.get("new-password");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, oldPassword));
        } catch (BadCredentialsException e) {
            //401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password");
        }
        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(userEntity);

        return ResponseEntity.ok("Password updated successfully");
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String server_otp=null;
    @PostMapping("/forgetPassword")
    public ResponseEntity<Void> forgetPassword(@RequestBody Map<String,String> email) {
        System.out.println(email);
        boolean check= userService.userExistByEmail(email.get("email"));

        if (!check) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        server_otp = generateOtp();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.get("email"));
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + server_otp);
        message.setFrom("your-email@gmail.com");

        mailSender.send(message);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestBody String otp) {
        System.out.println(otp);
        if (server_otp != null && server_otp.equals(otp)) {
            server_otp = null;
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String newPassword = requestData.get("new_password");

        UserEntity userEntity = userService.findUserbyEmail(email);
        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(userEntity);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userEntity.getUsername(), newPassword)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> new_account) {
        if (userService.existsByUsername(new_account.get("username"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(USERNAME_ALREADY_EXIST);
        }
        if (userService.existByEmail(new_account.get("email"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(EMAIL_ALREADY_EXIST);
        }
        String rawPassword = new_account.get("password");
        UserEntity userEntity = UserEntity.builder()
                .password(passwordEncoder.encode(rawPassword))
                .name(new_account.get("name"))
                .username(new_account.get("username"))
                .email(new_account.get("email"))
                .bio(new_account.get("bio"))
                .address(new_account.get("address"))
                .img_url(new_account.get("img_url"))
                .roles(new ArrayList<>(List.of("USER")))
                .build();
        userService.saveUser(userEntity);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userEntity.getUsername(), rawPassword));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        final UserDetails userDetails = userDetail.loadUserByUsername(userEntity.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        System.out.println(jwt);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> updateData) {
        String username = extractUsername(token);
        if (userService.existsByUsername(updateData.get("new_username"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (userService.existByEmail(updateData.get("new_email"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId = userEntity.getId();
        return ResponseEntity.ok(userService.updateUser(userId, updateData));
    }

    @PutMapping(value = "/updateProfileImage", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("profilePicture") MultipartFile profilePicture) {
        try {
            String username = extractUsername(token);
            userService.updateProfileImage(username, profilePicture);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteUser")
    public void deleteUser(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        userService.deleteRelationship(userEntity.getId());
        userService.deleteUser(userEntity);
    }

    @GetMapping("/getUserData")
    public ResponseEntity<UserResponse> getUserInfo(@RequestParam("userId") long userId) {
        UserEntity userEntity = userService.getUserById(userId).orElseThrow();

        String decodedImgUrl = null;
        if (userEntity.getImg_url() != null) {
            decodedImgUrl = new String(Base64.getDecoder().decode(userEntity.getImg_url()));
        }
        UserResponse userResponse = new UserResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getName(),
                userEntity.getEmail(),
                decodedImgUrl,
                userEntity.getBio(),
                userEntity.getAddress()
        );
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/addFriend")
    public ResponseEntity<String> addFriend(@RequestHeader("Authorization") String token, @RequestParam Long userId2) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId1 = userEntity.getId();

        String response = userService.addFriend(userId1, userId2);
        if (response.startsWith(ERROR_MSG)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/checkFriendStatus")
    public ResponseEntity<Boolean> checkFriendStatus(@RequestHeader("Authorization") String token,
                                                     @RequestParam Long userId2) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId1 = userEntity.getId();
        String response = userService.checkFriendStatus(userId1, userId2);
        if (response.equals(Y_FRIEND)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.OK).body(false);
    }

    @DeleteMapping("/unfriend")
    public ResponseEntity<Void> unfriend(@RequestHeader("Authorization") String token, @RequestParam Long userId2) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId1 = userEntity.getId();
        userService.unfriend(userId1, userId2);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/getAllFriends")
    public ResponseEntity<List<UserResponse>> getAllFriends(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId = userEntity.getId();
        List<UserResponse> friends = userService.getAllFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/search")
    public Map<String, Object> searchCombined(@RequestParam("keyword") String keyword) {
        System.out.println("Searching for keyword: " + keyword);

        List<UserEntity> userEntities = userService.searchUserByName(keyword);
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

    @PostMapping("/createSupportTicket")
    public ResponseEntity<Void> createSupportTicket(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody) {
        try {
            String title = (String) requestBody.get("title");
            String content = (String) requestBody.get("content");
            String username = extractUsername(token);
            UserEntity userEntity = userService.findUserbyUsername(username);

            SupportTicketEntity supportTicketEntity = SupportTicketEntity.builder()
                    .user(userEntity)
                    .title(title)
                    .content(content)
                    .status("In progress")
                    .createdAt(new Date())
                    .build();

            supportTicketService.createTicket(supportTicketEntity);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updateSupportTicket")
    public ResponseEntity<Void> updateSupportTicket(
            @RequestHeader("Authorization") String token,
            @RequestBody String content,
            @RequestParam Long t_id) {
        try {
            String username = extractUsername(token);
            UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
            Long userId = userEntity.getId();
            String response = supportTicketService.updateTicket(userId, content, t_id);
            if (response.equals(DENIED_ACCESS_TICKET)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{ticketId}/close")
    public ResponseEntity<Void> closeSupportTicket(@PathVariable Long ticketId) {
        supportTicketService.deleteSupportTicket(ticketId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/addTicketComment")
    public void addTicketComment(@RequestHeader("Authorization") String token, @RequestParam Long ticket_id, @RequestBody String text) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        SupportTicketEntity supportTicketEntity = supportTicketService.findSupportTicket(ticket_id);
        TicketCommentEntity ticketCommentEntity = TicketCommentEntity.builder()
                .text(text)
                .user(userEntity)
                .supportTicketEntity(supportTicketEntity)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();
        supportTicketService.addTicketComment(ticketCommentEntity);
    }

    @PutMapping("/updateTicketComment")
    public void updateTicketComment(@RequestParam Long comment_id, @RequestBody String text) {
        supportTicketService.updateTicketComment(comment_id, text);
    }

    @DeleteMapping("/deleteTicketComment")
    public void deleteTicketComment(@RequestParam Long comment_id) {
        supportTicketService.deleteTicketComment(comment_id);
    }

    @GetMapping("/getAllUserTicket")
    public List<SupportTicketResponse> getAllUserTicket(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println(userEntity.getId());

        List<SupportTicketEntity> supportTickets = supportTicketService.getAllTicketsByUserId(userEntity.getId());

        return supportTickets.stream()
                .map(ticket -> SupportTicketResponse.builder()
                        .id(ticket.getId())
                        .title(ticket.getTitle())
                        .content(ticket.getContent())
                        .status(ticket.getStatus())
                        .createdAt(ticket.getCreatedAt())
                        .userId(ticket.getUser().getId())
                        .comments(ticket.getTicketCommentEntities().stream()
                                .map(comment -> TicketCommentResponse.builder()
                                        .id(comment.getId())
                                        .text(comment.getText())
                                        .createdAt(comment.getCreatedAt())
                                        .updatedAt(comment.getUpdatedAt())
                                        .userId(comment.getUser().getId())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @GetMapping("/getUsername")
    public ResponseEntity<Map<String, String>> getUsername(@RequestParam("userId") long userId) {
        Map<String, String> response = new HashMap<>();
        response.put("username", userService.getUserName(userId));
        String decodedImgUrl = null;
        if (userService.getImageUrl(userId) != null) {
            decodedImgUrl = new String(Base64.getDecoder().decode(userService.getImageUrl(userId)));
        }
        response.put("imgUrl", decodedImgUrl);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
