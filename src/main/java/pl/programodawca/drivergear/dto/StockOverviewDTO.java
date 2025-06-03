package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.programodawca.drivergear.model.StockLevel;
import pl.programodawca.drivergear.model.WarehouseInventory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOverviewDTO {
    private String clothingTypeName;
    private int quantity;
    private StockLevel level;

    public static StockOverviewDTO fromEntity(WarehouseInventory inventory) {
        return StockOverviewDTO.builder()
                .clothingTypeName(inventory.getClothingType().getName())
                .quantity(inventory.getQuantity())
                .level(StockLevel.fromQuantity(inventory.getQuantity()))
                .build();
    }
}
