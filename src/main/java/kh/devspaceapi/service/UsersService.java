package kh.devspaceapi.service;

import kh.devspaceapi.model.dto.users.UsersRequestDto;
import kh.devspaceapi.model.entity.Users;

public interface UsersService {
    void addUser(UsersRequestDto request);

	Users findById(String userId);
}
