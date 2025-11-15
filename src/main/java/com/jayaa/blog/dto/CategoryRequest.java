package com.jayaa.blog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100)
    private String name;

    private String description;
}