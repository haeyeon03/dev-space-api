package kh.devspaceapi.model.dto.newsPost;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewsPostResponseDto {
    private Long newsPostId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
}	