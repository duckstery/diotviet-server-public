package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.constants.Role;
import diotviet.server.entities.Staff;
import diotviet.server.traits.OptimisticLockRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Function;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long>, QuerydslPredicateExecutor<Staff>, OptimisticLockRepository {
    /**
     * Find by multiple condition
     *
     * @param predicate
     * @param queryFunction
     * @param <S>
     * @param <R>
     * @return
     */
    @Override
    @EntityGraph(attributePaths = {"user"})
    <S extends Staff, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"user", "images"})
    <T> T findById(Long id, Class<T> classType);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"user", "images"})
    <T> T findByIdAndIsDeletedFalse(Long id, Class<T> classType);

    /**
     * Find all Staff with User
     *
     * @return
     */
    @Override
    @EntityGraph(attributePaths = {"user"})
    List<Staff> findAll();

    /**
     * Find all Staff with User by id
     *
     * @param longs
     * @return
     */
    @EntityGraph(attributePaths = {"user"})
    int countByIdInAndUserRoleGreaterThan(List<Long> ids, Role role);

    /**
     * Get first by code
     *
     * @param code
     * @return
     */
    Staff findFirstByCodeAndIsDeletedFalse(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Staff findFirstByCodeLikeOrderByCodeDesc(String code);

    /**
     * Check if Staff exists by phoneNumber
     *
     * @param phoneNumber
     * @return
     */
    Boolean existsByPhoneNumberAndIdNotAndIsDeletedIsFalse(String phoneNumber, Long id);

    /**
     * Delete Staff by ID (this operation won't delete assoc, need to delete assoc first)
     *
     * @param ids
     */
    @Modifying
    @Query(value = "UPDATE Staff c SET c.isDeleted = true, c.version = c.version + 1 WHERE c.id in :ids AND c.isDeleted = false")
    void softDeleteByIds(@Param("ids") Long[] ids);
}
