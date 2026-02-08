package diotviet.server.services.imports;

import diotviet.server.constants.Status;
import diotviet.server.entities.Customer;
import diotviet.server.entities.Item;
import diotviet.server.entities.Order;
import diotviet.server.entities.Product;
import diotviet.server.repositories.CustomerRepository;
import diotviet.server.repositories.OrderRepository;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.services.GroupService;
import diotviet.server.services.TransactionService;
import diotviet.server.services.UserService;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderImportService extends BaseImportService<Order> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Order repository
     */
    @Autowired
    private OrderRepository orderRepository;
    /**
     * Customer repository
     */
    @Autowired
    private CustomerRepository customerRepository;
    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
    /**
     * Transaction service
     */
    @Autowired
    private TransactionService transactionService;

    // ****************************
    // Cache
    // ****************************

    /**
     * Cache Order
     */
    private Order order;
    /**
     * Cached Order's code
     */
    private String code = "";
    /**
     * Cache Customer
     */
    private Customer customer;
    /**
     * Cache Product
     */
    private Map<String, Product> productMap;

    // ****************************
    // Public API
    // ****************************

    /**
     * Prepare to import Order
     *
     * @return
     */
    @Override
    public List<Order> prep() {
        // Init code
        order = null;
        code = "";
        customer = null;

        // Cache product map
        productMap = new HashMap<>();
        for (Product product : productRepository.findAll()) {
            productMap.put(product.getTitle().trim(), product);
        }

        return null;
    }

    /**
     * Convert legacy to Order
     *
     * @param row
     * @return
     */
    @Override
    public Order convert(Row row) {
        try {
            // Check if you should fetch for new Order
            if (!this.code.equals(resolve(row, 1))) {
                // Check if code is not empty
                if (StringUtils.isNotEmpty(this.code)) {
                    // Then, save Order before fetching new Order
                    orderRepository.save(this.order);
                    // Resolve Transaction
                    transactionService.resolve(this.order, null);
                }

                // Cache code
                this.code = resolve(row, 1);
                // Try to fetch Order, if it's not exists, create a new Order. Skip if fail to fetch (or convert)
                if (Objects.isNull(fetchOrConvertToOrder(row))) {
                    this.code = "";
                    return null;
                }
            }

            // Convert row to Item and add to Order
            this.order.getItems().add(convertToItem(row, this.order));
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return this.order;
    }

    /**
     * Re-attach any relationship
     *
     * @param orders
     * @return
     */
    @Override
    public void pull(List<Order> orders) {

    }

    /**
     * Import Order
     *
     * @param orders
     */
    @Override
    @Transactional
    public void runImport(List<Order> orders) {
        // Check if code is not empty
        if (StringUtils.isNotEmpty(this.code)) {
            // Save the last Order
            orderRepository.save(this.order);
            // Resolve Transaction
            transactionService.resolve(order, null);
        }
        // Flush all cache
        this.flush();
    }

    /**
     * Flush cache
     */
    @Override
    public void flush() {
        order = null;
        code = "";
        customer = null;

        productMap.clear();
        productMap = null;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Convert row to new Order
     *
     * @param row
     * @return
     */
    private Order fetchOrConvertToOrder(Row row) throws Exception {
        // Try to fetch Order through database and cache
        this.order = orderRepository.findByCode(this.code).orElse(new Order());
        // Initiate Order's items list
        if (Objects.isNull(this.order.getItems())) {
            this.order.setItems(new ArrayList<>());
        }

        // Check if Order is fetched successfully
        if (this.order.getId() != 0) return this.order;

        // Try to fetch Customer. Return false if fail to fetch
        if (Objects.isNull(fetchCustomer(row))) return null;

        // Set basic data
        this.order.setCustomer(this.customer);
        this.order.setCode(StringUtils.substring(resolve(row, 1), 0, 8));
        this.order.setCreatedAt(resolveDate(row, 6));
        this.order.setResolvedAt(resolveDate(row, 6));
        this.order.setPhoneNumber(resolvePhoneNumber(row, 9));
        this.order.setAddress(resolve(row, 10));
        this.order.setNote(resolve(row, 28));
        this.order.setProvisionalAmount(resolveDecimal(row, 29));
        this.order.setPaymentAmount(resolveDecimal(row, 29));
        this.order.setDiscount(resolveDecimal(row, 30));
        this.order.setDiscountUnit("cash");
        this.order.setStatus(Status.RESOLVED);
        this.order.setPoint(0L);
        this.order.setCreatedBy(UserService.getRequester());
        this.order.setGroups(new HashSet<>());

        // Return
        return this.order;
    }

    /**
     * Convert row to new Item
     *
     * @param row
     * @return
     */
    private Item convertToItem(Row row, Order order) {
        // Create output
        Item item = new Item();

        // Set basic data
        item.setOrder(order);
        item.setProduct(productMap.get(resolve(row, 40).trim()));
        item.setNote(resolve(row, 42));
        item.setQuantity(resolveDecimal(row, 43).intValue());
        item.setOriginalPrice(resolveDecimal(row, 44));
        item.setDiscount(resolveDecimal(row, 46));
        item.setDiscountUnit("cash");
        item.setActualPrice(resolveDecimal(row, 47));

        // Return
        return item;
    }

    /**
     * Fetch Customer
     *
     * @param row
     * @return
     */
    private Customer fetchCustomer(Row row) {
        // Check if cached Customer is usable
        if (Objects.nonNull(this.customer)) {
            // Check if the Customer need to fetch is the cached one
            if (StringUtils.equals(this.customer.getCode(), resolve(row, 7))) {
                return this.customer;
            }
        }

        // Fetch Customer
        return (this.customer = customerRepository.findFirstByCodeAndIsDeletedFalse(resolve(row, 7)));
    }
}
