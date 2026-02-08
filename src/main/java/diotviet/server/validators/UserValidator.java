package diotviet.server.validators;

import diotviet.server.entities.User;
import diotviet.server.traits.BusinessValidator;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserValidator extends BusinessValidator<User> {

}
