package sparta.bunny.domain.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sparta.bunny.domain.auth.jwt.UserDetailsImpl;
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
	public ResponseEntity<OrderResponseDto> createOrder(
		@AuthenticationPrincipal UserDetailsImpl userDetails
	) {
		return ResponseEntity.ok(orderService.createOrder(userDetails.getUser().getId()));
	}

	/**
	 * 사용자별 주문 내역 조회
	 * @param userDetails 사용자 id
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getOrderList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		return ResponseEntity.ok(orderService.getOrderList(userDetails.getUser().getId()));
	}

	/**
	 * 사용자별 주문 단건 조회
	 * @param userDetails
	 * @return
	 */
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderResponseDto> getOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@PathVariable Long orderId) {
		return ResponseEntity.ok(orderService.getOrder(userDetails.getUser().getId(), orderId));
	}

	/**
	 * 주문 상태 변경
	 */
	@PreAuthorize("hasRole('OWNER')")
	@PatchMapping("/{orderId}")
	public ResponseEntity<OrderResponseDto> changeOrderStatus(
		@PathVariable Long orderId,
		@RequestBody ChangeOrderStatusRequestDto requestDto
	) {
		return ResponseEntity.ok(orderService.changeOrderStatus(orderId, requestDto));
	}
}
