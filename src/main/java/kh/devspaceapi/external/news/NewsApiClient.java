package kh.devspaceapi.external.news;

import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class NewsApiClient {

    /**
     * 네이버 뉴스 API를 호출하여 뉴스 목록 JSON 문자열을 반환합니다.
     *
     * @param query 검색어
     * @param naverApiProperty API 인증 정보
     * @return JSON 문자열
     */
    public String fetchNews(String query, NaverApiProperty naverApiProperty) {
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + encodedQuery;

            System.out.println("Client ID: " + naverApiProperty.getClientId());
            System.out.println("Client Secret: " + naverApiProperty.getClientSecret());

            Map<String, String> requestHeaders = Map.of(
                    "X-Naver-Client-Id", naverApiProperty.getClientId(),
                    "X-Naver-Client-Secret", naverApiProperty.getClientSecret()
            );

            return get(apiURL, requestHeaders);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }
    }

    /**
     * HTTP GET 요청을 수행합니다.
     */
    private String get(String apiUrl, Map<String, String> requestHeaders) {
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else { // 오류 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청/응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    /**
     * URL 연결 생성
     */
    private HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("잘못된 URL: " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결 실패: " + apiUrl, e);
        }
    }

    /**
     * 응답 본문 읽기
     */
    private String readBody(InputStream body) {
        try (BufferedReader lineReader = new BufferedReader(new InputStreamReader(body))) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답 읽기 실패", e);
        }
    }
}
