package diotviet.server.data;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import diotviet.server.constants.Status;
import diotviet.server.entities.QOrder;
import diotviet.server.templates.Order.OrderSearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

@Component
public class OrderDAO {
    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    public BooleanBuilder createFilter(OrderSearchRequest request) {
        // Get QOrder
        QOrder order = QOrder.order;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by groups
        if (Objects.nonNull(request.group())) {
            query.and(order.groups.any().id.eq(request.group()));
        }
        // Filter by type
        if (Objects.nonNull(request.status())) {
            query.and(order.status.in(Arrays.stream(request.status()).map(Status::fromCode).toArray(Status[]::new)));
        }
        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(order.createdAt.goe(request.createAtFrom().atStartOfDay()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(order.createdAt.loe(request.createAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by min resolvedAt
        if (Objects.nonNull(request.resolvedAtFrom())) {
            query.and(order.resolvedAt.goe(request.resolvedAtFrom().atStartOfDay()));
        }
        // Filter by max resolvedAt
        if (Objects.nonNull(request.resolvedAtTo())) {
            query.and(order.resolvedAt.loe(request.resolvedAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by min price
        if (Objects.nonNull(request.priceFrom())) {
            query.and(order.paymentAmount.goe(request.priceFrom()));
        }
        // Filter by max price
        if (Objects.nonNull(request.priceTo())) {
            query.and(order.paymentAmount.loe(request.priceTo()));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            // Concat criteria
            StringExpression expression = order.code
                    .concat(order.phoneNumber.coalesce(""))
                    .concat(order.address.coalesce(""))
                    .concat(order.customer.name.coalesce(""))
                    .concat(order.customer.code.coalesce(""))
                    .toLowerCase();

            // Split search by space
            for (String segment : request.search().toLowerCase().split(" ")) {
                query.and(expression.contains(segment));
            }
        }

        // Connect expression
        return query;
    }
}
