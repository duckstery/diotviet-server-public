package diotviet.server.repositories;

import diotviet.server.constants.Type;
import diotviet.server.entities.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Find all categories of a Type
     *
     * @param type
     * @param sort
     * @return
     */
    List<Category> findAllByType(Type type, Sort sort);
}
