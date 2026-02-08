package diotviet.server.views.Customer;

import org.springframework.beans.factory.annotation.Value;

public interface CustomerSearchView {
    /**
     * ID
     *
     * @return
     */
    long getId();

    /**
     * Category name
     *
     * @return
     */
    @Value("#{target.category.name}")
    String getCategory();

    /**
     * Code
     *
     * @return
     */
    String getCode();

    /**
     * Name
     *
     * @return
     */
    String getName();

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
     * Birthday
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.birthday, \"dd-MM-yyyy\")}")
    String getBirthday();

    /**
     * Gender
     *
     * @return
     */
    boolean getIsMale();

    /**
     * Point
     *
     * @return
     */
    Long getPoint();

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
     * Get last order date time
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.lastOrderAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getLastOrderAt();

    /**
     * Get last transaction date time
     *
     * @return
     */
    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.lastTransactionAt, \"dd-MM-yyyy HH:mm:ss\")}")
    String getLastTransactionAt();
}
