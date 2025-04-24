package sparta.bunny.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularKeywordDto {

    private final Long rank;
    private final String keyword;

    public PopularKeywordDto(Long rank, String keyword) {
        this.rank = rank;
        this.keyword = keyword;
    }
}
