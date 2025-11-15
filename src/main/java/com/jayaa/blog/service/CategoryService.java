package com.jayaa.blog.service;

import com.jayaa.blog.dto.CategoryRequest;
import com.jayaa.blog.dto.CategoryResponse;
import com.jayaa.blog.exception.BadRequestException;
import com.jayaa.blog.exception.ResourceNotFoundException;
import com.jayaa.blog.model.Category;
import com.jayaa.blog.repository.CategoryRepository;
import com.jayaa.blog.util.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SlugUtil slugUtil;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return convertToResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        // Check if name exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category name already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(slugUtil.generateSlug(request.getName()));
        category.setDescription(request.getDescription());

        Category saved = categoryRepository.save(category);
        return convertToResponse(saved);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setSlug(slugUtil.generateSlug(request.getName()));
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        return convertToResponse(updated);
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    // ⭐ Convert entity to DTO
    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setSlug(category.getSlug());
        response.setDescription(category.getDescription());
        response.setArticleCount(category.getArticles() != null ? category.getArticles().size() : 0);
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}
