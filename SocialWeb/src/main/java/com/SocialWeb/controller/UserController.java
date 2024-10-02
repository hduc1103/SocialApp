package com.SocialWeb.controller;

import com.SocialWeb.domain.request.AuthRequest;
import com.SocialWeb.domain.response.*;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.SupportTicketEntity;
import com.SocialWeb.entity.TicketCommentEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.service.PostService;
import com.SocialWeb.service.SupportTicketService;
import com.SocialWeb.service.UserDetail;
import com.SocialWeb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.SocialWeb.security.JwtUtil;

import java.util.*;
import java.util.stream.Collectors;

import static com.SocialWeb.Message.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SupportTicketService supportTicketService;

    @Autowired
    private UserDetail userDetail;

    @Autowired
    private PostService postService;

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
                .username(new_account.get("username"))
                .email(new_account.get("email"))
                .bio(new_account.get("bio"))
                .address(new_account.get("address"))
                .img_url(new_account.get("img_url"))
                .roles(new ArrayList<>(List.of("USER")))
                .build();
        userService.createUser(userEntity);
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
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token, @RequestBody Map<String, String> updateData) {
        String username = extractUsername(token);
        if (userService.existsByUsername(updateData.get("new_username"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(USERNAME_ALREADY_EXIST);
        }
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId = userEntity.getId();
        return ResponseEntity.ok(userService.updateUser(userId, updateData));
    }

    @DeleteMapping("/deleteUser")
    public void deleteUser(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        userService.deleteUser(userEntity);
    }

    @GetMapping("/getUserData")
    public ResponseEntity<UserResponse> getUserInfo(@RequestParam("userId") long userId) {
        UserEntity userEntity = userService.getUserById(userId).orElseThrow();
        UserResponse userResponse = new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getName(), userEntity.getEmail(), userEntity.getImg_url(), userEntity.getBio(), userEntity.getAddress());
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
    public ResponseEntity<String> checkFriendStatus(@RequestHeader("Authorization") String token,
                                                    @RequestParam Long userId2) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId1 = userEntity.getId();

        String response = userService.checkFriendStatus(userId1, userId2);

        if (response.equals(Y_FRIEND)) {
            return ResponseEntity.status(HttpStatus.OK).body(Y_FRIEND);
        }
        return ResponseEntity.status(HttpStatus.OK).body(N_FRIEND);
    }

    @GetMapping("/search")
    public Map<String, Object> searchCombined(@RequestParam("keyword") String keyword) {
        System.out.println("Searching for keyword: " + keyword);

        List<UserEntity> userEntities = userService.searchUserByName(keyword);
        List<PostEntity> postEntities = postService.searchPostsByKeyWord(keyword);

        List<UserSummaryResponse> userSummaryResponses = userEntities.stream()
                .map(user -> UserSummaryResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());

        List<PostSummaryResponse> postResponses = postEntities.stream()
                .map(post -> PostSummaryResponse.builder()
                        .id(post.getId())
                        .content(post.getContent())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("users", userSummaryResponses);
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
    public ResponseEntity<Void> closeSupportTicket(@PathVariable Long ticketId){
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
    public ResponseEntity<Map<String,String>> getUsername(@RequestParam("userId")long userId){
        Map<String, String> response = new HashMap<>();
        response.put("username", userService.getUserName(userId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

