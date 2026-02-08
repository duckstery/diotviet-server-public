package diotviet.server.entities;

import com.querydsl.core.annotations.QueryEntity;
import diotviet.server.views.Identifiable;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.*;

import java.util.List;

@Entity
@Table(name = "images")
@Data
@Accessors(chain = true)
@QueryEntity
public class Image {
    /**
     * Image's id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq")
    @SequenceGenerator(name = "image_seq", sequenceName = "image_seq", allocationSize = 10)
    private long id;

    /**
     * Image's uid at ImgBB
     */
    @Column(length = 20)
    private String uid;

    /**
     * Image's src
     */
    @Column
    private String src;

    /**
     * Whether this is active image
     */
    @Column(nullable = false)
    private Boolean isActive;

    /**
     * Association with Product, Customer (Polymorph association)
     */
    @ManyToAny(fetch = FetchType.LAZY)
    @AnyKeyJavaClass(Long.class)
    @AnyDiscriminator(DiscriminatorType.STRING)
    @AnyDiscriminatorValue(discriminator = "product", entity = Product.class)
    @AnyDiscriminatorValue(discriminator = "customer", entity = Customer.class)
    @AnyDiscriminatorValue(discriminator = "staff", entity = Staff.class)
    @Column(name = "identifiable_type")
    @Cascade({})
    @JoinTable(
            name = "assoc_image_identifiable",
            joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "identifiable_id")
    )
    private List<Identifiable> owners;
}
