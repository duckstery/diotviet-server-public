package diotviet.server.controllers;

import diotviet.server.services.DocumentService;
import diotviet.server.services.TicketService;
import diotviet.server.templates.Document.PrintableTag;
import diotviet.server.templates.Ticket.TicketInitResponse;
import diotviet.server.templates.Ticket.TicketStoreRequest;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.PrintUtils;
import diotviet.server.views.Print.Ticket.TicketPrintView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/ticket", produces = "application/json")
public class TicketController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Ticket service
     */
    @Autowired
    private TicketService service;


    /**
     * Document service
     */
    @Autowired
    private DocumentService documentService;
    /**
     * Utilities for Entity interact
     */
    @Autowired
    private PrintUtils printUtils;

    // ****************************
    // Public API
    // ****************************

    @GetMapping("/init")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> init() {
        // Get active Document content
        String content = documentService.getActiveDocumentOfGroup("print_ticket").getContent();
        // Get PrintableTag.
        PrintableTag[] tags = printUtils.getPrintableTag(TicketPrintView.class);

        // Return data
        return ok(new TicketInitResponse(content, tags));
    }

    /**
     * Store (Create) item
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/store")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> store(@RequestBody TicketStoreRequest request) {
        // Store and return Ticket
        return ok(service.store(request));
    }

    /**
     * View ticket
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/view/{code}")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> view(@PathVariable String code) {
        // Store and return Ticket
        return ok(service.view(code));
    }

    /**
     * View ticket
     *
     * @param code
     * @return
     */
    @GetMapping(value = "/invalidate/{code}")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> invalidate(@PathVariable String code) {
        // Invalidate
        service.invalidate(code);

        // Store and return Ticket
        return ok("");
    }
}
