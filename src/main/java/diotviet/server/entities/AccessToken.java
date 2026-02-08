package diotviet.server.entities;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

@Entity
@Table(name = "access_tokens")
@Getter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE diotviet.access_tokens SET is_deleted = true WHERE id=?")
//@SQLDeleteAll(sql = "UPDATE access_tokens SET is_deleted = true")
//@FilterDef(name = "softDeleteAccessToken", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
//@Filter(name = "softDeleteAccessToken", condition = "is_deleted = :isDeleted")
public class AccessToken {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_tokens_seq")
    @SequenceGenerator(name = "access_tokens_seq", sequenceName = "access_tokens_seq", allocationSize = 1)
    @JsonIgnore
    private long id;
    /**
     * Value
     */
    @Column(length = 300, nullable = false)
    private String token;
    /**
     * Issue at
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private LocalDateTime issuedAt;
    /**
     * Expire at
     */
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private LocalDateTime expiredAt;
    /**
     * Corresponding User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @Column
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private boolean isDeleted = Boolean.FALSE;

    // ****************************
    // Constructors
    // ****************************

    /**
     * Constructors
     *
     * @param jwt
     */
    public AccessToken(DecodedJWT jwt) {
        this.token = jwt.getToken();
        this.issuedAt = LocalDateTime.ofInstant(jwt.getIssuedAtAsInstant(), ZoneId.systemDefault());
        this.expiredAt = LocalDateTime.ofInstant(jwt.getExpiresAtAsInstant(), ZoneId.systemDefault());
    }

    /**
     * Subscribe User
     */
    public void subscribe(User user) {
        if (Objects.isNull(this.user)) {
            this.user = user;
        }
    }

    /**
     * Check if this token's value match token
     *
     * @param token
     * @return
     */
    public boolean match(String token) {
        return this.token.equals(token);
    }
}
