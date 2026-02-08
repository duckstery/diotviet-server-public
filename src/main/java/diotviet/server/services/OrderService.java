package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.constants.Status;
import diotviet.server.data.OrderDAO;
import diotviet.server.entities.Item;
import diotviet.server.entities.Order;
import diotviet.server.repositories.ItemRepository;
import diotviet.server.repositories.OrderRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.structures.DataPoint;
import diotviet.server.structures.Dataset;
import diotviet.server.templates.Order.Interact.OrderItem;
import diotviet.server.templates.Order.OrderInteractRequest;
import diotviet.server.templates.Order.OrderPatchRequest;
import diotviet.server.templates.Order.OrderSearchRequest;
import diotviet.server.templates.Report.DetailReportRequest;
import diotviet.server.traits.ReportService;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.OrderValidator;
import diotviet.server.views.Order.OrderDetailView;
import diotviet.server.views.Order.OrderSearchView;
import diotviet.server.views.Print.Order.OrderOrderPrintView;
import diotviet.server.views.Report.OrderReportView;
import diotviet.server.views.Report.impl.OrderReportByMonth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService extends ReportService<OrderReportView> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Order repository
     */
    @Autowired
    private OrderRepository repository;
    /**
     * Order DAO
     */
    @Autowired
    private OrderDAO dao;
    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * Item repository
     */
    @Autowired
    private ItemRepository itemRepository;
    /**
     * Transaction service
     */
    @Autowired
    private TransactionService transactionService;
    /**
     * Order validator
     */
    @Autowired
    private OrderValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Order
     *
     * @param request
     * @return
     */
    public Page<OrderSearchView> paginate(OrderSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Order's data
        return repository.findBy(filter, q -> q.as(OrderSearchView.class).page(pageable));
    }

    /**
     * Get Order by id
     *
     * @param id
     * @return
     */
    public OrderDetailView findById(Long id) {
        return validator.isExist(repository.findById(id, OrderDetailView.class));
    }

    /**
     * Get Order print data
     *
     * @param id
     * @return
     */
    public OrderOrderPrintView print(Long id) {
        return validator.isExist(repository.findById(id, OrderOrderPrintView.class));
    }

    /**
     * Find code by id
     *
     * @param id
     * @return
     */
    public String findCodeById(Long id) {
        return repository.findCodeById(id);
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public Long store(OrderInteractRequest request, Status status) {
        // Common validate for create and update
        Order order = validator.validateAndExtract(request);
        // Setup
        basicSetup(request, order);
        // Check if order should be resolved
        if (Status.RESOLVED.equals(status)) {
            // Use transaction service to resolve Order
            transactionService.resolve(order, request.paymentAmount());
        }
        // For some reason, including this when saving Order cause multiple Selection point to Category and Group
        // For performance, Items will be saved separately by it repository
        List<Item> items = order.getItems();
        order.setItems(null);

        // Store and flush (immediate save to database), then proceed to store Product's Item
        Long id = repository.save(order).getId();
        itemRepository.saveAll(items);

        return id;
    }

    /**
     * Patch item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void patch(OrderPatchRequest request) {
        // Get status
        Status status = Status.fromCode(request.option());
        // Optimistic check
        validator.massCheckOptimisticLock(request.ids(), request.versions(), repository);
        // Get all Orders
        List<Order> orders = repository.findAllById(List.of(request.ids()));

        // Patch Orders
        switch (status) {
            case PROCESSING -> processOrders(orders, request.amount());
            case RESOLVED -> resolveOrders(orders, request.amount());
            case ABORTED -> abortOrders(orders, request.reason());
        }

        // Save
        repository.saveAll(orders);
    }

    /**
     * Search with a string
     *
     * @param request
     * @return
     */
    public List<OrderSearchView> query(OrderSearchRequest request) {
        return repository.findBy(dao.createFilter(request), q -> q
                        .sortBy(Sort.by(Sort.Direction.DESC, "createdAt", "code"))
                        .limit(100)
                        .all())
                .stream()
                .map(this::calculateCurrentPaymentAmount)
                .toList();
    }

    /**
     * Report
     *
     * @param request
     * @return
     */
    public List<Dataset<String, Long>> report(DetailReportRequest request) {
        // Prepare expected_income dataset
        Dataset<String, Long> createdOrderAmount = Dataset.of("created_order_amount", "0", "purple");
        // Prepare real_income_inside dataset
        Dataset<String, Long> processingOrderAmount = Dataset.of("processing_order_amount", "1", "yellow");
        // Prepare real_income_outside dataset
        Dataset<String, Long> resolvedOrderAmount = Dataset.of("resolved_order_amount", "2", "green");
        // Prepare usage dataset
        Dataset<String, Long> abortedOrderAmount = Dataset.of("aborted_order_amount", "3", "red");

        // Get report by date
        List<OrderReportView> report = repository.selectOrderReportByCreatedAt(request.from(), request.to());

        // Check if display mode is by month
        if (StringUtils.equals(request.displayMode(), "month")) {
            report = groupReportByMonth(report, OrderReportByMonth.class);
        }

        // Iterate through each income report's entry
        for (OrderReportView entry : report) {
            createdOrderAmount.add(DataPoint.of(entry.getTime(), entry.getCreatedOrderAmount()));
            processingOrderAmount.add(DataPoint.of(entry.getTime(), entry.getProcessingOrderAmount()));
            resolvedOrderAmount.add(DataPoint.of(entry.getTime(), entry.getResolvedOrderAmount()));
            abortedOrderAmount.add(DataPoint.of(entry.getTime(), entry.getAbortedOrderAmount()));
        }

        return List.of(createdOrderAmount, processingOrderAmount, resolvedOrderAmount, abortedOrderAmount);
    }

    /**
     * Get all Order for export
     *
     * @return
     */
    public List<Order> export(OrderSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);

        return repository.findBy(filter, FluentQuery.FetchableFluentQuery::all);
    }

    // ****************************
    // Private
    // ****************************

    /**
     * Calculate current payment amount for Order
     *
     * @param order
     * @return
     */
    private OrderSearchView calculateCurrentPaymentAmount(Order order) {
        // Create Projector
        ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();

        // Calculate Order current payment amount only for PROCESSING Order
        if (Status.PROCESSING.equals(order.getStatus())) {
            Long paymentAmount = order.getPaymentAmount();
            Long paidAmount = transactionService.getPaidAmountOf(order);
            order.setPaymentAmount(paymentAmount - paidAmount);
        }

        // Project OrderSearchView to Order
        return projectionFactory.createProjection(OrderSearchView.class, order);
    }

    /**
     * Basic info setup
     *
     * @param request
     * @param order
     */
    private void basicSetup(OrderInteractRequest request, Order order) {
        // Set created by
        order.setCreatedBy(UserService.getRequester())
                // Count Product that can be accumulated and set point
                .setPoint(productRepository.countByIdInAndCanBeAccumulatedTrueAndIsDeletedFalse(request
                        .items()
                        .stream()
                        .map(OrderItem::id)
                        .toList()))
                // Set address
                .setAddress(order.getCustomer().getAddress())
                // Set phone number
                .setPhoneNumber(order.getCustomer().getPhoneNumber())
                // Set customer last order at
                .getCustomer().setLastOrderAt(order.getCreatedAt());
    }

    /**
     * Resolve all Orders <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction <br>
     * For RESOLVED and ABORTED, throw Exception
     *
     * @param orders
     */
    private void processOrders(List<Order> orders, Long amount) {
        // Resolve Orders and save resolve Transaction
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING, PROCESSING -> transactionService.process(order, amount);
                // Do not allow to resolve RESOLVED and ABORTED
                case RESOLVED -> validator.abort("process_resolved_order");
                case ABORTED -> validator.abort("aborted_order");
            }
        }
    }

    /**
     * Resolve all Orders <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction <br>
     * For RESOLVED and ABORTED, throw Exception
     *
     * @param orders
     */
    private void resolveOrders(List<Order> orders, Long amount) {
        // Resolve Orders and save resolve Transaction
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING, PROCESSING -> transactionService.resolve(order, amount);
                // Do not allow to resolve RESOLVED and ABORTED
                case RESOLVED -> validator.abort("resolve_resolved_order");
                case ABORTED -> validator.abort("aborted_order");
            }
        }
    }

    /**
     * Abort all Orders
     * For PENDING, add a Transaction to save reason <br>
     * For PROCESSING and RESOLVED, their Transactions will be soft delete and have reason <br>
     * For ABORTED, throw Exception
     *
     * @param orders
     */
    private void abortOrders(List<Order> orders, String reason) {
        // Abort Orders
        for (Order order : orders) {
            switch (order.getStatus()) {
                case PENDING, PROCESSING, RESOLVED -> transactionService.abort(order, reason);
                // Do not allow to abort ABORTED
                case ABORTED -> validator.abort("aborted_order");
            }
        }
    }
}
