package com.synq.backend.mapper;

import com.synq.backend.dto.response.MembershipDto;
import com.synq.backend.model.Membership;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Membership entity
 */
@Mapper(componentModel = "spring")
public interface MembershipMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "frequency.id", target = "frequencyId")
    @Mapping(source = "frequency.name", target = "frequencyName")
    @Mapping(source = "banned", target = "isBanned")
    MembershipDto toDto(Membership membership);
}

