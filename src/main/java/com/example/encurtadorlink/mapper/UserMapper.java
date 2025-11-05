package com.example.encurtadorlink.mapper;

import com.example.encurtadorlink.dto.UserSummaryDTO;
import com.example.encurtadorlink.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserSummaryDTO toSummaryDTO(User user);
}
