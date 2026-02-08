package diotviet.server.services.imports;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import diotviet.server.entities.Group;
import diotviet.server.entities.Product;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.services.CategoryService;
import diotviet.server.services.GroupService;
import diotviet.server.services.ImageService;
import diotviet.server.structures.URLStreamFile;
import diotviet.server.utils.StorageUtils;
import org.apache.commons.collections4.SetUtils;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductImportService extends BaseImportService<Product> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private ProductRepository productRepository;
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
     * Image service
     */
    @Autowired
    private ImageService imageService;

    // ****************************
    // Cache
    // ****************************

    /**
     * Cache category
     */
    private HashMap<String, Category> categoryMap;
    /**
     * Cache Group
     */
    private HashMap<String, Group> groupMap;
    /**
     * Map between Product and image's URL
     */
    private HashMap<String, String> productToImgURLMap;

    // ****************************
    // Public API
    // ****************************

    /**
     * Prepare to import Product
     *
     * @return
     */
    @Override
    public List<Product> prep() {
        // Init code
        initializeCode("MKH", productRepository::findFirstByCodeLikeOrderByCodeDesc);
        // Cache category
        categoryMap = new HashMap<>();
        for (Category category : categoryService.getCategories(Type.PRODUCT)) {
            categoryMap.put(category.getName(), category);
        }
        // Cache group map
        groupMap = new HashMap<>();
        for (Group group : groupService.getGroups(Type.PRODUCT)) {
            groupMap.put(group.getName(), group);
        }
        // Create image's src map
        productToImgURLMap = new HashMap<>();

        return new ArrayList<>();
    }

    /**
     * Convert legacy to Product
     *
     * @param row
     * @return
     */
    @Override
    public Product convert(Row row) {
        // Create output
        Product product = new Product();

        try {
            // Set basic data
            product.setCode(generateCode());
            product.setTitle(resolve(row, 3));
            product.setOriginalPrice(Long.parseLong(resolve(row, 5).replaceAll(",|\\.\\d*", "")));
            product.setActualPrice(product.getOriginalPrice());
            product.setDiscount(0L);
            product.setDiscountUnit("%");
            product.setDescription("");
            product.setMeasureUnit(resolveValue(row, 12));
            product.setWeight(0);
            product.setCanBeAccumulated(resolve(row, 17).equals("1"));
            product.setIsInBusiness(resolve(row, 18).equals("1"));
            product.setCategory(categoryMap.get(resolve(row, 0)));
            product.setGroups(SetUtils.hashSet(groupMap.get(resolve(row, 1))));
            // Temporary cache Product image's src
            productToImgURLMap.put(product.getCode(), resolve(row, 15));
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        // Return
        return product;
    }

    /**
     * Re-attach any relationship
     *
     * @param e
     * @return
     */
    @Override
    public void pull(List<Product> products) {
        for (Product product : products) {
            // Pull Category
            product.setCategory(categoryMap.getOrDefault(
                    product.getCategory().getName(), // This is the staled Category name, use it to pull from the persisted Category
                    null)
            );

            // Create new Set
            product.setGroups(product
                    .getGroups()
                    .stream()
                    .map(group -> groupMap.getOrDefault(group.getName(), null)) // Iterate through each staled Group, use it to pull from the persisted Group
                    .collect(Collectors.toCollection(HashSet::new)));
        }
    }

    /**
     * Import Product
     *
     * @param products
     */
    @Override
    @Transactional
    public void runImport(List<Product> products) {
        // Iterate through each Product to upload image
        productRepository.saveAll(products).forEach(product -> {
            // Get url from mapper
            String url = productToImgURLMap.get(product.getCode());
            // Create an URLStreamFile
            URLStreamFile file;
            try {
                file = URLStreamFile.of(url);
            } catch (MalformedURLException e) {
                file = null;
            }

            // Save file and bind file to Product
            if (Objects.nonNull(file)) {
                imageService.uploadAndSave(product, List.of(file));
            }
        });
        // Flush all cache
        this.flush();
    }

    /**
     * Flush cache
     */
    @Override
    public void flush() {
        categoryMap.clear();
        categoryMap = null;

        groupMap.clear();
        groupMap = null;

        productToImgURLMap.clear();
        productToImgURLMap = null;
    }
}
