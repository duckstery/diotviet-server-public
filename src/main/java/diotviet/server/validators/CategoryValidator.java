package diotviet.server.validators;

import diotviet.server.entities.Category;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.CategoryRepository;
import diotviet.server.traits.BaseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CategoryValidator extends BaseValidator<Category> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Category repository
     */
    @Autowired
    private CategoryRepository categoryRepository;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate for Category existence and return it
     */
    public Category isExistById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }

        // Check if category is exists
        Category category = categoryRepository.findById(id).orElse(null);
        if (Objects.isNull(category)) {
            throw new ServiceValidationException("invalid_category", "", "category");
        }

        return category;
    }
}
