package sparta.bunny.stores.dto;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class StoreResponseDto {
    private final Long id;
    private final String storeName;
    private final LocalTime openTime;
    private final LocalTime closeTime;
    private final Integer minOrderPrice;
    private final String notice;
    private final Boolean isClosed;
    private final String categoryName;

    public StoreResponseDto(Long id, String storeName, LocalTime openTime, LocalTime closeTime, Integer minOrderPrice, String notice, Boolean isClosed, String categoryName) {
        this.id = id;
        this.storeName = storeName;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.minOrderPrice = minOrderPrice;
        this.notice = notice;
        this.isClosed = isClosed;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public String getStoreName() {
        return storeName;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public Integer getMinOrderPrice() {
        return minOrderPrice;
    }

    public String getNotice() {
        return notice;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
