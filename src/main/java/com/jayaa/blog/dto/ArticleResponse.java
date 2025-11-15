package com.jayaa.blog.dto;

import com.jayaa.blog.model.ArticleStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ArticleResponse {

    private Long id;
    private String title;
    private String slug;
    private String content;
    private String excerpt;
    private String featuredImage;
    private ArticleStatus status;
    private Integer viewCount;

    // ⭐ Author info (not full User object!)
    private AuthorInfo author;

    // ⭐ Category info
    private CategoryInfo category;

    // ⭐ Tags
    private Set<TagInfo> tags;

    // ⭐ Comment count
    private Integer commentCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested DTOs for related entities
    @Data
    public static class AuthorInfo {
        private Long id;
        private String username;
        private String fullName;
    }

    @Data
    public static class CategoryInfo {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    public static class TagInfo {
        private Long id;
        private String name;
        private String slug;
    }
}