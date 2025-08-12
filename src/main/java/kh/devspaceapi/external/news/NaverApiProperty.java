package kh.devspaceapi.external.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "api.naver")
@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NaverApiProperty {
    private String host;
    private String uri;
    private String clientId;
    private String clientSecret;
}
