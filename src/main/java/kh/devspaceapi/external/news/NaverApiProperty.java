package kh.devspaceapi.external.news;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "api.naver")
@Component
public class NaverApiProperty {
    private String host;
    private String uri;
    private String clientId;
    private String clientSecret;
}
