package pl.programodawca.drivergear.controller;

import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.dto.UpdatePositionDTO;
import pl.programodawca.drivergear.exception.PositionAlreadyExistsException;
import pl.programodawca.drivergear.model.Position;
import pl.programodawca.drivergear.service.DepartmentService;
import pl.programodawca.drivergear.service.PositionService;
import pl.programodawca.drivergear.model.Department;
import pl.programodawca.drivergear.service.DepartmentService;


import java.util.List;

@Slf4j
@Controller
@RequestMapping("/administration/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listPositions(Model model) {
        List<PositionDTO> positions = positionService.getAllPositions();
        model.addAttribute("positions", positionService.getAllPositions());
        return "administration/positions/position-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("position")) {
            model.addAttribute("position", new CreatePositionDTO());
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("isNew", true);
        return "administration/positions/position-create";
    }

    @PostMapping("/new")
    public String createPosition(
            @Valid @ModelAttribute("position") CreatePositionDTO positionDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", true);
            return "administration/positions/position-create";
        }

        try {
            positionService.createPosition(positionDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Stanowisko zostało utworzone pomyślnie");
            return "redirect:/administration/positions";
        } catch (PositionAlreadyExistsException e) {
            bindingResult.rejectValue("name", "error.position", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", true);
            return "administration/positions/position-create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            PositionDTO positionDTO = positionService.findById(id);
            List<Department> departments = departmentService.findAll(); // dodaj to!

            model.addAttribute("position", UpdatePositionDTO.fromPositionDTO(positionDTO)); // zmieniona nazwa atrybutu
            model.addAttribute("departments", departments); // dodaj listę działów
            model.addAttribute("id", id); // dodaj id do modelu
            return "administration/positions/position-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono stanowiska o ID: " + id);
            return "redirect:/administration/positions";
        }
    }



//    @GetMapping("/{id}/edit")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        if (!model.containsAttribute("position")) {
//            PositionDTO positionDTO = positionService.getPositionById(id);
//            CreatePositionDTO editDTO = new CreatePositionDTO();
//            editDTO.setName(positionDTO.getName());
//            editDTO.setCode(positionDTO.getCode());
//            editDTO.setDescription(positionDTO.getDescription());
//            editDTO.setDepartmentId(positionDTO.getDepartmentId());
//            model.addAttribute("position", editDTO);
//        }
//        model.addAttribute("positionId", id);
//        model.addAttribute("departments", departmentService.getAllDepartments());
//        model.addAttribute("isNew", false);
//        return "administration/positions/position-edit";
//    }

    @PostMapping("/{id}/edit")
    public String updatePosition(
            @PathVariable Long id,
            @Valid @ModelAttribute("position") UpdatePositionDTO updatePositionDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("positionId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", false);
            return "administration/positions/position-edit";
        }

        try {
            positionService.updatePosition(id, updatePositionDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Stanowisko zostało zaktualizowane pomyślnie");
            return "redirect:/administration/positions";
        } catch (PositionAlreadyExistsException e) {
            bindingResult.rejectValue("name", "error.position", e.getMessage());
            model.addAttribute("positionId", id);
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("isNew", false);
            return "administration/positions/position-edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String deletePosition(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionService.deletePosition(id);
            redirectAttributes.addFlashAttribute("successMessage", "Stanowisko zostało usunięte pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie można usunąć stanowiska. Sprawdź czy nie jest przypisane do żadnego pracownika.");
        }
        return "redirect:/administration/positions";
    }

    // Metoda pomocnicza do obsługi błędów
    @ExceptionHandler(Exception.class)
    public String handleError(Exception e, RedirectAttributes redirectAttributes) {
        log.error("Wystąpił błąd podczas operacji na stanowisku", e);
        redirectAttributes.addFlashAttribute("errorMessage",
                "Wystąpił nieoczekiwany błąd. Spróbuj ponownie później.");
        return "redirect:/administration/positions";
    }
}

