package org.mountblue.BlogApplication.Repository;

import org.mountblue.BlogApplication.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
    SELECT DISTINCT p FROM Post p
    LEFT JOIN p.tags t
    WHERE (:authors IS NULL OR p.author IN :authors)
      AND (:tags IS NULL OR t.name IN :tags)
      AND p.publishedAt >= :fromDate
      AND (
            LOWER(p.title) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(p.content) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(p.author) LIKE LOWER(CONCAT('%', :q, '%'))
         OR LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%'))
      )
""")
    Page<Post> filterPosts(String q,List<String> authors, List<String> tags, LocalDateTime fromDate, Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM Post p WHERE p.author IS NOT NULL")
    List<String> findDistinctAuthors();

}
