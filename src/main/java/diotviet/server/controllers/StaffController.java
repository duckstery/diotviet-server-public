package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.entities.Staff;
import diotviet.server.services.GroupService;
import diotviet.server.services.StaffService;
import diotviet.server.services.imports.StaffImportService;
import diotviet.server.templates.EntityHeader;
import diotviet.server.templates.Staff.*;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.views.Staff.StaffSearchView;
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
@RequestMapping(value = "/api/staff", produces = "application/json")
public class StaffController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Staff service
     */
    @Autowired
    private StaffService staffService;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;
    /**
     * Staff import service
     */
    @Autowired
    private StaffImportService importService;
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
    public ResponseEntity<?> index(StaffSearchRequest request) {
        // Get headers
        EntityHeader[] headers = entityUtils.getHeaders(Staff.class);
        // Get list of Staffs (get all data, no need to filter anything)
        Page<StaffSearchView> items = staffService.paginate(request);
        // Get group list for FilterPanel
        List<Group> groups = groupService.getGroups(Type.PARTNER);

        return ok(new StaffInitResponse(headers, items, groups));
    }

    /**
     * Search for Staff that satisfy condition
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(StaffSearchRequest request) {
        // Search for data and response
        return ok(new StaffSearchResponse(staffService.paginate(request)));
    }

    /**
     * Show detail
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(staffService.findById(id));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> store(StaffInteractRequest request) {
        // Store item
        return ok(staffService.store(request));
    }


    /**
     * Partial update item
     *
     * @param request
     * @return
     */
    @PatchMapping(value = "/patch")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<?> patch(@RequestBody StaffPatchRequest request) {
        // Store item
        staffService.patch(request);

        return ok("");
    }

    /**
     * Delete item
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/delete")
    public ResponseEntity<?> delete(@RequestParam("ids") Long[] ids) {
        // Store item
        staffService.delete(ids);

        return ok("");
    }

    /**
     * Import CSV
     *
     * @param file
     * @return
     */
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> importCSV(@RequestPart("file") MultipartFile file) {
        // Parse CSV file
        List<Staff> staffs = parse(file, Staff.class);
        // Prep the importer
        importService.prep();
        // Re-attach (or pull) any relationship
        importService.pull(staffs);
        // Run import
        importService.runImport(staffs);

        return ok("");
    }

    /**
     * Export CSV
     *
     * @return
     */
    @PostMapping(value = "/export")
    public ResponseEntity<?> exportCSV(StaffSearchRequest request) {
        // Export Bean to CSV
        byte[] bytes = export(staffService.export(request));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=staffs.csv")
                .contentLength(bytes.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(bytes));
    }
}
