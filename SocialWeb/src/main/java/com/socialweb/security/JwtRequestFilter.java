package com.socialweb.security;

import com.socialweb.entity.UserEntity;
import com.socialweb.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.NoSuchElementException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetail userDetail;
    private final UserRepository userRepository;

    public JwtRequestFilter(JwtUtil jwtUtil, UserDetail userDetail, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetail = userDetail;
        this.userRepository = userRepository;
    }

    /**
     * Filter to validate JWT token and authenticate user with Spring Security Context.
     * 
     * @param request  the incoming HTTP request
     * @param response the outgoing HTTP response
     * @param filterChain the filter chain to continue the request
     * @throws ServletException if the request could not be handled
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token has expired");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetail.loadUserByUsername(username);

            UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
            if (userEntity == null || userEntity.isDeleted()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("User account is disabled or does not exist");
                return;
            }

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
