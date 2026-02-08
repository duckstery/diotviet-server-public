package diotviet.server.templates.Ticket;

import diotviet.server.templates.Document.PrintableTag;

public record TicketInitResponse(
        String template,
        PrintableTag[] tags
) {
}
