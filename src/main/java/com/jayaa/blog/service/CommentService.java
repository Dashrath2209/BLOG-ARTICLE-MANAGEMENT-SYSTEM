package com.jayaa.blog.service;

import com.jayaa.blog.dto.CommentRequest;
import com.jayaa.blog.dto.CommentResponse;
import com.jayaa.blog.exception.*;
import com.jayaa.blog.model.*;
import com.jayaa.blog.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByArticle(Long articleId, Pageable pageable) {
        // Verify article exists
        if (!articleRepository.existsById(articleId)) {
            throw new ResourceNotFoundException("Article not found");
        }

        Page<Comment> comments = commentRepository.findByArticleId(articleId, pageable);
        return comments.map(this::convertToResponse);
    }

    public CommentResponse createComment(Long articleId, CommentRequest request) {
        String username = getCurrentUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setArticle(article); // ⭐ Set relationship
        comment.setUser(user); // ⭐ Set relationship

        Comment saved = commentRepository.save(comment);
        return convertToResponse(saved);
    }

    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Check ownership
        checkCommentOwnership(comment);

        comment.setContent(request.getContent());
        Comment updated = commentRepository.save(comment);
        return convertToResponse(updated);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        checkCommentOwnership(comment);

        commentRepository.delete(comment);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Not authenticated");
        }
        return auth.getName();
    }

    private void checkCommentOwnership(Comment comment) {
        String currentUsername = getCurrentUsername();
        if (!comment.getUser().getUsername().equals(currentUsername)) {
            throw new ForbiddenException("You can only modify your own comments");
        }
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        CommentResponse.UserInfo userInfo = new CommentResponse.UserInfo();
        userInfo.setId(comment.getUser().getId());
        userInfo.setUsername(comment.getUser().getUsername());
        userInfo.setFullName(comment.getUser().getFullName());
        response.setUser(userInfo);

        return response;
    }
}
