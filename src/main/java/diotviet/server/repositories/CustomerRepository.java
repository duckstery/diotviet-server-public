package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.entities.Customer;
import diotviet.server.traits.OptimisticLockRepository;
import diotviet.server.views.Point;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, QuerydslPredicateExecutor<Customer>, OptimisticLockRepository {
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
    @EntityGraph(attributePaths = {"category", "groups"})
    <S extends Customer, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"category", "groups", "images"})
    <T> T findById(Long id, Class<T> classType);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph(attributePaths = {"groups", "images"})
    <T> T findByIdAndIsDeletedFalse(Long id, Class<T> classType);

    @Override
    @EntityGraph(attributePaths = {"category", "groups"})
    List<Customer> findAll();

    /**
     * Get first by code
     *
     * @param code
     * @return
     */
    Customer findFirstByCodeAndIsDeletedFalse(String code);

    /**
     * Find first Product where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Customer findFirstByCodeLikeOrderByCodeDesc(String code);

    /**
     * Find with Group by id
     *
     * @return
     */
    @EntityGraph(attributePaths = {"groups"})
    Customer findWithGroupById(Long id);

    /**
     * Select top Product that has (highest or lowest base on [asc]) income
     *
     * @param from
     * @param to
     * @param asc
     * @return
     */
    @Query(value = "" +
            "WITH customer_report as (\n" +
            "    SELECT\n" +
            "        name,\n" +
            "        coalesce(total_income, 0) as total_income,\n" +
            "        coalesce(ordered_quantity, 0) as ordered_quantity,\n" +
            "        coalesce(round(total_income / ordered_quantity), 0) as average_income\n" +
            "    FROM diotviet.customers c\n" +
            "    LEFT JOIN (\n" +
            "        SELECT\n" +
            "            customer_id,\n" +
            "            sum(amount) as total_income,\n" +
            "            count(o.id) as ordered_quantity\n" +
            "        FROM diotviet.transactions t\n" +
            "        INNER JOIN diotviet.orders o\n" +
            "            ON o.id = t.order_id\n" +
            "            AND (cast(:from AS date) IS NULL OR (cast(o.created_at AS date) >= cast(:from AS date)))\n" +
            "            AND (cast(:to AS date) IS NULL OR (cast(o.created_at AS date) <= cast(:to AS date)))\n" +
            "        GROUP BY o.customer_id\n" +
            "    ) as customer_income\n" +
            "        ON c.id = customer_income.customer_id\n" +
            ")\n" +
            "(SELECT name as x, total_income as y FROM customer_report ORDER BY (total_income * :sortOrder) LIMIT :limit)\n" +
            "UNION ALL\n" +
            "(SELECT name as x, ordered_quantity as y FROM customer_report ORDER BY (ordered_quantity * :sortOrder) LIMIT :limit)\n" +
            "UNION ALL\n" +
            "(SELECT name as x, average_income as y FROM customer_report ORDER BY (average_income * :sortOrder) LIMIT :limit)"
            , nativeQuery = true)
    List<Point<String, Long>> selectTopReportByOrderCreatedAt(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("sortOrder") int sortOrder,
            @Param("limit") int limit
    );

    /**
     * Delete assoc between Group and Customer
     *
     * @param ids
     */
    @Modifying
    @Query(value = "DELETE FROM diotviet.assoc_groups_customers WHERE customer_id in :ids", nativeQuery = true)
    void deleteGroupAssocById(@Param("ids") Long[] ids);

    /**
     * Delete Customer by ID (this operation won't delete assoc, need to delete assoc first)
     *
     * @param ids
     */
    @Modifying
    @Query(value = "UPDATE Customer c SET c.isDeleted = true, c.version = c.version + 1 WHERE c.id in :ids AND c.isDeleted = false")
    void softDeleteByIds(@Param("ids") Long[] ids);
}
