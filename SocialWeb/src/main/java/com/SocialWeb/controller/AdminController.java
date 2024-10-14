package com.SocialWeb.controller;

import com.SocialWeb.domain.response.SupportTicketResponse;
import com.SocialWeb.domain.response.TicketCommentResponse;
import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.SupportTicketEntity;
import com.SocialWeb.entity.TicketCommentEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.MessageService;
import com.SocialWeb.service.interfaces.SupportTicketService;
import com.SocialWeb.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.SocialWeb.Message.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SupportTicketService supportTicketService;

    public AdminController(UserService userService,
                           JwtUtil jwtUtil,
                           SupportTicketService supportTicketService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.supportTicketService = supportTicketService;
    }

    private String extractUsername(String token) {
        String jwtToken = token.substring(7);
        return jwtUtil.extractUsername(jwtToken);
    }

    @GetMapping("/allUsers")
public List<UserResponse> getAllUsers() {
    return userService.getAllUserResponses();
}

@GetMapping("/oneUser")
public ResponseEntity<UserResponse> getOneUser(@RequestParam("userId") Long userId) {
    UserResponse userResponse = userService.getUserResponseById(userId);
    return ResponseEntity.status(HttpStatus.OK).body(userResponse);
}

@DeleteMapping("/deleteUser")
public ResponseEntity<?> deleteUser(@RequestParam("userId") Long userId) {
    userService.deleteUserById(userId);
    return ResponseEntity.ok(D_USER);
}

    @PutMapping("/updateUser")
    public ResponseEntity<UserResponse> updateUser(@RequestParam("userId") Long userId, @RequestBody Map<String, String> updateData) {
        UserResponse updatedUser = userService.updateUser(userId, updateData);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> newAccount) {
        try {
            userService.createUser(newAccount);
            return ResponseEntity.ok(USER_CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/getAllSupportTicket")
    public ResponseEntity<List<SupportTicketResponse>> getAllSupportTicket() {
        try {
            List<SupportTicketResponse> supportTicketResponses = supportTicketService.getAllSupportTicketResponses();
            return ResponseEntity.ok(supportTicketResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/addTicketComment")
    public ResponseEntity<?> addTicketComment(@RequestHeader("Authorization") String token, @RequestParam Long ticket_id, @RequestBody String text) {
        try {
            String username = extractUsername(token);
            supportTicketService.addTicketComment(ticket_id, username, text);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/AdmingetUsername")
    public ResponseEntity<String> getUsername(@RequestParam Long userId) {
        String username = userService.getUserName(userId);
        return ResponseEntity.ok(username);
    }

}

