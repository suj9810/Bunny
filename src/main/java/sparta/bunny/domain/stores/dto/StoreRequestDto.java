package sparta.bunny.domain.stores.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StoreRequestDto {

	@NotBlank(message = "가게 이름은 필수입니다.")
	private final String storeName;

	@NotBlank(message = "오픈시간 작성은 필수입니다.")
	private final String openTime;

	@NotBlank(message = "문닫는 시간 작성은 필수입니다.")
	private final String closeTime;

	@NotBlank(message = "최소금액 작성은 필수입니다.")
	private final Integer minOrderPrice;

	@NotBlank(message = "공지작성은 필수입니다.")
	private final String notice;

	@NotBlank(message = "카테고리작성은 필수입니다.")
	private final String categoryName;

	// 가게 등록 요청
	public StoreRequestDto(String storeName, String openTime, String closeTime, Integer minOrderPrice, String notice,
		String categoryName) {
		this.storeName = storeName;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.minOrderPrice = minOrderPrice;
		this.notice = notice;
		this.categoryName = categoryName;
	}

}
