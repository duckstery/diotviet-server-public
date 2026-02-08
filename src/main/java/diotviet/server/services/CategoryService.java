package diotviet.server.services;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Category repository
     */
    @Autowired
    private CategoryRepository repository;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get all categories
     *
     * @return
     */
    public List<Category> getCategories(Type type) {
        // Query for Product's data
        return repository.findAllByType(type, Sort.by("id").ascending());
    }
}
