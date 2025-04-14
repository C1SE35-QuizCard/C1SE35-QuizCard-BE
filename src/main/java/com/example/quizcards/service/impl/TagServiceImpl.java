package com.example.quizcards.service.impl;

import com.example.quizcards.entities.Tag;
import com.example.quizcards.repository.ITagRepository;
import com.example.quizcards.service.ITagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements ITagService {
    private final ITagRepository tagRepository;

    @Override
    public Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Override
    public Tag getOrCreateTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> createTag(name));
    }

    @Override
    public Set<Tag> getOrCreateTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            tags.add(getOrCreateTag(name));
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
} 