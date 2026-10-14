package com.ecommerce.inventory.data.implementations;

import com.ecommerce.inventory.core.domain.Stock;
import com.ecommerce.inventory.core.repositories.StockRepository;
import com.ecommerce.inventory.data.jpa.SpringDataInventoryRepository;
import com.ecommerce.inventory.data.mappers.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StockRepositoryImpl implements StockRepository {

    private final SpringDataInventoryRepository springData;

    @Override
    public Stock save(Stock stock) {
        springData.save(InventoryMapper.toJpa(stock));
        return stock;
    }

    @Override
    public Optional<Stock> findByProductId(String productId) {
        return springData.findById(productId).map(InventoryMapper::toDomain);
    }
}
