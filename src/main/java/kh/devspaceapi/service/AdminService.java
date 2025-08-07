package kh.devspaceapi.service;

import kh.devspaceapi.model.dto.users.UsersResponseDto;

public interface AdminService {
	
	//관리자 대시보드 카운트 service
	UsersResponseDto getTotalCount();
}
