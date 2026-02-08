package diotviet.server.entities;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Category but for user to create
 */
@Entity
@Table(name = "documents")
@Data
@Accessors(chain = true)
public class Document implements Lockable {
    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documents_seq")
    @SequenceGenerator(name = "documents_seq", sequenceName = "documents_seq", initialValue = 5, allocationSize = 1)
    @CsvIgnore
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @ToString.Exclude
    private Group group;

    @Column(length = 20)
    private String name;

    @Column(columnDefinition = "varchar")
    private String content;

    @Column
    private Boolean isActive = false;

    @Column(nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvIgnore
    private Long version;


    // ****************************
    // Redundant API
    // ****************************

    @Override
    public String getCode() {
        return null;
    }
    @Override
    public Identifiable setCode(String code) {
        return null;
    }
}
