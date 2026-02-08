package diotviet.server.templates.Report;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Request for Product report
 *
 * @param criteria
 * @param sort
 * @param from
 * @param to
 */
public record RankReportRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate to,
        int sort,
        int top
) {
}
