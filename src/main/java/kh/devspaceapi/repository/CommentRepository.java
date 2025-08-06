package kh.devspaceapi.repository;

import kh.devspaceapi.model.entity.Comments;
import kh.devspaceapi.model.entity.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Long>  {
    List<Comments> findByTargetIdAndTargetTypeOrderByCommentIdDesc(Long targetId, TargetType targetType);
}
