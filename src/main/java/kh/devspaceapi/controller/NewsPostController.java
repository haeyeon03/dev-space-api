package kh.devspaceapi.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kh.devspaceapi.comm.response.PageResponse;
import kh.devspaceapi.model.dto.newsPost.NewsPostRequestDto;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.service.NewsPostService;
import kh.devspaceapi.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 뉴스 게시글과 관련된 API를 처리하는 컨트롤러
 * 뉴스 게시글 조회, 단건 조회, 삭제
 * 뉴스 게시글 댓글 CRUD 및 페이징
 */
@RequestMapping("/api/news-posts")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NewsPostController {

	@Autowired
	private NewsPostService newsPostService;
	@Autowired
	private PostCommentService postCommentService;

	/**
     * 뉴스 게시글 목록 조회 API
     * 검색어 조건이 있을 경우 제목/내용/전체 검색 가능
     * @ModelAttribute 사용 → DTO에 자동 바인딩 + Model에 자동 등록
     *
     * @param request NewsPostRequestDto 검색 조건 DTO
     * @return PageResponse<NewsPostResponseDto> 페이징된 뉴스 게시글 리스트
	 */
	@GetMapping("/")
	public ResponseEntity<PageResponse<NewsPostResponseDto>> getNewsPost(@ModelAttribute NewsPostRequestDto request) {
		return ResponseEntity.ok(newsPostService.getNewsPost(request));
	}

	 /**
     * 뉴스 게시글 단건 조회 API
     *
     * 특정 ID(newsPostId)를 가진 뉴스 게시글을 조회
     * 로그인 안 해도 조회수 증가
     *
     * @param newsPostId 조회할 뉴스 게시글 ID
     * @return ResponseEntity<NewsPostResponseDto> 조회된 뉴스 게시글 데이터
     */
    @GetMapping("/{newsPostId}")
    public ResponseEntity<NewsPostResponseDto> getNewsPostById(@PathVariable Long newsPostId) {
        // 서비스에서 조회수 증가 + DTO 반환
        NewsPostResponseDto newsPost = newsPostService.getNewsPostById(newsPostId);
        return ResponseEntity.ok(newsPost);
    }

	/**
	 * 뉴스 게시글 조회 후 삭제
	 *
	 * @param newsPostId 조회하여 뉴스 게시글 삭제 처리
	 * @return false로 변환 처리 후 리스트 반환
	 */
	@DeleteMapping("/{newsPostId}")
	ResponseEntity<Long> deleteNewsPost(@PathVariable Long newsPostId) {
		Long removed = newsPostService.deleteNewsPost(newsPostId);
		return ResponseEntity.ok(removed);

	}

	 /**
     * 댓글 등록 API
     *
     * @param newsPostId 댓글을 등록할 뉴스 게시글 ID
     * @param body JSON body에서 "content" 추출
     * @return PostCommentResponseDto 등록된 댓글 DTO
     */
	@PostMapping("/{newsPostId}/comments")
	public ResponseEntity<PostCommentResponseDto> addComment(@PathVariable Long newsPostId,
			@RequestBody Map<String, String> body) {
		String content = body.get("content");

		// 🔑 JWT의 userId 꺼내기
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		PostCommentResponseDto dto = postCommentService.create(newsPostId, TargetType.NEWS, userId, content);

		return ResponseEntity.ok(dto);
	}

	/**
     * 댓글 페이징 조회 API
     *
     * @param newsPostId 댓글을 조회할 뉴스 게시글 ID
     * @param curPage 요청 페이지 번호, 기본값 0
     * @param pageSize 페이지당 댓글 수, 기본값 10
     * @return Page<PostCommentResponseDto> 페이징된 댓글 리스트
     */
	@GetMapping("/{newsPostId}/comments")
	public ResponseEntity<Page<PostCommentResponseDto>> getComments(@PathVariable Long newsPostId,
			@RequestParam(defaultValue = "0") int curPage, @RequestParam(defaultValue = "10") int pageSize) {
		Pageable pageable = PageRequest.of(curPage, pageSize, Sort.by("createdAt").descending());
		Page<PostCommentResponseDto> page = postCommentService.page(newsPostId, TargetType.NEWS, pageable);
		return ResponseEntity.ok(page);
	}

	 /**
     * 댓글 수정 API
     *
     * @param newsPostId 댓글이 속한 뉴스 게시글 ID
     * @param commentId 수정할 댓글 ID
     * @param body JSON body에서 "content" 추출
     * @return PostCommentResponseDto 수정된 댓글 DTO
     */
	@PutMapping("/{newsPostId}/comments/{commentId}")
	public ResponseEntity<PostCommentResponseDto> updateComment(@PathVariable Long newsPostId,
			@PathVariable Long commentId, @RequestBody Map<String, String> body) {

		String content = body.get("content");

		// JWT에서 userId 가져오기
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		PostCommentResponseDto dto = postCommentService.update(newsPostId, TargetType.NEWS, commentId, content, userId);
		return ResponseEntity.ok(dto);
	}

	 /**
     * 댓글 삭제 API
     *
     * @param newsPostId 댓글이 속한 뉴스 게시글 ID
     * @param commentId 삭제할 댓글 ID
     * @return ResponseEntity<Void> 삭제 완료 상태 반환
     */
	@DeleteMapping("/{newsPostId}/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long newsPostId, @PathVariable Long commentId) {

		// JWT에서 userId 가져오기
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();

		postCommentService.delete(newsPostId, TargetType.NEWS, commentId, userId);
		return ResponseEntity.noContent().build();
	}

}
