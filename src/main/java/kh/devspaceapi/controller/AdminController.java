package kh.devspaceapi.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kh.devspaceapi.comm.response.ApiResponse;
import kh.devspaceapi.model.dto.admin.stats.AgeGenderDistributionResponseDto;
import kh.devspaceapi.model.dto.admin.stats.DailyViewCountResponseDto;
import kh.devspaceapi.model.dto.admin.stats.GenderRatioResponseDto;
import kh.devspaceapi.model.dto.admin.stats.SummaryResponseDto;
import kh.devspaceapi.model.dto.admin.stats.UserListResponseDto;
import kh.devspaceapi.service.AdminService;
import kh.devspaceapi.service.PostViewLogService;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/admins")
@RestController
@RequiredArgsConstructor
public class AdminController {
	@Autowired
	private AdminService adminService;
	@Autowired
	private PostViewLogService postViewLogService;

	/*
	 * 홈페이지에 등록된 유저 수, 컨텐츠 갯수 조회 API 회원가입 한 총 유저의 수, 게시된 뉴스의 수, 게시판에 올라간 게시글의 갯수,
	 * 홈페이지에 등록된 댓글의 갯수
	 *
	 * @return ResponseEntity<StatsResponseDto> 통계 요약 데이터
	 */
	@GetMapping("/stats/summary")
	public ResponseEntity<ApiResponse<SummaryResponseDto>> getStatsSummary() {
		SummaryResponseDto summary = adminService.getStatsSummary();
		return ResponseEntity.ok(ApiResponse.success(summary));
	}

	/*
	 * 남녀 성별비율 통계 API
	 * 
	 * users의 gerder에서 "M", "F"를 체크
	 * 
	 * @return ResponseEntity.ok(genderRatio) 체크 된 "M"과"F"의 카운트 데이터
	 */
	@GetMapping("/stats/gender-ratio")
	public ResponseEntity<ApiResponse<GenderRatioResponseDto>> getGenderRatio() {
		GenderRatioResponseDto genderRatio = adminService.getGenderRatio();
		return ResponseEntity.ok(ApiResponse.success(genderRatio));
	}

	/*
	 * 일별 조회수 통계API
	 * 
	 * 날짜별 조회수를 불러오기 위해 @RequestParam, @DateTimeFormat, Timestamp를 추가하였으며
	 * 
	 * @RequestParam이 date파라미터를 가져오며 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	 * date파라미터 문자열을 LocalDate 객체로 변환하여 조회가 시작날짜와 끝나는 날짜별로 조회수를 가져올 수 있다.
	 * 
	 * @return ResponseEntity<List<DailyViewCountResponseDto>>로 가져온 일별 조회수
	 */
	@GetMapping("/stats/daily-views")
	public ResponseEntity<List<DailyViewCountResponseDto>> getDailyViewCount(
			@RequestParam LocalDate startTs,
			@RequestParam LocalDate endTsExclusive) {
		
		return ResponseEntity.ok(postViewLogService.getDailyViewCountBetween(startTs, endTsExclusive));
	}

	/*
	 * 기간별/연령대별 남여이용자 수 통계API
	 * 
	 * @return 연령대, 성별, 조회수를 기간별로 지정하여 리턴해준다.
	 */
	@GetMapping("/stats/age-gender")
	public ResponseEntity<List<AgeGenderDistributionResponseDto>> getAgeGenderDistribution(
			@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
		List<AgeGenderDistributionResponseDto> distribution = postViewLogService.getAgeGenderDistribution(startDate,
				endDate);
		return ResponseEntity.ok(distribution);
	}
	
	/**
     * 회원 리스트 조회 (검색/역할필터/정지여부/페이징)
     * searchType: name | nickname
     * role: admin | user | banned
     */
    @GetMapping("/members")
    public ResponseEntity<Page<UserListResponseDto>> getMembers(
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 10, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(
        		adminService.getUserList(searchType, keyword, role, pageable)
        );
    }
}
