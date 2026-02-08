package diotviet.server.templates.User;

/**
 * Login request format
 *
 * @param username
 * @param password
 */
public record LoginRequest(String username, String password) {
}
