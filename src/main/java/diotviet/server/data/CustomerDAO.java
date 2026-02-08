package diotviet.server.data;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import diotviet.server.entities.QCustomer;
import diotviet.server.templates.Customer.CustomerSearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Objects;

@Component
public class CustomerDAO {
    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    public BooleanBuilder createFilter(CustomerSearchRequest request) {
        // Get QCustomer
        QCustomer customer = QCustomer.customer;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by groups
        if (Objects.nonNull(request.group())) {
            query.and(customer.groups.any().id.eq(request.group()));
        }
        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(customer.createdAt.goe(request.createAtFrom().atStartOfDay()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(customer.createdAt.loe(request.createAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by min birthday
        if (Objects.nonNull(request.birthdayFrom())) {
            query.and(customer.birthday.goe(request.birthdayFrom()));
        }
        // Filter by max birthday
        if (Objects.nonNull(request.birthdayTo())) {
            query.and(customer.birthday.loe(request.birthdayTo()));
        }
        // Filter by min lastTransactionAt
        if (Objects.nonNull(request.lastTransactionAtFrom())) {
            query.and(customer.lastTransactionAt.goe(request.lastTransactionAtFrom().atStartOfDay()));
        }
        // Filter by max lastTransactionAt
        if (Objects.nonNull(request.lastTransactionAtTo())) {
            query.and(customer.lastTransactionAt.loe(request.lastTransactionAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by isMale flag
        if (Objects.nonNull(request.isMale())) {
            query.and(customer.isMale.eq(request.isMale()));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            // Concat criteria
            StringExpression expression = customer.name.coalesce("")
                    .concat(customer.code.coalesce(""))
                    .concat(customer.phoneNumber.coalesce(""))
                    .concat(customer.address.coalesce(""))
                    .toLowerCase();

            // Split search by space
            for (String segment : request.search().toLowerCase().split(" ")) {
                query.and(expression.contains(segment));
            }
        }

        // Connect expression
        return query.and(customer.isDeleted.isFalse());
    }
}
