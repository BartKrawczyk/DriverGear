package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.ClothingTypeDTO;
import pl.programodawca.drivergear.exception.ResourceNotFoundException;
import pl.programodawca.drivergear.model.ClothingType;
import pl.programodawca.drivergear.service.ClothingTypeService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/administration/clothing-types")
@RequiredArgsConstructor
public class ClothingTypeAdminController {
    private final ClothingTypeService clothingTypeService;

    @GetMapping
    public String listClothingTypes(@RequestParam(required = false) Boolean showInactive, Model model) {
        List<ClothingTypeDTO> clothingTypes;
        if (Boolean.TRUE.equals(showInactive)) {
            clothingTypes = clothingTypeService.findAllClothingTypes();
        } else {
            clothingTypes = clothingTypeService.findActiveClothingTypes();
        }
        model.addAttribute("clothingTypes", clothingTypes);
        return "administration/clothing-types/clothing-type-list";
    }

    @GetMapping("/new")
    public String showNewClothingTypeForm(Model model) {
        ClothingTypeDTO clothingTypeDTO = new ClothingTypeDTO();
        clothingTypeDTO.setActive(true);

        model.addAttribute("clothingTypeDTO", clothingTypeDTO);
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowy typ odzieży");

        return "administration/clothing-types/clothing-type-form";
    }

    @PostMapping("/new")
    public String createClothingType(@Valid @ModelAttribute("clothingTypeDTO") ClothingTypeDTO clothingTypeDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy typ odzieży");
            return "administration/clothing-types/clothing-type-form";
        }

        try {
            ClothingTypeDTO createdClothingType = clothingTypeService.createClothingType(clothingTypeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Typ odzieży został pomyślnie dodany.");
            return "redirect:/administration/clothing-types";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas dodawania typu odzieży: " + e.getMessage());
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowy typ odzieży");
            return "administration/clothing-types/clothing-type-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ClothingTypeDTO clothingTypeDTO = clothingTypeService.findById(id);

            model.addAttribute("clothingTypeDTO", clothingTypeDTO);
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja typu odzieży");

            return "administration/clothing-types/clothing-type-form";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/administration/clothing-types";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateClothingType(@PathVariable Long id,
                                    @Valid @ModelAttribute("clothingTypeDTO") ClothingTypeDTO clothingTypeDTO,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja typu odzieży");
            return "administration/clothing-types/clothing-type-form";
        }

        try {
            ClothingTypeDTO updatedClothingType = clothingTypeService.updateClothingType(id, clothingTypeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Typ odzieży został pomyślnie zaktualizowany.");
            return "redirect:/administration/clothing-types";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Wystąpił błąd podczas aktualizacji typu odzieży: " + e.getMessage());
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edycja typu odzieży");
            return "administration/clothing-types/clothing-type-form";
        }
    }

    @PostMapping("/{id}")
    public String deleteClothingType(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clothingTypeService.deleteClothingType(id);
            redirectAttributes.addFlashAttribute("successMessage", "Typ odzieży został pomyślnie usunięty.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas usuwania typu odzieży: " + e.getMessage());
        }
        return "redirect:/administration/clothing-types";
    }

    /**
     * API endpoint to fetch clothing type details for use in forms
     * @param id The ID of the clothing type to fetch
     * @return ClothingTypeDTO as JSON
     */
    @GetMapping("/{id}/details")
    @ResponseBody
    public ResponseEntity<ClothingTypeDTO> getClothingTypeDetails(@PathVariable Long id) {
        try {
            ClothingTypeDTO clothingTypeDTO = clothingTypeService.findById(id);
            return ResponseEntity.ok(clothingTypeDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
