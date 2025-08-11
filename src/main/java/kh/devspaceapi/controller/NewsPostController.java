package kh.devspaceapi.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kh.devspaceapi.comm.response.PageResponse;
import kh.devspaceapi.model.dto.newsPost.NewsPostRequestDto;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.service.NewsPostService;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/news-posts")
@RestController
@Slf4j
public class NewsPostController {
	@Autowired
	private NewsPostService newsPostService;

	/**
	 * 뉴스 게시글 단건 조회 API
	 *
	 * 특정 ID(newsPostId)를 가진 뉴스 게시글을 조회
	 *
	 * @param newsPostId 조회할 뉴스 게시글의 ID (Path Variable)
	 * @return ResponseEntity<NewsPostResponseDto> 조회된 뉴스 게시글 데이터
	 */
	@GetMapping("/{newsPostId}")
	public ResponseEntity<NewsPostResponseDto> getNewsPostById(@PathVariable Long newsPostId) {
		NewsPostResponseDto newsPost = newsPostService.getNewsPostById(newsPostId);
		return ResponseEntity.ok(newsPost);
	}

	/**
	 * 뉴스 게시글 상세 조회 API
	 *
	 * 특정 ID(newsPostId)를 가진 뉴스 게시글을 조회
	 *
	 * @param newsPostId 조회할 뉴스 게시글의 ID (Path Variable)
	 * @return ResponseEntity<NewsPostResponseDto> 조회된 뉴스 게시글 데이터
	 */
//    @GetMapping("/{newsPostId}/comments")
//    public ResponseEntity<List<CommentResponseDto>> getCommentsByNewsPostId(@PathVariable Long newsPostId) {
//        List<CommentResponseDto> comments = commentService.getCommentsByNewsPostId(newsPostId);
//        return ResponseEntity.ok(comments);
//    }

	/**
	 * 뉴스 게시글 검색어 설정 후 조회 API
	 * 
	 * 전체(검색어 설정을 안 했을 경우) 내용으로 검색 제목으로 검색 내용+전체로 검색
	 * 
	 * @ModelAttribute = 데이터 바인딩 + 모델 자동 등록, model.addAttribute()를 안 써도 됨 → 코드 깔끔
	 */
	@GetMapping("/")
	public ResponseEntity<PageResponse<NewsPostResponseDto>> getNewsPost(@ModelAttribute NewsPostRequestDto request) {
		return ResponseEntity.ok(newsPostService.getNewsPost(request));
	}

	/**
	 * 
	 * @param newsPostId 조회하여 뉴스 게시글 삭제
	 * @return 삭제 정상 처리 후 리스트 반환
	 */
	@DeleteMapping("/{newsPostId}")
	ResponseEntity<Map<String, String>> deleteNewsPost(@PathVariable Long newsPostId) {
		newsPostService.deleteNewsPost(newsPostId);
		return ResponseEntity.ok(Map.of("message", "삭제가 완료되었습니다."));

	}
}
