package diotviet.server.data;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import diotviet.server.entities.QProduct;
import diotviet.server.templates.Product.ProductSearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProductDAO {
    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    public BooleanBuilder createFilter(ProductSearchRequest request) {
        // Get QProduct
        QProduct product = QProduct.product;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by category
        if (Objects.nonNull(request.categories())) {
            query.and(product.category.id.in(request.categories()));
        }
        // Filter by groups
        if (Objects.nonNull(request.group())) {
            query.and(product.groups.any().id.eq(request.group()));
        }
        // Filter by min price
        if (Objects.nonNull(request.minPrice())) {
            query.and(product.actualPrice.goe(request.minPrice()));
        }
        // Filter by max price
        if (Objects.nonNull(request.maxPrice())) {
            query.and(product.actualPrice.loe(request.maxPrice()));
        }
        // Filter by canBeAccumulated flag
        if (Objects.nonNull(request.canBeAccumulated())) {
            query.and(product.canBeAccumulated.eq(request.canBeAccumulated()));
        }
        // Filter by isInBusiness flag
        if (Objects.nonNull(request.isInBusiness())) {
            query.and(product.isInBusiness.eq(request.isInBusiness()));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            // Concat criteria
            StringExpression expression = product.code
                    .concat(product.title.coalesce(""))
                    .concat(product.code.coalesce(""))
                    .toLowerCase();

            // Split search by space
            for (String segment : request.search().toLowerCase().split(" ")) {
                query.and(expression.contains(segment));
            }
        }

        // Connect expression
        return query.and(product.isDeleted.isFalse());
    }
}
