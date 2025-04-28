package sparta.bunny.domain.stores.dto.response;

import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sparta.bunny.domain.menu.dto.response.MenuResponse;
import sparta.bunny.domain.stores.entity.Category;

@Getter
@Builder
@AllArgsConstructor
public class StoreWithMenuResponseDto {
	private final Long id;
	private final String storeName;
	private final LocalTime openTime;
	private final LocalTime closeTime;
	private final Integer minOrderPrice;
	private final String notice;
	private final Boolean isClosed;
	private final Category categoryName;
	private final List<MenuResponse> menu;
}
