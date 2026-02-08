package diotviet.server.views.Print.Order;

import diotviet.server.annotations.PrintObject;
import diotviet.server.annotations.PrintTag;
import org.springframework.beans.factory.annotation.Value;

@PrintObject("customer")
public interface OrderCustomerPrintView {
    @PrintTag(sequence = 0, example = "John Doe")
    String getName();

    @Value("#{T(diotviet.server.utils.OtherUtils).formatDateTime(target.createdAt, \"dd-MM-yyyy HH:mm:ss\")}")
    @PrintTag(sequence = 1, example = "01-01-1970 12:34:56")
    String getBirthday();

    @PrintTag(sequence = 2, example = "john.doe@gmail.com")
    String getEmail();
}
