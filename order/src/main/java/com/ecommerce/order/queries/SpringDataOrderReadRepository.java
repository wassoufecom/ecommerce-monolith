package com.ecommerce.order.queries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOrderReadRepository extends JpaRepository<OrderReadModel, String> {

    List<OrderReadModel> findAllByOrderByCreatedAtDesc();
}
