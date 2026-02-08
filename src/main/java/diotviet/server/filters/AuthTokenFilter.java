package diotviet.server.filters;

import diotviet.server.entities.User;
import diotviet.server.services.UserService;
import diotviet.server.utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Objects;

public class AuthTokenFilter extends OncePerRequestFilter {

    // ****************************
    // Properties
    // ****************************

    /**
     * JWT Utility
     */
    @Autowired
    private JWTUtils jwtUtils;
    /**
     * User Service (AKA UserDetailsService)
     */
    @Autowired
    private UserService userService;
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    // ****************************
    // Overridden API
    // ****************************

    /**
     * Filter logic<br>
     * Verify JWT. If valid, create AuthenticationToken with user data
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Get JWT in request "Authorization" header
            String token = getToken(request);
            // Check if token is not null
            if (Objects.nonNull(token)) {
                // Verify and get user ID
                jwtUtils.verify(token);
                // Verify and load  user
                User user = userService.verifyToken(jwtUtils.decode(token));

                // Authenticate
                authenticate(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Get JWT in "Authorization" header
     *
     * @param request
     * @return
     */
    private String getToken(HttpServletRequest request) {
        // Get "Authorization" header
        String token = getTokenFromAuthorizationHeader(request);

        // Check if token is not empty
        if (Objects.isNull(token)) {
            return getTokenFromCookieHeader(request);
        }

        return token;
    }

    /**
     * Get token from "Authorization" header
     *
     * @param request
     * @return
     */
    private String getTokenFromAuthorizationHeader(HttpServletRequest request) {
        // Get "Authorization" header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Check if header has text and contains "Bearer "
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        // Check if cookie has token

        return null;
    }

    /**
     * Get token from "Cookie" header
     *
     * @param request
     * @return
     */
    private String getTokenFromCookieHeader(HttpServletRequest request) {
        // Get cookie that holding token
        Cookie cookie = WebUtils.getCookie(request, "_token");
        // Check if header has text and contains "Bearer "
        if (Objects.nonNull(cookie)) {
            return cookie.getValue();
        }

        return null;
    }

    /**
     * Authenticate user
     *
     * @param user
     * @param role
     */
    private void authenticate(User user) {
        // Create AuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        // Set AuthenticationToken to SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}
