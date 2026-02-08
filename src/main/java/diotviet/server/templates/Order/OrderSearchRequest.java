package diotviet.server.templates.Order;

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
public record OrderSearchRequest(
        Long group,
        Long[] status,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtTo,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate resolvedAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate resolvedAtTo,
        Long priceFrom,
        Long priceTo,
        Boolean isMale,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
