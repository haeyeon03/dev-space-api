package kh.devspaceapi.external.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentResult {
    private String contentText;
    private List<String> imageUrls;
}
