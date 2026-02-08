package diotviet.server.entities;

import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@NamedEntityGraph(
        name = "transaction_history",
        attributeNodes = {@NamedAttributeNode(value = "order", subgraph = "order_customer")},
        subgraphs = {@NamedSubgraph(name = "order_customer", attributeNodes = {@NamedAttributeNode("customer")})}
)
@Entity
@Table(name = "transactions")
@Data
@Accessors(chain = true)
@QueryEntity
public class Transaction {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactions_seq")
    @SequenceGenerator(name = "transactions_seq", sequenceName = "transactions_seq", allocationSize = 10)
    private long id;

    /**
     * Transactions
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "order_id")
    @InitIgnore
    private Order order;

    /**
     * Payed amount
     */
    @Column
    private Long amount;

    /**
     * LocalDateTime of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Reason of removal
     */
    @Column
    @InitIgnore
    private String reason;

    /**
     * Is deleted flag
     */
    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    private Boolean isDeleted = Boolean.FALSE;
}
