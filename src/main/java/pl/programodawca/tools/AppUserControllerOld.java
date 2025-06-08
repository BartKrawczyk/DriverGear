package pl.programodawca.tools;

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
@RequestMapping("/app-users")
@RequiredArgsConstructor
public class AppUserControllerOld {

    private final AppUserService appUserService;

    // Wyświetlanie listy użytkowników
    @GetMapping
    public String listAppUsers(Model model) {
        List<AppUserDTO> users = appUserService.getAllUsers();
        model.addAttribute("title", "Lista użytkowników");
        model.addAttribute("appUsers", users);
        return "app-user-list";
    }

    // Formularz dodawania nowego użytkownika
    @GetMapping("/new")
    public String newAppUserForm(Model model) {
        model.addAttribute("title", "Dodaj nowego użytkownika");
        model.addAttribute("createUserDTO", new CreateUserDTO());  // zmiana z "appUser" na "createUserDTO"
        return "app-user-form";
    }

    // Zapisywanie nowego użytkownika
    @PostMapping
    public String createUser(@Valid @ModelAttribute("createUserDTO") CreateUserDTO createUserDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Dodaj nowego użytkownika");
            // Dodaj szczegóły błędów do modelu
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "app-user-form";
        }

        try {
            appUserService.createUser(createUserDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został utworzony pomyślnie");
            return "redirect:/app-users";
        } catch (Exception e) {
            model.addAttribute("title", "Dodaj nowego użytkownika");
            model.addAttribute("errorMessage", "Wystąpił błąd podczas tworzenia użytkownika: " + e.getMessage());
            // Zachowaj wprowadzone dane
            model.addAttribute("createUserDTO", createUserDTO);
            return "app-user-form";
        }
    }


    // Aktualizacja użytkownika
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute UpdateUserDTO updateUserDTO,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("title", "Edytuj użytkownika");
            return "app-user-form";
        }

        try {
            updateUserDTO.setId(id); // upewniamy się, że ID jest ustawione
            appUserService.updateUser(id, updateUserDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został zaktualizowany pomyślnie");
            return "redirect:/app-users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Wystąpił błąd podczas aktualizacji użytkownika: " + e.getMessage());
            return "redirect:/app-users/" + id + "/edit";
        }
    }
    // Wyświetlanie formularza edycji
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            AppUserDTO appUser = appUserService.findById(id);
            UpdateUserDTO updateUserDTO = new UpdateUserDTO();
            updateUserDTO.setId(id);
            updateUserDTO.setFirstName(appUser.getFirstName());
            updateUserDTO.setLastName(appUser.getLastName());
            updateUserDTO.setRole(appUser.getRole());
            updateUserDTO.setLocked(appUser.isLocked());

            model.addAttribute("updateUserDTO", updateUserDTO);
            model.addAttribute("title", "Edytuj użytkownika");
            return "app-user-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie znaleziono użytkownika o ID: " + id);
            return "redirect:/app-users";
        }
    }




    // Usuwanie użytkownika
    @GetMapping("/{id}/delete")
    public String deleteAppUser(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        try {
            appUserService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Użytkownik został pomyślnie usunięty.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Wystąpił błąd podczas usuwania użytkownika: " + e.getMessage());
        }
        return "redirect:/app-users";
    }

    // Metoda pomocnicza do konwersji AppUserDTO na UpdateUserDTO
    private UpdateUserDTO convertToUpdateDTO(AppUserDTO userDTO) {
        return UpdateUserDTO.builder()
                .id(userDTO.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .role(userDTO.getRole())
                .locked(userDTO.isLocked())
                .build();
    }
}

