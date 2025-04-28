package sparta.bunny.domain.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.common.response.CommonResponse;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
import sparta.bunny.domain.order.code.OrderSuccessCode;
import sparta.bunny.domain.order.dto.ChangeOrderStatusRequestDto;
import sparta.bunny.domain.order.dto.OrderResponseDto;
import sparta.bunny.domain.order.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	/**
	 * 주문 생성
	 * @param userDetails 사용자 id
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<CommonResponse<OrderResponseDto>> createOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		OrderResponseDto orderResponseDto = orderService.createOrder(userDetails.getUser().getId());
		return ResponseEntity.ok(
			CommonResponse.of(OrderSuccessCode.ORDER_CREATE_SUCCESS, orderResponseDto)
		);
	}

	/**
	 * 사용자별 주문 내역 조회
	 * @param userDetails 사용자 id
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<CommonResponse<List<OrderResponseDto>>> getOrderList(
		@AuthenticationPrincipal UserDetailsImpl userDetails) {
		List<OrderResponseDto> orderList = orderService.getOrderList(userDetails.getUser().getId());
		return ResponseEntity.ok(
			CommonResponse.of(OrderSuccessCode.ORDER_LIST_GET_SUCCESS, orderList)
		);
	}

	/**
	 * 사용자별 주문 단건 조회
	 * @param userDetails
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponseDto>> getOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long orderId
	) {
		OrderResponseDto orderResponseDto = orderService.getOrder(userDetails.getUser().getId(), orderId);
		return ResponseEntity.ok(
			CommonResponse.of(OrderSuccessCode.ORDER_GET_SUCCESS, orderResponseDto)
		);
	}

	/**
	 * 주문 취소
	 * @param userDetails
	 * @param orderId
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{orderId}")
	public ResponseEntity<CommonResponse<Void>> deleteOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long orderId
	) {
		orderService.deleteOrder(userDetails.getUser().getId(), orderId);
		return ResponseEntity.ok(
			CommonResponse.of(OrderSuccessCode.ORDER_CANCEL_SUCCESS, null)
		);
	}

	/**
	 * 주문 상태 변경
	 */
	@PreAuthorize("hasRole('OWNER')")
	@PatchMapping("/{orderId}")
	public ResponseEntity<CommonResponse<OrderResponseDto>> changeOrderStatus(
		@PathVariable Long orderId,
		@RequestBody ChangeOrderStatusRequestDto requestDto
	) {
		OrderResponseDto orderResponseDto = orderService.changeOrderStatus(orderId, requestDto);
		return ResponseEntity.ok(
			CommonResponse.of(OrderSuccessCode.ORDER_STATUS_UPDATE_SUCCESS, orderResponseDto)
		);
	}
}
