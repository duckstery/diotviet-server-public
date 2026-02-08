package diotviet.server.views.Print.Ticket;

import diotviet.server.annotations.PrintObject;
import diotviet.server.annotations.PrintTag;
import diotviet.server.views.Print.Order.OrderCustomerPrintView;
import diotviet.server.views.Print.Order.OrderItemPrintView;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@PrintObject("ticket")
public interface TicketPrintView {
    @PrintTag(sequence = 0, example = "abc", isIdentifier = true)
    String getValue();

    @PrintTag(sequence = 1, example = "DH00001")
    String getCode();

    @PrintTag(sequence = 2, example = "Nguyễn Văn A")
    String getName();

    @PrintTag(sequence = 3, example = "0123456789")
    String getPhoneNumber();
}
