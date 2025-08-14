package kh.devspaceapi.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kh.devspaceapi.model.dto.admin.stats.ApplyPenaltyRequestDto;
import kh.devspaceapi.model.dto.admin.stats.GenderRatioResponseDto;
import kh.devspaceapi.model.dto.admin.stats.SummaryResponseDto;
import kh.devspaceapi.model.dto.admin.stats.UpdateRoleRequestDto;
import kh.devspaceapi.model.dto.admin.stats.UserDetailResponseDto;
import kh.devspaceapi.model.dto.admin.stats.UserListResponseDto;
import kh.devspaceapi.model.entity.UserPenalty;
import kh.devspaceapi.model.entity.Users;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.NewsPostRepository;
import kh.devspaceapi.repository.PostCommentRepository;
import kh.devspaceapi.repository.UserPenaltyRepository;
import kh.devspaceapi.repository.UsersRepository;
import kh.devspaceapi.service.AdminService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
	// 목록별 카운트를 위한 의존성 주입
	@Autowired
	private UsersRepository usersRepository;
	@Autowired
	private NewsPostRepository newsPostRepository;
	@Autowired
	private PostCommentRepository postCommentRepository;
	@Autowired
	private BoardPostRepository boardPostRepository;
	@Autowired
	private UserPenaltyRepository userPenaltyRepository;

	// 홈페이지에 등록된 컨텐츠의 수 메서드
	@Override
	public SummaryResponseDto getStatsSummary() {
		long totalUsers = usersRepository.count();
		long totalNewsPosts = newsPostRepository.count();
		long totalComments = postCommentRepository.count();
		long totalBoardPosts = boardPostRepository.count();
		// 각 repository에서 카운트 된 컨텐츠의 수를 리턴
		return new SummaryResponseDto(totalUsers, totalNewsPosts, totalComments, totalBoardPosts);
	}

	/*
	 * 남녀 성별비율 count 메서드 gender group by를 통해 불러온 데이터를
	 * List<Object[]>과 반복문을 사용하여
	 * 배열형태로 M:0명, 여:0명 데이터를 가져온당
	 */
	@Override
	public GenderRatioResponseDto getGenderRatio() {
		List<Object[]> results = usersRepository.countUserByGender();

		long maleCounts = 0;
		long femaleCounts = 0;

		for (Object[] row : results) {
			String gender = (String) row[0];
			Long count = (Long) row[1];

			if ("M".equals(gender)) {
				maleCounts = count;
			} else if ("F".equals(gender)) {
				femaleCounts = count;
			}
		}

		// userRepository에서 받은 M, F의 카운트 수를 리턴
		return new GenderRatioResponseDto(maleCounts, femaleCounts);
	}
	
	//유저 
	@Override
	public Page<UserListResponseDto> getUserList(String searchType, String keyword, String role, Pageable pageable) {

		// 1) 유저 페이지 조회 (검색 + admin/user 역할 필터)
        Page<Users> page = usersRepository.searchUsers(searchType, keyword, role, pageable);
        List<Users> users = page.getContent();

      if (users.isEmpty()) {
           return page.map(u -> toDto(u, false, null));
        }

        // 2) 해당 페이지 유저들의 userId 수집
        Set<String> userIds = users.stream().map(Users::getUserId).collect(Collectors.toSet());

        // 3) 현재 시점 기준 '정지중' 페널티 일괄 조회
        List<UserPenalty> activePenalties = userPenaltyRepository.findActivePenaltiesByUserIds(userIds);

        // 4) userId -> 가장 늦게 끝나는 banEndAt 매핑
        Map<String, LocalDateTime> banEndAtMap = new HashMap<>();
        for (UserPenalty up : activePenalties) {
            LocalDateTime endAt = up.getEffectiveAt().plusSeconds(up.getDurationSec());
            String uid = up.getUser().getUserId();
            banEndAtMap.merge(uid, endAt, (oldV, newV) -> oldV.isAfter(newV) ? oldV : newV);
        }

        // 5) DTO 변환
        List<UserListResponseDto> dtos = users.stream()
                .map(u -> {
                    LocalDateTime banEnd = banEndAtMap.get(u.getUserId());
                    boolean banned = (banEnd != null) && LocalDateTime.now().isBefore(banEnd);
                    return toDto(u, banned, banEnd);
                })
                // 6) role=banned 필터 요청 시 서비스단에서 최종 거르기
                .filter(dto -> {
                    if (role == null || role.isBlank() || role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("user")) {
                        return true;
                    }
                    if (role.equalsIgnoreCase("banned")) {
                        return dto.isBanned();
                    }
                    return true;
                })
                .toList();

        // 총 건수는 원래 page totalElements 사용(필터 후 사이즈가 줄어도 페이지네이션 일관성 유지)
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }
	
	//
    private UserListResponseDto toDto(Users u, boolean isBanned, LocalDateTime banEndAt) {
        return UserListResponseDto.builder()
                .userId(u.getUserId())
                .nickname(u.getNickname())
                .gender(u.getGender())
                .role(u.getRole())
                .banned(isBanned)
                .banEndAt(banEndAt)
                .build();

	}
    
    //유저 상세정보
    public UserDetailResponseDto getUserDetail(String userId) {
        Users u = usersRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

        // 최근 패널티 1건
        UserPenalty p = userPenaltyRepository
                .findFirstByUser_UserIdOrderByEffectiveAtDesc(userId)
                .orElse(null);

        boolean banned = false;
        LocalDateTime banEndAt = null;
        String banReason = null;
        LocalDateTime banEffectiveAt = null;
        Long banDurationSec = null;
        // 정지된 데이터가 있을경우 가장 최근에 활동정지 정보 표시
        if (p != null && p.getEffectiveAt() != null) {
            banEffectiveAt = p.getEffectiveAt();
            banDurationSec = p.getDurationSec();
            banEndAt = banEffectiveAt.plusSeconds(banDurationSec);
            banned = LocalDateTime.now().isBefore(banEndAt);
            banReason = p.getReason();
        }

        return UserDetailResponseDto.builder()
                .userId(u.getUserId())
                .nickname(u.getNickname())
                .gender(u.getGender())
                .role(u.getRole())
                .admin("admin".equalsIgnoreCase(u.getRole()))
                .banned(banned)
                .banReason(banReason)
                .banEffectiveAt(banEffectiveAt)
                .banDurationSec(banDurationSec)
                .banEndAt(banEndAt)
                .build();
    }
    
    @Override
	@Transactional
	public UserDetailResponseDto updateRole(String userId, UpdateRoleRequestDto req) {
		if (req.getRole() == null
				|| !(req.getRole().equalsIgnoreCase("admin") || req.getRole().equalsIgnoreCase("user"))) {
			throw new IllegalArgumentException("role은 'admin' 또는 'user'만 허용됩니다.");
		}

		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

		user.setRole(req.getRole().toLowerCase());
		usersRepository.save(user);

		return toDetailDto(user);
	}

	@Override
	@Transactional
	public UserDetailResponseDto applyPenalty(String userId, ApplyPenaltyRequestDto req) {
		if (req.getReason() == null || req.getReason().isBlank()) {
			throw new IllegalArgumentException("reason은 필수입니다.");
		}
		if (req.getEffectiveAt() == null) {
			throw new IllegalArgumentException("effectiveAt은 필수입니다.");
		}
		if (req.getDurationSec() < 0) {
			throw new IllegalArgumentException("durationSec은 0 이상이어야 합니다.");
		}

		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

		UserPenalty up = new UserPenalty();
		up.setUser(user);
		up.setReason(req.getReason());
		up.setReportedAt(LocalDateTime.now());
		up.setEffectiveAt(req.getEffectiveAt());
		up.setDurationSec(req.getDurationSec());

		userPenaltyRepository.save(up);

		return toDetailDto(user);
	}

	@Override
	@Transactional
	public UserDetailResponseDto liftPenaltyNow(String userId) {
		Users user = usersRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

		// 가장 최근 패널티 1건을 가져와 종료시각을 now로 맞춰줌
		userPenaltyRepository.findFirstByUser_UserIdOrderByEffectiveAtDesc(userId).ifPresent(latest -> {
			if (latest.getEffectiveAt() != null) {
				LocalDateTime now = LocalDateTime.now();
				if (now.isAfter(latest.getEffectiveAt())) {
					long newDuration = Duration.between(latest.getEffectiveAt(), now).getSeconds();
					latest.setDurationSec(Math.max(newDuration, 0));
					userPenaltyRepository.save(latest);
				} else {
					// effectiveAt이 미래면 즉시 해제하려면 duration=0
					latest.setDurationSec(0);
					userPenaltyRepository.save(latest);
				}
			}
		});

		return toDetailDto(user);
	}

	// 상세 DTO 변환
	private UserDetailResponseDto toDetailDto(Users user) {
		// 최신 패널티 1건
		UserPenalty latest = userPenaltyRepository.findFirstByUser_UserIdOrderByEffectiveAtDesc(user.getUserId())
				.orElse(null);

		boolean banned = false;
		LocalDateTime banEndAt = null;

		if (latest != null && latest.getEffectiveAt() != null) {
			banEndAt = latest.getEffectiveAt().plusSeconds(latest.getDurationSec());
			banned = LocalDateTime.now().isBefore(banEndAt);
		}
		return UserDetailResponseDto.builder().userId(user.getUserId()).nickname(user.getNickname())
				.gender(user.getGender()).role(user.getRole()).banned(banned).banEndAt(banEndAt)
				.banReason(latest != null ? latest.getReason() : null)
				.reportedAt(latest != null ? latest.getReportedAt() : null)
				.banEffectiveAt(latest != null ? latest.getEffectiveAt() : null)
				.banDurationSec(latest != null ? latest.getDurationSec() : null).build();
	}

}
