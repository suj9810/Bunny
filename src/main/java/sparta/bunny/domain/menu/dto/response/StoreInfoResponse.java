package sparta.bunny.domain.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sparta.bunny.domain.stores.entity.Store;

/**
 * 가게 정보 응답
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreInfoResponse {
	private Long storeId;
	private String storeName;

	public static StoreInfoResponse of(Store store) {
		return new StoreInfoResponse(store.getId(), store.getStoreName());
	}
}