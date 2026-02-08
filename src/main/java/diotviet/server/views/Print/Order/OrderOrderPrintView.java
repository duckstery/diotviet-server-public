package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;
import diotviet.server.annotations.PrintTag;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@PrintObject("order")
public interface OrderOrderPrintView {
    @PrintTag(sequence = 0, example = "742", ignored = true)
    String getId();

    @PrintTag(sequence = 1, example = "DH00001", isIdentifier = true)
    String getCode();

    @PrintTag(sequence = 2, component = OrderCustomerPrintView.class)
    OrderCustomerPrintView getCustomer();

    @PrintTag(sequence = 3, component = OrderItemPrintView.class, isIterable = true)
    List<OrderItemPrintView> getItems();

    @PrintTag(sequence = 4, example = "0123456789")
    String getPhoneNumber();

    @PrintTag(sequence = 5, example = "123 ABC, P.11, Q.5, TP.HCM")
    String getAddress();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(T(String).valueOf(target.provisionalAmount))}")
    @PrintTag(sequence = 6, example = "100,000")
    String getProvisionalAmount();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(T(String).valueOf(target.discount))}")
    @PrintTag(sequence = 7, example = "10,000")
    String getDiscount();

    @PrintTag(sequence = 8, example = "cash")
    String getDiscountUnit();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatMoney(T(String).valueOf(target.paymentAmount))}")
    @PrintTag(sequence = 9, example = "90,000")
    String getPaymentAmount();

    @PrintTag(sequence = 10, example = "Fragile")
    String getNote();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.createdAt, \"dd-MM-yyyy HH:mm:ss\")}")
    @PrintTag(sequence = 11, example = "01-01-2023 12:34:56")
    String getCreatedAt();

    @PrintTag(sequence = 12, example = "Duckster")
    String getCreatedBy();
}
