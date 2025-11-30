package com.synq.backend.mapper;

import com.synq.backend.dto.response.InviteDto;
import com.synq.backend.model.Invite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Invite entity
 */
@Mapper(componentModel = "spring")
public interface InviteMapper {

    @Mapping(source = "frequency.id", target = "frequencyId")
    @Mapping(source = "frequency.name", target = "frequencyName")
    @Mapping(source = "inviter.id", target = "inviterId")
    @Mapping(source = "inviter.username", target = "inviterUsername")
    InviteDto toDto(Invite invite);
}

