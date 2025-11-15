package com.jayaa.blog.controller;

import com.jayaa.blog.dto.CommentRequest;
import com.jayaa.blog.dto.CommentResponse;
import com.jayaa.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@CrossOrigin(origins = "*")
public class CommentController {
    @Autowired
    private CommentService commentService;

    // Get all comments for an article (public)
    @GetMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentsByArticle(
            @PathVariable Long articleId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId, pageable));
    }

    // Add comment to article (authenticated)
    @PostMapping("/api/articles/{articleId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long articleId,
            @Valid @RequestBody CommentRequest request
    ) {
        CommentResponse created = commentService.createComment(articleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Update own comment
    @PutMapping("/api/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.updateComment(id, request));
    }

    // Delete own comment
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
