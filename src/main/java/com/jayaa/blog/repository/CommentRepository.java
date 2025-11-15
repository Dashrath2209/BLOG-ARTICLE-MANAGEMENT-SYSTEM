package com.jayaa.blog.repository;

import com.jayaa.blog.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Find all comments for an article
    Page<Comment> findByArticleId(Long articleId, Pageable pageable);

    // Find all comments by a user
    Page<Comment> findByUserId(Long userId, Pageable pageable);
}