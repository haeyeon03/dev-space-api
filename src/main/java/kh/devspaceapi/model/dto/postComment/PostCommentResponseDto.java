package kh.devspaceapi.model.dto.postComment;

import java.util.List;

import kh.devspaceapi.model.dto.users.UsersResponseDto;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentResponseDto {
    private Long postCommentId;
    private String content;
    private Long targetId;
    private TargetType targetType;

    private UsersResponseDto user;
}
