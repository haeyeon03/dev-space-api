package kh.devspaceapi.external.site;

import kh.devspaceapi.external.api.dto.ContentResult;
import kh.devspaceapi.external.selector.NewsSiteSelector;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class NaverNewsFetcher {
    public ContentResult fetchContent(Document doc) throws IOException {
        String attr = NewsSiteSelector.NAVER_SELECTOR.getAttribute();
        String val = NewsSiteSelector.NAVER_SELECTOR.getValue();

        String cssSelector = String.format("[%s=%s]", attr, val);
        Element contentElement = doc.selectFirst(cssSelector);

        if (contentElement == null) {
            return null;
        }

        // 텍스트
        String text = contentElement.text();

        // 이미지 src 리스트
        List<String> imgUrls = new ArrayList<>();
        Elements imgs = contentElement.select("img");
        for (Element img : imgs) {
            String src = img.attr("data-src");
            if (src.isEmpty()) {
                src = img.attr("src");
            }
            if (!src.isEmpty()) {
                imgUrls.add(src);
            }
        }
        return new ContentResult(text, imgUrls);

    }
}
