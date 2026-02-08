package diotviet.server.services;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import diotviet.server.constants.Role;
import diotviet.server.entities.AccessToken;
import diotviet.server.entities.User;
import diotviet.server.repositories.AccessTokenRepository;
import diotviet.server.repositories.UserRepository;
import diotviet.server.templates.User.ChangePasswordRequest;
import diotviet.server.utils.JWTUtils;
import diotviet.server.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    // ****************************
    // Properties
    // ****************************

    /**
     * User repository
     */
    @Autowired
    private UserRepository repository;
    /**
     * User validator
     */
    @Autowired
    private UserValidator validator;

    /**
     * Access token repository
     */
    @Autowired
    private AccessTokenRepository accessTokenRepository;

    /**
     * JWT Utility
     */
    @Autowired
    JWTUtils jwtUtils;
    /**
     * I18N
     */
    @Autowired
    private MessageSource messageSource;

    /**
     * Password encoder
     */
    PasswordEncoder encoder;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get requester name
     *
     * @return
     */
    public static String getRequester() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName();
    }

    /**
     * Get User role
     *
     * @return
     */
    public static Role getRole() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRole();
    }

    /**
     * Set password encoder
     *
     * @param passwordEncoder
     */
    public PasswordEncoder setPasswordEncoder(PasswordEncoder encoder) {
        return (this.encoder = encoder);
    }

    /**
     * Check if user is existed
     *
     * @return
     */
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Save user
     *
     * @param user
     * @return
     */
    public User save(User user) {
        return repository.save(user);
    }

    /**
     * Issue token for user
     *
     * @param authentication
     * @return
     */
    public AccessToken issueToken(Authentication authentication) {
        // Get authenticated user
        User user = (User) authentication.getPrincipal();

        // Generate token
        String jwt = jwtUtils.generate(user);
        // Subscribe token
        AccessToken token = user.subscribeToken(jwtUtils.decode(jwt));

        // Save
        repository.save(user);

        return token;
    }

    /**
     * Unsubscribe token (unauthenticated)
     *
     * @param authentication
     */
    public void unsubscribeToken(Authentication authentication) {
        // Get authenticated user
        User user = (User) authentication.getPrincipal();
        // Delete current active token
        accessTokenRepository.deleteAccessTokenByToken(user.getActiveToken());
    }

    /**
     * Verify token
     *
     * @return
     */
    public User verifyToken(DecodedJWT jwt) {
        // Get claims
        Map<String, Claim> claims = jwt.getClaims();
        // Load user
        User user = (User) loadUserByUsername(claims.get("username").asString());

        // Check if token exists in user's valid tokens
        if (user.getValidTokens().stream().noneMatch(accessToken -> accessToken.match(jwt.getToken()))) {
            // Get message
            String message = messageSource.getMessage("token_expired", null, LocaleContextHolder.getLocale());

            throw new TokenExpiredException(message, jwt.getExpiresAtAsInstant());
        }

        // Set active token
        user.setActiveToken(jwt.getToken());

        return user;
    }

    /**
     * Register an User
     *
     * @param username
     * @return
     */
    public User register(String name, String username, Role role) {
        return new User()
                .setName(name)
                .setUsername(username)
                .setPassword(encoder.encode("123456"))
                .setRole(role);
    }

    /**
     * Change password
     *
     * @param request
     * @return
     */
    public void changePassword(AuthenticationManager manager, ChangePasswordRequest request) {
        try {
            System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
            System.out.println(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
            System.out.println(request.password());
            // Try to validate request's password
            Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(
                    ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername(),
                    request.password()
            ));

            // Change password
            User user = ((User) authentication.getPrincipal()).setPassword(encoder.encode(request.newPassword()));
            // Save
            repository.save(user);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            validator.interrupt("password_not_match", "user", "password");
        }
    }

    /**
     * Reset User's passwords
     *
     * @param ids
     */
    public void resetPassword(Long[] ids) {
        // Get Users by ids
        List<User> users = repository.findAllById(List.of(ids));

        // Reset all password to '123456'
        for (User user : users) {
            user.setPassword(encoder.encode("123456"));
        }
        // Save all
        repository.saveAll(users);
    }

    // ****************************
    // Overridden
    // ****************************

    /**
     * Load user by username
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }
}
