package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.services.DocumentService;
import diotviet.server.services.GroupService;
import diotviet.server.structures.FakeJSON;
import diotviet.server.templates.Document.DocumentInitResponse;
import diotviet.server.templates.Document.DocumentInteractRequest;
import diotviet.server.templates.Document.DocumentSelectResponse;
import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.PrintUtils;
import diotviet.server.views.Document.DocumentInitView;
import diotviet.server.views.Print.Order.OrderOrderPrintView;
import diotviet.server.views.Print.Ticket.TicketPrintView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/api/print", produces = "application/json")
public class DocumentController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Document service
     */
    @Autowired
    private DocumentService service;
    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;
    /**
     * Utilities for Entity interact
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
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> index() {
        // Get Group of Type.Print
        List<Group> groups = groupService.getGroups(Type.PRINT);
        // Init Document by group_id
        List<DocumentInitView> document = service.init(groups.get(0).getId());
        // Get PrintableTag
        PrintableTag[] printableFields = printUtils.getPrintableTag(OrderOrderPrintView.class);
        // Get example
        FakeJSON example = printUtils.getExample(OrderOrderPrintView.class);

        return ok(new DocumentInitResponse(groups, document, printableFields, example));
    }

    /**
     * Search by group id
     *
     * @return
     */
    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> searchByGroup(@PathVariable Long groupId) {
        // Init Document by group_id
        List<DocumentInitView> documents = service.init(groupId);
        // Get entityClass
        Class<?> entityClass = switch (documents.get(0).getKey()) {
            case "print_order" -> OrderOrderPrintView.class;
            case "print_ticket" -> TicketPrintView.class;
            default -> OrderOrderPrintView.class;
        };
        // Get PrintableTag
        PrintableTag[] printableFields = printUtils.getPrintableTag(entityClass);
        // Get example
        FakeJSON example = printUtils.getExample(entityClass);

        return ok(new DocumentSelectResponse(documents, printableFields, example));
    }

    /**
     * Find by id
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> show(@PathVariable Long id) {
        return ok(service.findById(id));
    }

    /**
     * Store document
     *
     * @param request
     * @return
     */
    @PostMapping("/store")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> store(@RequestBody DocumentInteractRequest request) {
        return ok(service.store(request));
    }

    /**
     * Delete document
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/delete/{groupId}/{id}")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> delete(@PathVariable("groupId") Long groupId, @PathVariable("id") Long id) {
        // Store item
        service.delete(groupId, id);

        return ok("");
    }
}
