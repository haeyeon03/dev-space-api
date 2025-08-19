package kh.devspaceapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kh.devspaceapi.model.entity.NewsPost;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

	// 전체 활성 뉴스, 페이징
	Page<NewsPost> findAllByActiveTrue(Pageable pageable);

	// 제목 포함 검색, 페이징
	Page<NewsPost> findAllByTitleContainingAndActiveTrue(String title, Pageable pageable);

	// 내용 포함 검색, 페이징
	Page<NewsPost> findAllByContentContainingAndActiveTrue(String content, Pageable pageable);

	// 제목 OR 내용 포함 검색, Pageable 지원
	@Query("SELECT n FROM NewsPost n "
			+ "WHERE n.active = true AND (n.title LIKE %:keyword% OR n.content LIKE %:keyword%) "
			+ "ORDER BY n.updatedAt DESC")
	Page<NewsPost> findByTitleOrContentContaining(@Param("keyword") String keyword, Pageable pageable);

	// 기타 조회
	NewsPost findByUrl(String url);

	Optional<NewsPost> findByNewsPostIdAndActiveTrue(Long newsPostId);
}
