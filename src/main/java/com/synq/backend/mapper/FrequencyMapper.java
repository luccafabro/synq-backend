package com.synq.backend.mapper;

import com.synq.backend.dto.request.CreateFrequencyDto;
import com.synq.backend.dto.request.UpdateFrequencyDto;
import com.synq.backend.dto.response.FrequencyDto;
import com.synq.backend.model.Frequency;
import org.mapstruct.*;

/**
 * MapStruct mapper for Frequency entity
 */
@Mapper(componentModel = "spring")
public interface FrequencyMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "private", target = "isPrivate")
    FrequencyDto toDto(Frequency frequency);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    @Mapping(target = "messages", ignore = true)
    Frequency toEntity(CreateFrequencyDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "memberships", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(source = "isPrivate", target = "private")
    void updateEntity(UpdateFrequencyDto dto, @MappingTarget Frequency frequency);
}

