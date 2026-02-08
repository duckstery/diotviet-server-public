package diotviet.server.templates.Order;

import diotviet.server.entities.Group;
import diotviet.server.templates.EntityHeader;
import diotviet.server.views.Order.OrderSearchView;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrderInitResponse(
        EntityHeader[] headers,
        Page<OrderSearchView> items,
        List<Group> groups) {
}
