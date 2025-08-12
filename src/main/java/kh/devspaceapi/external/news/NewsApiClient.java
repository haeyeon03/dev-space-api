package kh.devspaceapi.external.news;

import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

//@Component
//public class NewsApiClient {
//
//    private final String apiKey = "8c713eb2010344a4aefe3cd3de0b508b"; // 설정파일로 분리 권장
//
//    public NewsResponse fetchNews(String keyword, String fromDate, String language, String sortBy) {
//        String url = "https://newsapi.org/v2/everything?q=" + keyword
//                + "&from=" + fromDate
//                + "&language=" + language
//                + "&sortBy=" + sortBy
//                + "&apiKey=" + apiKey;
//
//        // RestTemplate 인스턴스 생성
//        RestTemplate restTemplate = new RestTemplate();
//
//        // API 요청에 필요한 헤더 (X-Api-Key)
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Api-Key", apiKey);
//
//        // 헤더와 함께 GET 요청 전송
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        // 요청 보내고 JSON 응답을 문자열로 받기
//        NewsResponse response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                entity,
//                NewsResponse.class
//        ).getBody();
//
//        return response;
//    }
//}

