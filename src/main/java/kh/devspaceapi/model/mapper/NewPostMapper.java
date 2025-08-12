package kh.devspaceapi.model.mapper;

import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.entity.NewsPost;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewPostMapper implements GenericMapper<NewsPostResponseDto, NewsPost> {
//    @Autowired Modelmapper modelmapper;

    @Override
    public NewsPost toEntity(NewsPostResponseDto dto) {
//        return modelmapper.map(dto, NewsPost.class);
        return null;
    }

    // NewsPost → NewsPostResponseDto 변환 메서드
    public NewsPostResponseDto toDto(NewsPost entity) {
        NewsPostResponseDto dto = new NewsPostResponseDto();
        dto.setNewsPostId(entity.getNewsPostId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPubDate(entity.getPubDate());
        dto.setImageUrl(entity.getImageUrl());
        dto.setUrl(entity.getUrl());
        return dto;
    }

    @Override
    public List<NewsPost> toEntityList(List<NewsPostResponseDto> dtoList) {
        return List.of();
    }

    @Override
    public List<NewsPostResponseDto> toDtoList(List<NewsPost> entityList) {
        return List.of();
    }
}
