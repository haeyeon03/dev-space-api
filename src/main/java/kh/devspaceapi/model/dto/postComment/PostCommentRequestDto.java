package kh.devspaceapi.model.dto.postComment;

import kh.devspaceapi.model.dto.base.BasePageRequestDto;
import kh.devspaceapi.model.entity.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentRequestDto extends BasePageRequestDto {
    private String content;   // 댓글 내용
    private Long targetId;    // 댓글 달린 글 ID
    private TargetType targetType; // NEWS, BOARD ...
    private Long userId;      // 댓글 작성자 ID
}
