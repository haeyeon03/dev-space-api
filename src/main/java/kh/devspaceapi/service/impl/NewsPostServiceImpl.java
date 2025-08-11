package kh.devspaceapi.service.impl;

import java.util.List;

import kh.devspaceapi.model.mapper.NewPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kh.devspaceapi.comm.exception.BusinessException;
import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.comm.response.PageResponse;
import kh.devspaceapi.model.dto.newsPost.NewsPostRequestDto;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.service.NewsPostService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewsPostServiceImpl implements NewsPostService {
	@Autowired
	private NewsPostRepository newsPostRepository;

	@Autowired
	private PostCommentRepository postCommentRepository;

	@Autowired
	private NewPostMapper newPostMapper;

	/**
	 * 지정한 뉴스 게시글 ID에 해당하는 뉴스 게시글과 관련된 댓글, 좋아요 정보를 조회
	 *
	 * @param newsPostId 조회할 뉴스 게시글의 고유 ID
	 * @return NewsPostResponseDto 뉴스 게시글, 댓글 리스트, 좋아요 리스트를 포함한 응답 DTO
	 * @throws EntityNotFoundException 해당 ID의 뉴스 게시글이 없을 경우 발생
	 */
	@Override
	public NewsPostResponseDto getNewsPostById(Long newsPostId) {
		NewsPost newsPost = newsPostRepository.findById(newsPostId)
				.orElseThrow(() -> new BusinessException(ErrorCode.NO_EXIST_NEWS_POST));
		// newsPost -> NewsPostResponseDto 변환

		List<PostComment> comments = postCommentRepository
				.findByTargetIdAndTargetTypeOrderByPostCommentIdDesc(newsPost.getNewsPostId(), TargetType.NEWS);
		// comments -> CommentResponseDto 변환

		// postLike 도 같은 방식으로 조회
		// postLike -> PostLikeResponseDto 변환

//        NewsPostResponseDto.setComments(comments);
//        NewsPostResponseDto.setPostLikes(comments);

//        return NewsPostResponseDto;
		return null;
	}

	/**
	 * 뉴스 게시글 검색어 설정 후 조회 API 전체(검색어 설정을 안 했을 경우) 내용으로 검색 제목으로 검색 내용+전체로 검색
	 *
	 * @return NewsPostResponseDto데이터 들이 페이지 단위로 끊긴 게시글 응답(active = true)
	 * @throws EntityNotFoundException 해당 제목 / 내용에 따른 뉴스 게시글이 없을 경우 발생
	 */
	@Override
	public PageResponse<NewsPostResponseDto> getNewsPost(NewsPostRequestDto request) {

		if (request.getCurPage() > 0) {
			request.setCurPage(request.getCurPage() - 1);
		}
		Pageable pageable = PageRequest.of(request.getCurPage(), request.getPageSize(),
				Sort.by(Sort.Direction.DESC, "updatedAt"));

		Page<NewsPost> newsPostList = null;

		String title = request.getTitle();
		String content = request.getContent();

		// 내용으로 검색
		if ((title == null || title.isBlank()) && (content != null && !content.isBlank())) {
			newsPostList = newsPostRepository.findAllByContentContainingAndActiveTrue(content, pageable);
			// 제목으로 검색
		} else if ((title != null && !title.isBlank()) && (content == null || content.isBlank())) {
			newsPostList = newsPostRepository.findAllByTitleContainingAndActiveTrue(title, pageable);
			// 제목 + 내용 검색
		} else if ((title != null && !title.isBlank()) && (content != null && !content.isBlank())) {
			newsPostList = newsPostRepository.findAllByTitleContainingAndContentContainingAndActiveTrue(title, content, pageable);
		} else {
			// 전체 검색
			newsPostList = newsPostRepository.findAllByActiveTrue(pageable);
		}

		// **Entity -> DTO 변환 (수동)**
		Page<NewsPostResponseDto> dtoPage = newsPostList.map(newPostMapper::toDto);

		// PageResponse 생성자에 Page 넣어서 반환
		return new PageResponse<>(dtoPage);
	}

	/**
	 * 뉴스 게시글 및 해당 게시글에 달린 모든 댓글을 논리적으로 삭제 처리 (물리 삭제(DELETE) 대신 active 필드를 false로
	 * 변경)
	 *
	 * @param newsPostId 조회할 뉴스 게시글의 고유 ID
	 * @return 삭제 성공 시 1L, 실패 시 0L
	 * @throws IllegalArgumentException 해당 ID의 뉴스 게시글이 존재하지 않을 경우 발생
	 */
	@Override
	@Transactional
	public Long deleteNewsPost(Long newsPostId) {
		try {
			// 1) 뉴스 게시글 존재 여부 확인
			// 존재하지 않으면 IllegalArgumentException 발생
			NewsPost newsPost = newsPostRepository.findById(newsPostId)
					.orElseThrow(() -> new IllegalArgumentException("해당 뉴스 게시글이 존재하지 않습니다."));

			// 2) 해당 게시글에 달린 모든 댓글 조회 (TargetType.NEWS)
			// 댓글도 논리 삭제(active=false)
			List<PostComment> comments = postCommentRepository.findByTargetIdAndTargetType(newsPostId, TargetType.NEWS);
			for (PostComment comment : comments) {
				comment.setActive(false);
			}
			postCommentRepository.saveAll(comments);

			// 3) 뉴스 게시글 논리 삭제 처리
			newsPost.setActive(false);
			newsPostRepository.save(newsPost);

			return 1L; // 성공 시 1 반환

		} catch (DataIntegrityViolationException e) {
			// DB 제약 조건 위반 시 (예: FK 제약, null 값 불가 등)
		} catch (IllegalArgumentException e) {
			// 존재하지 않는 뉴스 게시글 ID일 경우
			throw e; // 이 경우는 0L 반환 대신 예외를 컨트롤러로 던짐
		} catch (Exception e) {
			// 알 수 없는 예외 처리
		}

		// 예외가 발생해서 try 블록을 정상적으로 마치지 못한 경우 0 반환 (실패)
		return 0L;
	}
}
