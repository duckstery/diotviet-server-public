package diotviet.server.templates.Product;

/**
 * Request for Product search
 *
 * @param categories
 * @param group
 * @param minPrice
 * @param maxPrice
 * @param canBeAccumulated
 * @param isInBusiness
 * @param search
 * @param page
 * @param itemsPerPage
 */
public record ProductSearchRequest(
        Long[] categories,
        Long group,
        Long minPrice,
        Long maxPrice,
        Boolean canBeAccumulated,
        Boolean isInBusiness,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
