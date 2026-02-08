package diotviet.server.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Ticket;
import diotviet.server.generators.Base64EncodingTextEncryptor;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.repositories.TicketRepository;
import diotviet.server.templates.Ticket.TicketStoreRequest;
import diotviet.server.views.Ticket.TicketViewView;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;

@Service
public class TicketService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Ticket repository
     */
    @Autowired
    private TicketRepository repository;

    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Ticket's secret
     */
    @Value("${diotviet.app.ticket.secret}")
    private String secret;
    /**
     * Encryptor
     */
    private TextEncryptor encryptor;

    // ****************************
    // Public API
    // ****************************

    /**
     * Init encryptor
     */
    @PostConstruct
    public void init() {
        this.encryptor = new Base64EncodingTextEncryptor(Encryptors.stronger(secret, "00"));
    }

    /**
     * Create and store Ticket
     *
     * @param request
     */
    @Transactional
    public String store(TicketStoreRequest request) {
        // Encrypt request
        String ticketValue = encryptor.encrypt(toJson(request));
        // Create a Ticket
        Ticket ticket = (new Ticket()).setValue(ticketValue);
        // Save Ticket to db and return value
        return repository.save(ticket).getValue();
    }

    /**
     * Decrypt Ticket's value and return data
     *
     * @param value
     * @return
     */
    public TicketViewView view(String value) {
        // Decrypt and convert to JsonNode
        JsonNode json = toNode(encryptor.decrypt(value));

        // If JsonNode is invalid, return null
        if (Objects.isNull(json)) return null;
        // Access database to find Customer by code
        Customer customer = null;
        try {
            customer = customerRepository.findFirstByCodeAndIsDeletedFalse(json.get("code").asText());
        } catch (Exception e) {
            // Ignored
            System.out.println("Ticket view");
            e.printStackTrace();
        }

        if (Objects.isNull(customer)) {
            return new TicketViewView(json.get("code").asText(), null, null, false);
        }

        return new TicketViewView(
                json.get("code").asText(),
                customer.getName(),
                customer.getPhoneNumber(),
                repository.existsByValue(value)
        );
    }

    /**
     * Invalidate ticket
     */
    @Transactional
    public void invalidate(String value) {
        repository.deleteByValue(value);
    }

    /**
     * Convert request's data to json string
     *
     * @return
     */
    private String toJson(TicketStoreRequest request) {
        // Generate salt
        String salt = RandomStringUtils.randomAscii(new Random().nextInt(5, 10));
        // Generate timestamp
        Long timestamp = System.currentTimeMillis();

        // Output
        return serialize(request.code(), salt, timestamp);
    }

    /**
     * Convert json string to JsonNode
     *
     * @param string
     * @return
     */
    private JsonNode toNode(String string) {
        try {
            return (new ObjectMapper()).readTree(string);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Serialize Ticket
     *
     * @param code
     * @param salt
     * @param timestamp
     * @return
     */
    private String serialize(String code, String salt, Long timestamp) {
        return new ObjectMapper().createObjectNode()
                .put("code", code)
                .put("s", salt)
                .put("t", timestamp)
                .toString();
    }
}
