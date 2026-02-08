package diotviet.server.repositories;

import diotviet.server.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, QuerydslPredicateExecutor<Image> {

    /**
     * Find list of Image by identifiable id and type
     *
     * @param type
     * @param identifiableId
     * @return
     */
    @Query(value = "" +
            "SELECT i.id, i.is_active, i.src, i.uid " +
            "FROM diotviet.images i " +
            "INNER JOIN diotviet.assoc_image_identifiable a " +
            "   ON a.identifiable_id = :identifiableId " +
            "   AND a.identifiable_type = :type " +
            "   AND i.id = a.image_id"
            , nativeQuery = true)
    List<Image> findAllByIdentifiableIdAndType(@Param("type") String type, @Param("identifiableId") Long identifiableId);

    /**
     * Find by uid
     *
     * @param uid
     * @return
     */
    Optional<Image> findByUid(String uid);

    /**
     * Find in use Image's id by Image's ids
     *
     * @param imgIds
     * @return
     */
    @Query(value = "SELECT image_id FROM diotviet.assoc_image_identifiable WHERE image_id IN :imgIds GROUP BY image_id HAVING count(*) > 0", nativeQuery = true)
    List<Long> findInUseImageIdByImgIds(@Param("imgIds") Long[] imgIds);

    /**
     * Remove link between Identifiable and Image, then return Image's id
     *
     * @param type
     * @param identifiableIds
     * @return
     */
    @Modifying
    @Query(value = "DELETE FROM diotviet.assoc_image_identifiable WHERE identifiable_type = :type AND identifiable_id in :identifiableIds RETURNING image_id", nativeQuery = true)
    Set<Long> deleteImageAssocByTypeAndIdentifiableId(@Param("type") String type, @Param("identifiableIds") Long[] identifiableIds);

    /**
     * Delete Images by ids and return Image's uIds
     *
     * @param ids
     * @return
     */
    @Modifying
    @Query(value = "DELETE FROM diotviet.images WHERE id in :ids RETURNING uid", nativeQuery = true)
    List<String> deleteByIdsReturningUid(@Param("ids") Long[] ids);
}
