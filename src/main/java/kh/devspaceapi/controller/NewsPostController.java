package kh.devspaceapi.controller;

import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.service.NewsPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/news-posts")
@RestController
public class NewsPostController {
    @Autowired
    private NewsPostService newsPostService;

    /**
     * 뉴스 목록 조회 API
     *
     * @param newsPostId 조회할 뉴스 게시글의 ID (Path Variable)
     * @return ResponseEntity<NewsPostResponseDto>
     */
    @GetMapping("/")
    public ResponseEntity<NewsPostResponseDto> getNewsPost(@PathVariable Long newsPostId) {
        NewsPostResponseDto newsPost = newsPostService.getNewsPost(newsPostId);
        return ResponseEntity.ok(newsPost);
    }
}
