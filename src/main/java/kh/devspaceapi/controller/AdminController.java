package kh.devspaceapi.controller;

import kh.devspaceapi.model.dto.users.UsersGenderRatioDto;
import kh.devspaceapi.model.dto.users.UsersResponseDto;
import kh.devspaceapi.service.AdminService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/admins")
@RestController
@RequiredArgsConstructor
public class AdminController {
    @Autowired
    private AdminService adminService;
    
   
	/*
	 * 홈페이지에 등록된 유저 수, 컨텐츠 갯수 조회 API
	 * 회원가입 한 총 유저의 수, 게시된 뉴스의 수,
	 * 게시판에 올라간 게시글의 갯수, 홈페이지에 등록된 댓글의 갯수
	 * 
	 * @return ResponseEntity<NewsPostResponseDto> 통계 요약 데이터
	 */
    @GetMapping("/dash")
    public ResponseEntity<UsersResponseDto> getTotalCount(){
    	UsersResponseDto totalCount = adminService.getTotalCount();
    	return ResponseEntity.ok(totalCount);
    }
    
	/*
	 * 남녀 성별비율 통계 API
	 * users의 gerder에서 "M", "F"를 체크
	 * @return ResponseEntity.ok(genderRatio) 체크 된 "M"과"F"의 카운트 데이터
	 */
    @GetMapping("/dash/gender-ratio")
    public ResponseEntity<UsersGenderRatioDto> getUsersGenderRatio() {
    	UsersGenderRatioDto genderRatio = adminService.getUsersGenderRatio();
    	return ResponseEntity.ok(genderRatio);
    }
}
