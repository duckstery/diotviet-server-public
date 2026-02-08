package diotviet.server.templates.Product;

import diotviet.server.views.Product.ProductSearchView;
import org.springframework.data.domain.Page;

public record ProductSearchResponse(Page<ProductSearchView> items) {
}
