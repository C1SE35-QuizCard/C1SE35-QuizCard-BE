package com.example.quizcards.mapper;


import com.example.quizcards.dto.IAppUserDTO;
import com.example.quizcards.dto.request.AppUserRequest;
import com.example.quizcards.entities.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppUserMapper {
    // Chuyển AppUserRequest sang AppUser, bỏ qua những trường được tính toán hoặc xử lý riêng.
    @Mapping(target = "hashPassword", ignore = true)
    @Mapping(target = "userCode", ignore = true)
    @Mapping(target = "role", ignore = true)
    AppUser toEntity(AppUserRequest request);

    // Cập nhật entity từ request
    @Mapping(target = "hashPassword", ignore = true)
    @Mapping(target = "userCode", ignore = true)
    @Mapping(target = "role", ignore = true)
    void updateEntityFromRequest(AppUserRequest request, @MappingTarget AppUser user);
}
