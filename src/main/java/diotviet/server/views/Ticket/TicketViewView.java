package diotviet.server.views.Ticket;

public record TicketViewView(
        String code,
        String name,
        String phoneNumber,
        boolean isValid
) {
}
