package diotviet.server.templates.Product;

import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.templates.EntityHeader;
import diotviet.server.views.Product.ProductSearchView;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductInitResponse(
        EntityHeader[] headers,
        Page<ProductSearchView> items,
        List<Category> categories,
        List<Group> groups
) {
}
