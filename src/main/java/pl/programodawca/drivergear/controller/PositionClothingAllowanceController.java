package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.PositionClothingAllowanceDTO;
import pl.programodawca.drivergear.dto.PositionClothingItemDTO;
import pl.programodawca.drivergear.exception.BusinessException;
import pl.programodawca.drivergear.exception.EntityNotFoundException;
import pl.programodawca.drivergear.service.ClothingTypeService;
import pl.programodawca.drivergear.service.DepartmentService;
import pl.programodawca.drivergear.service.PositionClothingAllowanceService;
import pl.programodawca.drivergear.service.PositionService;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/administration/position-clothing-allowances")
@RequiredArgsConstructor
public class PositionClothingAllowanceController {
    private final PositionClothingAllowanceService positionAllowanceService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final ClothingTypeService clothingTypeService;

    @GetMapping
    public String listPositionAllowances(@RequestParam(required = false) Long departmentId,
                                        @RequestParam(required = false) Long positionId,
                                        @RequestParam(required = false) Long clothingTypeId,
                                        Model model) {
        List<PositionClothingAllowanceDTO> allowances;

        if (departmentId != null) {
            allowances = positionAllowanceService.getPositionAllowancesByDepartmentId(departmentId);
        } else if (positionId != null) {
            allowances = positionAllowanceService.getPositionAllowancesByPositionId(positionId);
        } else if (clothingTypeId != null) {
            allowances = positionAllowanceService.getPositionAllowancesByClothingTypeId(clothingTypeId);
        } else {
            allowances = positionAllowanceService.getAllPositionAllowances();
        }

        model.addAttribute("allowances", allowances);
        model.addAttribute("departments", departmentService.findAllDepartments());

        // Filter positions by department if a department is selected
        if (departmentId != null) {
            model.addAttribute("positions", positionService.findActivePositionsByDepartment(departmentId));
        } else {
            // When no department is selected, show all active positions
            model.addAttribute("positions", positionService.findActivePositions());
        }

        model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
        model.addAttribute("selectedDepartmentId", departmentId);
        model.addAttribute("selectedPositionId", positionId);
        model.addAttribute("selectedClothingTypeId", clothingTypeId);

        return "administration/position-clothing-allowances/position-clothing-allowance-list";
    }

    @GetMapping("/new")
    public String showNewAllowanceForm(@RequestParam(required = false) Long departmentId, Model model) {
        // Create a new allowance DTO
        PositionClothingAllowanceDTO allowanceDTO = new PositionClothingAllowanceDTO();
        allowanceDTO.setActive(true);

        // Set department ID if provided
        if (departmentId != null) {
            allowanceDTO.setDepartmentId(departmentId);
        }

        // Create a new clothing item DTO with default values
        List<PositionClothingItemDTO> clothingItems = new ArrayList<>();
        PositionClothingItemDTO itemDTO = new PositionClothingItemDTO();
        itemDTO.setQuantity(1);
        itemDTO.setValidityPeriod(12); // Default to 1 year
        itemDTO.setMandatory(true);
        itemDTO.setCompensationAmount(BigDecimal.ZERO);
        itemDTO.setActive(true);
        clothingItems.add(itemDTO);

        // Set the clothing items on the allowance DTO
        allowanceDTO.setClothingItems(clothingItems);

        // Add attributes to the model
        model.addAttribute("allowanceDTO", allowanceDTO);
        model.addAttribute("departments", departmentService.findAllDepartments());

        // Filter positions by department if a department is selected
        if (departmentId != null) {
            model.addAttribute("positions", positionService.findActivePositionsByDepartment(departmentId));
        } else {
            // When no department is selected, show all active positions
            model.addAttribute("positions", positionService.findActivePositions());
        }

        model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowy przydział odzieżowy dla stanowiska");

        return "administration/position-clothing-allowances/position-clothing-allowance-form";
    }

    @PostMapping("/new")
    public String createAllowance(@Valid @ModelAttribute("allowanceDTO") PositionClothingAllowanceDTO allowanceDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAllDepartments());

            // Filter positions by department if a department is selected
            if (allowanceDTO.getDepartmentId() != null) {
                model.addAttribute("positions", positionService.findActivePositionsByDepartment(allowanceDTO.getDepartmentId()));
            } else {
                // When no department is selected, show all active positions
                model.addAttribute("positions", positionService.findActivePositions());
            }

