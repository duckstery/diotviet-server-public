package diotviet.server.templates.Order;

import diotviet.server.templates.Order.Interact.OrderCustomer;
import diotviet.server.templates.Order.Interact.OrderItem;

import java.util.List;

public record OrderInteractRequest(
        Long id,
        String note,
        Long provisionalAmount,
        Long discount,
        String discountUnit,
        Long paymentAmount,
        OrderCustomer customer,
        List<OrderItem> items
) {
}
