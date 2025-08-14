package kh.devspaceapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kh.devspaceapi.model.dto.admin.stats.ApplyPenaltyRequestDto;
import kh.devspaceapi.model.dto.admin.stats.GenderRatioResponseDto;
import kh.devspaceapi.model.dto.admin.stats.SummaryResponseDto;
import kh.devspaceapi.model.dto.admin.stats.UpdateRoleRequestDto;
import kh.devspaceapi.model.dto.admin.stats.UserDetailResponseDto;
import kh.devspaceapi.model.dto.admin.stats.UserListResponseDto;

public interface AdminService {
	// 관리자 대시보드 카운트 service
	SummaryResponseDto getStatsSummary();

	// 관리자 대시보드 gender ratio 카운트 service
	GenderRatioResponseDto getGenderRatio();

	// 관리자 유저 리스트 service
	Page<UserListResponseDto> getUserList(String searchType, String keyword, String role, Pageable pageable);

	// 유저 상세정보 service
	UserDetailResponseDto getUserDetail(String userId);

	// 관리자 유저 권한 수정 service
	UserDetailResponseDto updateRole(String userId, UpdateRoleRequestDto req);

	// 관리자 유저활동 정지 적용 service
	UserDetailResponseDto applyPenalty(String userId, ApplyPenaltyRequestDto req);

	// 관리자 유저활동 정지 해제 service
	UserDetailResponseDto liftPenaltyNow(String userId);

}
