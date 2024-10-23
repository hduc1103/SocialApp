package com.socialweb.security;

import com.socialweb.entity.UserEntity;
import com.socialweb.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.socialweb.Message.USER_NOT_FOUND;

@Service
public class UserDetail implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user details from the database using the provided username.
     * This method retrieves the user entity and converts its roles into
     * granted authorities required by Spring Security.
     *
     * @param username the username of the user to be loaded
     * @return UserDetails containing the user's username, password, and authorities
     * @throws UsernameNotFoundException if a user with the given username is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND));
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
