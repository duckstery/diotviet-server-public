package diotviet.server.views.Report.impl;

import diotviet.server.views.Report.OrderReportView;
import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
public class OrderReportByMonth implements OrderReportView {

    // ****************************
    // Properties
    // ****************************

    /**
     * Time
     */
    private String time;

    /**
     * Get amount of created order
     *
     * @return
     */
    private Long createdOrderAmount;

    /**
     * Get amount of processing order
     *
     * @return
     */
    private Long processingOrderAmount;

    /**
     * Get amount of resolved order
     *
     * @return
     */
    private Long resolvedOrderAmount;

    /**
     * Get amount of aborted order
     *
     * @return
     */
    private Long abortedOrderAmount;

    // ****************************
    // Public API
    // ****************************

    /**
     * Constructor
     *
     * @param yearMonth
     */
    public OrderReportByMonth(YearMonth yearMonth) {
        this.time = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        this.createdOrderAmount = 0L;
        this.processingOrderAmount = 0L;
        this.resolvedOrderAmount = 0L;
        this.abortedOrderAmount = 0L;
    }

    /**
     * Get amount of created order
     *
     * @return
     */
    public void addCreatedOrderAmount(Long value) {
        this.createdOrderAmount += value;
    }

    /**
     * Get amount of processing order
     *
     * @return
     */
    public void addProcessingOrderAmount(Long value) {
        this.processingOrderAmount += value;
    }

    /**
     * Get amount of resolved order
     *
     * @return
     */
    public void addResolvedOrderAmount(Long value) {
        this.resolvedOrderAmount += value;
    }

    /**
     * Get amount of aborted order
     *
     * @return
     */
    public void addAbortedOrderAmount(Long value) {
        this.abortedOrderAmount += value;
    }
}
