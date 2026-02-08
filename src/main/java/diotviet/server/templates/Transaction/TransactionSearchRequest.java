package diotviet.server.templates.Transaction;

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
public record TransactionSearchRequest(
        Boolean type,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate createAtTo,
        Long priceFrom,
        Long priceTo,

        // Common part but cannot be inherited anymore
        String search,
        Integer page,
        Integer itemsPerPage
) {

}
