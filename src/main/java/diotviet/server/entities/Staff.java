package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.CsvRecurse;
import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.annotations.InitField;
import diotviet.server.annotations.InitHide;
import diotviet.server.annotations.InitIgnore;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
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

/**
 * User model
 */
@Entity
@Table(name = "staffs")
@Data
@Accessors(chain = true)
@QueryEntity
public class Staff implements Identifiable, Lockable {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "staffs_seq")
    @SequenceGenerator(name = "staffs_seq", sequenceName = "staffs_seq", allocationSize = 1)
    @CsvIgnore
    private long id;

    /**
     * Code
     */
    @Column(length = 10)
    @CsvBindByName(column = "staffCode")
    private String code;

    /**
     * Name
     */
    @Column(length = 50)
    @CsvBindByName(column = "staffName")
    private String name;

    /**
     * User account
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    @CsvRecurse
    @InitField("role")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    /**
     * Phone number
     */
    @Column(length = 15)
    @CsvBindByName(column = "staffPhoneNumber")
    private String phoneNumber;

    /**
     * House address
     */
    @Column(length = 100)
    @InitHide
    @CsvBindByName(column = "staffAddress")
    private String address;

    /**
     * Birthday
     */
    @Temporal(TemporalType.DATE)
    @InitHide
    @CsvBindByName(column = "staffBirthday")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @CsvDate("yyyy-MM-dd")
    private LocalDate birthday;

    /**
     * Gender
     */
    @Column(nullable = false)
    @CsvBindByName(column = "staffIsMale")
    private boolean isMale;

    /**
     * Email
     */
    @Column
    @InitHide
    @CsvBindByName(column = "staffEmail")
    private String email;

    /**
     * Facebook profile URL
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "staffFacebook")
    private String facebook;

    /**
     * Description
     */
    @Column
    @InitIgnore
    @CsvBindByName(column = "staffDescription")
    private String description;

    /**
     * Point
     */
    @Column
    @InitHide
    @CsvBindByName(column = "staffPoint")
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
    @WhereJoinTable(clause = "identifiable_type = 'staff'")
    @InitIgnore
    @CsvIgnore
    private List<Image> images;

    /**
     * Name of creator
     */
    @Column(length = 20)
    @InitHide
    @CsvBindByName(column = "staffCreatedBy")
    private String createdBy;

    /**
     * LocalDateTime of creation
     */
    @Temporal(TemporalType.TIMESTAMP)
    @InitHide
    @CsvDate("yyyy-MM-dd HH:mm:ss")
    @CsvBindByName(column = "staffCreatedAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Is deleted flag
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvBindByName(column = "staffIsDeleted")
    private Boolean isDeleted = Boolean.FALSE;

    /**
     * Is deactivated flag
     */
    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvBindByName(column = "staffIsDeactivated")
    private Boolean isDeactivated = Boolean.FALSE;

    @Column(nullable = false)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @InitIgnore
    @CsvIgnore
    private Long version = 0L;
}
