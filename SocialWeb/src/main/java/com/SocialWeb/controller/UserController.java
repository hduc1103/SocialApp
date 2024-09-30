package com.SocialWeb.controller;

import com.SocialWeb.domain.request.AuthRequest;
import com.SocialWeb.domain.response.AuthResponse;
import com.SocialWeb.domain.response.UserResponse;
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

    private String extractUsername(String token){
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
    public long getUserId(@RequestHeader("Authorization") String token){
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
        if(userService.existByEmail(new_account.get("email"))){
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
    public void deleteUser(@RequestHeader("Authorization") String token){
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        userService.deleteUser(userEntity);
    }

    @GetMapping("/getUserData")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        UserResponse userResponse = new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getImg_url(), userEntity.getBio(), userEntity.getAddress());
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
        List<UserEntity> userEntityEntities = userService.searchUserByName(keyword);
        List<PostEntity> postEntityEntities = postService.searchPostsByKeyWord(keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("users", userEntityEntities);
        result.put("posts", postEntityEntities);
        return result;
    }
    @PostMapping("/createSupportTicket")
    public ResponseEntity<String> createSupportTicket(@RequestHeader("Authorization") String token, @RequestBody List<String> content){
        String username = extractUsername(token);
        UserEntity userEntity = userService.findUserbyUsername(username);
        SupportTicketEntity supportTicketEntity= SupportTicketEntity.builder()
                .user(userEntity)
                .content(content)
                .status("In progress")
                .createdAt(new Date())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(supportTicketService.createTicket(supportTicketEntity));
    }

    @PutMapping("/updateSupportTicket")
    public ResponseEntity<String> updateSupportTicket(@RequestHeader("Authorization") String token,@RequestBody List<String> content, @RequestParam Long t_id){
        String username = extractUsername(token);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        Long userId = userEntity.getId();
        String response = supportTicketService.updateTicket(userId, content, t_id);
        if (response.equals(DENIED_ACCESS_TICKET)){
            ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(DENIED_ACCESS_TICKET);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/addTicketComment")
    public void addTicketComment (@RequestHeader("Authorization")String token, @RequestParam Long ticket_id ,@RequestBody String text){
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
    public void updateTicketComment(@RequestParam Long comment_id, @RequestBody String text){
        supportTicketService.updateTicketComment(comment_id, text);
    }

    @DeleteMapping("/deleteTicketComment")
    public void deleteTicketComment(@RequestParam Long comment_id){
        supportTicketService.deleteTicketComment(comment_id);
    }
}

