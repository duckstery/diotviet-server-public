package diotviet.server.templates.User;

public record ChangePasswordRequest(
        String password,
        String newPassword
) {
}
