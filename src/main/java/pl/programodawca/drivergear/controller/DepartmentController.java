package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.UpdateDepartmentDTO;
import pl.programodawca.drivergear.service.DepartmentService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/administration/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private static final String DEPARTMENTS_LIST_VIEW = "administration/departments/department-list";
    private static final String DEPARTMENT_FORM_VIEW = "administration/departments/department-form";
    private static final String REDIRECT_TO_DEPARTMENTS = "redirect:/administration/departments";

    private final DepartmentService departmentService;

    @GetMapping
    public String listDepartments(@RequestParam(required = false) Boolean showInactive, Model model) {
        List<DepartmentDTO> departments;
        if (Boolean.TRUE.equals(showInactive)) {
            departments = departmentService.findAllDepartments();
        } else {
            departments = departmentService.getAllActiveDepartments(); // zmiana tutaj
        }
        model.addAttribute("departments", departments);
        return DEPARTMENTS_LIST_VIEW;
    }


    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("departmentDTO", new CreateDepartmentDTO());
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowy dział");
        return DEPARTMENT_FORM_VIEW;
    }

    @PostMapping
    public String createDepartment(
            @Valid @ModelAttribute("departmentDTO") CreateDepartmentDTO createDepartmentDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy dział");
            return DEPARTMENT_FORM_VIEW;
        }

        try {
            departmentService.createDepartment(createDepartmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Dział został utworzony pomyślnie");
            return REDIRECT_TO_DEPARTMENTS;
        } catch (Exception e) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy dział");
            model.addAttribute("errorMessage", e.getMessage());
            return DEPARTMENT_FORM_VIEW;
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            DepartmentDTO department = departmentService.getDepartmentById(id);
            UpdateDepartmentDTO updateDTO = UpdateDepartmentDTO.fromDepartmentDTO(department);

            model.addAttribute("departmentDTO", updateDTO);
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj dział");
            return DEPARTMENT_FORM_VIEW;
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return REDIRECT_TO_DEPARTMENTS;
        }
    }

    @PostMapping("/{id}")
    public String updateDepartment(
            @PathVariable Long id,
            @Valid @ModelAttribute("departmentDTO") UpdateDepartmentDTO updateDepartmentDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj dział");
            return DEPARTMENT_FORM_VIEW;
        }

        try {
            departmentService.updateDepartment(id, updateDepartmentDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Dział został zaktualizowany pomyślnie");
            return REDIRECT_TO_DEPARTMENTS;
        } catch (Exception e) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj dział");
            model.addAttribute("errorMessage", e.getMessage());
            return DEPARTMENT_FORM_VIEW;
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dział został pomyślnie usunięty");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (EntityNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania działu: " + e.getMessage());
        }
        return REDIRECT_TO_DEPARTMENTS;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return REDIRECT_TO_DEPARTMENTS;
    }
}
