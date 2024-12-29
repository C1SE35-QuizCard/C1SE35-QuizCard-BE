package com.example.quizcards.service.impl;

import com.example.quizcards.entities.AppRole;
import com.example.quizcards.repository.IAppRoleRepository;
import com.example.quizcards.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private IAppRoleRepository roleRepo;


    @Override
    public List<AppRole> getAllRoles() {
        return roleRepo.findAll();
    }
}
