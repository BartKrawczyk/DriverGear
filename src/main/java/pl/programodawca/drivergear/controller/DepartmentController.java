package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.DepartmentDTO;
import pl.programodawca.drivergear.dto.CreateDepartmentDTO;
import pl.programodawca.drivergear.dto.UpdateDepartmentDTO;
import pl.programodawca.drivergear.service.DepartmentService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/administration/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String listDepartments(Model model) {
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "administration/departments/department-list";
    }

    @GetMapping("/new")
    public String newDepartmentForm(Model model) {
        model.addAttribute("departmentDTO", new CreateDepartmentDTO());
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowy dział");
        return "administration/departments/department-form";
    }

    @PostMapping
    public String createDepartment(@Valid @ModelAttribute("departmentDTO") CreateDepartmentDTO createDepartmentDTO,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy dział");
            return "administration/departments/department-form";
        }

        try {
            departmentService.createDepartment(createDepartmentDTO); // przekazujemy bezpośrednio
            redirectAttributes.addFlashAttribute("successMessage", "Dział został utworzony pomyślnie");
            return "redirect:/administration/departments";
        } catch (Exception e) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy dział");
            model.addAttribute("errorMessage", e.getMessage());
            return "administration/departments/department-form";
        }
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            DepartmentDTO department = departmentService.findById(id);
            model.addAttribute("departmentDTO", UpdateDepartmentDTO.fromDepartmentDTO(department));
            model.addAttribute("departmentDTO", department);
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj dział");
            return "administration/departments/department-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono działu o ID: " + id);
            return "redirect:/administration/departments";
        }
    }

    @PostMapping("/{id}")
    public String updateDepartment(@PathVariable Long id,
                                   @Valid @ModelAttribute("departmentDTO") UpdateDepartmentDTO updateDepartmentDTO,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj dział");
            return "administration/departments/department-form";
        }

        try {
            updateDepartmentDTO.setId(id);
            departmentService.updateDepartment(id, updateDepartmentDTO); // przekazujemy bezpośrednio
            redirectAttributes.addFlashAttribute("successMessage", "Dział został zaktualizowany pomyślnie");
            return "redirect:/administration/departments";
        } catch (Exception e) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj dział");
            model.addAttribute("errorMessage", e.getMessage());
            return "administration/departments/department-form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Dział został pomyślnie usunięty");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania działu: " + e.getMessage());
        }
        return "redirect:/administration/departments";
    }
}

