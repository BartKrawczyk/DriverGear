package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.programodawca.drivergear.dto.ClothingAssignmentDTO;
import pl.programodawca.drivergear.dto.ClothingCompensationDTO;
import pl.programodawca.drivergear.service.ClothingTypeService;
import pl.programodawca.drivergear.service.WarehouseHistoryService;

import java.time.LocalDate;

/**
 * Controller for warehouse history views.
 */
@Controller
@RequestMapping("/warehouse/history")
@RequiredArgsConstructor
public class WarehouseHistoryController {

    private final WarehouseHistoryService warehouseHistoryService;
    private final ClothingTypeService clothingTypeService;

    /**
     * Display the issued clothing history view.
     */
    @GetMapping("/issued-clothing")
    public String showIssuedClothingHistory(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) Long clothingTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "issuedDate"));
        
        Page<ClothingAssignmentDTO> issuedClothingPage = warehouseHistoryService.getIssuedClothingHistory(
                firstName, lastName, employeeNumber, clothingTypeId, startDate, endDate, pageable);
        
        model.addAttribute("issuedClothingPage", issuedClothingPage);
        model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
        
        // Add filter values to model for form persistence
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("employeeNumber", employeeNumber);
        model.addAttribute("clothingTypeId", clothingTypeId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        model.addAttribute("title", "Issued Clothing History");
        
        return "warehouse/history/issued-clothing-history";
    }

    /**
     * Display the paid compensation history view.
     */
    @GetMapping("/paid-compensations")
    public String showPaidCompensationHistory(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String employeeNumber,
            @RequestParam(required = false) Long clothingTypeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));
        
        Page<ClothingCompensationDTO> paidCompensationPage = warehouseHistoryService.getPaidCompensationHistory(
                firstName, lastName, employeeNumber, clothingTypeId, startDate, endDate, pageable);
        
        model.addAttribute("paidCompensationPage", paidCompensationPage);
        model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
        
        // Add filter values to model for form persistence
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("employeeNumber", employeeNumber);
        model.addAttribute("clothingTypeId", clothingTypeId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        model.addAttribute("title", "Paid Compensation History");
        
        return "warehouse/history/paid-compensations-history";
    }
}