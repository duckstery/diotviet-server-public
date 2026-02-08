package diotviet.server.repositories;

import diotviet.server.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    /**
     * Check if Ticket is existing by value
     *
     * @param value
     * @return
     */
    boolean existsByValue(String value);

    /**
     * Delete Ticket by value
     *
     * @param value
     * @return
     */
    void deleteByValue(String value);
}
