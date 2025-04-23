package sparta.bunny.stores.dto;

import lombok.Getter;

@Getter
public class StoreStatusDto {

    private Boolean isClosed;

    public StoreStatusDto(Boolean isClosed){
        this.isClosed = isClosed;
    }

}
