package diotviet.server.traits;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Optimistic Lock repository
 *
 * @param <ID>
 */
public interface OptimisticLockRepository {
    /**
     * Check if this version of Entity is exists
     *
     * @param id
     * @param version
     * @return
     */
    boolean existsByIdAndVersion(Long id, Long version);

    /**
     * Check if all tuple of (id, version) are exist
     *
     * @param lockTuples
     * @return
     */
    @Query("SELECT CASE WHEN count(t) = :#{#lockTuples.length} THEN true ELSE false END FROM #{#entityName} t WHERE CONCAT(t.id, '-', t.version) in :lockTuples")
    boolean existsAllByTuplesOfIdAndVersion(@Param("lockTuples") String[] lockTuples);
}
