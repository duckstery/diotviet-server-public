package diotviet.server.views.Item;

import org.springframework.beans.factory.annotation.Value;

public interface ItemDetailView {
    /**
     * Get id
     *
     * @return
     */
    long getId();

    /**
     * Get code
     *
     * @return
     */
    @Value("#{target.product.code}")
    String getCode();

    /**
     * Product id
     *
     * @return
     */
    @Value("#{target.product.id}")
    long getProductId();

    /**
     * Title
     *
     * @return
     */
    @Value("#{target.product.title}")
    String getTitle();

    /**
     * Original price
     *
     * @return
     */
    String getOriginalPrice();

    /**
     * Get discount
     *
     * @return
     */
    String getDiscount();

    /**
     * Measure unit
     *
     * @return
     */
    String getDiscountUnit();

    /**
     * Actual price
     *
     * @return
     */
    String getActualPrice();

    /**
     * Note
     *
     * @return
     */
    String getNote();

    /**
     * Quantity
     *
     * @return
     */
    Integer getQuantity();
}
