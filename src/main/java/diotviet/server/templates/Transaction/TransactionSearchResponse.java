package diotviet.server.templates.Transaction;

import diotviet.server.views.Transaction.TransactionSearchView;
import org.springframework.data.domain.Page;

public record TransactionSearchResponse(Page<TransactionSearchView> items) {
}
