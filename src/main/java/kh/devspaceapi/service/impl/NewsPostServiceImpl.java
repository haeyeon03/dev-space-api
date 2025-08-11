package kh.devspaceapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
	 * 
	 * @param 뉴스 게시글 검색어 설정 후 조회 API 전체(검색어 설정을 안 했을 경우) 내용으로 검색 제목으로 검색 내용+전체로 검색
	 * @return NewsPostResponseDto데이터 들이 페이지 단위로 끊긴 게시글 응답
	 * @throws EntityNotFoundException 해당 제목 / 내용에 따른 뉴스 게시글이 없을 경우 발생
	 */
	@Override
	public PageResponse<NewsPostResponseDto> getNewsPost(NewsPostRequestDto request) {
		log.info("NewsPostServiceImpl.getNewsPost 요청: page={}, size={}, title={}, content={}", request.getCurPage(),
				request.getPageSize(), request.getTitle(), request.getContent());

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
			newsPostList = newsPostRepository.findAllByContentContaining(content, pageable);
			// 제목으로 검색
		} else if ((title != null && !title.isBlank()) && (content == null || content.isBlank())) {
			newsPostList = newsPostRepository.findAllByTitleContaining(title, pageable);
			// 제목 + 내용 검색
		} else if ((title != null && !title.isBlank()) && (content != null && !content.isBlank())) {
			newsPostList = newsPostRepository.findAllByTitleContainingAndContentContaining(title, content, pageable);
		} else {
			// 전체 검색
			newsPostList = newsPostRepository.findAll(pageable);
		}

		// **Entity -> DTO 변환 (수동)**
		Page<NewsPostResponseDto> dtoPage = newsPostList.map(this::toDto);

		// PageResponse 생성자에 Page 넣어서 반환
		return new PageResponse<>(dtoPage);
	}

	// NewsPost → NewsPostResponseDto 변환 메서드
	private NewsPostResponseDto toDto(NewsPost entity) {
		NewsPostResponseDto dto = new NewsPostResponseDto();
		dto.setNewsPostId(entity.getNewsPostId());
		dto.setTitle(entity.getTitle());
		dto.setContent(entity.getContent());
		dto.setUpdatedAt(entity.getUpdatedAt());
		return dto;

	}

	@Override
	public void deleteNewsPost(Long newsPostId) {
		// TODO Auto-generated method stub
		
	}

}
