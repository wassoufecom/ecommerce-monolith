package com.ecommerce.catalog.presenters.rest;

import com.ecommerce.catalog.commands.CreateCategoryCommand;
import com.ecommerce.catalog.commands.CreateCategoryCommandHandler;
import com.ecommerce.catalog.commands.CreateProductCommand;
import com.ecommerce.catalog.commands.CreateProductCommandHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Catalog — commands")
public class CatalogCommandController {

    private final CreateProductCommandHandler createProductCommandHandler;
    private final CreateCategoryCommandHandler createCategoryCommandHandler;

    @Operation(summary = "Create a product (title non-empty, price > 0, categories optional)")
    @PostMapping("/product/create")
    public ResponseEntity<Map<String, String>> createProduct(@Valid @RequestBody CreateProductCommand command) {
        String productId = createProductCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("productId", productId));
    }

    @Operation(summary = "Create a category (name unique and non-empty)")
    @PostMapping("/category/create")
    public ResponseEntity<Map<String, String>> createCategory(@Valid @RequestBody CreateCategoryCommand command) {
        String categoryId = createCategoryCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("categoryId", categoryId));
    }
}
