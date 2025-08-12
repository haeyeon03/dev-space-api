package kh.devspaceapi.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import kh.devspaceapi.model.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "NEWS_POST")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(name = "news_post_seq_gen", sequenceName = "NEWS_POST_SEQ", allocationSize = 1)
public class NewsPost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_post_seq_gen")
    private Long newsPostId;

    private String title;

    private String originallink;
    private String link;
    private String description;
    private LocalDateTime pubDate;

    @Lob
    private String content; // 크롤링한 본문

    private String imageUrl; // 크롤링한 대표 이미지 URL
    
}
