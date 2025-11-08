package com.example.encurtadorlink.mapper;

import com.example.encurtadorlink.dto.LinkCreateDTO;
import com.example.encurtadorlink.dto.LinkResponseDTO;
import com.example.encurtadorlink.model.Link;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { UserMapper.class })
public interface LinkMapper {
    Link toEntity(LinkCreateDTO dto);

    LinkResponseDTO fromEntity(Link entity);
}
