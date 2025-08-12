package kh.devspaceapi.model.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import kh.devspaceapi.model.dto.users.UsersResponseDto;
import kh.devspaceapi.model.entity.Users;

@Component
public class UserMapper implements GenericMapper<UsersResponseDto, Users> {

	@Override
	public Users toEntity(UsersResponseDto dto) {
		if (dto == null) {
			return null;
		}
		Users entity = new Users();
		entity.setUserId(dto.getUserId());
		entity.setNickname(dto.getNickname());
		entity.setGender(dto.getGender());
		entity.setEmail(dto.getEmail());
		entity.setRole(dto.getRole());
		entity.setBirthdate(dto.getBirthdate());
		return entity;
	}

	@Override
	public UsersResponseDto toDto(Users entity) {
		if (entity == null) {
			return null;
		}
		UsersResponseDto dto = new UsersResponseDto();
		dto.setUserId(entity.getUserId());
		dto.setNickname(entity.getNickname());
		dto.setGender(entity.getGender());
		dto.setEmail(entity.getEmail());
		dto.setRole(entity.getRole());
		dto.setBirthdate(entity.getBirthdate());
		return dto;
	}

	@Override
	public List<Users> toEntityList(List<UsersResponseDto> dtoList) {
		if (dtoList == null) {
			return null;
		}
		return dtoList.stream().map(this::toEntity).collect(Collectors.toList());
	}

	@Override
	public List<UsersResponseDto> toDtoList(List<Users> entityList) {
		if (entityList == null) {
			return null;
		}
		return entityList.stream().map(this::toDto).collect(Collectors.toList());
	}

}
