package kh.devspaceapi.model.dto.newsPost;

import java.time.LocalDateTime;
import java.util.List;

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
    private String lastBuildDate;
    private int total;
    private int start;
    private int display;
    private List<NewsItem> items;

}	