package com.jayaa.blog.service;

import com.jayaa.blog.dto.TagRequest;
import com.jayaa.blog.dto.TagResponse;
import com.jayaa.blog.exception.BadRequestException;
import com.jayaa.blog.exception.ResourceNotFoundException;
import com.jayaa.blog.model.Tag;
import com.jayaa.blog.repository.TagRepository;
import com.jayaa.blog.util.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SlugUtil slugUtil;

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagResponse getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        return convertToResponse(tag);
    }

    public TagResponse createTag(TagRequest request) {
        if (tagRepository.existsByName(request.getName())) {
            throw new BadRequestException("Tag name already exists");
        }

        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setSlug(slugUtil.generateSlug(request.getName()));

        Tag saved = tagRepository.save(tag);
        return convertToResponse(saved);
    }

    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag not found");
        }
        tagRepository.deleteById(id);
    }

    private TagResponse convertToResponse(Tag tag) {
        TagResponse response = new TagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setSlug(tag.getSlug());
        response.setArticleCount(tag.getArticles() != null ? tag.getArticles().size() : 0);
        return response;
    }
}