package sparta.bunny.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchHistoriesListDto {

    private final List<SearchHistoriesDto> searchHistoriesDtoList;

    public SearchHistoriesListDto(final List<SearchHistoriesDto> list) {
        this.searchHistoriesDtoList = list;
    }
}
