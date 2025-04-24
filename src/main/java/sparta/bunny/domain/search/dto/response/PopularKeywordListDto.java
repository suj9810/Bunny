package sparta.bunny.domain.search.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopularKeywordListDto {

    private final List<PopularKeywordDto> popularKeywordDtoList;

    public PopularKeywordListDto(List<PopularKeywordDto> list) {
        this.popularKeywordDtoList = list;
    }
}
