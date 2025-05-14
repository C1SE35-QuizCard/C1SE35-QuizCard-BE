package com.example.quizcards.service.impl;

import com.example.quizcards.dto.RoleBasicDTO;
import com.example.quizcards.entities.AppRole;
import com.example.quizcards.repository.RoleRepository;
import com.example.quizcards.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<RoleBasicDTO> getAllRoles() {
        return roleRepository.findAllBasicRoles().stream()
            .map(row -> new RoleBasicDTO(
                ((Number) row[0]).longValue(), // roleId
                (String) row[1]                // roleName
            ))
            .collect(Collectors.toList());
    }

    @Override
    public RoleBasicDTO getRoleById(Long id) {
        return roleRepository.findById(id)
            .map(role -> new RoleBasicDTO(role.getRoleId(), role.getRoleName()))
            .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Override
    public RoleBasicDTO getRoleByName(String name) {
        return roleRepository.findByName(name)
            .map(role -> new RoleBasicDTO(role.getRoleId(), role.getRoleName()))
            .orElseThrow(() -> new RuntimeException("Role not found with name: " + name));
    }
} 