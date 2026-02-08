package diotviet.server.services;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.constants.PageConstants;
import diotviet.server.data.ProductDAO;
import diotviet.server.entities.Item;
import diotviet.server.entities.Order;
import diotviet.server.entities.Product;
import diotviet.server.repositories.ProductRepository;
import diotviet.server.structures.Dataset;
import diotviet.server.templates.Product.ProductInteractRequest;
import diotviet.server.templates.Product.ProductPatchRequest;
import diotviet.server.templates.Product.ProductSearchRequest;
import diotviet.server.templates.Report.RankReportRequest;
import diotviet.server.utils.OtherUtils;
import diotviet.server.validators.ProductValidator;
import diotviet.server.views.Point;
import diotviet.server.views.Product.ProductDetailView;
import diotviet.server.views.Product.ProductDisplayView;
import diotviet.server.views.Product.ProductSearchView;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ProductService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Product repository
     */
    @Autowired
    private ProductRepository repository;
    /**
     * Product DAO
     */
    @Autowired
    private ProductDAO dao;
    /**
     * Product validator
     */
    @Autowired
    private ProductValidator validator;

    /**
     * Image service
     */
    @Autowired
    private ImageService imageService;


    // ****************************
    // Public API
    // ****************************

    /**
     * Get list (paginate) of Product
     *
     * @param request
     * @return
     */
    public Page<ProductSearchView> paginate(ProductSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);
        // Create pageable
        Pageable pageable = PageRequest.of(
                OtherUtils.get(request.page(), PageConstants.INIT_PAGE),
                OtherUtils.get(request.itemsPerPage(), PageConstants.INIT_ITEMS_PER_PAGE),
                Sort.by("code")
        );

        // Query for Product's data // .project("title") ??????????
        return repository.findBy(filter, q -> q.as(ProductSearchView.class).page(pageable));
    }

    /**
     * Get all displayable Product
     *
     * @return
     */
    public List<ProductDisplayView> display() {
        return repository.findAllByIsInBusinessTrueAndIsDeletedFalseOrderById();
    }

    /**
     * Get Product by id
     *
     * @param id
     * @return
     */
    public ProductDetailView findById(Long id) {
        return validator.isExist(repository.findByIdAndIsDeletedFalse(id, ProductDetailView.class));
    }

    /**
     * Store item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void store(ProductInteractRequest request) {
        // Common validate for create and update, then save it
        Product product = repository.save(validator.validateAndExtract(request));

        // Check if there is file to upload
        if (Objects.nonNull(request.file())) {
            // Save file and bind file to Customer
            imageService.uploadAndSave(product, List.of(request.file()));
        }
    }

    /**
     * Patch item
     *
     * @param request
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void patch(ProductPatchRequest request) {
        if (request.target().equals("business")) {
            repository.updateIsInBusinessByIds(request.option(), request.ids());
        } else if (request.target().equals("accumulating")) {
            repository.updateCanBeAccumulatedByIds(request.option(), request.ids());
        }
    }

    /**
     * Delete multiple item with ids
     *
     * @param ids
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void delete(Long[] ids) {
        // Delete assoc
        repository.deleteGroupAssocById(ids);
        // Delete and get image path (this is physical resource, not database resource)
        repository.softDeleteByIds(ids);
        // Delete Image
        imageService.delete("product", ids);
    }

    /**
     * Get all Product for export
     *
     * @return
     */
    public List<Product> export(ProductSearchRequest request) {
        // Create filter
        BooleanBuilder filter = dao.createFilter(request);

        return repository.findBy(filter, FluentQuery.FetchableFluentQuery::all);
    }

    /**
     * Produce a Product Item
     *
     * @param items
     * @return
     */
    public List<Item> produce(List<Item> items, Order order) {
        // Get Product base on requested Item
        List<Product> products = repository.findByIdInAndIsInBusinessTrueAndIsDeletedFalse(items.stream().map(Item::getId).toList());

        // Iterate through each Item in items
        for (Item item : items) {
            // Find matching Product to produce
            Product match = IterableUtils.find(products, product -> product.getId() == item.getId());
            // If no matching, it means Item is invalid
            if (Objects.isNull(match)) return null;
            // Set original Product for Item
            item.setProduct(match)
                    // Clear item's id since it was Product's id
                    .setId(0)
                    // Set order
                    .setOrder(order);
        }

        return items;
    }

    /**
     * Report
     *
     * @param request
     * @return
     */
    public List<Dataset<String, Long>> report(RankReportRequest request) {
        // Prepare expected_income dataset
        Dataset<String, Long> totalIncome = Dataset.of("total_income", "0", "blue");
        // Prepare real_income_inside dataset
        Dataset<String, Long> orderedQuantity = Dataset.of("ordered_quantity", "1", "yellow");
        // Prepare real_income_outside dataset
        Dataset<String, Long> averageIncome = Dataset.of("average_income", "2", "red");

        // Get report by date
        List<Point<String, Long>> report = repository.selectTopReportByOrderCreatedAt(request.from(), request.to(), request.sort(), request.top());

        // Iterate through each income report's entry
        for (Point<String, Long> entry : report) {
            // Since we union all top [top] in one List, we need to check each Dataset's size is equal [top] before proceed to next Dataset
            if (totalIncome.size() < request.top()) {
                // Check if totalIncome Dataset has enough DataPoint
                totalIncome.add(entry);
            } else if (orderedQuantity.size() < request.top()) {
                // Check if totalIncome Dataset has enough DataPoint
                orderedQuantity.add(entry);
            } else {
                // Add the rest to averageIncome
                averageIncome.add(entry);
            }
        }

        return List.of(totalIncome, orderedQuantity, averageIncome);
    }
}
