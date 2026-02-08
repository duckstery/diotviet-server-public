package diotviet.server.repositories;

import diotviet.server.entities.Document;
import diotviet.server.traits.OptimisticLockRepository;
import diotviet.server.views.Document.DocumentInitView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, OptimisticLockRepository {
    /**
     * Find all Document by GroupId and orderBy id
     *
     * @param groupId
     * @return
     */
    List<DocumentInitView> findAllByGroupIdOrderById(Long groupId);

    /**
     * Find by id
     *
     * @param id
     * @return
     */
    <T> T findById(Long id, Class<T> type);

    /**
     * Count all Document of Group except for specify id
     *
     * @param id
     * @param groupId
     * @return
     */
    long countByGroupIdAndIdNot(Long groupId, Long id);

    /**
     * Find active Document by group name
     *
     * @param groupName
     * @return
     */
    Document findByIsActiveTrueAndGroupName(String groupName);
}
