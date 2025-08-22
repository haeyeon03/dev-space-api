package kh.devspaceapi.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.persistence.EntityNotFoundException;
import kh.devspaceapi.comm.exception.BusinessException;
import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.comm.response.PageResponse;
import kh.devspaceapi.model.dto.newsPost.NewsPostRequestDto;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.dto.postComment.PostCommentRequestDto;
import kh.devspaceapi.model.dto.postComment.PostCommentResponseDto;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.model.mapper.NewPostMapper;
import kh.devspaceapi.model.mapper.PostCommentMapper;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.service.NewsPostService;
import kh.devspaceapi.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsPostServiceImpl implements NewsPostService {
	private final NewsPostRepository newsPostRepository;
	private final PostCommentRepository postCommentRepository;
	private final NewPostMapper newPostMapper;
	private final PostCommentMapper postCommentMapper;
	private final PostCommentService postCommentService;

	/**
	 * 뉴스 게시글 검색 조건에 따라 페이지 단위로 조회
	 *
	 * 검색어(title, content)에 따라 전체, 제목, 내용, 제목+내용 조건 분기
	 * 게시글은 active=true인 것만 조회
	 *
	 * @param request 검색 조건과 페이지 정보를 포함한 DTO
	 * @return PageResponse<NewsPostResponseDto> : 페이징된 뉴스 게시글 리스트
	 * @throws EntityNotFoundException 검색 결과가 없을 경우
	 */
	@Override
	public PageResponse<NewsPostResponseDto> getNewsPost(NewsPostRequestDto request) {

		// 페이지 번호 0 기반 맞춤
		int curPage = request.getCurPage() > 0 ? request.getCurPage() - 1 : 0;
		int pageSize = request.getPageSize();

		String title = request.getTitle();
		String content = request.getContent();

		Page<NewsPost> newsPostPage;

		// 검색 조건에 따라 분기
		if ((title == null || title.isBlank()) && (content == null || content.isBlank())) {
			// 전체 조회
			Pageable pageable = PageRequest.of(curPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
			newsPostPage = newsPostRepository.findAllByActiveTrue(pageable);
		} else if ((title != null && !title.isBlank()) && (content == null || content.isBlank())) {
			// 제목만 검색
			Pageable pageable = PageRequest.of(curPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
			newsPostPage = newsPostRepository.findAllByTitleContainingAndActiveTrue(title, pageable);
		} else if ((title == null || title.isBlank()) && (content != null && !content.isBlank())) {
			// 내용만 검색
			Pageable pageable = PageRequest.of(curPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
			newsPostPage = newsPostRepository.findAllByContentContainingAndActiveTrue(content, pageable);
		} else {
			// 제목 + 내용 OR 검색
			Pageable pageable = PageRequest.of(curPage, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
			String keyword = title; // 프론트에서 searchText 동일하게 전달
			newsPostPage = newsPostRepository.findByTitleOrContentContaining(keyword, pageable);
		}

		// 엔티티 -> DTO 변환
		Page<NewsPostResponseDto> dtoPage = newsPostPage.map(newPostMapper::toDto);

		// PageResponse로 감싸서 반환
		return new PageResponse<>(dtoPage);
	}

	/**
	 * 단건 뉴스 게시글 조회
	 *
	 * @param newsPostId 조회할 뉴스 게시글 ID
	 * @return NewsPostResponseDto 뉴스 게시글 상세 정보
	 * @throws BusinessException 해당 게시글이 없거나 비활성화된 경우
	 */
	@Override
	public NewsPostResponseDto getNewsPostById(Long newsPostId) {
		NewsPost newsPost = newsPostRepository.findByNewsPostIdAndActiveTrue(newsPostId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NO_EXIST_NEWS_POST));

		// 엔티티 -> DTO 변환
		NewsPostResponseDto newsPostDto = newPostMapper.toDto(newsPost);

		// TODO: 댓글, 좋아요 등 관련 데이터 DTO에 포함 가능
		return newsPostDto;
	}

	/**
	 * 뉴스 게시글에 달린 댓글 조회 (페이징)
	 *
	 * @param newsPostId 뉴스 게시글 ID
	 * @param request    페이지 번호, 크기 등의 요청 정보
	 * @return Page<PostCommentResponseDto> : 페이징된 댓글 리스트
	 */
	@Override
	public Page<PostCommentResponseDto> getCommentsByNewsPostId(Long newsPostId, PostCommentRequestDto request) {
		int curPage = request.getCurPage() > 0 ? request.getCurPage() - 1 : 0;
		int pageSize = request.getPageSize() > 0 ? request.getPageSize() : 10;

		Pageable pageable = PageRequest.of(curPage, pageSize, Sort.by("createdAt").descending());

		// targetId(newsPostId)와 targetType(NEWS) 기준으로 댓글 조회
		Page<PostComment> commentPage = postCommentRepository
				.findCommentsByTargetIdAndTargetTypeAndActiveTrue(newsPostId, TargetType.NEWS, pageable);

		// 엔티티 -> DTO 변환 후 반환
		return commentPage.map(postCommentMapper::toDto);
	}

	/**
	 * 뉴스 게시글에 댓글 추가
	 *
	 * @param newsPostId 뉴스 게시글 ID
	 * @param body       요청 바디에서 content 추출
	 * @return PostCommentResponseDto : 생성된 댓글 DTO
	 */
	@PostMapping("/{newsPostId}/comments")
	public ResponseEntity<PostCommentResponseDto> addComment(@PathVariable Long newsPostId,
			@RequestBody Map<String, String> body) {
		String content = body.get("content");

		// 현재 로그인된 사용자 ID 가져오기 (JWT subject)
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String userId = authentication.getName();

		PostCommentResponseDto dto = postCommentService.create(newsPostId, TargetType.NEWS, userId, content);

		return ResponseEntity.ok(dto);
	}

	/**
	 * 뉴스 게시글과 해당 게시글의 모든 댓글 논리 삭제
	 *
	 * active 필드를 false로 변경하여 삭제 처리
	 *
	 * @param newsPostId 삭제할 뉴스 게시글 ID
	 * @return 1L 성공, 0L 실패
	 * @throws IllegalArgumentException 뉴스 게시글이 존재하지 않을 경우
	 */
	@Override
	@Transactional
	public Long deleteNewsPost(Long newsPostId) {
		try {
			// 1) 뉴스 게시글 존재 여부 확인
			NewsPost newsPost = newsPostRepository.findById(newsPostId)
					.orElseThrow(() -> new IllegalArgumentException("해당 뉴스 게시글이 존재하지 않습니다."));

			// 2) 해당 게시글에 달린 모든 댓글 조회 후 논리 삭제(active=false)
			List<PostComment> comments = postCommentRepository.findByTargetIdAndTargetType(newsPostId, TargetType.NEWS);
			for (PostComment comment : comments) {
				comment.setActive(false);
			}
			postCommentRepository.saveAll(comments);

			// 3) 뉴스 게시글 논리 삭제
			newsPost.setActive(false);
			newsPostRepository.save(newsPost);

			return 1L;

		} catch (DataIntegrityViolationException e) {
			// DB 제약 조건 위반 시
			log.error("DB 제약 조건 위반: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			// 존재하지 않는 뉴스 게시글
			throw e;
		} catch (Exception e) {
			// 기타 알 수 없는 예외
			log.error("뉴스 게시글 삭제 중 오류: {}", e.getMessage());
		}

		return 0L; // 실패 시 0 반환
	}
}
