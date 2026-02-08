package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Group;
import diotviet.server.services.CategoryService;
import diotviet.server.services.CustomerService;
import diotviet.server.services.GroupService;
import diotviet.server.services.imports.CustomerImportService;
import diotviet.server.templates.Customer.CustomerInitResponse;
import diotviet.server.templates.Customer.CustomerInteractRequest;
import diotviet.server.templates.Customer.CustomerSearchRequest;
import diotviet.server.templates.Customer.CustomerSearchResponse;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Report.RankReportRequest;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Customer.CustomerSearchView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping(value = "/api/customer", produces = "application/json")
public class CustomerController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Customer service
     */
    @Autowired
    private CustomerService customerService;
    /**
     * Category service
     */
    @Autowired
    private CategoryService categoryService;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;
    /**
     * Customer import service
     */
    @Autowired
    private CustomerImportService importService;
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
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> index(CustomerSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Customer.class);
        // Get list of Customers (get all data, no need to filter anything)
        Page<CustomerSearchView> items = customerService.paginate(request);

         List<Category> categories = categoryService.getCategories(Type.PARTNER);
        // Get group list for FilterPanel
        List<Group> groups = groupService.getGroups(Type.PARTNER);

        return ok(new CustomerInitResponse(headers, items, categories, groups));
    }

    /**
     * Search for Customer that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> search(CustomerSearchRequest request) {
        // Search for data and response
        return ok(new CustomerSearchResponse(customerService.paginate(request)));
    }

    /**
     * Show detail
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(customerService.findById(id));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> store(CustomerInteractRequest request) {
        // Store item
        return ok(customerService.store(request));
    }

    /**
     * Delete item
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/delete")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> delete(@RequestParam("ids") Long[] ids) {
        // Store item
        customerService.delete(ids);

        return ok("");
    }

    /**
     * Import CSV
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> importCSV(@RequestPart("file") MultipartFile file) {
        // Parse CSV file
        List<Customer> customers = parse(file, Customer.class);
        // Prep the importer
        importService.prep();
        // Re-attach (or pull) any relationship
        importService.pull(customers);
        // Run import
        importService.runImport(customers);

        return ok("");
    }

    /**
     * Export CSV
     *
     * @return
     */
    @PostMapping(value = "/export")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> exportCSV(CustomerSearchRequest request) {
        // Export Bean to CSV
        byte[] bytes = export(customerService.export(request));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=customers.csv")
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(bytes));
    }

    /**
     * Simple search
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/query")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> simpleSearch(CustomerSearchRequest request) {
        return ok(customerService.query(request));
    }

    /**
     * Report Customer
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/report")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> report(RankReportRequest request) {
        return ok(customerService.report(request));
    }
}
