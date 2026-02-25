package com.example.encurtadorlink.mapper;

import com.example.encurtadorlink.dto.AccessContextDTO;
import com.example.encurtadorlink.dto.LogResponseDTO;
import com.example.encurtadorlink.model.LogAccess;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LogAccessMapper {
    LogAccess toEntity(AccessContextDTO dto);
    LogResponseDTO fromEntity(LogAccess entity);
}
