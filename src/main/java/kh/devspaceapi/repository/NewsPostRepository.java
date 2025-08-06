package kh.devspaceapi.repository;

import kh.devspaceapi.model.entity.NewsPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsPostRepository extends JpaRepository<NewsPost, Long> {
}
