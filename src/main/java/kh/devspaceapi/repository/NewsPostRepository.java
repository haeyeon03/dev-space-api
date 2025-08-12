package kh.devspaceapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.NewsPost;

import java.util.Optional;


public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

	Page<NewsPost> findAllByContentContaining(String content, Pageable pageable);

	Page<NewsPost> findAllByTitleContaining(String title, Pageable pageable);

	Page<NewsPost> findAllByTitleContainingAndContentContaining(String title, String content, Pageable pageable);

    NewsPost findByUrl(String url);

	Optional<NewsPost> findByNewsPostIdAndActiveTrue(Long newsPostId);

}
