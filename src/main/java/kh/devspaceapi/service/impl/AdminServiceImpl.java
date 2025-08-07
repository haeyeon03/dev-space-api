package kh.devspaceapi.service.impl;

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
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NewsPostRepository newsPostRepository;
	@Autowired
	private PostCommentRepository postCommentRepository;
	@Autowired
	private BoardPostRepository boardPostRepository;
	
	@Override
	public UsersResponseDto getTotalCount() {
		long totalUsers = userRepository.count();
		long totalNewsPost = newsPostRepository.count();
		long totalComments = postCommentRepository.count();
		long totalBoardPost = boardPostRepository.count();
		
		return new UsersResponseDto(totalUsers,totalNewsPost,totalComments,totalBoardPost);
	}
	
	


}
