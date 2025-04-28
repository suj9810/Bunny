package sparta.bunny.domain.stores.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import sparta.bunny.domain.stores.entity.Category;

@Getter
public class StoreRequestDto {

	@NotBlank(message = "가게 이름은 필수입니다.")
	private String storeName;

	@NotBlank(message = "오픈시간 작성은 필수입니다.")
	private String openTime;

	@NotBlank(message = "문닫는 시간 작성은 필수입니다.")
	private String closeTime;

	@NotNull(message = "최소금액 작성은 필수입니다.")
	@Min(value = 10000, message = "최소 주문 금액은 10000원 이상이어야 합니다.")
	private Integer minOrderPrice;

	@NotBlank(message = "공지작성은 필수입니다.")
	private String notice;

	@NotNull(message = "카테고리 작성은 필수입니다.")
	private Category categoryName;

	// 가게 등록 요청
	@Builder
	public StoreRequestDto(String storeName, String openTime, String closeTime, Integer minOrderPrice, String notice,
		Category categoryName) {
		this.storeName = storeName;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.minOrderPrice = minOrderPrice;
		this.notice = notice;
		this.categoryName = categoryName;
	}

}
