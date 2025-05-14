package com.example.quizcards.service;

import com.example.quizcards.dto.RoleBasicDTO;
import java.util.List;

public interface RoleService {
    List<RoleBasicDTO> getAllRoles();
    RoleBasicDTO getRoleById(Long id);
    RoleBasicDTO getRoleByName(String name);
} 