package diotviet.server.data;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.Status;
import diotviet.server.entities.QTransaction;
import diotviet.server.templates.Order.OrderSearchRequest;
import diotviet.server.templates.Transaction.TransactionSearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

@Component
public class TransactionDAO {
    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    public BooleanBuilder createFilter(TransactionSearchRequest request) {
        // Get QTransaction
        QTransaction transaction = QTransaction.transaction;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(transaction.createdAt.goe(request.createAtFrom().atStartOfDay()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(transaction.createdAt.loe(request.createAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by type
        if (Objects.isNull(request.type())) {
            // This means find Transaction of all type
            // Filter by min price
            if (Objects.nonNull(request.priceFrom())) {
                query.and(transaction.amount.abs().goe(request.priceFrom()));
            }
            // Filter by max price
            if (Objects.nonNull(request.priceTo())) {
                query.and(transaction.amount.abs().loe(request.priceTo()));
            }
        } else {
            // This means find Transaction of type Collect (amount >= 0) or Spend (amount < 0)
            // Get sign
            Long sign = request.type() ? 1L : -1L;
            if (sign == 1L) {
                query.and(transaction.amount.multiply(sign).goe(0));
            } else {
                query.and(transaction.amount.multiply(sign).gt(0));
            }

            // Filter by min price
            if (Objects.nonNull(request.priceFrom())) {
                query.and(transaction.amount.multiply(sign).goe(request.priceFrom()));
            }
            // Filter by max price
            if (Objects.nonNull(request.priceTo())) {
                query.and(transaction.amount.multiply(sign).loe(request.priceTo()));
            }
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            query.and(transaction.amount.stringValue().coalesce("")
                    .concat(transaction.createdAt.stringValue().coalesce(""))
                    .toLowerCase()
                    .contains(request.search().toLowerCase()));
        }

        // Connect expression
        return query;
    }
}
