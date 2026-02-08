package diotviet.server.views.Product;

import diotviet.server.views.Visualizer;
import org.springframework.beans.factory.annotation.Value;

public interface ProductDetailView extends ProductSearchView, Visualizer {
    /**
     * Category ID
     *
     * @return
     */
    @Value("#{target.category.id}")
    long getCategoryId();
    /**
     * ID of groups
     *
     * @return
     */
    @Value("#{target.groups.![id]}")
    long[] getGroupIds();

    /**
     * Name of groups
     *
     * @return
     */
    @Value("#{T(String).join(', ', T(diotviet.server.utils.OtherUtils).sort(target.groups.![name]))}")
    String getGroups();

    /**
     * Description
     *
     * @return
     */
    String getDescription();

    /**
     * Get original price
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
}
