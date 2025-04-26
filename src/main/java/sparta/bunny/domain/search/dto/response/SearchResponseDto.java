package sparta.bunny.domain.search.dto.response;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sparta.bunny.domain.stores.entity.Store;

@Getter
@Builder
@AllArgsConstructor
public class SearchResponseDto {

	// 가게 정보
	private final Long storeId;
	private final String storeName;
	private final Integer minOrderPrice;
	private final LocalTime openTime;
	private final LocalTime closeTime;
	private final boolean isClosed;

	// 가게의 메뉴 정보
	private final List<MenuSummaryResponseDto> menus;

	public SearchResponseDto(Store store, List<MenuSummaryResponseDto> menus) {
		this.storeId = store.getId();
		this.storeName = store.getStoreName();
		this.minOrderPrice = store.getMinOrderPrice();
		this.openTime = store.getOpenTime();
		this.closeTime = store.getCloseTime();
		this.isClosed = store.getIsClosed();
		this.menus = menus;
	}

}
