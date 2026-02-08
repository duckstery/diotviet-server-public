package diotviet.server.templates.Order.Interact;

public record OrderItem(
        Long id,
        String note,
        Long originalPrice,
        Long discount,
        String discountUnit,
        Long actualPrice,
        Integer quantity
) {
}
