package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
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
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * User model
 */
@Entity
@Table(name = "customers")
@Data
@Accessors(chain = true)
@QueryEntity
public class Customer implements Identifiable, Lockable, Organizable {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customers_seq")
    @SequenceGenerator(name = "customers_seq", sequenceName = "customers_seq", allocationSize = 1)
    @CsvIgnore
    private long id;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName(column = "customerCode")
    private String code;

    /**
     * Category
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @CsvCustomBindByName(column = "customerCategory", converter = NameableField.class)
    @InitIgnore
    @ToString.Exclude
    private Category category;

    /**
     * Group
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "assoc_groups_customers",
            joinColumns = {@JoinColumn(name = "customer_id")},
            inverseJoinColumns = {@JoinColumn(name = "group_id")}
    )
    @InitIgnore
    @CsvCustomBindByName(column = "customerGroups", converter = NameableSetField.class)
    @ToString.Exclude
    private Set<Group> groups;

    /**
     * Name
     */
    @Column(length = 50)
    @CsvBindByName(column = "customerName")
    private String name;

    /**
     * Phone number
     */
    @Column(length = 15)
    @CsvBindByName(column = "customerPhoneNumber")
    private String phoneNumber;

    /**
     * House address
     */
    @Column(length = 100)
    @InitHide
    @CsvBindByName(column = "customerAddress")
    private String address;

    /**
     * Birthday
     */
    @Temporal(TemporalType.DATE)
    @InitHide
    @CsvBindByName(column = "customerBirthday")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @CsvDate("yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * Gender
     */
    @Column(nullable = false)
    @CsvBindByName(column = "customerGender")
    private boolean isMale;

    /**
     * Email
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "customerEmail")
    private String email;

    /**
     * Facebook profile URL
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "customerFacebook")
    private String facebook;

    /**
     * Description
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "customerDescription")
    private String description;

    /**
     * Point
     */
    @Column
    @InitHide
    @CsvBindByName(column = "customerPoint")
    private Long point;

    /**
     * Image source
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "assoc_image_identifiable",
            joinColumns = @JoinColumn(name = "identifiable_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id"),
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    @WhereJoinTable(clause = "identifiable_type = 'customer'")
    @InitIgnore
    @CsvIgnore
    private List<Image> images;

    /**
     * Name of creator
     */
    @Column(length = 20)
    @InitHide
    @CsvBindByName(column = "customerCreatedBy")
    private String createdBy;

    /**
     * LocalDateTime of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "customerCreatedAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * LocalDateTime of last order
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "customerLastOrderAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastOrderAt;

    /**
     * LocalDateTime of last transaction
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "customerLastTransactionAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastTransactionAt;

    /**
     * Is deleted flag
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvBindByName(column = "customerIsDeleted")
    private Boolean isDeleted = Boolean.FALSE;

    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvIgnore
    private Long version = 0L;
}
