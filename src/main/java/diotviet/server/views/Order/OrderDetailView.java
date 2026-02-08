package diotviet.server.views.Order;

import diotviet.server.views.Item.ItemDetailView;
import org.springframework.beans.factory.annotation.Value;

public interface OrderDetailView extends OrderSearchView {
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
     * Email
     *
     * @return
     */
    @Value("#{target.customer.email}")
    String getEmail();

    /**
     * Provisional amount
     *
     * @return
     */
    String getProvisionalAmount();

    /**
     * Discount
     *
     * @return
     */
    String getDiscount();

    /**
     * Discount unit
     *
     * @return
     */
    String getDiscountUnit();

    /**
     * Items
     *
     * @return
     */
    ItemDetailView[] getItems();

    /**
     * Note
     *
     * @return
     */
    String getNote();
}
