package diotviet.server.services.imports;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Group;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.services.CategoryService;
import diotviet.server.services.GroupService;
import diotviet.server.services.UserService;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerImportService extends BaseImportService<Customer> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository customerRepository;
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

    // ****************************
    // Cache
    // ****************************

    /**
     * Cache category
     */
    private Category category;
    /**
     * Cache Group
     */
    private HashMap<String, Group> groupMap;

    // ****************************
    // Public API
    // ****************************

    /**
     * Prepare to import Customer
     *
     * @return
     */
    @Override
    public List<Customer> prep() {
        // Cache category
        category = categoryService.getCategories(Type.PARTNER).stream().findFirst().orElseThrow();
        // Cache group map
        groupMap = new HashMap<>();
        for (Group group : groupService.getGroups(Type.PARTNER)) {
            groupMap.put(group.getName(), group);
        }

        return new ArrayList<>();
    }

    /**
     * Convert legacy to Customer
     *
     * @param row
     * @return
     */
    @Override
    public Customer convert(Row row) {
        // Create output
        Customer customer = new Customer();

        try {
            // Set basic data
            customer.setCategory(category);
            customer.setCode(StringUtils.substring(StringUtils.deleteWhitespace(resolve(row, 2)), 0, 10));
            customer.setName(resolve(row, 3));
            customer.setPhoneNumber(resolvePhoneNumber(row, 4));
            customer.setAddress(resolve(row, 5));
            customer.setBirthday(null);
            customer.setMale(resolveGender(row, 11));
            customer.setEmail(resolve(row, 12));
            customer.setFacebook(resolve(row, 13));
            customer.setDescription(resolve(row, 15));
            customer.setPoint(resolvePoint(row, 17));
            customer.setCreatedAt(resolveDate(row, 19));
            customer.setCreatedBy(UserService.getRequester());

            customer.setGroups(SetUtils.hashSet(groupMap.get(resolve(row, 14))));
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Return
        return customer;
    }

    /**
     * Re-attach any relationship
     *
     * @param customers
     * @return
     */
    @Override
    public void pull(List<Customer> customers) {
        for (Customer customer : customers) {
            // Create new Set
            customer.setGroups(customer
                    .getGroups()
                    .stream()
                    .map(group -> groupMap.getOrDefault(group.getName(), null)) // Iterate through each staled Group, use it to pull from the persisted Group
                    .collect(Collectors.toCollection(HashSet::new)));
        }
    }

    /**
     * Import Customer
     *
     * @param customers
     */
    @Override
    @Transactional
    public void runImport(List<Customer> customers) {
        // Bulk insert
        customerRepository.saveAll(customers);
        // Flush all cache
        this.flush();
    }

    /**
     * Flush cache
     */
    @Override
    public void flush() {
        category = null;

        groupMap.clear();
        groupMap = null;
    }
}
