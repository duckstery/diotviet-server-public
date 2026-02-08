package diotviet.server.views.Report;

public interface OrderReportView extends ReportView {
    /**
     * Get amount of created order
     *
     * @return
     */
    Long getCreatedOrderAmount();

    /**
     * Get amount of processing order
     *
     * @return
     */
    Long getProcessingOrderAmount();

    /**
     * Get amount of resolved order
     *
     * @return
     */
    Long getResolvedOrderAmount();

    /**
     * Get amount of aborted order
     *
     * @return
     */
    Long getAbortedOrderAmount();
}
