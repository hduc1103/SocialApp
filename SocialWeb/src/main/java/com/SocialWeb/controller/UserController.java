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
import org.springframework.security.access.AccessDeniedException;
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


    public UserController(JwtUtil jwtUtil, AuthenticationManager authenticationManager, UserService userService, PasswordEncoder passwordEncoder, SupportTicketService supportTicketService, UserDetail userDetail, PostService postService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.supportTicketService = supportTicketService;
        this.userDetail = userDetail;
        this.postService = postService;

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
    public ResponseEntity<Long> getUserId(@RequestHeader("Authorization") String token) {
        try {
            Long userId = userService.getUserIdByToken(token);
            return ResponseEntity.ok(userId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getUserRole")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token) {
        try {
            String role = userService.getUserRoleByToken(token);
            return ResponseEntity.ok(role);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> passwordData) {
        try {
            userService.changeUserPassword(token, passwordData);
            return ResponseEntity.ok("Password updated successfully");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<Void> forgetPassword(@RequestBody Map<String, String> email) {
        try {
            userService.sendOtpForPasswordReset(email.get("email"));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<Void> verifyOtp(@RequestBody String otp) {
        if (userService.verifyOtp(otp)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> requestData) {
        try {
            userService.resetUserPassword(requestData.get("email"), requestData.get("new_password"));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // sua catch loi tren frontend
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> new_account) {
        try {
            String jwt = userService.createNewUser(new_account);
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updateUser")
    public ResponseEntity<UserResponse> updateUser(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> updateData) {
        try {
            UserResponse updatedUser = userService.updateUserByToken(token, updateData);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping(value = "/updateProfileImage", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam("profilePicture") MultipartFile profilePicture) {
        try {
            userService.updateProfileImage(token, profilePicture);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<Void> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            userService.deleteUserByToken(token);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 204 No Content on successful deletion
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 404 if user not found
        }
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
        try {
            String response = userService.addFriend(token, userId2);
            if (response.startsWith(ERROR_MSG)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UNEXPECTED_ERROR + e.getMessage());
        }
    }

    @GetMapping("/checkFriendStatus")
    public ResponseEntity<Boolean> checkFriendStatus(@RequestHeader("Authorization") String token,
                                                     @RequestParam Long userId2) {
        try {
            boolean isFriend = userService.checkFriendStatus(token, userId2);
            return ResponseEntity.status(HttpStatus.OK).body(isFriend);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @DeleteMapping("/unfriend")
    public ResponseEntity<Void> unfriend(@RequestHeader("Authorization") String token, @RequestParam Long userId2) {
        try {
            userService.unfriend(token, userId2);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllFriends")
    public ResponseEntity<List<UserResponse>> getAllFriends(@RequestHeader("Authorization") String token) {
        try {
            List<UserResponse> friends = userService.getAllFriends(token);
            return ResponseEntity.ok(friends);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchCombined(@RequestParam("keyword") String keyword) {
        try {
            Map<String, Object> result = userService.searchCombined(keyword);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createSupportTicket")
    public ResponseEntity<Void> createSupportTicket(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Object> requestBody) {
        try {
            supportTicketService.createSupportTicketByToken(token, requestBody);
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
            supportTicketService.updateSupportTicket(token, content, t_id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
    public ResponseEntity<Void> addTicketComment(@RequestHeader("Authorization") String token, @RequestParam Long ticket_id, @RequestBody String text) {
        try {
            supportTicketService.addTicketCommentByToken(token, ticket_id, text);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    public ResponseEntity<List<SupportTicketResponse>> getAllUserTicket(@RequestHeader("Authorization") String token) {
        try {
            List<SupportTicketResponse> tickets = supportTicketService.getAllTicketsByToken(token);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
