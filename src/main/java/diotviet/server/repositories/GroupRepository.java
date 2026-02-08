package diotviet.server.repositories;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    /**
     * Find all groups of a Type
     *
     * @param type
     * @param sort
     * @return
     */
    List<Group> findAllByType(Type type, Sort sort);

    /**
     * Find all by ids
     *
     * @param ids
     * @return
     */
    Set<Group> findAllByIdIn(Long[] ids);

    /**
     * Delete by ID
     *
     * @param id
     */
    @Override
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM diotviet.groups WHERE id = :id", nativeQuery = true)
    void deleteById(@Param("id") Long id);

    /**
     * Find by id and type
     *
     * @param id
     * @param type
     * @return
     */
    Group findByIdAndType(Long id, Type type);

    /**
     * Delete assoc between Group and Product
     *
     * @param id
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM diotviet.assoc_groups_products WHERE group_id = :id", nativeQuery = true)
    void deleteAssocProductById(@Param("id") Long id);

    /**
     * Delete assoc between Group and Customer
     *
     * @param id
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM diotviet.assoc_groups_customers WHERE group_id = :id", nativeQuery = true)
    void deleteAssocCustomerById(@Param("id") Long id);
}
