package diotviet.server.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Category but for user to create
 */
@Entity
@Table(name = "tickets")
@Data
@Accessors(chain = true)
public class Ticket {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tickets_seq")
    @SequenceGenerator(name = "tickets_seq", sequenceName = "tickets_seq", allocationSize = 10)
    private long id;

    /**
     * Name
     */
    @Column(length = 512)
    private String value;
}
