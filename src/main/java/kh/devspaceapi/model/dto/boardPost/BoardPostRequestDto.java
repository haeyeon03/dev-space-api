package kh.devspaceapi.model.dto.boardPost;

import kh.devspaceapi.model.dto.base.BasePageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPostRequestDto extends BasePageRequestDto {
    private String searchType; // 타입(내용, 닉네임, 내용+제목)
    private String keyword; // 검색어
    
    private String sortBy = "createdAt";
    private String orderBy = "desc"; 
}
