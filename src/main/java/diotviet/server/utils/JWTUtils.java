package diotviet.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import diotviet.server.entities.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JWTUtils {

    // ****************************
    // Properties
    // ****************************

    /**
     * JWT Secret key
     */
    @Value("${diotviet.app.jwt.secret}")
    private String secret;
    /**
     * JWT Token Expiration
     */
    @Value("${diotviet.app.jwt.expiration}")
    private String expiration;
    /**
     * JWT algorithm
     */
    private Algorithm algorithm;
    /**
     * JWT Verifier
     */
    private JWTVerifier verifier;

    // ****************************
    // Constructors
    // ****************************

    @PostConstruct
    public void init() {
        // Create algorithm
        algorithm = Algorithm.HMAC256(secret);
        // Create verifier
        verifier = JWT.require(algorithm)
                .withIssuer("diotviet")
                .build();
    }

    // ****************************
    // Public API
    // ****************************

    /**
     * Generate JWT
     *
     * @param authentication
     * @return
     */
    public String generate(User userPrincipal) {
        return JWT.create()
                .withIssuer("diotviet")
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + Integer.parseInt(expiration)))
                .withJWTId(UUID.randomUUID().toString())
                .withSubject(userPrincipal.getName())
                .withClaim("id", userPrincipal.getId())
                .withClaim("username", userPrincipal.getUsername())
                .withClaim("role", userPrincipal.getRole().getCode())
                .sign(algorithm);
    }

    /**
     * Verify JWT and get claims
     *
     * @param token
     * @return
     */
    public void verify(String token) {
        try {
            // Try to verify token
            verifier.verify(token);
        } catch (JWTVerificationException e) {
            System.out.println("Auth: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Decode JWT token
     *
     * @param token
     * @return
     */
    public DecodedJWT decode(String token) {
        return JWT.decode(token);
    }
}
