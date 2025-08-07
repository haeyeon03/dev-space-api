package kh.devspaceapi.controller;

import kh.devspaceapi.comm.response.ApiResponse;
import kh.devspaceapi.model.dto.users.UsersRequestDto;
import kh.devspaceapi.service.UsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UsersController {
    @Autowired
    private UsersService usersService;

    /**
     * 사용자 회원가입 API
     *
     * @param request {"userId": String, "password": String, "nickname": String, "gender": String, "birthdate": LocalDate}
     */
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> addUser(@RequestBody UsersRequestDto request) {
        usersService.addUser(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
