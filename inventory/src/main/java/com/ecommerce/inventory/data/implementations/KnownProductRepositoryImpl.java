package com.ecommerce.inventory.data.implementations;

import com.ecommerce.inventory.core.repositories.KnownProductRepository;
import com.ecommerce.inventory.data.jpa.KnownProductJpaEntity;
import com.ecommerce.inventory.data.jpa.SpringDataKnownProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KnownProductRepositoryImpl implements KnownProductRepository {

    private final SpringDataKnownProductRepository springData;

    @Override
    public boolean exists(String productId) {
        return springData.existsById(productId);
    }

    @Override
    public void save(String productId, String title) {
        KnownProductJpaEntity entity = new KnownProductJpaEntity();
        entity.setProductId(productId);
        entity.setTitle(title);
        springData.save(entity);
    }
}
