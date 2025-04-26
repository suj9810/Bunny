package sparta.bunny.domain.stores.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.stores.code.StoreExceptionCode;
import sparta.bunny.domain.stores.code.StoreSuccessCode;
import sparta.bunny.domain.stores.dto.StoreRequestDto;
import sparta.bunny.domain.stores.dto.StoreResponseDto;
import sparta.bunny.domain.stores.dto.StoreStatusDto;
import sparta.bunny.domain.stores.service.OwnerStoreService;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class OwnerStoreController {

	private final OwnerStoreService ownerStoreService;

	// 가게 등록
	@PreAuthorize("hasRole('OWNER')")
	@PostMapping
	public ResponseEntity<CommonResponse<Void>> createStore(@RequestBody @Valid StoreRequestDto requestDto) {
		ownerStoreService.createStore(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(StoreSuccessCode.STORE_CREATE_SUCCESS, null));
	}

	// 가게 수정
	@PutMapping("/{storeId}")
	public ResponseEntity<CommonResponse<Void>> updateStore(@PathVariable Long storeId,
		@RequestBody @Valid StoreRequestDto requestDto) {
		try {
			ownerStoreService.updateStore(storeId, requestDto);
			return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_UPDATE_SUCCESS, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(CommonResponse.of(StoreExceptionCode.STORE_NOT_FOUND, null));
		}
	}

	// 전체 가게 조회
	@GetMapping
	public ResponseEntity<CommonResponse<List<StoreResponseDto>>> getAllStores(
		@RequestParam(required = false) String categories, @RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		List<StoreResponseDto> storeList = ownerStoreService.getAllStores(categories, page, size);
		return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_FETCH_ALL_SUCCESS, storeList));
	}

	// 가게 폐업 처리
	@PatchMapping("/{storeId}")
	public ResponseEntity<CommonResponse<Void>> closeStore(@PathVariable Long storeId,
		@RequestBody @Valid StoreStatusDto storeStatusDto) {
		try {
			ownerStoreService.closeStore(storeId, storeStatusDto.getIsClosed());
			return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_CLOSE_SUCCESS, null));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(CommonResponse.of(StoreExceptionCode.UNAUTHORIZED_ACCESS, null));
		}
	}

}

