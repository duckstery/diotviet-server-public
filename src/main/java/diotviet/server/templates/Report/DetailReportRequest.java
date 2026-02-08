package diotviet.server.templates.Report;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request for Transaction report
 *
 * @param from
 * @param to
 * @param displayMode
 */
public record DetailReportRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,
        String displayMode
) {
}
