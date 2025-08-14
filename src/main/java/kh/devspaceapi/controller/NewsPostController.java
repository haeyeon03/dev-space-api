package kh.devspaceapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
import kh.devspaceapi.model.dto.postComment.PostCommentRequestDto;
import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.service.NewsPostService;
import kh.devspaceapi.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
	 * 뉴스 게시글 댓글 목록 조회 API
	 *
	 * 특정 뉴스 게시글(newsPostId)에 달린 댓글을 페이지 단위로 조회 요청 시
	 * page, size, sort 등의 페이징 정보를 함께 전달 가능
	 *
	 * @param newsPostId 조회할 뉴스 게시글 ID
	 * @param request    페이지 번호(page), 페이지 크기(size) 등의 페이징 요청 정보
	 * @return 페이징 처리된 댓글 목록
	 */
	@GetMapping("/{newsPostId}/comments")
	public ResponseEntity<Page<PostCommentResponseDto>> getCommentsByNewsPostId(
	        @PathVariable Long newsPostId,
	        @RequestParam(defaultValue = "0") int curPage,
	        @RequestParam(defaultValue = "10") int pageSize) {

	    PostCommentRequestDto request = new PostCommentRequestDto();
	    request.setCurPage(curPage);
	    request.setPageSize(pageSize);

	    Page<PostCommentResponseDto> comments = newsPostService.getCommentsByNewsPostId(newsPostId, request);
	    return ResponseEntity.ok(comments);
	}


	/**
	 * 뉴스 게시글 댓글 등록
	 *
	 * @param newsPostId 댓글을 달 뉴스 게시글 ID
	 * @param requestDto 댓글 내용 등을 담은 DTO
	 * @return 등록된 댓글 정보를 담은 PostCommentResponseDto
	 */
	@PostMapping("/{newsPostId}/comments")
	public ResponseEntity<PostCommentResponseDto> createNewsPostComment(@PathVariable Long newsPostId,
			@RequestBody PostCommentRequestDto requestDto) {

		PostCommentResponseDto created = postCommentService.create(newsPostId, TargetType.NEWS, // 뉴스 게시판 타입
				requestDto.getTargetId(), requestDto.getContent());

		return ResponseEntity.ok(created);

	}

	/**
	 * 특정 뉴스 게시글 댓글 수정
	 *
	 * @param newsPostId 수정할 댓글이 속한 뉴스 게시글 ID
	 * @param commentId  수정할 댓글 ID
	 * @param requestDto 수정할 댓글 내용을 담은 DTO
	 * @return 수정된 댓글 정보를 담은 PostCommentResponseDto
	 */
	@PutMapping("/{newsPostId}/comments/{commentId}")
	public ResponseEntity<PostCommentResponseDto> updateNewsPostComment(@PathVariable Long newsPostId,
			@PathVariable Long commentId, @RequestBody PostCommentRequestDto requestDto) {

		PostCommentResponseDto updatedComment = postCommentService.update(newsPostId, TargetType.NEWS, commentId,
				requestDto.getContent());

		return ResponseEntity.ok(updatedComment);
	}

	/**
	 * 특정 뉴스 게시글 댓글 삭제
	 * 논리 삭제(active=false)로 처리
	 *
	 * @param newsPostId 삭제할 댓글이 속한 뉴스 게시글 ID
	 * @param commentId  삭제할 댓글 ID
	 * @return 삭제 성공 시 HTTP 204 No Content
	 */
	@DeleteMapping("/{newsPostId}/comments/{commentId}")
	public ResponseEntity<Void> deleteNewsPostComment(@PathVariable Long newsPostId, @PathVariable Long commentId) {

		postCommentService.delete(newsPostId, TargetType.NEWS, commentId);
		return ResponseEntity.noContent().build();
	}

}
