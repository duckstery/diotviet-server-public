package diotviet.server.views.Transaction;

import diotviet.server.views.Order.OrderHistoryView;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;

public interface TransactionHistoryView {
    /**
     * Get YearMonth of createdAt
     *
     * @return
     */
    @Value("#{T(java.time.YearMonth).from(target.createdAt)}")
    YearMonth getYearMonth();

    /**
     * Created at (as date)
     *
     * @return
     */
    @Value("#{target.createdAt.toLocalDate()}")
    LocalDate getDate();

    /**
     * Created at (as time)
     *
     * @return
     */
    @Value("#{target.createdAt.toLocalTime()}")
    LocalTime getTime();

    /**
     * Check if Transaction's type is Collect
     *
     * @return
     */
    @Value("#{target.amount >= 0}")
    Boolean getIsCollect();

    /**
     * Amount
     *
     * @return
     */
    @Value("#{T(Math).abs(target.amount)}")
    Long getAmount();

    /**
     * Reason
     *
     * @return
     */
    String getReason();

    /**
     * Get orders
     *
     * @return
     */
    OrderHistoryView getOrder();
}
