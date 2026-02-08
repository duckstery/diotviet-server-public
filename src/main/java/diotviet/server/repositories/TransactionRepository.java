package diotviet.server.repositories;

import diotviet.server.entities.Order;
import diotviet.server.entities.Transaction;
import diotviet.server.views.Report.IncomeReportView;
import diotviet.server.views.Transaction.TransactionHistoryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {
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
     * Filter id of Orders that have Transaction
     *
     * @param orders
     * @return
     */
    @Query("SELECT t.order.id FROM Transaction t WHERE t.order IN :orders")
    List<Long> filterOrderIdsWithTransaction(@Param("orders") List<Order> orders);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Transaction t SET t.isDeleted = true, t.reason = :reason WHERE t.order.id IN :ids")
    void softDeleteTransactionByOrderIds(@Param("ids") List<Long> ids, @Param("reason") String reason);

    /**
     * Select earned amount
     *
     * @param from
     * @param to
     * @return
     */
    @Query(value = "SELECT\n" +
            "    cast(t.created_at AS date) AS time,\n" +
            "    coalesce(expected_income, 0) as expectedIncome,\n" +
            "    sum(case when cast(t.created_at AS date) = cast(o.created_at AS date) THEN amount ELSE 0 END) AS realIncomeInside,\n" +
            "    sum(case when cast(t.created_at AS date) <> cast(o.created_at AS date) AND amount > 0 THEN amount ELSE 0 END) AS realIncomeOutside,\n" +
            "    sum(case when amount < 0 THEN amount ELSE 0 END) * -1 AS usage\n" +
            "FROM diotviet.transactions t\n" +
            "LEFT JOIN diotviet.orders o\n" +
            "    ON t.order_id = o.id\n" +
            "    AND o.status <> 3\n" +
            "    AND (cast(:from AS date) IS NULL OR (cast(o.created_at AS date) >= cast(:from AS date)))\n" +
            "    AND (cast(:to AS date) IS NULL OR (cast(o.created_at AS date) <= cast(:to AS date)))\n" +
            "LEFT JOIN (\n" +
            "    SELECT\n" +
            "        cast(created_at AS date) AS order_created_at,\n" +
            "        sum(payment_amount) AS expected_income\n" +
            "    FROM diotviet.orders\n" +
            "    WHERE\n" +
            "        status <> 3\n" +
            "        AND (cast(:from AS date) IS NULL OR (cast(created_at AS date) >= cast(:from AS date)))\n" +
            "        AND (cast(:to AS date) IS NULL OR (cast(created_at AS date) <= cast(:to AS date)))\n" +
            "    GROUP BY cast(created_at AS date)\n" +
            ") AS e -- expected_income_by_date\n" +
            "    ON e.order_created_at = cast(t.created_at AS date)\n" +
            "WHERE\n" +
            "    t.is_deleted = false \n" +
            "    AND (cast(:from AS date) IS NULL OR (cast(t.created_at AS date) >= cast(:from AS date)))\n" +
            "    AND (cast(:to AS date) IS NULL OR (cast(t.created_at AS date) <= cast(:to AS date)))\n" +
            "GROUP BY time, expected_income\n" +
            "ORDER BY time"
            , nativeQuery = true)
    List<IncomeReportView> selectIncomeReportByCreatedAt(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Get Transaction history
     *
     * @param from
     * @param to
     * @return
     */
    @EntityGraph("transaction_history")
    Slice<TransactionHistoryView> findAllByCreatedAtBetweenAndIsDeletedIsFalse(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );
}
