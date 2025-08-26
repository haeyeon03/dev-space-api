package kh.devspaceapi.controller;

import kh.devspaceapi.comm.exception.ErrorCode;
import kh.devspaceapi.comm.response.ApiResponse;
import kh.devspaceapi.model.dto.users.InquiryRequestDto;
import kh.devspaceapi.model.dto.users.UsersRequestDto;
import kh.devspaceapi.service.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UsersController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String email;
    /**
     * 사용자 회원가입 API
     *
     * @param request
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> addUser(@RequestBody UsersRequestDto request) {
        usersService.addUser(request);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 사용자 1:1문의 API
     *
     * @param request
     */
    @PostMapping("/inquiry")
    public ResponseEntity<ApiResponse<String>> addUser(@RequestBody InquiryRequestDto request) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("[1:1 문의] " + request.getTitle());
            msg.setText("문의자 이메일: " + request.getEmail() +
                    "\n\n문의 내용:\n" + request.getMessage());
            mailSender.send(msg);

            // 성공 응답
            return ResponseEntity.ok(
                    ApiResponse.success(
                            ErrorCode.MAIL_SEND_SUCCESS.getCode(),
                            ErrorCode.MAIL_SEND_SUCCESS.getMessage()
                    )
            );
        } catch (Exception e) {
            // 실패 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            ErrorCode.MAIL_SEND_FAIL.getCode(),
                            ErrorCode.MAIL_SEND_FAIL.getMessage()
                    ));
        }
    }
}
