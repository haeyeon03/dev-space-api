package kh.devspaceapi.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.entity.PostComment;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.model.entity.enums.TargetType;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.service.NewsPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsPostServiceImpl implements NewsPostService {
    @Autowired
    private NewsPostRepository newsPostRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    /**
     * 지정한 뉴스 게시글 ID에 해당하는 뉴스 게시글과 관련된 댓글, 좋아요 정보를 조회
     *
     * @param newsPostId 조회할 뉴스 게시글의 고유 ID
     * @return NewsPostResponseDto 뉴스 게시글, 댓글 리스트, 좋아요 리스트를 포함한 응답 DTO
     * @throws EntityNotFoundException 해당 ID의 뉴스 게시글이 없을 경우 발생
     */
    @Override
    public NewsPostResponseDto getNewsPost(Long newsPostId) {
        NewsPost newsPost = newsPostRepository.findById(newsPostId)
                .orElseThrow(() -> new EntityNotFoundException("해당 뉴스 게시글을 찾을 수 없습니다. ID: " + newsPostId));
        // newsPost -> NewsPostResponseDto 변환

        List<PostComment> comments = postCommentRepository.findByTargetIdAndTargetTypeOrderByCommentIdDesc(newsPost.getNewsPostId(), TargetType.NEWS);
        // comments -> CommentResponseDto 변환

        // postLike 도 같은 방식으로 조회
        // postLike -> PostLikeResponseDto 변환

        System.out.println("cs");
//        NewsPostResponseDto.setComments(comments);
//        NewsPostResponseDto.setPostLikes(comments);

//        return NewsPostResponseDto;
        return null;
    }
}
