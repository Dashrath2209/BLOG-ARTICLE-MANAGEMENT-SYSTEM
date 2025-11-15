package com.jayaa.blog.dto;

import lombok.Data;

@Data
public class TagResponse {

    private Long id;
    private String name;
    private String slug;
    private Integer articleCount; // How many articles have this tag
}