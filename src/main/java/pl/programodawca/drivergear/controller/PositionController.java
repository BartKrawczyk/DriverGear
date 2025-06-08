package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.CreatePositionDTO;
import pl.programodawca.drivergear.dto.PositionDTO;
import pl.programodawca.drivergear.dto.UpdatePositionDTO;
import pl.programodawca.drivergear.exception.ResourceAlreadyExistsException;
import pl.programodawca.drivergear.exception.ResourceInUseException;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.service.DepartmentService;
import pl.programodawca.drivergear.service.PositionService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/administration/positions")
@RequiredArgsConstructor
public class PositionController {
    private final PositionService positionService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listPositions(@RequestParam(required = false) Boolean showInactive,
                                @RequestParam(required = false) Long departmentId,
                                Model model) {
        List<PositionDTO> positions;

        if (departmentId != null) {
            positions = showInactive != null && showInactive
                    ? positionService.findPositionsByDepartment(departmentId)
                    : positionService.findActivePositionsByDepartment(departmentId);
        } else {
            positions = showInactive != null && showInactive
                    ? positionService.findAllPositions()
                    : positionService.findActivePositions();
        }

        model.addAttribute("positions", positions);
        model.addAttribute("departments", departmentService.getAllActiveDepartments()); // POPRAWIONE
        model.addAttribute("selectedDepartmentId", departmentId);
        return "administration/positions/position-list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Long departmentId, Model model) {
        CreatePositionDTO createPositionDTO = new CreatePositionDTO();
        if (departmentId != null) {
            createPositionDTO.setDepartmentId(departmentId);
        }

        model.addAttribute("positionDTO", createPositionDTO);
        model.addAttribute("departments", departmentService.getAllActiveDepartments()); // POPRAWIONE
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowe stanowisko");
        return "administration/positions/position-form";
    }

    @GetMapping("/check-code")
    @ResponseBody
    public ResponseEntity<Boolean> checkCodeAvailability(@RequestParam String code,
                                                         @RequestParam(required = false) Long excludeId) {
        boolean isAvailable = excludeId != null
                ? positionService.isCodeUnique(code, excludeId)
                : positionService.isCodeUnique(code);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/by-code/{code}")
    @ResponseBody
    public ResponseEntity<PositionDTO> getPositionByCode(@PathVariable String code) {
        try {
            PositionDTO position = positionService.findPositionByCode(code);
            return ResponseEntity.ok(position);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-department/{departmentId}")
    @ResponseBody
    public ResponseEntity<List<PositionDTO>> getPositionsByDepartment(@PathVariable Long departmentId, 
                                                                      @RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {
        List<PositionDTO> positions = activeOnly 
                ? positionService.findActivePositionsByDepartment(departmentId)
                : positionService.findPositionsByDepartment(departmentId);
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<PositionDTO>> getAllPositions(@RequestParam(required = false, defaultValue = "true") Boolean activeOnly) {
        List<PositionDTO> positions = activeOnly 
                ? positionService.findActivePositions()
                : positionService.findAllPositions();
        return ResponseEntity.ok(positions);
    }

    @PostMapping
    public String createPosition(@Valid @ModelAttribute("positionDTO") CreatePositionDTO createPositionDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowe stanowisko");
            return "administration/positions/position-form";
        }

        try {
            PositionDTO createdPosition = positionService.createPosition(createPositionDTO);
            redirectAttributes.addFlashAttribute("message", "Stanowisko zostało utworzone");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/administration/positions";
        } catch (ResourceAlreadyExistsException e) {
            if (e.getMessage().contains("kod")) {
                bindingResult.rejectValue("code", "error.position", e.getMessage());
            } else {
                bindingResult.rejectValue("name", "error.position", e.getMessage());
            }
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowe stanowisko");
            return "administration/positions/position-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            PositionDTO positionDTO = positionService.findPositionById(id);
            UpdatePositionDTO updatePositionDTO = new UpdatePositionDTO();
            updatePositionDTO.setId(positionDTO.getId());
            updatePositionDTO.setName(positionDTO.getName());
            updatePositionDTO.setCode(positionDTO.getCode());
            updatePositionDTO.setDescription(positionDTO.getDescription());
            updatePositionDTO.setActive(positionDTO.isActive());
            updatePositionDTO.setDepartmentId(positionDTO.getDepartmentId());

            model.addAttribute("positionDTO", updatePositionDTO);
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja stanowiska");
            return "administration/positions/position-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/administration/positions";
        }
    }

    @PostMapping("/{id}")
    public String updatePosition(@PathVariable Long id,
                                 @Valid @ModelAttribute("positionDTO") UpdatePositionDTO updatePositionDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja stanowiska");
            return "administration/positions/position-form";
        }

        try {
            positionService.updatePosition(id, updatePositionDTO);
            redirectAttributes.addFlashAttribute("message", "Stanowisko zostało zaktualizowane");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/administration/positions";
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            if (e instanceof ResourceAlreadyExistsException && e.getMessage().contains("kod")) {
                bindingResult.rejectValue("code", "error.position", e.getMessage());
            } else {
                bindingResult.rejectValue("name", "error.position", e.getMessage());
            }
            model.addAttribute("departments", departmentService.getAllActiveDepartments());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja stanowiska");
            return "administration/positions/position-form";
        }
    }


    @PostMapping("/{id}/delete")
    public String deletePosition(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        try {
            positionService.deletePosition(id);
            redirectAttributes.addFlashAttribute("message", "Stanowisko zostało usunięte");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (ResourceNotFoundException | ResourceInUseException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/administration/positions";
    }
}
