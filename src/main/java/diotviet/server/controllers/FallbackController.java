package diotviet.server.controllers;

import diotviet.server.constants.Role;
import diotviet.server.entities.Customer;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.repositories.TransactionRepository;
import diotviet.server.templates.GeneralResponse;
import diotviet.server.traits.BaseController;
import diotviet.server.utils.EntityUtils;
import diotviet.server.utils.StorageUtils;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(value="/api/fallback", produces="application/json")
public class FallbackController extends BaseController {

    @Autowired
    private EntityUtils entityUtils;

    @Autowired
    private StorageUtils storageUtils;

    @Autowired
    private TransactionRepository repository;

    @GetMapping("")
    public String index() {
//        StorageUtils.delete("3NKgDjW");
        return "Greetings from Spring Boot!";
    }

    @PostMapping ("/test")
    public String index(@RequestPart("file") MultipartFile file) throws IOException {
//        System.out.println(file.getName());
//        String fileId = storageUtils.upload(file).get("data").get("id").asText();
//        System.out.println(fileId);
        storageUtils.delete(List.of("6wwQKmc", "TTq6VcH"));
        return "Greetings from Spring Boot!";
    }

    @GetMapping("/ping")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<GeneralResponse> ping(@RequestParam("from") LocalDate from, @RequestParam("to") LocalDate to) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        return ok("hello");
    }

    /**
     * Health check
     *
     * @return
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("");
    }
}
