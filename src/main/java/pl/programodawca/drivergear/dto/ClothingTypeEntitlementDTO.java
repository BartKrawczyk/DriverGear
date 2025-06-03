package pl.programodawca.drivergear.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing the entitlement for a specific clothing type for an employee.
 * Contains information about standard quantities, issued quantities, and eligibility for compensation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class ClothingTypeEntitlementDTO {
    private Long clothingTypeId;
    private String clothingTypeName;
    private Integer standardQuantity;       // From position allowance
    private Integer validityMonths;         // From position allowance
    private Integer issuedQuantity;         // Currently issued and still valid
    private Integer pendingQuantity;        // Assigned but not issued
    private Integer expiredQuantity;        // Expired and not compensated
    private LocalDate lastIssuedDate;       // Most recent issuance date
    private LocalDate nextRenewalDate;      // When next item of this type can be issued
    private LocalDate lastExpiredDate;      // Most recent expiry date of expired assignments
    private Boolean eligibleForCompensation;
    private BigDecimal compensationValue;
}
