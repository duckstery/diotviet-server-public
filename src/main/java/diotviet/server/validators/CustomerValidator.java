package diotviet.server.validators;

import diotviet.server.constants.Type;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Product;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.services.CategoryService;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

@Component
public class CustomerValidator extends BusinessValidator<Customer> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository repository;
    /**
     * Group validator
     */
    @Autowired
    private GroupValidator groupValidator;
    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Customer validateAndExtract(CustomerInteractRequest request) {
        // Primary validation
        validate(request);
        // Convert request to Customer
        Customer customer = Objects.isNull(request.id())
                ? map(request, Customer.class)
                : directMap(request, repository.findWithGroupById(request.id()));
        // Optimistic lock check
        checkOptimisticLock(customer, repository);

        // Check if request's group is not empty
        if (ArrayUtils.isNotEmpty(request.groups())) {
            // Check and get valid Group
            groupValidator.assignGroups(customer, request.groups());
        }
        // Check and get the valid code
        checkCode(customer, "KH", repository::findFirstByCodeAndIsDeletedFalse, repository::findFirstByCodeLikeOrderByCodeDesc);
        // Preserve LocalDate type data
        checkDateData(customer);

        // Set category
        customer.setCategory(categoryService.getCategories(Type.PARTNER).stream().findFirst().orElseThrow());

        return customer;
    }

    /**
     * Check if customer is valid by id
     *
     * @param id
     * @return
     */
    public Customer isValid(Long id) {
        // Get Customer that is not deleted by id
        Customer customer = repository.findByIdAndIsDeletedFalse(id, Customer.class);
        // Check if customer is not exist
        if (Objects.isNull(customer)) {
            interrupt("inconsistent_data", "customer");
        }

        return customer;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(CustomerInteractRequest request) {
        assertStringRequired(request, "name", 50);
        assertStringNonRequired(request, "code", 0, 10);
        assertStringNonRequired(request, "phoneNumber", 0, 15);
    }

    /**
     * Check and preserve LocalDate data
     *
     * @param customer
     */
    private void checkDateData(Customer customer) {
        // Check if Customer is not exist, so nothing need to be preserved
        Optional<Customer> readonly = repository.findById(customer.getId());
        if (readonly.isEmpty()) {
            return;
        }

        // Get original customer to preserve LocalDate data
        Customer original = readonly.get();

        // Set data for modified customer
        customer.setCreatedAt(original.getCreatedAt())
                .setLastOrderAt(original.getLastOrderAt())
                .setLastTransactionAt(original.getLastTransactionAt());
    }
}
