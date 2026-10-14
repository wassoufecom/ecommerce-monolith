package com.ecommerce.inventory.presenters.rest;

import com.ecommerce.inventory.commands.AddStockCommand;
import com.ecommerce.inventory.commands.AddStockCommandHandler;
import com.ecommerce.inventory.queries.InventoryQueryService;
import com.ecommerce.inventory.queries.InventoryQueryService.StockView;
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
@Tag(name = "Inventory")
public class InventoryController {

    private final AddStockCommandHandler addStockCommandHandler;
    private final InventoryQueryService inventoryQueryService;

    @Operation(summary = "Add stock for a product (product exists, quantity > 0)")
    @PostMapping("/inventory/add")
    public ResponseEntity<Map<String, String>> addStock(@Valid @RequestBody AddStockCommand command) {
        addStockCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "stock added", "productId", command.productId()));
    }

    @Operation(summary = "List stock levels (query side, Redis-cached)")
    @GetMapping("/inventory")
    public List<StockView> listStock() {
        return inventoryQueryService.listStock();
    }

    @Operation(summary = "Get stock for one product (query side, Redis-cached)")
    @GetMapping("/inventory/{productId}")
    public StockView getStock(@PathVariable String productId) {
        return inventoryQueryService.getStock(productId);
    }
}
