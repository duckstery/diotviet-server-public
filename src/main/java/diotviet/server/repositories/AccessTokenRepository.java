package diotviet.server.repositories;

import diotviet.server.entities.AccessToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    /**
     * Soft delete token by token
     *
     * @param token
     */
    @Transactional
    void deleteAccessTokenByToken(String token);
}
