package com.SocialWeb.controller;

import com.SocialWeb.domain.response.SupportTicketResponse;
import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.interfaces.SupportTicketService;
import com.SocialWeb.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.SocialWeb.Message.D_USER;
import static com.SocialWeb.Message.USER_CREATED;

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

    @GetMapping("/all-users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUserResponses();
    }

    @GetMapping("/one-user")
    public ResponseEntity<?> getOneUser(@RequestParam("userId") Long userId) {
        try {
            UserResponse userResponse = userService.getUserResponseById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userResponse);
        } catch (NoSuchElementException e) {
            System.out.println("here");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User "+ userId.toString()+ " not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam("userId") Long userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok(D_USER);
    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestParam("userId") Long userId, @RequestBody Map<String, String> updateData) {
        try {
            UserResponse updatedUser = userService.updateUser(userId, updateData);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/create-user")
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

    @GetMapping("/get-all-support-ticket")
    public ResponseEntity<List<SupportTicketResponse>> getAllSupportTicket() {
        try {
            List<SupportTicketResponse> supportTicketResponses = supportTicketService.getAllSupportTicketResponses();
            return ResponseEntity.ok(supportTicketResponses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-ticket-comment")
    public ResponseEntity<?> addTicketComment(@RequestHeader("Authorization") String token, @RequestParam Long ticket_id, @RequestBody String text) {
        try {
            String username = extractUsername(token);
            supportTicketService.addTicketComment(ticket_id, username, text);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-username")
    public ResponseEntity<String> getUsername(@RequestParam Long userId) {
        String username = userService.getUserName(userId);
        return ResponseEntity.ok(username);
    }

}

