package com.example.quizcards.service;

import com.example.quizcards.entities.Tag;

import java.util.List;
import java.util.Set;

public interface ITagService {
    Tag createTag(String name);
    Tag getOrCreateTag(String name);
    Set<Tag> getOrCreateTags(Set<String> tagNames);
    List<Tag> getAllTags();
} 