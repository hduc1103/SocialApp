package com.SocialWeb.controller;

import com.SocialWeb.domain.response.AuthResponse;
import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.PostEntity;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.repository.PostRepository;
import com.SocialWeb.repository.UserRepository;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetail userDetail;

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity) {
        if (userService.existsByUsername(userEntity.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        String rawPassword = userEntity.getPassword();
        userEntity.setPassword(passwordEncoder.encode(rawPassword));
        System.out.println(userEntity.getPassword());
        userRepository.save(userEntity);
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

    @PostMapping("/deleteUser")
    public void deleteUser(@RequestHeader("Authorization") String token){
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);

        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        userService.deleteUser(userEntity);
    }

    @GetMapping("/getUserData")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
        UserEntity userEntity = userService.getUserByUsername(username).orElseThrow();
        UserResponse userResponse = new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail());
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/addFriend")
    public ResponseEntity<String> addFriend(@RequestHeader("Authorization") String token, @RequestParam Long userId2) {
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);
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
        String jwtToken = token.substring(7);
        String username = jwtUtil.extractUsername(jwtToken);

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
        List<UserEntity> userEntityEntities = userRepository.searchUsersByUsername(keyword);
        List<PostEntity> postEntityEntities = postRepository.searchPostsByContent(keyword);

        Map<String, Object> result = new HashMap<>();
        result.put("users", userEntityEntities);
        result.put("posts", postEntityEntities);

        return result;
    }
}
