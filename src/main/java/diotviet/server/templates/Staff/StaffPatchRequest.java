package diotviet.server.templates.Staff;

/**
 * Request to patch Staff
 *
 * @param ids
 * @param target
 * @param option
 */
public record StaffPatchRequest(
        Long[] ids,
        Long[] versions,
        String target,
        Boolean option
) {
}
