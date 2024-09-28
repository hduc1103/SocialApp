package com.SocialWeb.controller;

import com.SocialWeb.domain.response.AuthResponse;
import com.SocialWeb.domain.response.UserResponse;
import com.SocialWeb.entity.UserEntity;
import com.SocialWeb.security.JwtUtil;
import com.SocialWeb.service.UserDetail;
import com.SocialWeb.service.UserService;
import jdk.jfr.Relational;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.SocialWeb.Message.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    UserService userService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserDetail userDetail;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
//    @GetMapping("/allUsers")
//    public List<UserResponse> getAllUsers() {
//        List<UserEntity> userEntities = userService.getAllUsers();
//        return userEntities.stream()
//                .map(userEntity -> new UserResponse(
//                        userEntity.getId(),
//                        userEntity.getUsername(),
//                        userEntity.getEmail(),
//                        userEntity.getImg_url(),
//                        userEntity.getBio(),
//                        userEntity.getAddress()
//                ))
//                .collect(Collectors.toList());
//    }
    @GetMapping("/allUsers")
    public List<UserResponse> getAllUsers(){
        List<UserResponse> result = new ArrayList<>();
        List<UserEntity> userEntities =userService.getAllUsers();
        for(UserEntity userEntity : userEntities){
            result.add(new UserResponse(userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    userEntity.getImg_url(),
                    userEntity.getBio(),
                    userEntity.getAddress()));
        }
        return result;
    }

    @GetMapping("/oneUser")
    public ResponseEntity<UserResponse> getOneUser(@RequestParam("userId") Long userId){
        UserEntity userEntity = userService.getUserById(userId).orElseThrow();
        UserResponse response = new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getEmail(), userEntity.getImg_url(), userEntity.getBio(), userEntity.getAddress());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestParam("userId") Long userId){
        UserEntity userEntity = userService.getUserById(userId).orElseThrow();
        userService.deleteUser(userEntity);
        return ResponseEntity.ok(D_USER);
    }
    
    @PutMapping("/updateUser")
    public ResponseEntity<String> updateUser(@RequestParam("userId") Long userId, @RequestBody Map<String, String> updateData){
        if(userService.existsByUsername(updateData.get("new_username"))){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(USERNAME_ALREADY_EXIST);
        }
        UserEntity userEntity= userService.getUserById(userId).orElseThrow();
        return ResponseEntity.ok(userService.updateUser(userId, updateData));
    }
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> new_account){
        if(userService.existsByUsername(new_account.get("username"))){
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
//        UserEntity userEntity = new UserEntity();
//        userEntity.setPassword(passwordEncoder.encode(rawPassword));
//        userEntity.setEmail(new_account.get("email"));
//        userEntity.setBio(new_account.get("bio"));
//        userEntity.setAddress(new_account.get("address"));
//        userEntity.setImg_url(new_account.get("img_url"));
//        userEntity.setRoles(new ArrayList<>(List.of("USER")));
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
}