            model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy przydział odzieżowy dla stanowiska");
            return "administration/position-clothing-allowances/position-clothing-allowance-form";
        }

        try {
            // Validate that all clothing items have required fields
            for (PositionClothingItemDTO item : allowanceDTO.getClothingItems()) {
                if (item.getClothingTypeId() == null) {
                    throw new BusinessException("Typ odzieży jest wymagany dla wszystkich elementów");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new BusinessException("Ilość musi być większa od zera dla wszystkich elementów");
                }
                if (item.getValidityPeriod() == null || item.getValidityPeriod() <= 0) {
                    throw new BusinessException("Okres ważności musi być większy od zera dla wszystkich elementów");
                }
                // Ensure compensationAmount is not null (will be set to ZERO if null in service)
                if (item.getCompensationAmount() == null) {
                    item.setCompensationAmount(BigDecimal.ZERO);
                }
            }

            PositionClothingAllowanceDTO createdAllowance = positionAllowanceService.createPositionAllowance(
                    allowanceDTO.getDepartmentId(),
                    allowanceDTO.getPositionId(),
                    allowanceDTO.getClothingItems(),
                    allowanceDTO.getNotes()
            );
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy dla stanowiska został pomyślnie dodany.");
            return "redirect:/administration/position-clothing-allowances";
        } catch (EntityNotFoundException | BusinessException e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas dodawania przydziału odzieżowego: " + e.getMessage());
            model.addAttribute("departments", departmentService.findAllDepartments());

            // Filter positions by department if a department is selected
            if (allowanceDTO.getDepartmentId() != null) {
                model.addAttribute("positions", positionService.findActivePositionsByDepartment(allowanceDTO.getDepartmentId()));
            } else {
                // When no department is selected, show all active positions
                model.addAttribute("positions", positionService.findActivePositions());
            }

            model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy przydział odzieżowy dla stanowiska");
            return "administration/position-clothing-allowances/position-clothing-allowance-form";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Redirect with error message
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił nieoczekiwany błąd podczas dodawania przydziału odzieżowego. Szczegóły: " + e.getMessage());
            return "redirect:/administration/position-clothing-allowances";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            PositionClothingAllowanceDTO allowanceDTO = positionAllowanceService.getPositionAllowanceById(id);

            model.addAttribute("allowanceDTO", allowanceDTO);
            model.addAttribute("departments", departmentService.findAllDepartments());

            // Filter positions by department
            if (allowanceDTO.getDepartmentId() != null) {
                model.addAttribute("positions", positionService.findActivePositionsByDepartment(allowanceDTO.getDepartmentId()));
            } else {
                // When no department is selected, show all active positions
                model.addAttribute("positions", positionService.findActivePositions());
            }

            model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja przydziału odzieżowego dla stanowiska");

            return "administration/position-clothing-allowances/position-clothing-allowance-form";
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/administration/position-clothing-allowances";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateAllowance(@PathVariable Long id,
                                 @Valid @ModelAttribute("allowanceDTO") PositionClothingAllowanceDTO allowanceDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.findAllDepartments());

            // Filter positions by department
            if (allowanceDTO.getDepartmentId() != null) {
                model.addAttribute("positions", positionService.findActivePositionsByDepartment(allowanceDTO.getDepartmentId()));
            } else {
                // When no department is selected, show all active positions
                model.addAttribute("positions", positionService.findActivePositions());
            }

            model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja przydziału odzieżowego dla stanowiska");
            return "administration/position-clothing-allowances/position-clothing-allowance-form";
        }

        try {
            // Validate that all clothing items have required fields
            for (PositionClothingItemDTO item : allowanceDTO.getClothingItems()) {
                if (item.getClothingTypeId() == null) {
                    throw new BusinessException("Typ odzieży jest wymagany dla wszystkich elementów");
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new BusinessException("Ilość musi być większa od zera dla wszystkich elementów");
                }
                if (item.getValidityPeriod() == null || item.getValidityPeriod() <= 0) {
                    throw new BusinessException("Okres ważności musi być większy od zera dla wszystkich elementów");
                }
                // Ensure compensationAmount is not null (will be set to ZERO if null in service)
                if (item.getCompensationAmount() == null) {
                    item.setCompensationAmount(BigDecimal.ZERO);
                }
            }

            PositionClothingAllowanceDTO updatedAllowance = positionAllowanceService.updatePositionAllowance(
                    id,
                    allowanceDTO.getClothingItems(),
                    allowanceDTO.getActive(),
                    allowanceDTO.getNotes()
            );
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy dla stanowiska został pomyślnie zaktualizowany.");
            return "redirect:/administration/position-clothing-allowances";
        } catch (EntityNotFoundException | BusinessException e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas aktualizacji przydziału odzieżowego: " + e.getMessage());
            model.addAttribute("departments", departmentService.findAllDepartments());

            // Filter positions by department
            if (allowanceDTO.getDepartmentId() != null) {
                model.addAttribute("positions", positionService.findActivePositionsByDepartment(allowanceDTO.getDepartmentId()));
            } else {
                // When no department is selected, show all active positions
                model.addAttribute("positions", positionService.findActivePositions());
            }

            model.addAttribute("clothingTypes", clothingTypeService.findActiveClothingTypes());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja przydziału odzieżowego dla stanowiska");
            return "administration/position-clothing-allowances/position-clothing-allowance-form";
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            // Redirect with error message
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił nieoczekiwany błąd podczas aktualizacji przydziału odzieżowego. Szczegóły: " + e.getMessage());
            return "redirect:/administration/position-clothing-allowances";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAllowance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionAllowanceService.deletePositionAllowance(id);
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy dla stanowiska został pomyślnie usunięty.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie można usunąć przydziału odzieżowego: " + e.getMessage());
        }
        return "redirect:/administration/position-clothing-allowances";
    }

    @PostMapping("/{id}/activate")
    public String activateAllowance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionAllowanceService.activatePositionAllowance(id);
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy dla stanowiska został pomyślnie aktywowany.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/administration/position-clothing-allowances";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivateAllowance(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionAllowanceService.deactivatePositionAllowance(id);
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy dla stanowiska został pomyślnie dezaktywowany.");
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/administration/position-clothing-allowances";
    }
}
