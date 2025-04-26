package sparta.bunny.domain.stores.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreStatusDto {

	@NotNull(message = "폐업여부는 필수입니다.")
	private Boolean isClosed;

	public StoreStatusDto(Boolean isClosed) {
		this.isClosed = isClosed;
	}

}
