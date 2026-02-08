package diotviet.server.validators;

import diotviet.server.entities.Order;
import diotviet.server.repositories.OrderRepository;
import diotviet.server.services.ProductService;
import diotviet.server.templates.Order.OrderInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OrderValidator extends BusinessValidator<Order> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Order repository
     */
    @Autowired
    private OrderRepository repository;
    /**
     * Customer validator
     */
    @Autowired
    private CustomerValidator customerValidator;
    /**
     * Product service
     */
    @Autowired
    private ProductService productService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Order validateAndExtract(OrderInteractRequest request) {
        // Convert request to Customer
        Order order = map(request, Order.class);

        // Check if no customer is specified
        if (Objects.isNull(request.customer()) || Objects.isNull(request.customer().id())) {
            interrupt("specify_customer", "customer");
        }
        // Check if customer is not exist
        order.setCustomer(customerValidator.isValid(request.customer().id()));

        // Check if no item is specified
        if (CollectionUtils.isEmpty(request.items())) {
            interrupt("specify_least_item", "product");
        }
        // Produce Product's Item
        order.setItems(productService.produce(order.getItems(), order));
        // If items is null, it means some Product is not valid
        if (Objects.isNull(order.getItems())) {
            interrupt("inconsistent_data", "product");
        }

        // Make sure code will always be null, so it'll only be set in the next statement
        order.setCode(null);
        // Generate code
        checkCode(order, "MDH", 6, null, repository::findFirstByCodeLikeOrderByCodeDesc);

        return order;
    }
}
