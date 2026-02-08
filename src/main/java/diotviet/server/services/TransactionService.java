package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.constants.Status;
import diotviet.server.data.TransactionDAO;
import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.structures.DataPoint;
import diotviet.server.structures.Dataset;
import diotviet.server.templates.DetailHistoryRequest;
import diotviet.server.templates.Report.DetailReportRequest;
import diotviet.server.templates.Transaction.TransactionInteractRequest;
import diotviet.server.templates.Transaction.TransactionSearchRequest;
import diotviet.server.traits.ReportService;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.TransactionValidator;
import diotviet.server.views.Order.OrderDetailView;
import diotviet.server.views.Report.IncomeReportView;
import diotviet.server.views.Report.impl.IncomeReportByMonth;
import diotviet.server.views.Transaction.TransactionDetailView;
import diotviet.server.views.Transaction.TransactionHistoryView;
import diotviet.server.views.Transaction.TransactionSearchView;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionService extends ReportService<IncomeReportView> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Transaction repository
     */
    @Autowired
    private TransactionRepository repository;
    /**
     * Transaction validator
     */
    @Autowired
    private TransactionValidator validator;
    /**
     * Transaction DAO
     */
    @Autowired
    private TransactionDAO dao;


    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Transaction
     *
     * @param request
     * @return
     */
    public Page<TransactionSearchView> paginate(TransactionSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("id")
        );

        // Query for Order's data
        return repository.findBy(filter, q -> q.as(TransactionSearchView.class).page(pageable));
    }

    /**
     * Get Transaction by id
     *
     * @param id
     * @return
     */
    public TransactionDetailView findById(Long id) {
        return validator.isExist(repository.findById(id, TransactionDetailView.class));
    }

    /**
     * Process order <br>
     * For both PENDING and PROCESSING, create a Transaction with provided amount <br>
     * If the amount is equal or surpass Order current payment amount, resolve Order
     *
     * @param order
     */
    public void process(Order order, Long amount) {
        // Get Order payment amount and convert to Long
        long paymentAmount = order.getPaymentAmount();
        // Parse provided amount
        long processAmount = amount;
        // Get paid amount
        long paidAmount = getPaidAmountOf(order);

        // Check if the amount is equal or surpass Order current payment amount
        if (processAmount >= (paymentAmount - paidAmount)) {
            // Resolve Order
            resolve(order, amount);
        } else {
            // Process Order and create a process transaction
            order.setStatus(Status.PROCESSING).getTransactions().add(createTransactionFor(order, amount));
        }
    }

    /**
     * Resolve order <br>
     * For PENDING, create a Transaction with amount of Order <br>
     * For PROCESSING, create a Transaction with amount of leftover after previous Transaction
     *
     * @param order
     */
    public void resolve(Order order, Long amount) {
        // Create a resolve Transaction
        Transaction resolveTransaction;

        if (Objects.isNull(amount)) {
            // If amount is null, resolve by subtract paid amount from payment amount
            Long paymentAmount = order.getPaymentAmount();
            Long paidAmount = getPaidAmountOf(order);

            // Create Transaction
            resolveTransaction = createTransactionFor(order, paymentAmount - paidAmount);
        } else {
            // Else, apply requested amount as resolve amount
            resolveTransaction = createTransactionFor(order, amount);
        }

        // Setup Order
        order.setStatus(Status.RESOLVED)
                .setResolvedAt(LocalDateTime.now())
                .setTransactions(new ArrayList<>(List.of(resolveTransaction)));
    }

    /**
     * Abort order
     * For PENDING, add a Transaction to save reason <br>
     * For PROCESSING and RESOLVED, their Transactions will be soft delete and have reason
     *
     * @param order
     * @return
     */
    public void abort(Order order, String reason) {
        // Create Transaction list for Order
        List<Transaction> transactions = new ArrayList<>();

        // Check if Order has any Transaction
        if (CollectionUtils.isNotEmpty(order.getTransactions())) {
            // Add all modified Transaction to new list
            transactions.addAll(order.getTransactions().stream()
                    // Peak through each transaction, soft delete and update reason for them
                    .peek(transaction -> transaction.setIsDeleted(true).setReason(reason))
                    .toList());
        } else {
            // Create a Transaction to set reason for Order
            transactions.add(createTransactionFor(order, 0L).setReason(reason));
        }

        // Setup Order
        order.setStatus(Status.ABORTED).setTransactions(transactions);
    }

    /**
     * Get Order paid amount
     *
     * @param order
     * @return
     */
    public Long getPaidAmountOf(Order order) {
        // Check if Order has any Transaction
        if (CollectionUtils.isEmpty(order.getTransactions())) {
            return 0L;
        }

        // If Order has some Transactions, iterate through those Transaction to see how much money is paid
        return order.getTransactions().stream()
                .map(Transaction::getAmount)
                .reduce(0L, Long::sum);
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void store(TransactionInteractRequest request) {
        // Common validate for create and update, then save it
        repository.save(validator.validateAndExtract(request));
    }

    /**
     * Report
     *
     * @param request
     * @return
     */
    public List<Dataset<String, Long>> report(DetailReportRequest request) {
        // Prepare expected_income dataset
        Dataset<String, Long> expectedIncome = Dataset.of("expected_income", "0", "blue");
        // Prepare real_income_inside dataset
        Dataset<String, Long> realIncomeInside = Dataset.of("real_income_inside", "1", "yellow");
        // Prepare real_income_outside dataset
        Dataset<String, Long> realIncomeOutside = Dataset.of("real_income_outside", "1", "purple");
        // Prepare usage dataset
        Dataset<String, Long> usage = Dataset.of("usage", "2", "red");

        // Get report by date
        List<IncomeReportView> report = repository.selectIncomeReportByCreatedAt(request.from(), request.to());

        // Check if display mode is by month
        if (StringUtils.equals(request.displayMode(), "month")) {
            report = groupReportByMonth(report, IncomeReportByMonth.class);
        }

        // Iterate through each income report's entry
        for (IncomeReportView entry : report) {
            expectedIncome.add(DataPoint.of(entry.getTime(), entry.getExpectedIncome()));
            realIncomeInside.add(DataPoint.of(entry.getTime(), entry.getRealIncomeInside()));
            realIncomeOutside.add(DataPoint.of(entry.getTime(), entry.getRealIncomeOutside()));
            usage.add(DataPoint.of(entry.getTime(), entry.getUsage()));
        }

        return List.of(expectedIncome, realIncomeInside, realIncomeOutside, usage);
    }

    /**
     * Get history
     *
     * @param request
     * @return
     */
    public Slice<TransactionHistoryView> history(DetailHistoryRequest request) {
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), 20),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return repository.findAllByCreatedAtBetweenAndIsDeletedIsFalse(
                request.from().atStartOfDay(),
                request.to().atTime(LocalTime.MAX),
                pageable
        );
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Create Transaction with amount for Order
     *
     * @param order
     * @param amount
     * @return
     */
    private Transaction createTransactionFor(Order order, Long amount) {
        // Create Transaction and setup
        return new Transaction()
                .setAmount(amount)
                .setCreatedAt(OtherUtils.get(order.getResolvedAt(), LocalDateTime.now()))
                .setOrder(order);
    }
}
