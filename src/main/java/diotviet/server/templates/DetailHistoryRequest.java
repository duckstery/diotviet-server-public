package diotviet.server.templates;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request for Transaction report
 *
 * @param from
 * @param to
 * @param displayMode
 */
public record DetailHistoryRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,
        String displayMode,

        // Pagination
        Integer page,
        Integer itemsPerPage
) {
}
