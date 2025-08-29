package kh.devspaceapi.model.dto.postComment;

import java.time.LocalDateTime;

import kh.devspaceapi.model.dto.users.UsersResponseDto;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentResponseDto {
    private Long postCommentId;
    private String content;
    private Long targetId;
    private TargetType targetType;
    
    private String userNickname;
    private UsersResponseDto user;
    
    private LocalDateTime createdAt;
}
