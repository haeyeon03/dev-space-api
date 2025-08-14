package kh.devspaceapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.BoardPost;

/**
 * 게시글 조회용 리포지토리.
 * 목록, 검색 메서드는 모두 active=true(보이는 글)만 조회
 */
public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

	// 닉네임으로 검색 (부분일치), 보이는 글만.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndUser_NicknameContaining(String nickname, Pageable pageable);

	// 제목으로 검색 (부분일치), 보이는 글만.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndTitleContaining(String title, Pageable pageable);

	// 제목 또는 내용으로 검색 (부분일치), 보이는 글만.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndTitleContainingOrActiveTrueAndContentContaining(String title, String content,
			Pageable pageable);

	// PK로 단건 조회.
	// active 여부는 체크하지 않음(상세 조회용).
	@EntityGraph(attributePaths = "user")
	Optional<BoardPost> findByBoardPostId(Long boardPostId);

	// 전체 목록(보이는 글만), 페이징/정렬 포함.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrue(Pageable pageable);

	// 카테고리별 목록(보이는 글만).
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndCategory(String category, Pageable pageable);

	// 카테고리 + 제목 검색(부분일치), 보이는 글만.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndCategoryAndTitleContaining(String category, String title, Pageable pageable);

	// 카테고리 + 작성자 닉네임 검색(부분일치), 보이는 글만.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndCategoryAndUser_NicknameContaining(String category, String nickname,
			Pageable pageable);

	// 카테고리 + (제목 or 내용) 검색(부분일치), 보이는 글만.
	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndCategoryAndTitleContainingOrActiveTrueAndCategoryAndContentContaining(
			String category1, String title, String category2, String content, Pageable pageable);
}
