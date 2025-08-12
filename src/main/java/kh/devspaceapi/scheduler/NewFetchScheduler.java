package kh.devspaceapi.scheduler;

import kh.devspaceapi.external.news.Article;
import kh.devspaceapi.external.news.NewsApiClient;
import kh.devspaceapi.external.news.NewsResponse;
import kh.devspaceapi.model.entity.NewsPost;
import kh.devspaceapi.repository.NewsPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NewFetchScheduler {
    @Autowired
    NewsPostRepository newsPostRepository;
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedDelay = 10000) // 10초
    public void fetchDailyNews() {
        // NewsApiClient 내 fetch() 호출하여 뉴스 API 를 통해 데이터 가져오기
        // NewsResponse data = NewsApiClient.fetch()
        // NewsProcessor.process(data); // 후처리 작업


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        NewsApiClient client = new NewsApiClient();
        // 오늘 날짜
        LocalDate today = LocalDate.now();
        // 어제 날짜 계산
        LocalDate yesterdayDate = today.minusDays(1);
        // 문자열 포맷
//        String yesterday = yesterdayDate.format(formatter);

        String[] keywordArray = {"tech"};
        for(String keyword: keywordArray){
            String yesterday ="2025-08-08";
            String language = "ko";
            String sortBy = "popularity";

            NewsResponse response = client.fetchNews(keyword, yesterday, language, sortBy);
            response.getArticles().forEach(a -> {
                System.out.println(a.getContent());
            });
//
//            if (response != null && "ok".equals(response.getStatus())) {
//                List<Article> articles = response.getArticles();
//                System.out.println("총 " + articles.size() + "개의 뉴스 기사");
//
//                for (Article article : articles) {
//                    // 중복 체크 (URL 기준)
//                    boolean exists = newsPostRepository.existsByUrl(article.getUrl());
//                    if (!exists) {
//                        NewsPost post = new NewsPost();
//                        post.setTitle(article.getTitle());
//                        System.out.println(article.getContent());
//                        post.setContent(article.getContent());
//                        post.setDescription(article.getDescription());
//                        post.setAuthor(article.getAuthor());
//                        post.setSourceName(article.getSource().getName());
//                        post.setUrl(article.getUrl());
//                        post.setUrlToImage(article.getUrlToImage());
//                        post.setPublishedAt(OffsetDateTime.parse(article.getPublishedAt()).toLocalDateTime());
//
//                        post.setActive(true);  // active 1 넣기
//                        LocalDateTime now = LocalDateTime.now();
//                        post.setCreatedAt(now);   // 생성시간 현재 시각
//                        post.setUpdatedAt(now);   // 수정시간 현재 시각
//
////                        newsPostRepository.save(post);
//                    }
//                }
//            } else {
//                System.out.println("뉴스 API 호출 실패 또는 결과 없음");
//            }

        }
    }
}
