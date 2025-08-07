package kh.devspaceapi.service.impl;

import kh.devspaceapi.comm.exception.BusinessException;
import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.model.dto.users.UsersRequestDto;
import kh.devspaceapi.model.entity.Users;
import kh.devspaceapi.repository.UsersRepository;
import kh.devspaceapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UsersServiceImpl implements UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void addUser(UsersRequestDto request) {
        if (!StringUtils.hasText(request.getUserId())) {
            throw new BusinessException(ErrorCode.EMPTY_USER_ID);
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException(ErrorCode.EMPTY_PASSWORD);
        }
        if (!StringUtils.hasText(request.getNickname())) {
            throw new BusinessException(ErrorCode.EMPTY_NICKNAME);
        }
        if (!StringUtils.hasText(request.getGender())) {
            throw new BusinessException(ErrorCode.EMPTY_GENDER);
        }
        if (request.getBirthdate() == null) {
            throw new BusinessException(ErrorCode.EMPTY_BIRTHDATE);
        }
        // 중복 사용자 검사
        if (usersRepository.existsById(request.getUserId())) {
            throw new BusinessException(ErrorCode.DUPLICATED_USER);
        }

        // 사용자 정보 저장
        usersRepository.save(Users.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword())) // 비밀번호 암호화
                .nickname(request.getNickname())
                .gender(request.getGender())
                .birthdate(request.getBirthdate())
                .role("1")
                .build()
        );

    }
}
