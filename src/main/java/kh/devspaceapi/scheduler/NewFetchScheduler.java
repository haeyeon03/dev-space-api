package kh.devspaceapi.scheduler;

import kh.devspaceapi.external.news.Article;
import kh.devspaceapi.external.news.NewsApiClient;
import kh.devspaceapi.external.news.NewsResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewFetchScheduler {

//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedDelay = 10000) // 10초
    public void fetchDailyNews() {
        // NewsApiClient 내 fetch() 호출하여 뉴스 API 를 통해 데이터 가져오기
        // NewsResponse data = NewsApiClient.fetch()
        // NewsProcessor.process(data); // 후처리 작업

        NewsApiClient client = new NewsApiClient();

        String keyword = "tech";
        String yesterday = "2025-07-22";
        String language = "ko";
        String sortBy = "popularity";

        NewsResponse response = client.fetchNews(keyword, yesterday, language, sortBy);

        if (response != null && "ok".equals(response.getStatus())) {
            List<Article> articles = response.getArticles();
            System.out.println("총 " + articles.size() + "개의 뉴스 기사");
            for (Article article : articles) {
                System.out.println(article.toString());
            }
        } else {
            System.out.println("뉴스 API 호출 실패 또는 결과 없음");
        }

    }
}
