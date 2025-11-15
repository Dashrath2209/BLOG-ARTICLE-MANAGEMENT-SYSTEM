package com.jayaa.blog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class ArticleRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 50, message = "Content must be at least 50 characters")
    private String content;

    @Size(max = 255, message = "Excerpt too long")
    private String excerpt;

    @NotNull(message = "Category is required")
    private Long categoryId;

    private String status; // DRAFT, PUBLISHED, ARCHIVED

    // Tag IDs to associate with article
    private Set<Long> tagIds;
}