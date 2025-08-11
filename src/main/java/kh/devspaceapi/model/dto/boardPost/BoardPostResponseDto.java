package kh.devspaceapi.model.dto.boardPost;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostResponseDto {
    private Long boardPostId;
    private String title;
    private String category;
    private String content;
    private String userNickname;
    private LocalDateTime createdAt;
    private int viewCount;
    private int commentCount;
}