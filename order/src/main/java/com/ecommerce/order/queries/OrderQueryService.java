package com.ecommerce.order.queries;

import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Query side of the order context — reads from order_read_model only. Redis-cached.
 */
@Service
@RequiredArgsConstructor
public class OrderQueryService {

    public record OrderItemView(String productId, int quantity, BigDecimal unitPrice) implements java.io.Serializable {
    }

    public record OrderView(String id, String userId, String status, BigDecimal totalAmount,
                            List<OrderItemView> items, String street, String city, String zip,
                            String country, Instant createdAt) implements java.io.Serializable {
    }

    private final SpringDataOrderReadRepository readRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "order", key = "#id")
    public OrderView getOrder(String id) {
        return readRepository.findById(id).map(this::toView)
                .orElseThrow(() -> new NotFoundException("order " + id + " not found"));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orders", key = "'all'")
    public List<OrderView> listOrders() {
        return readRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toView)
                .toList();
    }

    private OrderView toView(OrderReadModel readModel) {
        List<OrderItemView> items;
        try {
            items = List.of(objectMapper.readValue(readModel.getItems(), OrderItemView[].class));
        } catch (Exception e) {
            items = List.of();
        }
        return new OrderView(readModel.getId(), readModel.getUserId(), readModel.getStatus(),
                readModel.getTotalAmount(), items, readModel.getStreet(), readModel.getCity(),
                readModel.getZip(), readModel.getCountry(), readModel.getCreatedAt());
    }
}
