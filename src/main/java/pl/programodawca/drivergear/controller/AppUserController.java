package pl.programodawca.drivergear.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.programodawca.drivergear.dto.AppUserDTO;
import pl.programodawca.drivergear.dto.CreateUserDTO;
import pl.programodawca.drivergear.dto.UpdateUserDTO;
import pl.programodawca.drivergear.service.AppUserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/administration/users")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    // Wyświetlanie listy użytkowników
    @GetMapping
    public String listAppUsers(Model model) {
        List<AppUserDTO> users = appUserService.getAllUsers();
        model.addAttribute("appUsers", users);
        return "administration/users/app-user-list";
    }

    // Formularz dodawania nowego użytkownika
    @GetMapping("/new")
    public String newAppUserForm(Model model) {
        model.addAttribute("createUserDTO", new CreateUserDTO());
        model.addAttribute("isNew", true);
        model.addAttribute("title", "Dodaj nowego użytkownika");
        return "administration/users/app-user-create-form";
    }

    // Zapisywanie nowego użytkownika
    @PostMapping
    public String createUser(@Valid @ModelAttribute("createUserDTO") CreateUserDTO createUserDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowego użytkownika");
            return "administration/users/app-user-create-form";
        }

        try {
            appUserService.createUser(createUserDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został utworzony pomyślnie");
            return "redirect:/administration/users";
        } catch (Exception e) {
            model.addAttribute("isNew", true);
            model.addAttribute("title", "Dodaj nowego użytkownika");
            model.addAttribute("errorMessage", e.getMessage());
            return "administration/users/app-user-create-form";
        }
    }

    // Wyświetlanie formularza edycji
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            AppUserDTO appUser = appUserService.findById(id);
            UpdateUserDTO updateUserDTO = convertToUpdateDTO(appUser);
            model.addAttribute("updateUserDTO", updateUserDTO);
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj użytkownika");
            return "administration/users/app-user-edit-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono użytkownika o ID: " + id);
            return "redirect:/administration/users";
        }
    }

    // Aktualizacja użytkownika
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("updateUserDTO") UpdateUserDTO updateUserDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj użytkownika");
            return "administration/users/app-user-edit-form";
        }

        try {
            updateUserDTO.setId(id);
            appUserService.updateUser(id, updateUserDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został zaktualizowany pomyślnie");
            return "redirect:/administration/users";
        } catch (Exception e) {
            model.addAttribute("isNew", false);
            model.addAttribute("title", "Edytuj użytkownika");
            model.addAttribute("errorMessage", e.getMessage());
            return "administration/users/app-user-edit-form";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteAppUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appUserService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został pomyślnie usunięty");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania użytkownika: " + e.getMessage());
        }
        return "redirect:/administration/users";
    }

    private UpdateUserDTO convertToUpdateDTO(AppUserDTO userDTO) {
        return UpdateUserDTO.builder()
                .id(userDTO.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .username(userDTO.getUsername())
                .role(userDTO.getRole())
                .locked(userDTO.isLocked())
                .build();
    }
}

