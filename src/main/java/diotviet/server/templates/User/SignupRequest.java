package diotviet.server.templates.User;

import diotviet.server.constants.Role;

public record SignupRequest(String name, String username, String password, Role role) {
}
