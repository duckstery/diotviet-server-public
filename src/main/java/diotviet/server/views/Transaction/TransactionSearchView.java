package diotviet.server.views.Transaction;

import org.springframework.beans.factory.annotation.Value;

public interface TransactionSearchView {
    /**
     * ID
     *
     * @return
     */
    long getId();

    /**
     * Type
     *
     * @return
     */
    @Value("#{target.amount >= 0L}")
    boolean getType();

    /**
     * Code
     *
     * @return
     */
    @Value("#{T(Math).abs(target.amount)}")
    String getAmount();

    /**
     * Get create date
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.createdAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getCreatedAt();
}
