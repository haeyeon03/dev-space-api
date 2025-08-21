package kh.devspaceapi.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kh.devspaceapi.auth.security.CustomUserDetails;
import kh.devspaceapi.model.dto.boardPost.BoardPostResponseDto;
import kh.devspaceapi.model.dto.mypage.ChangeEmailRequest;
import kh.devspaceapi.model.dto.mypage.ChangePasswordRequest;
import kh.devspaceapi.model.dto.mypage.MyProfileResponseDto;
import kh.devspaceapi.model.dto.mypage.UpdateProfileRequest;
import kh.devspaceapi.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @Value("${app.upload.profile-dir:uploads/profile}")
    private String profileDir;

    /** 내 프로필 조회 */
    @GetMapping
    public ResponseEntity<MyProfileResponseDto> me(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = principal.getUsername();
        return ResponseEntity.ok(myPageService.getMe(userId));
    }

    /** 내 프로필 수정 (닉네임/성별/생년월일) */
    @PutMapping("/update")
    public ResponseEntity<MyProfileResponseDto> updateProfile(
            @RequestBody UpdateProfileRequest req,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = principal.getUsername();
        return ResponseEntity.ok(myPageService.updateProfile(userId, req));
    }

    /** 프로필 이미지 업로드 */
    @PostMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProfileImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of("message", "파일이 없습니다.")
            );
        }

        try {
            String userId = principal.getUsername();
            log.info("uploadProfileImage by principal.username={}", userId);
            return ResponseEntity.ok(myPageService.updateProfileImage(userId, file));
        } catch (IllegalArgumentException e) {
            // 사용자 없음 / 이미지 아님 등 비즈니스 에러는 400
            log.warn("uploadProfileImage bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                java.util.Map.of("message", e.getMessage())
            );
        } catch (Exception e) {
            // 디버그 편의: 500일 때 상세 내용도 내려줌
            log.error("uploadProfileImage failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                java.util.Map.of(
                    "message", "업로드 실패",
                    "error", e.getClass().getSimpleName(),
                    "detail", e.getMessage()
                )
            );
        }
    }

    /** 내가 쓴 글 목록 (최신순) */
    @GetMapping("/postlist")
    public ResponseEntity<Page<BoardPostResponseDto>> myPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = principal.getUsername();
        Pageable pageable = PageRequest.of(page, size); // 정렬은 서비스에서 최신순 고정
        return ResponseEntity.ok(myPageService.myPosts(userId, pageable));
    }

    /** 비밀번호 변경 */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest req,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = principal.getUsername();
        myPageService.changePassword(userId, req.getCurrentPassword(), req.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    /** 이메일 변경 */
    @PutMapping("/email")
    public ResponseEntity<Void> changeEmail(
            @RequestBody ChangeEmailRequest req,
            @AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = principal.getUsername();
        myPageService.changeEmail(userId, req.getNewEmail());
        return ResponseEntity.noContent().build();
    }

    /** 계정 탈퇴(소프트 삭제: active=false) */
    @DeleteMapping("/deactivate")
    public ResponseEntity<Void> deactivate(@AuthenticationPrincipal CustomUserDetails principal) {
        if (principal == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        String userId = principal.getUsername();
        myPageService.deactivate(userId);
        return ResponseEntity.noContent().build();
    }

    /** 프로필 이미지 스트리밍 */
    @GetMapping("/profile-image/{filename:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String filename) {
        try {
            // 경로 탈출 방지
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Path base = Paths.get(profileDir).toAbsolutePath().normalize();
            Path file = base.resolve(filename).normalize();
            if (!file.startsWith(base)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toUri());

            // 파일 확장자 기반 + 파일 시스템 탐지 기반으로 MIME 결정
            MediaType mt = MediaTypeFactory.getMediaType(filename).orElse(null);
            if (mt == null) {
                String probed = Files.probeContentType(file);
                mt = (probed != null) ? MediaType.parseMediaType(probed) : MediaType.APPLICATION_OCTET_STREAM;
            }

            return ResponseEntity.ok()
                    .contentType(mt)
                    .cacheControl(CacheControl.maxAge(Duration.ofHours(1)).cachePublic())
                    .body(resource);

        } catch (Exception e) {
            log.error("getProfileImage failed for {}", filename, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
