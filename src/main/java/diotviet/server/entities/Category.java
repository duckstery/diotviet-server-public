package diotviet.server.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import diotviet.server.constants.Type;
import diotviet.server.views.Nameable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * Predefined category
 */
@Entity
@Table(name = "categories")
@Data
@Accessors(chain = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category implements Nameable {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_seq")
    @SequenceGenerator(name = "categories_seq", sequenceName = "categories_seq", allocationSize = 1)
    private long id;

    /**
     * Name
     */
    @Column(length = 20)
    private String name;

    /**
     * Type
     */
    @Enumerated
    @Column(columnDefinition = "smallint")
    @JsonIgnore
    private Type type;

    /**
     * Products
     */
    @OneToMany(mappedBy = "category", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Product> products;
}
