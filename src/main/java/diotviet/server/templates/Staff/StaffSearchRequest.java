package diotviet.server.templates.Staff;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request for Staff search
 *
 * @param group
 * @param createAtFrom
 * @param createAtTo
 * @param birthdayFrom
 * @param birthdayTo
 * @param isDeactivated
 * @param isMale
 * @param search
 * @param page
 * @param itemsPerPage
 */
public record StaffSearchRequest(
        Long group,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthdayFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthdayTo,
        Boolean isMale,
        Boolean isDeactivated,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
