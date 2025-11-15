package com.jayaa.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentResponse {

    private Long id;
    private String content;
    private UserInfo user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String fullName;
    }
}