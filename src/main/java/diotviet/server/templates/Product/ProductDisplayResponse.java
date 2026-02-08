package diotviet.server.templates.Product;

import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.views.Product.ProductDisplayView;

import java.util.List;

public record ProductDisplayResponse(
        List<ProductDisplayView> items,
        String template,
        PrintableTag[] tags
) {
}
