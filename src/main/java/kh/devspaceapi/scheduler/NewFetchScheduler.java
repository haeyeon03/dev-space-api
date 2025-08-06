package kh.devspaceapi.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewFetchScheduler {

//    @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(fixedDelay = 10000) // 10초
    public void fetchDailyNews() {
        // NewsApiClient 내 fetch() 호출하여 뉴스 API 를 통해 데이터 가져오기
        // NewsResponse data = NewsApiClient.fetch()
        // NewsProcessor.process(data); // 후처리 작업
    }
}
