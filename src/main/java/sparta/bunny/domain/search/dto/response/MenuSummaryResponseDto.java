package sparta.bunny.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuSummaryResponseDto {

    private final Long menuId;
    private final String name;

    public MenuSummaryResponseDto(Long menuId, String menuName) {
        this.menuId = menuId;
        this.name = menuName;
    }


}
