package kh.devspaceapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.BoardPost;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

}
