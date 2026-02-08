package diotviet.server.controllers;

import diotviet.server.entities.Transaction;
import diotviet.server.services.TransactionService;
import diotviet.server.templates.DetailHistoryRequest;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Report.DetailReportRequest;
import diotviet.server.templates.Transaction.TransactionInitResponse;
import diotviet.server.templates.Transaction.TransactionInteractRequest;
import diotviet.server.templates.Transaction.TransactionSearchRequest;
import diotviet.server.templates.Transaction.TransactionSearchResponse;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Transaction.TransactionSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api/transaction", produces = "application/json")
public class TransactionController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Transaction service
     */
    @Autowired
    private TransactionService service;
    /**
     * Utilities for Entity interact
     */
    @Autowired
    private EntityUtils entityUtils;

    // ****************************
    // Public API
    // ****************************

    /**
     * Index page
     *
     * @return
     */
    @GetMapping("/index")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> index(TransactionSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Transaction.class);
        // Get list of Customers (get all data, no need to filter anything)
        Page<TransactionSearchView> items = service.paginate(request);

        return ok(new TransactionInitResponse(headers, items));
    }

    /**
     * Search for Transaction that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> search(TransactionSearchRequest request) {
        // Search for data and response
        return ok(new TransactionSearchResponse(service.paginate(request)));
    }

    /**
     * Show detail
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(service.findById(id));
    }

    /**
     * Store (Create) transaction
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> store(TransactionInteractRequest request) {
        // Store item
        service.store(request);

        return ok("");
    }

    /**
     * Report income
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/report")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> report(DetailReportRequest request) {
        return ok(service.report(request));
    }

    /**
     * Get Transaction history
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/history")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> history(DetailHistoryRequest request) {
        return ok(service.history(request));
    }
}
