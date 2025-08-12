package kh.devspaceapi.external.selector;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NewsSiteSelector {
    NAVER_SELECTOR("naver.com", "id", "dic_area"),
    AI_TIMES_SELECTOR("aitimes.kr", "id", "article-view-content-div");
    private final String domain;
    private final String attribute;
    private final String value;
}
