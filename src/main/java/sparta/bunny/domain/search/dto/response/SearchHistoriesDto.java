package sparta.bunny.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchHistoriesDto {

    private final String keyword;

    public SearchHistoriesDto(String keyword) {
        this.keyword = keyword;
    }

}
