package kh.devspaceapi.service.impl;

import kh.devspaceapi.model.dto.boardPost.BoardPostResponseDto;
import kh.devspaceapi.model.dto.mypage.MyProfileResponseDto;
import kh.devspaceapi.model.dto.mypage.UpdateProfileRequest;
import kh.devspaceapi.model.entity.BoardPost;
import kh.devspaceapi.model.entity.Users;
import kh.devspaceapi.repository.BoardPostRepository;
import kh.devspaceapi.repository.UsersRepository;
import kh.devspaceapi.service.BoardPostService;
import kh.devspaceapi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UsersRepository usersRepository;
    private final BoardPostRepository boardPostRepository;
    private final BoardPostService boardPostService; // view/comment 합계 재사용
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.profile-dir:uploads/profile}")
    private String profileDir; // 로컬 저장 경로(기본값)

    @Override
    @Transactional(readOnly = true)
    public MyProfileResponseDto getMe(String userId) {
        Users u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));
        return toProfileDto(u);
    }

    @Override
    @Transactional
    public MyProfileResponseDto updateProfile(String userId, UpdateProfileRequest req) {
        Users u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));
        if (req.getNickname() != null) u.setNickname(req.getNickname());
        if (req.getGender() != null) u.setGender(req.getGender());
        if (req.getBirthdate() != null) u.setBirthdate(req.getBirthdate());
        // updatedAt 같은 필드가 있으면 갱신
        return toProfileDto(u);
    }

    @Override
    @Transactional
    public MyProfileResponseDto updateProfileImage(String userId, MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new IllegalArgumentException("파일이 없습니다.");

        // 이미지 여부(관대하게) - contentType가 비어오는 케이스 대비
        String ct = file.getContentType();
        boolean looksImage = (ct != null && ct.startsWith("image/"));
        if (!looksImage) {
            String extLower = guessExt(file.getOriginalFilename()).toLowerCase();
            if (!extLower.matches("\\.(png|jpg|jpeg|gif|webp|bmp|heic|heif)$")) {
                throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
            }
        }

        Users u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));

        Path base = null;
        Path dest = null;
        try {
            // 저장 루트 확보 (절대경로 + 생성 + 쓰기 가능, 실패 시 홈 디렉터리로 폴백)
            base = ensureWritableBase(profileDir);

            // 안전한 파일명 생성
            String ext = guessExt(file.getOriginalFilename());
            String rawName = userId + "_" + System.currentTimeMillis() + ext;
            String safeName = rawName.replaceAll("[^a-zA-Z0-9._-]", "_");

            dest = base.resolve(safeName).normalize();
            if (!dest.startsWith(base)) {
                throw new IllegalArgumentException("잘못된 파일명입니다.");
            }

            // Files.copy 사용 (환경 호환성 ↑)
            try (var in = file.getInputStream()) {
                Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
            }

            // 컨트롤러 스트리밍 URL
            String url = "/api/mypage/profile-image/" + safeName;
            u.setProfileImageUrl(url);

            return toProfileDto(u);

        } catch (AccessDeniedException e) {
            log.error("Profile upload failed (AccessDenied) base={} dest={} dirProp={}", base, dest, profileDir, e);
            throw new RuntimeException("파일 저장 실패: 권한 거부(AccessDenied) - " + e.getMessage(), e);
        } catch (NoSuchFileException e) {
            log.error("Profile upload failed (NoSuchFile) base={} dest={} dirProp={}", base, dest, profileDir, e);
            throw new RuntimeException("파일 저장 실패: 경로 없음(NoSuchFile) - " + e.getMessage(), e);
        } catch (FileSystemException e) {
            log.error("Profile upload failed (FileSystem) base={} dest={} dirProp={}", base, dest, profileDir, e);
            throw new RuntimeException("파일 저장 실패: 파일시스템 오류(FileSystem) - " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Profile upload failed (IO) base={} dest={} dirProp={}", base, dest, profileDir, e);
            throw new RuntimeException("파일 저장 실패: IO 오류 - " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Profile upload failed (Unknown) base={} dest={} dirProp={}", base, dest, profileDir, e);
            throw new RuntimeException("파일 저장 실패: 알 수 없는 오류 - " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoardPostResponseDto> myPosts(String userId, Pageable pageable) {
        // 최신순으로 고정
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<BoardPost> page = boardPostRepository
                .findByActiveTrueAndUser_UserId(userId, sorted);
        return page.map(this::toBoardPostDto);
    }

    @Override
    @Transactional
    public void changePassword(String userId, String currentPw, String newPw) {
        Users u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));
        if (currentPw == null || newPw == null) throw new IllegalArgumentException("비밀번호를 입력하세요.");
        if (!passwordEncoder.matches(currentPw, u.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        u.setPassword(passwordEncoder.encode(newPw));
    }

    @Override
    @Transactional
    public void changeEmail(String userId, String newEmailRaw) {
        // 공백/대소문자 정리
        String newEmail = newEmailRaw == null ? null : newEmailRaw.trim().toLowerCase();

        Users u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));

        // 비어있거나 기존과 같으면 변경 없이 종료 (에러 아님)
        String current = u.getEmail();
        if (newEmail == null || newEmail.isBlank() ||
            (current != null && newEmail.equalsIgnoreCase(current))) {
            return;
        }

        // "본인 제외" 중복 검사: existsByEmail 결과가 true라도
        // 그 이메일이 현재 내 이메일과 같다면 허용, 다르면 충돌
        boolean usedBySomeone = usersRepository.existsByEmail(newEmail)
                && (current == null || !newEmail.equalsIgnoreCase(current));

        if (usedBySomeone) {
            // 500 대신 409(CONFLICT) + JSON 에러 바디로 내려감
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.");
        }

        u.setEmail(newEmail);
    }

    @Override
    @Transactional
    public void deactivate(String userId) {
        Users u = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));
        u.setActive(false); // boolean이라면 false, 숫자면 0으로 매핑
    }

    private MyProfileResponseDto toProfileDto(Users u) {
        return MyProfileResponseDto.builder()
                .userId(u.getUserId())
                .nickname(u.getNickname())
                .email(u.getEmail())
                .gender(u.getGender())
                .birthdate(u.getBirthdate())
                .profileImageUrl(u.getProfileImageUrl())
                .build();
    }

    private BoardPostResponseDto toBoardPostDto(BoardPost e) {
        int views = boardPostService.getViewCountOf(e.getBoardPostId());
        int comments = boardPostService.getCommentCountOf(e.getBoardPostId());
        return BoardPostResponseDto.builder()
                .boardPostId(e.getBoardPostId())
                .title(e.getTitle())
                .category(e.getCategory())
                .content(e.getContent())
                .userNickname(e.getUser() != null ? e.getUser().getNickname() : null)
                .authorId(e.getUser() != null ? e.getUser().getUserId() : null)
                .createdAt(e.getCreatedAt())
                .viewCount(views)
                .commentCount(comments)
                .build();
    }

    private String guessExt(String original) {
        if (original == null) return "";
        int dot = original.lastIndexOf('.');
        return (dot >= 0) ? original.substring(dot) : "";
    }

    /** 지정 경로 생성/검증, 실패 시 홈 디렉터리로 폴백 */
    private Path ensureWritableBase(String preferred) throws IOException {
        Path base = Paths.get(preferred).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException e) {
            Path fallback = Paths.get(System.getProperty("user.home"), "devspace", "uploads", "profile")
                    .toAbsolutePath().normalize();
            Files.createDirectories(fallback);
            base = fallback;
        }
        if (!Files.isWritable(base)) {
            Path fallback = Paths.get(System.getProperty("user.home"), "devspace", "uploads", "profile")
                    .toAbsolutePath().normalize();
            Files.createDirectories(fallback);
            base = fallback;
        }
        return base;
    }
    
    /** 닉네임 중복 확인 */
    @Override
    public boolean isNicknameAvailable(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            return false; // 빈 닉네임은 불가
        }
        return !usersRepository.existsByNickname(nickname);
    }
    
    /** 이메일 중복 확인 */
    @Override
    public boolean isEmailAvailable(String email) {
        if (email == null || email.isBlank()) {
            return false; // 빈 값은 불가
        }
        return !usersRepository.existsByEmail(email);
    }
}
