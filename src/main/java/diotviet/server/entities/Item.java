package diotviet.server.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvRecurse;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Product
 */
@Entity
@Table(name = "items")
@Data
@Accessors(chain = true)
@QueryEntity
public class Item {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "items_seq")
    @SequenceGenerator(name = "items_seq", sequenceName = "items_seq", allocationSize = 10)
    @CsvIgnore
//    @Column(columnDefinition = "bigint default nextval('diotviet.items_seq'::regclass)")
    private long id;

    /**
     * Customer
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @CsvRecurse
    @JoinColumn(name = "product_id", nullable = false)
    @ToString.Exclude
    private Product product;

    /**
     * Order
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @CsvRecurse
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude
    private Order order;

    /**
     * Price before discount
     */
    @Column
    @CsvBindByName(column = "itemOriginalPrice")
    private Long originalPrice;

    /**
     * Discount's amount
     */
    @Column
    @CsvBindByName(column = "itemDiscount")
    private Long discount;

    /**
     * Discount's unit
     */
    @Column
    @CsvBindByName(column = "itemDiscountUnit")
    private String discountUnit;

    /**
     * Price after discount
     */
    @Column(length = 11)
    @CsvBindByName(column = "itemActualPrice")
    private Long actualPrice;

    /**
     * Note
     */
    @Column
    @CsvBindByName(column = "itemNote")
    private String note;

    /**
     * Quantity
     */
    @Column
    @CsvBindByName
    private Integer quantity;
}
