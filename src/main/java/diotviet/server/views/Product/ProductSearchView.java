package diotviet.server.views.Product;

import org.springframework.beans.factory.annotation.Value;

public interface ProductSearchView {
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
     * Title
     *
     * @return
     */
    String getTitle();

    /**
     * Actual price
     *
     * @return
     */
    String getActualPrice();

    /**
     * Measure unit
     *
     * @return
     */
    String getMeasureUnit();

    /**
     * Weight
     *
     * @return
     */
    String getWeight();

    /**
     * Can be accumulated
     *
     * @return
     */
    Boolean getCanBeAccumulated();

    /**
     * Is in business
     *
     * @return
     */
    Boolean getIsInBusiness();
}
