package kh.devspaceapi.external.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@ConfigurationProperties(prefix = "api.naver")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverNewsApiProperty {
    private String host;
    private String uri;
    private String clientId;
    private String clientSecret;
    private String display;
    private List<String> keywords;

}
