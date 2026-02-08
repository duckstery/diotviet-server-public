package diotviet.server.templates.Staff;

import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.templates.EntityHeader;
import diotviet.server.views.Customer.CustomerSearchView;
import diotviet.server.views.Staff.StaffSearchView;
import org.springframework.data.domain.Page;

import java.util.List;

public record StaffInitResponse(
        EntityHeader[] headers,
        Page<StaffSearchView> items,
        List<Group> groups
) {
}
