package kh.devspaceapi.external.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NewsNewsApiClient {
	private final NaverNewsApiProperty props;

	/**
	 * 네이버 뉴스 API를 호출하여 뉴스 목록 JSON 문자열을 반환합니다.
	 *
	 * @param query 검색어
	 * @return JSON 문자열
	 */
	public String fetchNews(String query) {
		String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
		String url = props.getHost() + props.getUri() + "?query=" + encodedQuery + "&display" + props.getDisplay();

		Map<String, String> requestHeaders = Map.of("X-Naver-Client-Id", props.getClientId(), "X-Naver-Client-Secret",
				props.getClientSecret());
		return get(url, requestHeaders);
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
