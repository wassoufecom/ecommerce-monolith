package com.ecommerce.inventory.core.repositories;

import com.ecommerce.inventory.core.domain.Stock;

import java.util.Optional;

public interface StockRepository {

    Stock save(Stock stock);

    Optional<Stock> findByProductId(String productId);
}
