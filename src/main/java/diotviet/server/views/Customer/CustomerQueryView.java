package diotviet.server.views.Customer;

public interface CustomerQueryView {
    /**
     * ID
     *
     * @return
     */
    long getId();

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
     * Point
     *
     * @return
     */
    Long getPoint();
}
