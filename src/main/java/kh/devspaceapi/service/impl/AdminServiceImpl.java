package kh.devspaceapi.service.impl;

import kh.devspaceapi.model.dto.admin.stats.SummaryResponseDto;
import kh.devspaceapi.model.dto.admin.stats.GenderRatioResponseDto;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.UserRepository;
import kh.devspaceapi.service.AdminService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    //목록별 카운트를 위한 의존성 주입
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NewsPostRepository newsPostRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private BoardPostRepository boardPostRepository;

    //홈페이지에 등록된 컨텐츠의 수 메서드
    @Override
    public SummaryResponseDto getStatsSummary() {
        long totalUsers = userRepository.count();
        long totalNewsPosts = newsPostRepository.count();
        long totalComments = postCommentRepository.count();
        long totalBoardPosts = boardPostRepository.count();
        //각 repository에서 카운트 된 컨텐츠의 수를 리턴
        return new SummaryResponseDto(totalUsers, totalNewsPosts, totalComments, totalBoardPosts);
    }

    //남녀 성별비율 count 메서드
    @Override
    public GenderRatioResponseDto getGenderRatio() {
        long maleCounts = userRepository.countByGender("M");
        long femaleCounts = userRepository.countByGender("F");
        //
        //이 경우 DB 커넥션이 같은 테이블에 두 번 생겨
        //최소한의 커넥션으로 한번에 데이터를 가져오는 것을 추천!
        //Group By 활용해서 한번에!
        //
        //userRepository에서 받은 M, F의 카운트 수를 리턴
        return new GenderRatioResponseDto(maleCounts, femaleCounts);
    }


}
