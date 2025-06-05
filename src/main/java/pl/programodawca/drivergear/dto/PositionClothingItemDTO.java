package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.PositionClothingItem;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class PositionClothingItemDTO {
    private Long id;
    
    private Long positionClothingAllowanceId;
    
    @NotNull(message = "Typ odzieży jest wymagany")
    private Long clothingTypeId;
    private String clothingTypeName;
    
    @NotNull(message = "Ilość jest wymagana")
    @Min(value = 1, message = "Ilość musi być większa od 0")
    private Integer quantity;
    
    @NotNull(message = "Okres ważności jest wymagany")
    @Min(value = 1, message = "Okres ważności musi być większy od 0")
    private Integer validityPeriod;
    
    @NotNull(message = "Pole obowiązkowy jest wymagane")
    private Boolean mandatory;
    
    private String barcode;
    
    @NotNull(message = "Kwota ekwiwalentu jest wymagana")
    @DecimalMin(value = "0.0", inclusive = true, message = "Kwota ekwiwalentu nie może być ujemna")
    private BigDecimal compensationAmount;
    
    private Boolean active = true;
    private String notes;

    private ClothingType clothingType;
    
    public static PositionClothingItemDTO fromEntity(PositionClothingItem item) {
        if (item == null) {
            return null;
        }
        
        return PositionClothingItemDTO.builder()
                .id(item.getId())
                .positionClothingAllowanceId(item.getPositionClothingAllowance().getId())
                .clothingTypeId(item.getClothingType().getId())
                .clothingTypeName(item.getClothingType().getName())
                .clothingType(item.getClothingType()) // <-- DODANE
                .quantity(item.getQuantity())
                .validityPeriod(item.getValidityPeriod())
                .mandatory(item.getMandatory())
                .barcode(item.getBarcode())
                .compensationAmount(item.getCompensationAmount())
                .active(item.getActive())
                .notes(item.getNotes())
                .build();
    }
    
    public static List<PositionClothingItemDTO> fromEntities(List<PositionClothingItem> items) {
        if (items == null) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(PositionClothingItemDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public PositionClothingItem toEntity() {
        PositionClothingItem item = new PositionClothingItem();
        // Note: PositionClothingAllowance and ClothingType must be set by the service layer
        item.setQuantity(this.quantity);
        item.setValidityPeriod(this.validityPeriod);
        item.setMandatory(this.mandatory);
        item.setBarcode(this.barcode);
        item.setCompensationAmount(this.compensationAmount);
        item.setActive(this.active);
        item.setNotes(this.notes);
        return item;
    }
}