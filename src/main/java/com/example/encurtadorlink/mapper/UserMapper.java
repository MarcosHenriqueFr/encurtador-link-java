package com.example.encurtadorlink.mapper;

import com.example.encurtadorlink.dto.UserCreateDTO;
import com.example.encurtadorlink.dto.UserResponseDTO;
import com.example.encurtadorlink.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User toEntity (UserCreateDTO dto);
    UserResponseDTO fromEntity (User entity);
}
