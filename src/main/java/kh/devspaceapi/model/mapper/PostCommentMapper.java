package kh.devspaceapi.model.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.dto.users.UsersResponseDto;
import kh.devspaceapi.model.entity.PostComment;

@Component
public class PostCommentMapper implements GenericMapper<PostCommentResponseDto, PostComment> {

    private final UserMapper userMapper;

    public PostCommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public PostComment toEntity(PostCommentResponseDto dto) {
        return null;
    }

    @Override
    public PostCommentResponseDto toDto(PostComment entity) {
        PostCommentResponseDto dto = new PostCommentResponseDto();

        dto.setPostCommentId(entity.getPostCommentId());
        dto.setContent(entity.getContent());
        dto.setTargetId(entity.getTargetId());
        dto.setTargetType(entity.getTargetType());

        if (entity.getUser() != null) {
            UsersResponseDto userDto = userMapper.toDto(entity.getUser());
            dto.setUser(userDto);
        }

        return dto;
    }

    @Override
    public List<PostComment> toEntityList(List<PostCommentResponseDto> dtoList) {
        return null;
    }

    @Override
    public List<PostCommentResponseDto> toDtoList(List<PostComment> entityList) {
        if (entityList == null)
            return Collections.emptyList();

        return entityList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
