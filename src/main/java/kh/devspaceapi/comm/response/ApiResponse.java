package kh.devspaceapi.comm.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class ApiResponse<T> {
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success() {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.OK.value()); // = 200
        apiResponse.setCode("SUCCESS");
        apiResponse.setMessage("요청이 성공적으로 완료되었습니다.");
        return apiResponse;
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.OK.value()); // = 200
        apiResponse.setCode("SUCCESS");
        apiResponse.setMessage("요청이 성공적으로 완료되었습니다.");
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(HttpStatus.BAD_REQUEST.value()); // = 400
        apiResponse.setCode(code);
        apiResponse.setMessage(message);
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(int status, String code, String message) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setStatus(status); // = 400
        apiResponse.setCode(code);
        apiResponse.setMessage(message);
        return apiResponse;
    }
}
