package com.synq.backend.mapper;

import com.synq.backend.dto.request.CreateMessageDto;
import com.synq.backend.dto.request.UpdateMessageDto;
import com.synq.backend.dto.response.MessageDto;
import com.synq.backend.model.Message;
import org.mapstruct.*;

/**
 * MapStruct mapper for Message entity
 */
@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "frequency.id", target = "frequencyId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "replyTo.id", target = "replyToId")
    MessageDto toDto(Message message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "frequency", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "editedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "replies", ignore = true)
    Message toEntity(CreateMessageDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "frequency", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "editedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "replies", ignore = true)
    void updateEntity(UpdateMessageDto dto, @MappingTarget Message message);
}

