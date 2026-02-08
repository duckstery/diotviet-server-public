package diotviet.server.templates.Product;

/**
 * Request to patch Product
 *
 * @param ids
 * @param target
 * @param option
 */
public record ProductPatchRequest(
        Long[] ids,
        String target,
        Boolean option
) {
}
