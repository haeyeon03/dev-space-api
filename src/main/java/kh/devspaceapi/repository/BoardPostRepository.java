package kh.devspaceapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.BoardPost;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndUser_NicknameContaining(String nickname, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndTitleContaining(String title, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrueAndTitleContainingOrActiveTrueAndContentContaining(String title, String content,
			Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Optional<BoardPost> findByBoardPostId(Long boardPostId);

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveTrueAndCategory(String category, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveTrueAndCategoryAndTitleContaining(
            String category, String title, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveTrueAndCategoryAndUser_NicknameContaining(
            String category, String nickname, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveTrueAndCategoryAndTitleContainingOrActiveTrueAndCategoryAndContentContaining(
            String category1, String title,
            String category2, String content,
            Pageable pageable);
}