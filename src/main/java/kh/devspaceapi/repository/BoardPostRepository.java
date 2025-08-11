package kh.devspaceapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.BoardPost;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveFalseAndUser_NicknameContaining(String nickname, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveFalseAndTitleContaining(String title, Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveFalseAndTitleContainingOrActiveFalseAndContentContaining(String title, String content,
			Pageable pageable);

	@EntityGraph(attributePaths = "user")
	Optional<BoardPost> findByBoardPostId(Long boardPostId);

	@EntityGraph(attributePaths = "user")
	Page<BoardPost> findByActiveFalse(Pageable pageable);
	
    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveFalseAndCategory(String category, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveFalseAndCategoryAndTitleContaining(
            String category, String title, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveFalseAndCategoryAndUser_NicknameContaining(
            String category, String nickname, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<BoardPost> findByActiveFalseAndCategoryAndTitleContainingOrActiveFalseAndCategoryAndContentContaining(
            String category1, String title,
            String category2, String content,
            Pageable pageable);
}