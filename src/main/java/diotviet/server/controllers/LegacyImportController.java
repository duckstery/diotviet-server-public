package diotviet.server.controllers;

import diotviet.server.exceptions.FileUploadingException;
import diotviet.server.services.imports.BaseImportService;
import diotviet.server.services.imports.CustomerImportService;
import diotviet.server.services.imports.OrderImportService;
import diotviet.server.services.imports.ProductImportService;
import diotviet.server.traits.BaseController;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Support imports from legacy (KiotViet)
 */
@Controller
@RequestMapping(value = "/api/legacy", produces = "application/json")
public class LegacyImportController extends BaseController {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product service
     */
    @Autowired
    private ProductImportService productService;
    /**
     * Customer service
     */
    @Autowired
    private CustomerImportService customerService;
    /**
     * Order service
     */
    @Autowired
    private OrderImportService orderService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Import KiotViet Product
     *
     * @param file
     * @return
     */
    @PostMapping(value = "product/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> importProduct(@RequestPart("file") MultipartFile file) {
        // Import legacy Product
        importCSV(productService, file);
        return ok("");
    }

    /**
     * Import KiotViet Customer
     *
     * @param file
     * @return
     */
    @PostMapping(value = "customer/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> importCustomer(@RequestPart("file") MultipartFile file) {
        // Import legacy Customer
        importCSV(customerService, file);
        return ok("");
    }

    /**
     * Import KiotViet Order
     *
     * @param file
     * @return
     */
    @PostMapping(value = "order/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> importOrder(@RequestPart("file") MultipartFile file) {
        // Import legacy Customer
        importCSV(orderService, file);
        return ok("");
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Open stream to read XLSX
     *
     * @param file
     * @param action
     */
    private void openStream(MultipartFile file, Consumer<Row> action) {
        // Read xlsx file
        try (ReadableWorkbook workbook = new ReadableWorkbook(file.getInputStream())) {
            // Get first sheet
            Sheet sheet = workbook.getFirstSheet();
            // Read through each row
            sheet.openStream().skip(1).forEach(action);
        } catch (IOException ignored) {
            throw new FileUploadingException();
        }
    }

    /**
     * Common import logic
     *
     * @param service
     * @param file
     * @param <T>
     */
    private <T> void importCSV(BaseImportService<T> service, MultipartFile file) {
        // Create Product list
        List<T> items = service.prep();
        // Open stream to file
        openStream(file, row -> {
            // Convert row to item
            T item = service.convert(row);
            // Check if item is not null
            if (Objects.nonNull(items) && Objects.nonNull(item)) {
                // Add item
                items.add(item);
            }
        });
        System.out.println(items);
        // Import data
        service.runImport(items);
    }
}
