package sparta.bunny.domain.stores.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.common.response.CommonResponses;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.stores.code.StoreSuccessCode;
import sparta.bunny.domain.stores.dto.response.StoreResponseDto;
import sparta.bunny.domain.stores.dto.response.StoreWithMenuResponseDto;
import sparta.bunny.domain.stores.service.UserStoreService;

@RestController
@RequestMapping("/user/store")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserStoreController {

	private final UserStoreService userStoreService;

	/**
	 * 전체 가게 조회
	 * @param userDetails 로그인한 사용자 정보
	 * @param categories 카테고리별로 나타내기
	 * @param page 페이지 번호
	 * @param size 한 페이지당 몇개의 가게 보여줄지
	 * @return 가게 목록
	 */
	@GetMapping
	public ResponseEntity<CommonResponses<StoreResponseDto>> getStores(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam(required = false) String categories,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		Pageable pageable = PageRequest.of(page, size);

		Page<StoreResponseDto> storeList = userStoreService.getStores(categories, pageable);

		return ResponseEntity.ok(CommonResponses.of(StoreSuccessCode.STORE_FETCH_ALL_SUCCESS, storeList));
	}

	/**
	 * 단일 가게 조회(메뉴 포함)
	 * @param storeId 조회할 가게 Id
	 * @param userDetails 로그인한 사용자 정보
	 * @return 해당 가게 및 메뉴 정보 응답
	 */
	@GetMapping("/{storeId}")
	public ResponseEntity<CommonResponse<StoreWithMenuResponseDto>> getStoreWithMenus(
		@PathVariable Long storeId,
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		StoreWithMenuResponseDto storeWithMenus = userStoreService.getStoreWithMenus(storeId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(CommonResponse.of(StoreSuccessCode.STORE_FETCH_SUCCESS, storeWithMenus));
	}
}
