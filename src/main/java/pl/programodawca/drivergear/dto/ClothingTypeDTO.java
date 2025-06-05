package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import pl.programodawca.drivergear.model.ClothingType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ClothingTypeDTO {
    private Long id;

    @NotBlank(message = "Nazwa typu odzieży jest wymagana")
    @Size(min = 2, max = 100, message = "Nazwa musi mieć od 2 do 100 znaków")
    private String name;

    @Size(max = 500, message = "Opis nie może przekraczać 500 znaków")
    private String description;

    @Size(max = 50, message = "Kod kreskowy nie może przekraczać 50 znaków")
    private String barcode;

    @NotNull(message = "Wartość ekwiwalentu jest wymagana")
    @DecimalMin(value = "0.0", inclusive = true, message = "Wartość ekwiwalentu nie może być ujemna")
    private BigDecimal compensationValue;

    @NotNull(message = "Standardowy okres użytkowania jest wymagany")
    @Min(value = 1, message = "Standardowy okres użytkowania musi być większy od 0")
    private Integer standardLifetimeMonths;

    private boolean active = true;

    // Liczba przydziałów związanych z tym typem odzieży
    private int positionAllowancesCount;

    // Liczba ekwiwalentów związanych z tym typem odzieży
    private int compensationsCount;

    public static ClothingTypeDTO fromEntity(ClothingType clothingType) {
        if (clothingType == null) {
            return null;
        }

        return ClothingTypeDTO.builder()
                .id(clothingType.getId())
                .name(clothingType.getName())
                .description(clothingType.getDescription())
                .barcode(clothingType.getBarcode())
                .compensationValue(clothingType.getCompensationValue())
                .standardLifetimeMonths(clothingType.getStandardLifetimeMonths())
                .active(clothingType.isActive())
                .positionAllowancesCount(clothingType.getClothingItems().size())
                .compensationsCount(0)
                .build();
    }

    public static List<ClothingTypeDTO> fromEntities(List<ClothingType> clothingTypes) {
        if (clothingTypes == null) {
            return Collections.emptyList();
        }
        return clothingTypes.stream()
                .map(ClothingTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public ClothingType toEntity() {
        ClothingType clothingType = new ClothingType();
        updateEntity(clothingType);
        return clothingType;
    }

    public void updateEntity(ClothingType clothingType) {
        clothingType.setName(this.name);
        clothingType.setDescription(this.description);
        clothingType.setBarcode(this.barcode);
        clothingType.setCompensationValue(this.compensationValue);
        clothingType.setStandardLifetimeMonths(this.standardLifetimeMonths);
        clothingType.setActive(this.active);
    }
}
