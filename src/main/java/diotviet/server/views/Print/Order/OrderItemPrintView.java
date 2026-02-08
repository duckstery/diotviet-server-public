package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;
import diotviet.server.annotations.PrintTag;
import org.springframework.beans.factory.annotation.Value;

@PrintObject(value = "item", sizeOfExample = 3)
public interface OrderItemPrintView {
    @Value("#{target.product.title}")
    @PrintTag(sequence = 0, example = {"Apple", "Orange", "Mango"})
    String getTitle();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(T(String).valueOf(target.originalPrice))}")
    @PrintTag(sequence = 1, example = {"10,000", "20,000", "15,000"})
    String getOriginalPrice();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(T(String).valueOf(target.discount))}")
    @PrintTag(sequence = 2, example = {"1,000", "2,000", "1,500"})
    String getDiscount();

    @PrintTag(sequence = 3, example = {"cash", "cash", "cash"})
    String getDiscountUnit();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(T(String).valueOf(target.actualPrice))}")
    @PrintTag(sequence = 4, example = {"9,000", "18,000", "13,500"})
    String getActualPrice();

    @Value("#{T(String).valueOf(target.actualPrice * target.quantity)}")
    @PrintTag(sequence = 5, example = {"27,000", "36,000", "27,000"})
    String getTotalPrice();

    @PrintTag(sequence = 6, example = {"Fresh", "Round", "Yellow"})
    String getNote();

    @PrintTag(sequence = 7, example = {"3", "2", "2"})
    int getQuantity();
}
