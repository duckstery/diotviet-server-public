package diotviet.server.templates.Ticket;

/**
 * Request for storing ticket
 *
 * @param code
 * @param name
 * @param phoneNumber
 */
public record TicketStoreRequest(
        String code
) {
}
