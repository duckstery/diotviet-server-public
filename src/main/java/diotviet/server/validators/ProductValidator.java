package diotviet.server.validators;

import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.templates.Product.ProductInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.stream.Streams;
import org.modelmapper.spi.DestinationSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ProductValidator extends BusinessValidator<Product> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private ProductRepository repository;
    /**
     * Category validator
     */
    @Autowired
    private CategoryValidator categoryValidator;
    /**
     * Group validator
     */
    @Autowired
    private GroupValidator groupValidator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     */
    public Product validateAndExtract(ProductInteractRequest request) {
        // Primary validate
        validate(request);
        // Convert request to Product
        Product product = Objects.isNull(request.id())
                ? map(request, Product.class)
                : directMap(request, repository.findWithGroupById(request.id()));

        // Check if request's category is not null
        if (Objects.nonNull(request.category())) {
            // Check and get valid Category
            product.setCategory(categoryValidator.isExistById(request.category()));
        }
        // Check if request's group is not empty
        if (ArrayUtils.isNotEmpty(request.groups())) {
            // Assign Groups for Product
            groupValidator.assignGroups(product, request.groups());
        }
        // Check code
        checkCode(product, "MKH", repository::findFirstByCodeAndIsDeletedFalse, repository::findFirstByCodeLikeOrderByCodeDesc);

        return product;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(ProductInteractRequest request) {
        assertStringRequired(request, "title", 50);
        assertStringNonRequired(request, "code", 0, 10);
        assertNumb(request, "originalPrice", true, 0, 999999999999L);
        assertNumb(request, "actualPrice", true, 0, 999999999999L);
        assertNumb(request, "discount", true, 0, 999999999999L);
        assertStringNonRequired(request, "measureUnit", 0, 10);
        assertNumb(request, "weight", false, 0, 1000);
    }
}
