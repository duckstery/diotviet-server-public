package diotviet.server.templates.Transaction;

public record TransactionInteractRequest(
        Integer type,
        Long amount,
        String reason
) {
}
