package diotviet.server.views.Order;

import diotviet.server.views.Lockable;
import org.springframework.beans.factory.annotation.Value;

public interface OrderSearchView extends Lockable {
    /**
     * ID
     *
     * @return
     */
    long getId();

    /**
     * Code
     *
     * @return
     */
    String getCode();

    /**
     * Customer name
     *
     * @return
     */
    @Value("#{target.customer.id}")
    String getCustomerId();

    /**
     * Customer name
     *
     * @return
     */
    @Value("#{target.customer.code + \" - \" + target.customer.name}")
    String getCustomer();

    /**
     * Phone number
     *
     * @return
     */
    String getPhoneNumber();

    /**
     * Address
     *
     * @return
     */
    String getAddress();

    /**
     * Actual price
     *
     * @return
     */
    String getPaymentAmount();

    /**
     * Status
     *
     * @return
     */
    @Value("#{target.status.getCode()}")
    int getStatus();

    /**
     * Point
     *
     * @return
     */
    String getPoint();

    /**
     * Get creator
     *
     * @return
     */
    String getCreatedBy();

    /**
     * Get create date
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.createdAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getCreatedAt();

    /**
     * Get resolve date
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.resolvedAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getResolvedAt();
}
