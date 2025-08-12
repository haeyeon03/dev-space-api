package kh.devspaceapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.NewsPost;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

	Page<NewsPost> findAllByActiveTrue(Pageable pageable);

	Page<NewsPost> findAllByContentContainingAndActiveTrue(String content, Pageable pageable);

	Page<NewsPost> findAllByTitleContainingAndActiveTrue(String title, Pageable pageable);

	Page<NewsPost> findAllByTitleContainingAndContentContainingAndActiveTrue(String title, String content, Pageable pageable);

	boolean existsByLink(String link);

	Optional<NewsPost> findByNewsPostIdAndActiveTrue(Long newsPostId);
}
