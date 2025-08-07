package kh.devspaceapi.controller;

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
    
    /**
     * 홈페이지에 등록된 유저 수, 컨텐츠 갯수 조회 API
     * 회원가입 한 총 유저의 수, 게시된 뉴스의 수, 게시판에 올라간 게시글의 갯수, 홈페이지에 등록된 댓글의 갯수
     * @return ResponseEntity<NewsPostResponseDto> 통계 요약 데이터
     */
    @GetMapping("/dash")
    public ResponseEntity<UsersResponseDto> getTotalCount(){
    	UsersResponseDto totalCount = adminService.getTotalCount();
    	return ResponseEntity.ok(totalCount);
    }
    
    //남녀 성별비율 통계 API 
    //@GetMapping(/)
}
