package kh.devspaceapi.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kh.devspaceapi.model.entity.BoardPost;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.service.BoardPostService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardPostServiceImpl implements BoardPostService {

	private final BoardPostRepository boardPostRepository;

	@Override
	@Transactional
	public void insert(BoardPost boardPost) {
		LocalDateTime now = LocalDateTime.now();
		boardPost.setCreatedAt(now);
		boardPost.setUpdatedAt(now);
		boardPostRepository.save(boardPost);
	}

	@Override
	@Transactional
	public BoardPost select(BoardPost boardPost) {
		BoardPost selectedPost = boardPostRepository.findByBoardPostId(boardPost.getBoardPostId())
				.orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + boardPost.getBoardPostId()));
		selectedPost.setViewCount(selectedPost.getViewCount() + 1);
		return selectedPost;
	}

	@Override
	@Transactional
	public BoardPost update(BoardPost boardPost) {
		BoardPost updatedPost = boardPostRepository.findByBoardPostId(boardPost.getBoardPostId())
				.orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + boardPost.getBoardPostId()));
		updatedPost.setTitle(boardPost.getTitle());
		updatedPost.setContent(boardPost.getContent());
		updatedPost.setCategory(boardPost.getCategory());
		updatedPost.setUpdatedAt(LocalDateTime.now());
		return updatedPost;
	}

	@Override
	@Transactional
	public void delete(BoardPost boardPost) {
		BoardPost deletedPost = boardPostRepository.findById(boardPost.getBoardPostId())
				.orElseThrow(() -> new IllegalArgumentException("게시글 찾을 수 없음 " + boardPost.getBoardPostId()));
		deletedPost.setActive(true);
		deletedPost.setUpdatedAt(LocalDateTime.now());
	}

	@Override
	public Page<BoardPost> selectAll(Pageable pageable) {
		return boardPostRepository.findByActiveFalse(pageable);
	}

	@Override
	public Page<BoardPost> selectAllByCategory(String category, Pageable pageable) {
		return boardPostRepository.findByActiveFalseAndCategory(category, pageable);
	}

	@Override
	public Page<BoardPost> search(String searchType, String keyword, String category, Pageable pageable) {
		String type = (searchType == null) ? "" : searchType.trim().toLowerCase();
		String kw = (keyword == null) ? "" : keyword.trim();
		boolean hasKw = !kw.isEmpty();
		boolean hasCat = category != null && !category.trim().isEmpty();

		if (!hasKw) {
			// 키워드 없고 카테고리만 있으면: 해당 카테고리 전체
			return hasCat ? boardPostRepository.findByActiveFalseAndCategory(category, pageable)
					: boardPostRepository.findByActiveFalse(pageable);
		}

		// 키워드가 있을 때
		if (hasCat) {
			if ("title".equals(type)) {
				return boardPostRepository.findByActiveFalseAndCategoryAndTitleContaining(category, kw, pageable);
			} else if ("nickname".equals(type)) {
				return boardPostRepository.findByActiveFalseAndCategoryAndUser_NicknameContaining(category, kw,
						pageable);
			} else if ("titlecontent".equals(type) || "title+content".equals(type)) {
				return boardPostRepository
						.findByActiveFalseAndCategoryAndTitleContainingOrActiveFalseAndCategoryAndContentContaining(
								category, kw, category, kw, pageable);
			} else {
				// 타입이 없거나 이상하면 카테고리 전체
				return boardPostRepository.findByActiveFalseAndCategory(category, pageable);
			}
		} else {
			// 카테고리 없음(기존 로직)
			if ("title".equals(type)) {
				return boardPostRepository.findByActiveFalseAndTitleContaining(kw, pageable);
			} else if ("nickname".equals(type)) {
				return boardPostRepository.findByActiveFalseAndUser_NicknameContaining(kw, pageable);
			} else if ("titlecontent".equals(type) || "title+content".equals(type)) {
				return boardPostRepository.findByActiveFalseAndTitleContainingOrActiveFalseAndContentContaining(kw, kw,
						pageable);
			} else {
				return boardPostRepository.findByActiveFalse(pageable);
			}
		}
	}

}
