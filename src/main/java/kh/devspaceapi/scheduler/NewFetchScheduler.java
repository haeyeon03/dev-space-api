package kh.devspaceapi.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.devspaceapi.external.site.AiTimesNewsFetcher;
import kh.devspaceapi.external.site.NaverNewsFetcher;
import kh.devspaceapi.external.api.NaverNewsApiProperty;
import kh.devspaceapi.external.api.NewsNewsApiClient;
import kh.devspaceapi.external.api.dto.ContentResult;
import kh.devspaceapi.external.api.dto.NaverNewsApiResponseDto;
import kh.devspaceapi.external.api.dto.NaverNewsApiItem;
import kh.devspaceapi.external.selector.NewsSiteSelector;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.repository.NewsPostRepository;

import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class NewFetchScheduler {
    private final NewsNewsApiClient newsNewsApiClient;
    private final NewsPostRepository newsPostRepository;
    private final NaverNewsFetcher naverNewsFetcher;
    private final AiTimesNewsFetcher aiTimesNewsFetcher;
    private final ObjectMapper objectMapper;
    private final NaverNewsApiProperty props;

    /**
     * 24시간 마다 네이버 뉴스 API에서 "IT" 뉴스 가져와 DB 저장
     */
    @Scheduled(fixedDelay = 1000000) // 24시간 간격 (ms)
    public void fetchDailyNews() {
        log.info("[Scheduler] 뉴스 스케줄러 시작");

        for (String Keyword : props.getKeywords()) {
            log.info("[Scheduler] Keyword: {}", Keyword);
            String responseBody = newsNewsApiClient.fetchNews(Keyword);

            NaverNewsApiResponseDto newsResponse;
            try {
                newsResponse = objectMapper.readValue(responseBody, NaverNewsApiResponseDto.class);
            } catch (JsonProcessingException e) {
                log.error("[Scheduler] 뉴스 API 응답 JSON 파싱 실패", e);
                continue;
            }

            List<NewsPost> entityList = new ArrayList<>();

            for (NaverNewsApiItem item : newsResponse.getItems()) {
                if (!StringUtils.hasText(item.getLink())) {
                    continue;
                }

                try {
                    if (item.getLink().contains(NewsSiteSelector.NAVER_SELECTOR.getDomain())) {
                        Document doc = Jsoup.connect(item.getLink()).get();
                        ContentResult contentResult = naverNewsFetcher.fetchContent(doc);
                        if (contentResult == null || !StringUtils.hasText(contentResult.getContentText())) {
                            continue;
                        }

                        NewsPost entity = buildNewsPost(item, contentResult);
                        entityList.add(entity);
                    } else if (item.getLink().contains(NewsSiteSelector.AI_TIMES_SELECTOR.getDomain())) {
                        Document doc = Jsoup.connect(item.getLink()).get();
                        ContentResult contentResult = aiTimesNewsFetcher.fetchContent(doc);
                        if (contentResult == null || !StringUtils.hasText(contentResult.getContentText())) {
                            continue;
                        }

                        NewsPost entity = buildNewsPost(item, contentResult);
                        entityList.add(entity);
                    }


                } catch (Exception e) {
                    log.warn("[Scheduler] 본문 크롤링 실패 URL: {}", item.getLink(), e);
                }
            }

            if (entityList.isEmpty()) {
                log.info("[Scheduler] 신규 수집 뉴스 없음");
                continue;
            }

            // DB 저장 (업데이트 또는 신규 삽입)
            for (NewsPost entity : entityList) {
                try {
                    upsertNewsPost(entity);
                } catch (Exception e) {
                    log.error("[Scheduler] 뉴스 저장 실패. URL: {}", entity.getUrl(), e);
                }
            }
        }
        log.info("[Scheduler] 뉴스 스케줄러 종료");
    }

    private NewsPost buildNewsPost(NaverNewsApiItem item, ContentResult contentResult) {
        NewsPost entity = new NewsPost();
        entity.setTitle(item.getTitle());
        entity.setContent(contentResult.getContentText());
        entity.setUrl(item.getLink());

        if (contentResult.getImageUrls() != null && !contentResult.getImageUrls().isEmpty()) {
            entity.setImageUrl(contentResult.getImageUrls().get(0));
        } else {
            entity.setImageUrl(null);
        }

        entity.setPubDate(item.getPubDate());
        return entity;
    }

    private void upsertNewsPost(NewsPost entity) {
        NewsPost existing = newsPostRepository.findByUrl(entity.getUrl());

        if (existing != null) {
            // 기존 엔티티 업데이트할 필드들
            existing.setTitle(entity.getTitle());
            existing.setContent(entity.getContent());
            existing.setImageUrl(entity.getImageUrl());
            existing.setPubDate(entity.getPubDate());

            newsPostRepository.save(existing);
            log.info("뉴스 업데이트: {}", entity.getUrl());
        } else {
            newsPostRepository.save(entity);
            log.info("뉴스 신규 삽입: {}", entity.getUrl());
        }
    }
}