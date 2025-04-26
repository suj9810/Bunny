package sparta.bunny.domain.stores.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.stores.code.StoreSuccessCode;
import sparta.bunny.domain.stores.dto.request.StoreRequestDto;
import sparta.bunny.domain.stores.dto.request.StoreStatusDto;
import sparta.bunny.domain.stores.dto.response.StoreResponseDto;
import sparta.bunny.domain.stores.dto.response.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.service.OwnerStoreService;
import sparta.bunny.domain.user.entity.User;

@RestController
@RequestMapping("/owner/store")
@RequiredArgsConstructor
public class OwnerStoreController {

	private final OwnerStoreService ownerStoreService;

	/**
	 *  가게 등록
	 * @param requestDto 가게 등록에 필요한 정보
	 * @param userDetails 인증된 사용자 정보
	 * @return 가게 생성 성공 응답
	 */
	@PostMapping
	public ResponseEntity<CommonResponse<Void>> createStore(@RequestBody @Valid StoreRequestDto requestDto,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		ownerStoreService.createStore(requestDto, userDetails.getUser());
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(CommonResponse.of(StoreSuccessCode.STORE_CREATE_SUCCESS, null));
	}

	/**
	 * 가게 수정
	 * @param storeId 수정할 가게 ID
	 * @param requestDto 수정할 가게 정보
	 * @param userDetails 인증된 사용자 정보
	 * @return 가게 수정 성공 응답
	 */
	@PutMapping("/{storeId}")
	public ResponseEntity<CommonResponse<Void>> updateStore(@PathVariable Long storeId,
		@RequestBody @Valid StoreRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		ownerStoreService.updateStore(storeId, requestDto, userDetails.getUser());
		return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_UPDATE_SUCCESS, null));
	}

	/**
	 * 전체 가게 조회
	 * @param categories 카테고리별로 구분
	 * @param page 페이지 번호
	 * @param size 한 페이지당 몇개의 가게 보여줄지
	 * @param userDetails 인증된 사용자 정보
	 * @return 가게 목록과 페이지 정보
	 */
	@GetMapping
	public ResponseEntity<CommonResponse<Map<String, Object>>> getAllStores(
		@RequestParam(required = false) String categories,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@AuthenticationPrincipal UserDetailsImpl userDetails) {

		Pageable pageable = PageRequest.of(page, size);
		Page<StoreResponseDto> storeList = ownerStoreService.getStoresByCategory(userDetails.getUser(), categories,
			pageable);

		Map<String, Object> data = new HashMap<>();
		data.put("totalElements", storeList.getTotalElements()); // 총 갯수
		data.put("totalPages", storeList.getTotalPages()); // 총 페이지 갯수
		data.put("hasNextPage", storeList.hasNext());  // 다음 페이지 여부
		data.put("hasPreviousPage", storeList.hasPrevious());  // 이전 페이지 여부
		data.put("content", storeList.getContent());

		return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_FETCH_ALL_SUCCESS, data));
	}

	/**
	 * 단일 가게 조회 (메뉴 포함)
	 * @param storeId 조회할 가게 Id
	 * @param user 인증된 사용자 정보
	 * @return 가게 정보 및 메뉴 목록
	 */
	@GetMapping("/owner/stores/{storeId}")
	public ResponseEntity<StoreWithMenuResponseDto> getStoreWithMenus(
		@PathVariable Long storeId,
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(ownerStoreService.getStoreWithMenus(storeId, user));
	}

	/**
	 * 가게 폐업 처리
	 * @param storeId 폐업할 가게 Id
	 * @param storeStatusDto 가게 폐업 상태 정보
	 * @param userDetails 인증된 사용자 정보
	 * @return 폐업 처리 성공 응답
	 */
	@PatchMapping("/{storeId}")
	public ResponseEntity<CommonResponse<Void>> closeStore(@PathVariable Long storeId,
		@RequestBody @Valid StoreStatusDto storeStatusDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
		ownerStoreService.closeStore(storeId, storeStatusDto.getIsClosed(), userDetails.getUser());
		if (storeStatusDto.getIsClosed()) {
			return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_CLOSE_SUCCESS, null));
		} else {
			return ResponseEntity.ok(CommonResponse.of(StoreSuccessCode.STORE_CLOSE_CANCELLATION_SUCCESS, null));
		}
	}

}

