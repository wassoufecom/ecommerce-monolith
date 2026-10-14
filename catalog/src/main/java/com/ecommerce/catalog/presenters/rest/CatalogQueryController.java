package com.ecommerce.catalog.presenters.rest;

import com.ecommerce.catalog.queries.ProductQueryService;
import com.ecommerce.catalog.queries.ProductQueryService.CategoryView;
import com.ecommerce.catalog.queries.ProductQueryService.ProductView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Catalog — queries")
public class CatalogQueryController {

    private final ProductQueryService productQueryService;

    @Operation(summary = "List all products (query side, Redis-cached)")
    @GetMapping("/products")
    public List<ProductView> listProducts() {
        return productQueryService.listProducts();
    }

    @Operation(summary = "Get one product by id (query side, Redis-cached)")
    @GetMapping("/product/{id}")
    public ProductView getProduct(@PathVariable String id) {
        return productQueryService.getProduct(id);
    }

    @Operation(summary = "List all categories (query side, Redis-cached)")
    @GetMapping("/categories")
    public List<CategoryView> listCategories() {
        return productQueryService.listCategories();
    }
}
