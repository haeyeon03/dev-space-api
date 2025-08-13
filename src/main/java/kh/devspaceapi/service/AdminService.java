package kh.devspaceapi.service;

import kh.devspaceapi.model.dto.admin.stats.SummaryResponseDto;
import kh.devspaceapi.model.dto.admin.stats.UserListResponseDto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kh.devspaceapi.model.dto.admin.stats.GenderRatioResponseDto;

public interface AdminService {
    //관리자 대시보드 카운트 service
    SummaryResponseDto getStatsSummary();

    //관리자 대시보드 gender ratio 카운트 service
    GenderRatioResponseDto getGenderRatio();
    
    //관리자 유저 리스트 service
    Page<UserListResponseDto> getUserList(String searchType, String keyword, String role, Pageable pageable);

}
