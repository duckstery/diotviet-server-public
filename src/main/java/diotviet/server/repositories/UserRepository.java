package diotviet.server.repositories;

import diotviet.server.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find User by username
     *
     * @param username
     * @return
     */
    @EntityGraph(attributePaths = {"validTokens"})
    Optional<User> findByUsername(String username);

    /**
     * Check if User is existing by username
     *
     * @param username
     * @return
     */
    Boolean existsByUsername(String username);
}
