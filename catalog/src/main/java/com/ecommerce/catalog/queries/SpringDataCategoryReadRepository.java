package com.ecommerce.catalog.queries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataCategoryReadRepository extends JpaRepository<CategoryReadModel, String> {

    List<CategoryReadModel> findAllByOrderByNameAsc();
}
