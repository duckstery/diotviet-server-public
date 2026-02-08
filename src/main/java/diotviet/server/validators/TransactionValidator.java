package diotviet.server.validators;

import diotviet.server.entities.Transaction;
import diotviet.server.templates.Transaction.TransactionInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.springframework.stereotype.Component;

@Component
public class TransactionValidator extends BusinessValidator<Transaction> {

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     */
    public Transaction validateAndExtract(TransactionInteractRequest request) {
        // Primary validate
        validate(request);

        return (new Transaction())
                .setAmount(request.amount() * request.type())
                .setReason(request.reason());
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(TransactionInteractRequest request) {
        assertNumb(request, "amount", true, 0, 999999999999L);
        assertStringRequired(request, "reason", 255);
    }
}
