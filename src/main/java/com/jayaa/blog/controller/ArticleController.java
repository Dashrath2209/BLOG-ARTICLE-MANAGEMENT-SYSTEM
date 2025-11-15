package com.jayaa.blog.controller;

import com.jayaa.blog.dto.ArticleRequest;
import com.jayaa.blog.dto.ArticleResponse;
import com.jayaa.blog.service.ArticleService;
import com.jayaa.blog.util.FileStorageUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    // ========== PUBLIC ENDPOINTS (No auth required) ==========

    @GetMapping
    public ResponseEntity<Page<ArticleResponse>> getAllPublishedArticles(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(articleService.getAllPublishedArticles(pageable));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ArticleResponse> getArticleBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getArticleBySlug(slug));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByCategory(
            @PathVariable Long categoryId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(articleService.getArticlesByCategory(categoryId, pageable));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<ArticleResponse>> getArticlesByTag(
            @PathVariable Long tagId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(articleService.getArticlesByTag(tagId, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleResponse>> searchArticles(
            @RequestParam String q,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(articleService.searchArticles(q, pageable));
    }

    // ========== AUTHENTICATED ENDPOINTS ==========

    @GetMapping("/my-articles")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<Page<ArticleResponse>> getMyArticles(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(articleService.getMyArticles(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request) {
        ArticleResponse created = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request
    ) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    // ⭐ FILE UPLOAD ENDPOINT
    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN')")
    public ResponseEntity<ArticleResponse> uploadFeaturedImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {
        // Store file
        String filename = fileStorageUtil.storeFile(file);

        // Update article
        ArticleResponse updated = articleService.updateFeaturedImage(id, filename);

        return ResponseEntity.ok(updated);
    }
}