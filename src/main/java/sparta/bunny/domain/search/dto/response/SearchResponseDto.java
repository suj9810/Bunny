package sparta.bunny.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sparta.bunny.domain.stores.entity.Store;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SearchResponseDto {

    // 가게 정보
    private final Long storeId;
    private final String storeName;
    private final Integer minOrderPlace;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final boolean isClosed;

    // 가게 메뉴 정보
    private final List<MenuSummaryResponseDto> menuList;

    public SearchResponseDto(Store store, List<MenuSummaryResponseDto> list) {
        this.storeId = store.getId();
        this.storeName = store.getStoreName();
        this.minOrderPlace = store.getMinOrderPrice();
        this.openTime = store.getOpenTime();
        this.closeTime = store.getCloseTime();
        this.isClosed = store.getIsClosed();
        this.menuList = list;
    }

}
