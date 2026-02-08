package diotviet.server.templates.Order;

import diotviet.server.views.Order.OrderSearchView;
import org.springframework.data.domain.Page;

public record OrderSearchResponse(Page<OrderSearchView> items) {
}
