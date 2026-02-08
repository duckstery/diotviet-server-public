package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.services.CategoryService;
import diotviet.server.services.DocumentService;
import diotviet.server.services.GroupService;
import diotviet.server.services.ProductService;
import diotviet.server.services.imports.ProductImportService;
import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Product.*;
import diotviet.server.templates.Report.RankReportRequest;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.utils.PrintUtils;
import diotviet.server.views.Print.Order.OrderOrderPrintView;
import diotviet.server.views.Product.ProductDisplayView;
import diotviet.server.views.Product.ProductSearchView;
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
@RequestMapping(value = "/api/product", produces = "application/json")
public class ProductController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Product service
     */
    @Autowired
    private ProductService productService;
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
     * Document Service
     */
    @Autowired
    private DocumentService documentService;
    /**
     * Product import service
     */
    @Autowired
    private ProductImportService importService;
    /**
     * Utilities for Entity interact
     */
    @Autowired
    private EntityUtils entityUtils;
    /**
     * Utilities for print
     */
    @Autowired
    private PrintUtils printUtils;

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
    public ResponseEntity<?> index(ProductSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Product.class);
        // Get list of Products (get all data, no need to filter anything)
        Page<ProductSearchView> items = productService.paginate(request);

        List<Category> categories = categoryService.getCategories(Type.PRODUCT);

        // Get group list for FilterPanel
        List<Group> groups = groupService.getGroups(Type.PRODUCT);

        return ok(new ProductInitResponse(headers, items, categories, groups));
    }

    /**
     * Display items for page
     *
     * @return
     */
    @GetMapping("/display")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> display() {
        // Get Product
        List<ProductDisplayView> products = productService.display();
        // Get active Document content
        String content = documentService.getActiveDocumentOfGroup("print_order").getContent();
        // Get PrintableTag
        PrintableTag[] tags = printUtils.getPrintableTag(OrderOrderPrintView.class);

        // Return data
        return ok(new ProductDisplayResponse(products, content, tags));
    }

    /**
     * Search for Product that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> search(ProductSearchRequest request) {
        // Search for data and response
        return ok(new ProductSearchResponse(productService.paginate(request)));
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
        return ok(productService.findById(id));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> store(ProductInteractRequest request) {
        // Store item
        productService.store(request);

        return ok("");
    }

    /**
     * Partial update item
     *
     * @param request
     * @return
     */
    @PatchMapping(value = "/patch")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> patch(@RequestBody ProductPatchRequest request) {
        // Store item
        productService.patch(request);

        return ok("");
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
        productService.delete(ids);

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
        List<Product> products = parse(file, Product.class);
        // Prep the importer
        importService.prep();
        // Re-attach (or pull) any relationship
        importService.pull(products);
        // Run import
        importService.runImport(products);

        return ok("");
    }

    @PostMapping(value = "/export")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> exportCSV(ProductSearchRequest request) {
        // Export Bean to CSV
        byte[] bytes = export(productService.export(request));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=products.csv")
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(bytes));
    }

    /**
     * Report Product
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/report")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> report(RankReportRequest request) {
        return ok(productService.report(request));
    }
}
