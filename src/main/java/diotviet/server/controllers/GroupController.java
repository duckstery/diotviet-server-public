package diotviet.server.controllers;

import diotviet.server.constants.Type;
import diotviet.server.services.GroupService;
import diotviet.server.templates.Group.GroupInteractRequest;
import diotviet.server.traits.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api/group", produces = "application/json")
public class GroupController extends BaseController {
    // ****************************
    // Properties
    // ****************************

    /**
     * Group service
     */
    @Autowired
    private GroupService groupService;

    // ****************************
    // Public API
    // ****************************

    /**
     * Index page
     *
     * @return
     */
    @GetMapping("/index/{code}")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<?> index(@PathVariable int code) {
        return ok(groupService.getGroups(Type.fromCode(code)));
    }

    /**
     * Store (Create) item
     *
     * @param name
     * @param code
     * @return
     */
    @PostMapping(value = "/store", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> store(GroupInteractRequest groupInteractRequest) {
        // Store item
        groupService.store(groupInteractRequest);

        return ok("");
    }

    /**
     * Delete item
     *
     * @param ids
     * @return
     */
    @DeleteMapping(value = "/delete")
    @PreAuthorize("hasAuthority('SUPER')")
    public ResponseEntity<?> delete(@RequestParam("id") Long id, @RequestParam("type") Integer type) {
        // Store item
        groupService.delete(id, Type.fromCode(type));

        return ok("");
    }
}
