package sparta.bunny.domain.search.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SearchRequestDto {

    @NotBlank
    @Size(max = 25, message = "글자 수는 25자까지 가능합니다.")
    private String keyword;
}
