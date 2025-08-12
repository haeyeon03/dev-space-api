package kh.devspaceapi.model.dto.newsPost;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsItem {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}