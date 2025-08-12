package kh.devspaceapi.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.devspaceapi.external.news.NaverApiProperty;
import kh.devspaceapi.external.news.NewsApiClient;
import kh.devspaceapi.model.dto.newsPost.NewsItem;
import kh.devspaceapi.model.dto.newsPost.NewsPostResponseDto;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.repository.NewsPostRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


@Component
public class NewFetchScheduler {

    @Autowired
    private NaverApiProperty naverApiProperty;

    @Autowired
    private NewsApiClient newsApiClient;

    @Autowired
    private NewsPostRepository newsPostRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 24시간 마다 네이버 뉴스 API에서 "tech" 뉴스 가져와 DB 저장
     */
//    @Scheduled(fixedDelay = 10000)
    @Scheduled(cron = "0 0 0 * * *")
    public void fetchDailyNews() {
        try {
            // API 호출
            String responseBody = newsApiClient.fetchNews("IT", naverApiProperty);

            // JSON -> DTO 매핑
            NewsPostResponseDto newsPostResponseDto =
                    objectMapper.readValue(responseBody, NewsPostResponseDto.class);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);


                // 본문 가져오기 (네이버 뉴스 페이지 예시)
                for (NewsItem item : newsPostResponseDto.getItems()) {

                    // 중복 체크 (link 기준)
                    if (newsPostRepository.existsByLink(item.getLink())) {
                        continue;  // 이미 있으면 저장하지 않음
                    }

                    NewsPost post = new NewsPost();
                    post.setTitle(item.getTitle());
                    post.setOriginallink(item.getOriginallink());
                    post.setLink(item.getLink());
                    post.setDescription(item.getDescription());
                    post.setCreatedAt(LocalDateTime.now());
                    post.setUpdatedAt(LocalDateTime.now());
                    post.setActive(true);

                    Document doc = Jsoup.connect(item.getLink()).get();

                    String combinedSelector = "div#newsct_article, div#articleBody, .news_body, .article-content, div.contents[itemprop=articleBody], div#article-view-content-div, div#newsEndContents, article#content";
                    Element contentElement = doc.selectFirst(combinedSelector);

                    if (contentElement != null) {
                        // 불필요한 태그 제거
                        contentElement.select("div[style*=text-align:center], script, iframe, noscript, .ad, .advertisement").remove();

                        // 모든 텍스트 가져오기
                        String content = contentElement.text().trim();

                        post.setContent(content);
                    }


                // 대표 이미지 가져오기
                String imageUrl = doc.select("meta[property=og:image]").attr("content");
                post.setImageUrl(imageUrl);



                // 날짜 변환
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(item.getPubDate(), formatter);
                post.setPubDate(zonedDateTime.toLocalDateTime());

                    newsPostRepository.save(post);

                }
        } catch (Exception e) {
            e.printStackTrace(); // 운영에서는 logger.warn/error 로 변경 권장
        }
    }
}