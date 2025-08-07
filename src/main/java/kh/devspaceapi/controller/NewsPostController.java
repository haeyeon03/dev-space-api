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
     * 뉴스 게시글 단건 조회 API
     *
     * 특정 ID(newsPostId)를 가진 뉴스 게시글을 조회합니다.
     *
     * @param newsPostId 조회할 뉴스 게시글의 ID (Path Variable)
     * @return ResponseEntity<NewsPostResponseDto> 조회된 뉴스 게시글 데이터
     */
    @GetMapping("/{newsPostId}")
    public ResponseEntity<NewsPostResponseDto> getNewsPostById(@PathVariable Long newsPostId) {
        NewsPostResponseDto newsPost = newsPostService.getNewsPostById(newsPostId);
        return ResponseEntity.ok(newsPost);
    }
}
