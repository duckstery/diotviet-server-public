package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.*;
import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.constants.Status;
import diotviet.server.generators.NameableField;
import diotviet.server.generators.NameableSetField;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
import diotviet.server.views.Organizable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * User model
 */
@NamedEntityGraph(
        name = "order_detail",
        attributeNodes = {
                @NamedAttributeNode("groups"),
                @NamedAttributeNode("customer"),
                @NamedAttributeNode(value = "items", subgraph = "item_product")
        },
        subgraphs = {@NamedSubgraph(name = "item_product", attributeNodes = {@NamedAttributeNode("product")})}
)
@Entity
@Table(name = "orders")
@Data
@Accessors(chain = true)
@QueryEntity
public class Order implements Identifiable, Lockable, Organizable {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_seq", allocationSize = 10)
    @CsvIgnore
    private long id;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName(column = "orderCode")
    private String code;

    /**
     * Group
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "assoc_groups_orders",
            joinColumns = {@JoinColumn(name = "order_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    @CsvCustomBindByName(column = "orderGroups", converter = NameableSetField.class)
    @InitIgnore
    @ToString.Exclude
    private Set<Group> groups;

    /**
     * Customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @CsvRecurse
    @ToString.Exclude
    private Customer customer;

    /**
     * Items
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "order")
    @CsvCustomBindByName(converter = NameableSetField.class)
    @InitIgnore
    @ToString.Exclude
    private List<Item> items;

    /**
     * Transactions
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "order")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvIgnore
    @InitIgnore
    private List<Transaction> transactions;

    /**
     * Phone number
     */
    @Column(length = 15)
    @CsvBindByName(column = "orderPhoneNumber")
    private String phoneNumber;

    /**
     * House address
     */
    @Column(length = 100)
    @InitHide
    @CsvBindByName(column = "orderAddress")
    private String address;

    /**
     * Price before discount
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "orderProvisionalAmount")
    private Long provisionalAmount;

    /**
     * Discount's amount
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "orderDiscount")
    private Long discount;

    /**
     * Discount's unit
     */
    @Column(length = 4)
    @InitIgnore
    @CsvBindByName(column = "orderDiscountUnit")
    private String discountUnit;

    /**
     * Price after discount
     */
    @Column
    @CsvBindByName(column = "orderPaymentAmount")
    private Long paymentAmount;

    /**
     * Status
     */
    @Enumerated
    @CsvBindByName(column = "orderStatus")
    @Column(columnDefinition = "smallint")
    private Status status = Status.PENDING;

    /**
     * Point
     */
    @Column
    @InitHide
    @CsvBindByName(column = "orderPoint")
    private Long point;

    /**
     * Description
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "orderNote")
    private String note;

    /**
     * Name of creator
     */
    @Column(length = 20)
    @InitHide
    @CsvBindByName(column = "orderCreatedBy")
    private String createdBy;

    /**
     * LocalDateTime of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "orderCreatedAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * LocalDateTime of resolve
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "orderResolvedAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime resolvedAt;

    /**
     * Version
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvIgnore
    private Long version = 0L;
}
