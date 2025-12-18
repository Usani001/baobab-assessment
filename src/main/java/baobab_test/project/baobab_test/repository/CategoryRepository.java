package baobab_test.project.baobab_test.repository;

import baobab_test.project.baobab_test.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Fetch subtree
    List<Category> findAllByPathStartingWith(String path);

    // Delete subtree
    long deleteByPathStartingWith(String path);

    // Move subtree (bulk update, very fast)
    @Modifying
    @Query("""
        UPDATE Category c
        SET c.path = CONCAT(:newPath, SUBSTRING(c.path, :oldLength + 1))
        WHERE c.path LIKE CONCAT(:oldPath, '%')
    """)
    int updateSubtreePath(
            @Param("oldPath") String oldPath,
            @Param("newPath") String newPath,
            @Param("oldLength") int oldLength
    );
}
