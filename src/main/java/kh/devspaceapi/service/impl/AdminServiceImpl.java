package kh.devspaceapi.service.impl;

import kh.devspaceapi.model.dto.users.UsersGenderRatioDto;
import kh.devspaceapi.model.dto.users.UsersResponseDto;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.UserRepository;
import kh.devspaceapi.service.AdminService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.hibernate.annotations.AnyDiscriminator;
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
	public UsersResponseDto getTotalCount() {
		long totalUsers = userRepository.count();
		long totalNewsPost = newsPostRepository.count();
		long totalComments = postCommentRepository.count();
		long totalBoardPost = boardPostRepository.count();
		//각 repository에서 카운트 된 컨텐츠의 수를 리턴
		return new UsersResponseDto(totalUsers,totalNewsPost,totalComments,totalBoardPost);
	}
	
	//남녀 성별비율 count 메서드
	@Override
	public UsersGenderRatioDto getUsersGenderRatio() {
		long maleCount = userRepository.countByGender("M");
		long femaleCount = userRepository.countByGender("F");
		//userRepository에서 받은 M, F의 카운트 수를 리턴
		return new UsersGenderRatioDto(maleCount, femaleCount);
	}


}
