package diotviet.server.templates.Document;

/**
 * Document interact request
 *
 * @param id
 * @param name
 * @param content
 */
public record DocumentInteractRequest(
        Long id,
        Long groupId,
        String name,
        String content,
        Long version
) {
}
