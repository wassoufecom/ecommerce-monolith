package com.ecommerce.order.presenters.rest;

import com.ecommerce.order.commands.CreateOrderCommand;
import com.ecommerce.order.commands.CreateOrderCommandHandler;
import com.ecommerce.order.queries.OrderQueryService;
import com.ecommerce.order.queries.OrderQueryService.OrderView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Order")
public class OrderController {

    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final OrderQueryService orderQueryService;

    @Operation(summary = "Create an order (>= 1 item, price > 0, address complete) — triggers the saga")
    @PostMapping("/order/create")
    public ResponseEntity<Map<String, String>> createOrder(@Valid @RequestBody CreateOrderCommand command) {
        String orderId = createOrderCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("orderId", orderId));
    }

    @Operation(summary = "Get an order by id (query side, Redis-cached)")
    @GetMapping("/order/{orderId}")
    public OrderView getOrder(@PathVariable String orderId) {
        return orderQueryService.getOrder(orderId);
    }

    @Operation(summary = "List all orders (query side, Redis-cached)")
    @GetMapping("/orders")
    public List<OrderView> listOrders() {
        return orderQueryService.listOrders();
    }
}
