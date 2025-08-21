package kh.devspaceapi.service;

import kh.devspaceapi.model.dto.boardPost.BoardPostResponseDto;
import kh.devspaceapi.model.dto.mypage.MyProfileResponseDto;
import kh.devspaceapi.model.dto.mypage.UpdateProfileRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    MyProfileResponseDto getMe(String userId);
    MyProfileResponseDto updateProfile(String userId, UpdateProfileRequest req);
    MyProfileResponseDto updateProfileImage(String userId, MultipartFile file);

    Page<BoardPostResponseDto> myPosts(String userId, Pageable pageable);

    void changePassword(String userId, String currentPw, String newPw);
    void changeEmail(String userId, String newEmail);
    void deactivate(String userId);
}
