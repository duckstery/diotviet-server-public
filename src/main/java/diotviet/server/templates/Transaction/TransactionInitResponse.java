package diotviet.server.templates.Transaction;

import diotviet.server.templates.EntityHeader;
import diotviet.server.views.Transaction.TransactionSearchView;
import org.springframework.data.domain.Page;

public record TransactionInitResponse(
        EntityHeader[] headers,
        Page<TransactionSearchView> items) {
}
