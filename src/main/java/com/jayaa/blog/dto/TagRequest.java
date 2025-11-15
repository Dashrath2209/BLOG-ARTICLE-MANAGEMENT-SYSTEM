package com.jayaa.blog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class TagRequest {
    @NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 50)
    private String name;
}