package sparta.bunny.stores.dto;

import lombok.Getter;

@Getter
public class StoreRequestDto {

    private final String storeName;
    private final String openTime;
    private final String closeTime;
    private final Integer minOrderPrice;
    private final String notice;
    private final String categoryName;

    // 가게 등록 요청
    public StoreRequestDto(String storeName, String openTime, String closeTime, Integer minOrderPrice, String notice, String categoryName) {
        this.storeName = storeName;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.minOrderPrice = minOrderPrice;
        this.notice = notice;
        this.categoryName = categoryName;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getOpenTime() {
        return openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public Integer getMinOrderPrice() {
        return minOrderPrice;
    }

    public String getNotice() {
        return notice;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
