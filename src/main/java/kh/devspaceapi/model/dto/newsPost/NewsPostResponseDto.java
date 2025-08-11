package kh.devspaceapi.model.dto.newsPost;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsPostResponseDto {
    private Long newsPostId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
}	