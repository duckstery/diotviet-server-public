package diotviet.server.templates.Customer;

import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.templates.EntityHeader;
import diotviet.server.views.Customer.CustomerSearchView;
import org.springframework.data.domain.Page;

import java.util.List;

public record CustomerInitResponse(
        EntityHeader[] headers,
        Page<CustomerSearchView> items,
        List<Category> categories,
        List<Group> groups
) {
}
