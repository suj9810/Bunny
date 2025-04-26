package sparta.bunny.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sparta.bunny.domain.order.dto.ChangeOrderStatusRequestDto;
import sparta.bunny.domain.order.dto.OrderRequestDto;
import sparta.bunny.domain.order.dto.OrderResponseDto;
import sparta.bunny.domain.order.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성
     * @param userId
     * @return
     */
    @PostMapping("/{userId}")
    public ResponseEntity<OrderResponseDto> createOrder(
            @PathVariable Long userId
    ){
        return ResponseEntity.ok(orderService.createOrder(userId));
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/{orderId}")
    public void changeOrderStatus(@PathVariable Long orderId, @RequestBody ChangeOrderStatusRequestDto requestDto){
        orderService.changeOrderStatus(orderId, requestDto);
    }
}
