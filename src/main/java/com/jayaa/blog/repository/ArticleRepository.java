package com.jayaa.blog.repository;

import com.jayaa.blog.model.Article;
import com.jayaa.blog.model.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Find by slug
    Optional<Article> findBySlug(String slug);

    // Find by status
    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);

    // Find by author
    Page<Article> findByAuthorId(Long authorId, Pageable pageable);

    // Find by category
    Page<Article> findByCategoryId(Long categoryId, Pageable pageable);

    // ⭐ NEW: Custom search query
    @Query("SELECT a FROM Article a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Article> searchArticles(@Param("query") String query, Pageable pageable);

    // ⭐ NEW: Find by tag
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.id = :tagId")
    Page<Article> findByTagId(@Param("tagId") Long tagId, Pageable pageable);

    // Check if slug exists
    boolean existsBySlug(String slug);
}