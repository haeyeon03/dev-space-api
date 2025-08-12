package kh.devspaceapi.model.dto.newsPost;

import java.time.LocalDateTime;
import java.util.List;

import kh.devspaceapi.external.api.dto.NaverNewsApiItem;
import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
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
    private LocalDateTime createdAt;
    private String url;
    private String imageUrl;
    private LocalDateTime pubDate;

    private List<PostCommentResponseDto> postCommentList;
}