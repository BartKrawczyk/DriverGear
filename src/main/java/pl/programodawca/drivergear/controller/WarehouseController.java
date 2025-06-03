package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.StockOverviewDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.model.WarehouseInventory;
import pl.programodawca.drivergear.repository.ClothingTypeRepository;
import pl.programodawca.drivergear.service.ClothingTypeService;
import pl.programodawca.drivergear.service.WarehouseInventoryService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final ClothingTypeService clothingTypeService;
    private final WarehouseInventoryService warehouseInventoryService;
    private final ClothingTypeRepository clothingTypeRepository;

    @GetMapping
    public String showWarehouseDashboard(Model model, Authentication authentication) {
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Add stock overview data to the model
        List<StockOverviewDTO> stockOverview = warehouseInventoryService.getStockOverview();
        model.addAttribute("stockOverview", stockOverview);

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("title", "Panel Magazynu");
        return "warehouse/warehouse-dashboard";
    }

    @GetMapping("/inventory")
    public String showInventory(
            @RequestParam(required = false) Long clothingTypeId,
            Model model) {

        // Get all inventory items
        List<WarehouseInventory> allItems = warehouseInventoryService.findAllInventory();

        // Apply filters if provided
        List<WarehouseInventory> filteredItems = allItems;

        if (clothingTypeId != null) {
            filteredItems = filteredItems.stream()
                    .filter(item -> item.getClothingType().getId().equals(clothingTypeId))
                    .collect(Collectors.toList());
        }

        model.addAttribute("inventoryItems", filteredItems);
        model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
        model.addAttribute("clothingTypeId", clothingTypeId);

        return "warehouse/inventory/inventory-list";
    }

    @GetMapping("/inventory/add")
    public String showAddInventoryForm(Model model) {
        model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
        model.addAttribute("inventoryForm", new InventoryForm());
        return "warehouse/inventory/add-inventory";
    }

    @PostMapping("/inventory/add")
    public String addInventory(@ModelAttribute("inventoryForm") InventoryForm form, 
                              RedirectAttributes redirectAttributes) {
        try {
            // Get the clothing type from the repository to ensure it's a managed entity
            ClothingType clothingType = clothingTypeRepository.findById(form.getClothingTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Typ odzieży", "id", form.getClothingTypeId()));

            // Increase the stock
            warehouseInventoryService.increaseStock(clothingType, form.getQuantity());

            redirectAttributes.addFlashAttribute("successMessage", 
                    "Dodano " + form.getQuantity() + " sztuk " + clothingType.getName() + " do magazynu.");

            return "redirect:/warehouse/inventory";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Błąd podczas dodawania do magazynu: " + e.getMessage());
            return "redirect:/warehouse/inventory";
        }
    }

    /**
     * Endpoint to get stock overview data for the warehouse dashboard.
     * 
     * @return JSON response with stock overview data
     */
    @GetMapping("/stock-overview")
    @ResponseBody
    public ResponseEntity<List<StockOverviewDTO>> getStockOverview() {
        List<StockOverviewDTO> stockOverview = warehouseInventoryService.getStockOverview();
        return ResponseEntity.ok(stockOverview);
    }

    /**
     * View for the stock overview page.
     * 
     * NOTE: This endpoint is no longer needed as the stock overview has been integrated into the main warehouse dashboard.
     */
    /*
    @GetMapping("/stock-overview-view")
    public String showStockOverview(Model model) {
        List<StockOverviewDTO> stockOverview = warehouseInventoryService.getStockOverview();
        model.addAttribute("stockOverview", stockOverview);
        return "warehouse/stock-overview";
    }
    */

    // Form backing object
    public static class InventoryForm {
        private Long clothingTypeId;
        private Integer quantity = 1;

        public Long getClothingTypeId() {
            return clothingTypeId;
        }

        public void setClothingTypeId(Long clothingTypeId) {
            this.clothingTypeId = clothingTypeId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
