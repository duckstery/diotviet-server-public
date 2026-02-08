package diotviet.server.views.Order;

import org.springframework.beans.factory.annotation.Value;

public interface OrderHistoryView {
    /**
     * Get id
     *
     * @return
     */
    Long getId();

    /**
     * Created at (as date)
     *
     * @return
     */
    String getCode();

    /**
     * Customer name
     *
     * @return
     */
    @Value("#{target.customer.name}")
    String getCustomerName();

    /**
     * Customer code
     *
     * @return
     */
    @Value("#{target.customer.code}")
    String getCustomerCode();
}
