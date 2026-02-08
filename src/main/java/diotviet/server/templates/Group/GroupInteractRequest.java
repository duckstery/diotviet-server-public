package diotviet.server.templates.Group;

/**
 * Group interact request
 *
 * @param id
 * @param name
 * @param type
 */
public record GroupInteractRequest(
        Long id,
        String name,
        Integer type
) {
}
