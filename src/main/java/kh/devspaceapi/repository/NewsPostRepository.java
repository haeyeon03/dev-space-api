package kh.devspaceapi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.NewsPost;

import java.util.Optional;


public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {

	Page<NewsPost> findAllByActiveTrue(Pageable pageable);

	Page<NewsPost> findAllByContentContainingAndActiveTrue(String content, Pageable pageable);

	Page<NewsPost> findAllByTitleContainingAndActiveTrue(String title, Pageable pageable);

	Page<NewsPost> findAllByTitleContainingAndContentContainingAndActiveTrue(String title, String content, Pageable pageable);

    NewsPost findByUrl(String url);

	Optional<NewsPost> findByNewsPostIdAndActiveTrue(Long newsPostId);

}
