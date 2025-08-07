package kh.devspaceapi.service;

import kh.devspaceapi.model.dto.users.UsersRequestDto;

public interface UsersService {
    void addUser(UsersRequestDto request);
}
