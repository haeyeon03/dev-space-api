package kh.devspaceapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.NewsPost;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

	Page<NewsPost> findAllByContentContaining(String content, Pageable pageable);

	Page<NewsPost> findAllByTitleContaining(String title, Pageable pageable);

	Page<NewsPost> findAllByTitleContainingAndContentContaining(String title, String content, Pageable pageable);

	boolean existsByLink(String link);
}
