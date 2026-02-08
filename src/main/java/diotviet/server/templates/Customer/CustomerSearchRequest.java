package diotviet.server.templates.Customer;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request for Customer search
 *
 * @param group
 * @param createAtFrom
 * @param createAtTo
 * @param birthdayFrom
 * @param birthdayTo
 * @param lastTransactionAtFrom
 * @param lastTransactionAtTo
 * @param isMale
 * @param search
 * @param page
 * @param itemsPerPage
 */
public record CustomerSearchRequest(
        Long group,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthdayFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthdayTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate lastTransactionAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate lastTransactionAtTo,
        Boolean isMale,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
