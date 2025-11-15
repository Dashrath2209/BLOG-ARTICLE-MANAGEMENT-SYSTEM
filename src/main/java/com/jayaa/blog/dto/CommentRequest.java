package com.jayaa.blog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class CommentRequest {
    @NotBlank(message = "Comment content is required")
    @Size(min = 5, max = 1000, message = "Comment must be between 5 and 1000 characters")
    private String content;
}