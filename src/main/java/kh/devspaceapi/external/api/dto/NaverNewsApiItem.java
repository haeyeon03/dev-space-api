package kh.devspaceapi.external.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import kh.devspaceapi.comm.config.json.CustomLocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverNewsApiItem {
    private String title;
    @JsonProperty("originallink")
    private String originalLink;
    private String link;
    private String description;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime pubDate;
}