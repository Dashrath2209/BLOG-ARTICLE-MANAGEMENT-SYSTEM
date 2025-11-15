package com.jayaa.blog.service;

import com.jayaa.blog.dto.*;
import com.jayaa.blog.exception.*;
import com.jayaa.blog.model.*;
import com.jayaa.blog.repository.*;
import com.jayaa.blog.util.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SlugUtil slugUtil;

    // ⭐ Get all published articles (public)
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getAllPublishedArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findByStatus(ArticleStatus.PUBLISHED, pageable);
        return articles.map(this::convertToResponse);
    }

    // ⭐ Get single article by slug
    @Transactional(readOnly = true)
    public ArticleResponse getArticleBySlug(String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        // Increment view count
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);

        return convertToResponse(article);
    }

    // ⭐ Get articles by category
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getArticlesByCategory(Long categoryId, Pageable pageable) {
        Page<Article> articles = articleRepository.findByCategoryId(categoryId, pageable);
        return articles.map(this::convertToResponse);
    }

    // ⭐ Get articles by tag
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getArticlesByTag(Long tagId, Pageable pageable) {
        Page<Article> articles = articleRepository.findByTagId(tagId, pageable);
        return articles.map(this::convertToResponse);
    }

    // ⭐ Search articles
    @Transactional(readOnly = true)
    public Page<ArticleResponse> searchArticles(String query, Pageable pageable) {
        Page<Article> articles = articleRepository.searchArticles(query, pageable);
        return articles.map(this::convertToResponse);
    }

    // ⭐ Get articles by current author
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getMyArticles(Pageable pageable) {
        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Article> articles = articleRepository.findByAuthorId(user.getId(), pageable);
        return articles.map(this::convertToResponse);
    }

    // ⭐ CREATE ARTICLE (Complex!)
    public ArticleResponse createArticle(ArticleRequest request) {
        String username = getCurrentUsername();
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Create article
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setSlug(generateUniqueSlug(request.getTitle()));
        article.setContent(request.getContent());
        article.setExcerpt(request.getExcerpt());
        article.setAuthor(author); // ⭐ Set relationship
        article.setCategory(category); // ⭐ Set relationship

        // Set status
        if (request.getStatus() != null) {
            article.setStatus(ArticleStatus.valueOf(request.getStatus().toUpperCase()));
        } else {
            article.setStatus(ArticleStatus.DRAFT);
        }

        // ⭐ Handle tags (Many-to-Many relationship!)
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagId));
                tags.add(tag);
            }
            article.setTags(tags);
        }

        Article saved = articleRepository.save(article);
        return convertToResponse(saved);
    }

    // ⭐ UPDATE ARTICLE (with authorization check!)
    public ArticleResponse updateArticle(Long id, ArticleRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        // ⭐ Authorization: Only author or admin can update
        checkArticleOwnership(article);

        // Update fields
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setExcerpt(request.getExcerpt());

        // Update category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        article.setCategory(category);

        // Update status
        if (request.getStatus() != null) {
            article.setStatus(ArticleStatus.valueOf(request.getStatus().toUpperCase()));
        }

        // ⭐ Update tags (Many-to-Many)
        if (request.getTagIds() != null) {
            article.getTags().clear(); // Remove old tags
            Set<Tag> newTags = new HashSet<>();
            for (Long tagId : request.getTagIds()) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + tagId));
                newTags.add(tag);
            }
            article.setTags(newTags);
        }

        Article updated = articleRepository.save(article);
        return convertToResponse(updated);
    }

    // ⭐ DELETE ARTICLE
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        // Authorization check
        checkArticleOwnership(article);

        articleRepository.delete(article);
    }

    // ⭐ UPLOAD FEATURED IMAGE
    public ArticleResponse updateFeaturedImage(Long id, String filename) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        checkArticleOwnership(article);

        article.setFeaturedImage(filename);
        Article updated = articleRepository.save(article);
        return convertToResponse(updated);
    }

    // ========== HELPER METHODS ==========

    // ⭐ Get current authenticated username
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Not authenticated");
        }
        return auth.getName();
    }

    // ⭐ Check if current user owns the article
    private void checkArticleOwnership(Article article) {
        String currentUsername = getCurrentUsername();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!article.getAuthor().getUsername().equals(currentUsername) && !isAdmin) {
            throw new ForbiddenException("You don't have permission to modify this article");
        }
    }

    // ⭐ Generate unique slug
    private String generateUniqueSlug(String title) {
        String baseSlug = slugUtil.generateSlug(title);
        String slug = baseSlug;
        int attempt = 0;

        while (articleRepository.existsBySlug(slug)) {
            attempt++;
            slug = baseSlug + "-" + attempt;
        }

        return slug;
    }

    // ⭐ CONVERT ENTITY TO DTO (Complex!)
    private ArticleResponse convertToResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setSlug(article.getSlug());
        response.setContent(article.getContent());
        response.setExcerpt(article.getExcerpt());
        response.setFeaturedImage(article.getFeaturedImage());
        response.setStatus(article.getStatus());
        response.setViewCount(article.getViewCount());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());

        // ⭐ Convert author to nested DTO
        ArticleResponse.AuthorInfo authorInfo = new ArticleResponse.AuthorInfo();
        authorInfo.setId(article.getAuthor().getId());
        authorInfo.setUsername(article.getAuthor().getUsername());
        authorInfo.setFullName(article.getAuthor().getFullName());
        response.setAuthor(authorInfo);

        // ⭐ Convert category to nested DTO
        if (article.getCategory() != null) {
            ArticleResponse.CategoryInfo categoryInfo = new ArticleResponse.CategoryInfo();
            categoryInfo.setId(article.getCategory().getId());
            categoryInfo.setName(article.getCategory().getName());
            categoryInfo.setSlug(article.getCategory().getSlug());
            response.setCategory(categoryInfo);
        }

        // ⭐ Convert tags to nested DTOs
        if (article.getTags() != null) {
            Set<ArticleResponse.TagInfo> tagInfos = article.getTags().stream()
                    .map(tag -> {
                        ArticleResponse.TagInfo tagInfo = new ArticleResponse.TagInfo();
                        tagInfo.setId(tag.getId());
                        tagInfo.setName(tag.getName());
                        tagInfo.setSlug(tag.getSlug());
                        return tagInfo;
                    })
                    .collect(Collectors.toSet());
            response.setTags(tagInfos);
        }

        // ⭐ Count comments
        response.setCommentCount(article.getComments() != null ? article.getComments().size() : 0);

        return response;
    }
}