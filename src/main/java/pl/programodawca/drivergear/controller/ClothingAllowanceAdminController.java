package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.ClothingAllowanceDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.AllowanceStatus;
import pl.programodawca.drivergear.service.ClothingAllowanceService;
import pl.programodawca.drivergear.service.EmployeeService;
import pl.programodawca.drivergear.service.PositionService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/administration/clothing-allowances")
@RequiredArgsConstructor
public class ClothingAllowanceAdminController {
    private final ClothingAllowanceService clothingAllowanceService;
    private final EmployeeService employeeService;
    private final PositionService positionService;

    @GetMapping
    public String listClothingAllowances(@RequestParam(required = false) AllowanceStatus status,
                                        @RequestParam(required = false) Long employeeId,
                                        @RequestParam(required = false) Long positionId,
                                        Model model) {
        List<ClothingAllowanceDTO> allowances;

        if (employeeId != null) {
            if (status != null) {
                allowances = clothingAllowanceService.getAllowancesByEmployeeIdAndStatus(employeeId, status);
            } else {
                allowances = clothingAllowanceService.getAllowancesByEmployeeId(employeeId);
            }
        } else if (positionId != null) {
            allowances = clothingAllowanceService.getAllowancesByPositionId(positionId);
        } else if (status != null) {
            allowances = clothingAllowanceService.getAllowancesByStatus(status);
        } else {
            // Get all allowances by combining results for all statuses
            List<ClothingAllowanceDTO> allAllowances = new ArrayList<>();
            for (AllowanceStatus s : AllowanceStatus.values()) {
                allAllowances.addAll(clothingAllowanceService.getAllowancesByStatus(s));
            }
            allowances = allAllowances;
        }

        model.addAttribute("allowances", allowances);
        model.addAttribute("statuses", AllowanceStatus.values());
        model.addAttribute("employees", employeeService.findAll(null).getContent());
        model.addAttribute("positions", positionService.findAllPositions());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedEmployeeId", employeeId);
        model.addAttribute("selectedPositionId", positionId);

        return "administration/clothing-allowances/clothing-allowance-list";
    }

    @GetMapping("/new")
    public String showNewAllowanceForm(Model model) {
        ClothingAllowanceDTO allowanceDTO = new ClothingAllowanceDTO();
        allowanceDTO.setStartDate(LocalDate.now());
        allowanceDTO.setEndDate(LocalDate.now().plusMonths(12)); // Default to 1 year
        allowanceDTO.setStatus(AllowanceStatus.ACTIVE);

        model.addAttribute("allowanceDTO", allowanceDTO);
        model.addAttribute("employees", employeeService.findAll(null).getContent());
        model.addAttribute("positions", positionService.findAllPositions());
        model.addAttribute("statuses", AllowanceStatus.values());
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowy przydział odzieżowy");

        return "administration/clothing-allowances/clothing-allowance-form";
    }

    @PostMapping("/new")
    public String createAllowance(@Valid @ModelAttribute("allowanceDTO") ClothingAllowanceDTO allowanceDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.findAll(null).getContent());
            model.addAttribute("positions", positionService.findAllPositions());
            model.addAttribute("statuses", AllowanceStatus.values());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy przydział odzieżowy");
            return "administration/clothing-allowances/clothing-allowance-form";
        }

        try {
            ClothingAllowanceDTO createdAllowance = clothingAllowanceService.createAllowance(
                    allowanceDTO.getEmployeeId(),
                    allowanceDTO.getPositionId(),
                    allowanceDTO.getStartDate(),
                    allowanceDTO.getEndDate(),
                    allowanceDTO.getNotes()
            );
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy został pomyślnie dodany.");
            return "redirect:/administration/clothing-allowances";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas dodawania przydziału odzieżowego: " + e.getMessage());
            model.addAttribute("employees", employeeService.findAll(null).getContent());
            model.addAttribute("positions", positionService.findAllPositions());
            model.addAttribute("statuses", AllowanceStatus.values());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy przydział odzieżowy");
            return "administration/clothing-allowances/clothing-allowance-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ClothingAllowanceDTO allowanceDTO = clothingAllowanceService.getAllowanceById(id);

            model.addAttribute("allowanceDTO", allowanceDTO);
            model.addAttribute("employees", employeeService.findAll(null).getContent());
            model.addAttribute("positions", positionService.findAllPositions());
            model.addAttribute("statuses", AllowanceStatus.values());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja przydziału odzieżowego");

            return "administration/clothing-allowances/clothing-allowance-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/administration/clothing-allowances";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateAllowance(@PathVariable Long id,
                                 @Valid @ModelAttribute("allowanceDTO") ClothingAllowanceDTO allowanceDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("employees", employeeService.findAll(null).getContent());
            model.addAttribute("positions", positionService.findAllPositions());
            model.addAttribute("statuses", AllowanceStatus.values());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja przydziału odzieżowego");
            return "administration/clothing-allowances/clothing-allowance-form";
        }

        try {
            // Update dates
            clothingAllowanceService.updateAllowanceDates(id, allowanceDTO.getStartDate(), allowanceDTO.getEndDate());

            // Update status
            clothingAllowanceService.updateAllowanceStatus(id, allowanceDTO.getStatus());

            // Update notes
            clothingAllowanceService.updateAllowanceNotes(id, allowanceDTO.getNotes());

            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy został pomyślnie zaktualizowany.");
            return "redirect:/administration/clothing-allowances";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas aktualizacji przydziału odzieżowego: " + e.getMessage());
            model.addAttribute("employees", employeeService.findAll(null).getContent());
            model.addAttribute("positions", positionService.findAllPositions());
            model.addAttribute("statuses", AllowanceStatus.values());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja przydziału odzieżowego");
            return "administration/clothing-allowances/clothing-allowance-form";
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelAllowance(@PathVariable Long id,
                                 @RequestParam String reason,
                                 RedirectAttributes redirectAttributes) {
        try {
            clothingAllowanceService.cancelAllowance(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Przydział odzieżowy został pomyślnie anulowany.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas anulowania przydziału odzieżowego: " + e.getMessage());
        }
        return "redirect:/administration/clothing-allowances";
    }

    @PostMapping("/update-expired")
    public String updateExpiredAllowances(RedirectAttributes redirectAttributes) {
        try {
            int count = clothingAllowanceService.updateExpiredAllowances();
            redirectAttributes.addFlashAttribute("successMessage", "Zaktualizowano status " + count + " wygasłych przydziałów odzieżowych.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas aktualizacji wygasłych przydziałów odzieżowych: " + e.getMessage());
        }
        return "redirect:/administration/clothing-allowances";
    }
}
