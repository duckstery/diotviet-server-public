package diotviet.server.templates.Staff;

import diotviet.server.constants.Role;
import org.springframework.web.multipart.MultipartFile;

/**
 * Customer interact request
 *
 * @param id
 * @param groups
 * @param code
 * @param name
 * @param address
 * @param phoneNumber
 * @param email
 * @param facebook
 * @param isMale
 * @param src
 * @param description
 * @param file
 */
public record StaffInteractRequest(
        Long id,
        String code,
        String name,
        Integer role,
        String address,
        String phoneNumber,
        String email,
        String facebook,
        Boolean isMale,
        Long imgId,
        String src,
        String description,
        MultipartFile file,
        Long version
) {
}
