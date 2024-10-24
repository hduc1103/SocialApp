package com.socialweb.controller;

import com.socialweb.domain.response.PostResponse;
import com.socialweb.domain.response.SupportTicketResponse;
import com.socialweb.domain.response.UserResponse;
import com.socialweb.service.interfaces.NotificationService;
import com.socialweb.service.interfaces.PostService;
import com.socialweb.service.interfaces.SupportTicketService;
import com.socialweb.service.interfaces.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.socialweb.Message.D_USER;
import static com.socialweb.Message.USER_CREATED;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final SupportTicketService supportTicketService;
    private final PostService postService;
    private final NotificationService notificationService;

    public AdminController(UserService userService,
                           SupportTicketService supportTicketService, PostService postService, NotificationService notificationService) {
        this.userService = userService;
        this.supportTicketService = supportTicketService;
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @GetMapping("/all-users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUserResponses();
    }

    @GetMapping("/get-user-post")
    public ResponseEntity<List<PostResponse>> getUserPosts(@RequestParam("userId") long userId) {
        try {
            List<PostResponse> postResponses = postService.admin_getUserPosts(userId);
            return ResponseEntity.ok(postResponses);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/one-user")
    public ResponseEntity<?> getOneUser(@RequestParam("userId") Long userId) {
        try {
            UserResponse userResponse = userService.getUserResponseById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(userResponse);
        } catch (NoSuchElementException e) {
            System.out.println("here");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User " + userId.toString() + " not found");
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
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add-ticket-comment")
    public ResponseEntity<?> addTicketComment(@RequestHeader("Authorization") String token, @RequestParam Long ticket_id, @RequestBody String text) {
        try {
            supportTicketService.addTicketComment(ticket_id, token, text);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/global-notification")
    public ResponseEntity<Void> sendGlobalNotification(@RequestBody String message) {
        notificationService.sendGlobalNotification(message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user-notification")
    public ResponseEntity<Void> sendUserNotification(@RequestParam Long userId, @RequestBody String message) {
        notificationService.sendUserNotification(userId, message);
        return ResponseEntity.ok().build();
    }
}

