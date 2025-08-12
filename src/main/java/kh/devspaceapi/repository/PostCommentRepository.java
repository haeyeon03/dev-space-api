package kh.devspaceapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.enums.TargetType;

public interface PostCommentRepository extends JpaRepository<PostComment, Long>  {
    List<PostComment> findByTargetIdAndTargetTypeOrderByPostCommentIdDesc(Long targetId, TargetType targetType);

    List<PostComment> findByTargetIdAndTargetType(Long newsPostId, TargetType targetType);
}
