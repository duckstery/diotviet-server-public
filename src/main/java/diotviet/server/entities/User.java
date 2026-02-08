package diotviet.server.entities;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import diotviet.server.constants.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

/**
 * User model
 */
@Entity
@Table(name = "users")
@NamedEntityGraph(name = "User.validTokens", attributeNodes = @NamedAttributeNode("validTokens"))
@Data
@Accessors(chain = true)
public class User implements UserDetails {

    // ****************************
    // Properties
    // ****************************

    /**
     * Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    private long id;

    /**
     * Name
     */
    @Column(length = 30)
    @CsvBindByName(column = "accountName")
    private String name;
    /**
     * Username
     */
    @Column(length = 50, nullable = false)
    @CsvBindByName
    private String username;

    /**
     * Password
     */
    @Column(length = 100, nullable = false)
    @JsonIgnore
    @CsvBindByName
    private String password;

    /**
     * Role
     */
    @Enumerated
    @Column(columnDefinition = "smallint")
    @CsvBindByName
    private Role role;

    /**
     * Access tokens
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvIgnore
    private Collection<AccessToken> tokens;

    /**
     * Token that is valid (not expired yet)
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Where(clause = "expired_at > CURRENT_TIMESTAMP and is_deleted = false")
    @CsvIgnore
    private Collection<AccessToken> validTokens;

    @Transient
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @CsvIgnore
    private String activeToken;

//    /**
//     * Staff
//     */
//    @OneToOne
//    @MapsId
//    @JsonIgnore
//    @EqualsAndHashCode.Exclude
//    @ToString.Exclude
//    @CsvIgnore
//    private Staff staff;

    // ****************************
    // Public API
    // ****************************

    /**
     * Subscribe token
     *
     * @param token
     * @return
     */
    public AccessToken subscribeToken(DecodedJWT token) {
        // Create AccessToken instance
        AccessToken accessToken = new AccessToken(token);

        // Subscribe
        accessToken.subscribe(this);
        // Create token instance and add it into token list
        tokens.add(accessToken);

        return accessToken;
    }

    // ****************************
    // Overridden API
    // ****************************

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(Arrays.stream(switch (role) {
            case ADMIN -> new Role[]{Role.ADMIN, Role.OWNER, Role.SUPER, Role.STAFF, Role.GUEST};
            case OWNER -> new Role[]{Role.OWNER, Role.SUPER, Role.STAFF};
            case SUPER -> new Role[]{Role.SUPER, Role.STAFF};
            case STAFF -> new Role[]{Role.STAFF};
            default -> new Role[]{Role.GUEST};
        }).map(Role::toString).toArray(String[]::new));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
