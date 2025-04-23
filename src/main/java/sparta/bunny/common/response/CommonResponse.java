package sparta.bunny.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse<T> {
	private String status;
	private String message;
	private T data;

	// 성공
	public static <T> CommonResponse<T> of(ResponseCode responseCode, T result) {
		return CommonResponse.<T>builder()
			.status(responseCode.getCode())
			.message(responseCode.getMessage())
			.data(result)
			.build();
	}

	// 실패
	public static <T> CommonResponse<T> of(String status, String message) {
		return CommonResponse.<T>builder()
			.status(status)
			.message(message)
			.build();
	}

	// 실패응답
	public static <T> CommonResponse<T> from(ResponseCode responseCode) {
		return CommonResponse.<T>builder()
			.status(responseCode.getCode())
			.message(responseCode.getMessage())
			.build();
	}
}
