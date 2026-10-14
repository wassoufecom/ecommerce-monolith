package com.ecommerce.delivery.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataOrderAddressSnapshotRepository extends JpaRepository<OrderAddressSnapshotJpaEntity, String> {
}
