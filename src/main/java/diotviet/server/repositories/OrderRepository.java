package diotviet.server.repositories;

import com.querydsl.core.types.Predicate;
import diotviet.server.constants.Status;
import diotviet.server.entities.Order;
import diotviet.server.traits.OptimisticLockRepository;
import diotviet.server.views.Report.OrderReportView;
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
import java.util.Optional;
import java.util.function.Function;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslPredicateExecutor<Order>, OptimisticLockRepository {
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
    @EntityGraph(attributePaths = {"groups", "customer", "transactions"})
    <S extends Order, R> R findBy(Predicate predicate, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction);

    /**
     * Find by id
     *
     * @param id
     * @param classType
     * @param <T>
     * @return
     */
    @EntityGraph("order_detail")
    <T> T findById(Long id, Class<T> classType);

    /**
     * Find code by id
     *
     * @param id
     * @return
     */
    @Query("SELECT o.code FROM Order o WHERE o.id = :id")
    String findCodeById(@Param("id") Long id);

    /**
     * Find first Order where code like "?" Order by code desc
     *
     * @param code
     * @return
     */
    Order findFirstByCodeLikeOrderByCodeDesc(String code);


    /**
     * Find by ids and exclude that equal status
     *
     * @param ids
     * @param status
     * @return
     */
    @EntityGraph(attributePaths = {"transactions"})
    List<Order> findByIdInAndStatusNot(List<Long> ids, Status status);

    /**
     * Find Order by code
     *
     * @param code
     * @return
     */
    @EntityGraph("order_detail")
    Optional<Order> findByCode(String code);

    /**
     * Update Order's status
     *
     * @param status
     * @param ids
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Order o set o.status = :status, o.version = o.version + 1 WHERE o.id IN :ids")
    void updateStatusByIds(@Param("status") Status status, @Param("ids") Long[] ids);

    /**
     * Select earned amount
     *
     * @param from
     * @param to
     * @return
     */
    @Query(value = "" +
            "SELECT\n" +
            "    cast(created_at AS date) as time,\n" +
            "    count(*) AS createdOrderAmount,\n" +
            "    count(*) FILTER (WHERE status = 0 OR status = 1) AS processingOrderAmount,\n" +
            "    count(*) FILTER (WHERE status = 2) AS resolvedOrderAmount,\n" +
            "    count(*) FILTER (WHERE status = 3) AS abortedOrderAmount\n" +
            "FROM diotviet.orders o\n" +
            "WHERE\n" +
            "    (cast(:from AS date) IS NULL OR (cast(o.created_at AS date) >= cast(:from AS date)))\n" +
            "    AND (cast(:to AS date) IS NULL OR (cast(o.created_at AS date) <= cast(:to AS date)))\n" +
            "GROUP BY time\n" +
            "ORDER BY time"
            , nativeQuery = true)
    List<OrderReportView> selectOrderReportByCreatedAt(@Param("from") LocalDate from, @Param("to") LocalDate to);

    //
//    @Override
//    @EntityGraph(attributePaths = {"category", "groups"})
//    List<Order> findAll();
//
//    /**
//     * Get first by code
//     *
//     * @param code
//     * @return
//     */
//    Order findFirstByCodeAndIsDeletedFalse(String code);
//
//
//    /**
//     * Delete assoc between Group and Order
//     *
//     * @param ids
//     */
//    @Modifying(clearAutomatically = true)
//    @Query(value = "DELETE FROM diotviet.assoc_groups_orders WHERE order_id in :ids", nativeQuery = true)
//    void deleteGroupAssocById(@Param("ids") Long[] ids);
}
