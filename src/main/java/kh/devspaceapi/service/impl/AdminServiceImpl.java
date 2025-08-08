package kh.devspaceapi.service.impl;

import kh.devspaceapi.model.dto.admin.stats.SummaryResponseDto;
import kh.devspaceapi.model.dto.admin.stats.GenderRatioResponseDto;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.UsersRepository;
import kh.devspaceapi.service.AdminService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    //목록별 카운트를 위한 의존성 주입
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private NewsPostRepository newsPostRepository;
    @Autowired
    private PostCommentRepository postCommentRepository;
    @Autowired
    private BoardPostRepository boardPostRepository;

    //홈페이지에 등록된 컨텐츠의 수 메서드
    @Override
    public SummaryResponseDto getStatsSummary() {
        long totalUsers = usersRepository.count();
        long totalNewsPosts = newsPostRepository.count();
        long totalComments = postCommentRepository.count();
        long totalBoardPosts = boardPostRepository.count();
        //각 repository에서 카운트 된 컨텐츠의 수를 리턴
        return new SummaryResponseDto(totalUsers, totalNewsPosts, totalComments, totalBoardPosts);
    }

    //남녀 성별비율 count 메서드
    //gender group by를 통해 불러온 데이터를
    //List<Object[]>과 반복문을 사용하여 배열형태로 M:0명, 여:0명 데이터를 가져온당
    @Override
    public GenderRatioResponseDto getGenderRatio() {
    	List<Object[]> results = usersRepository.countUserByGender();
    	
        long maleCounts = 0;
        long femaleCounts = 0;
        
        for(Object[] row : results) {
        	 String gender = (String) row[0];
        	 Long count = (Long) row[1];
        	 
        	 if ("M".equals(gender)) {
                 maleCounts = count;
             } else if ("F".equals(gender)) {
                 femaleCounts = count;
             }
        }
        
        //userRepository에서 받은 M, F의 카운트 수를 리턴
        return new GenderRatioResponseDto(maleCounts, femaleCounts);
    }


}
