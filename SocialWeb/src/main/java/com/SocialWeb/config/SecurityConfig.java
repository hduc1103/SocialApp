package com.SocialWeb.config;

import com.SocialWeb.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/login").permitAll()
                        .requestMatchers("user/forgetPassword").permitAll()
                        .requestMatchers("user/verifyOtp").permitAll()
                        .requestMatchers("/user/resetPassword").permitAll()
                        .requestMatchers("/chat/**").authenticated()
                        .requestMatchers("/interact/**").authenticated()
                        .requestMatchers("/post/numberOfLikes").permitAll()
                        .requestMatchers("/post/getUserPost").authenticated()
                        .requestMatchers("/post/createPost").authenticated()
                        .requestMatchers("/post/deletePost").authenticated()
                        .requestMatchers("/post/updatePost").authenticated()
                        .requestMatchers("/post/getPostById").authenticated()
                        .requestMatchers("/post/retrieveFriendsPosts").authenticated()
                        .requestMatchers("/user/createUser").permitAll()
                        .requestMatchers("/user/deleteUSer").permitAll()
                        .requestMatchers("/user/search").permitAll()
                        .requestMatchers("/user/createUser").permitAll()
                        .requestMatchers("/user/getUserData").authenticated()
                        .requestMatchers("/user/addFriend").authenticated()
                        .requestMatchers("/user/checkFriendStatus").authenticated()
                        .requestMatchers("/user/unfriend").authenticated()
                        .requestMatchers("/user/getAllFriends").authenticated()
                        .requestMatchers("/user/updateUser").authenticated()
                        .requestMatchers("/user/createSupportTicket").authenticated()
                        .requestMatchers("/user/updateSupportTicket").authenticated()
                        .requestMatchers("/user/getUserId").authenticated()
                        .requestMatchers("/user/getUserRole").authenticated()
                        .requestMatchers("/user/addTicketComment").authenticated()
                        .requestMatchers("/user/createSupportTicket").authenticated()
                        .requestMatchers("/user/getAllUserTicket").authenticated()
                        .requestMatchers("/user/addTicketComment").authenticated()
                        .requestMatchers("/user/getUsername").authenticated()
                        .requestMatchers("/user/*/close").authenticated()
                        .requestMatchers("user/changePassword").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/ws/**", "/websocket/**", "/sockjs/**").permitAll()
                        .requestMatchers("/chat-websocket/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/user/updateProfileImage").authenticated()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN"))
                .cors(withDefaults())
                .headers(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3001");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
