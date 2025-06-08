package pl.programodawca.drivergear.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/administration")
public class AdministrationController {

    @GetMapping
    public String showAdministrationDashboard(Model model, Authentication authentication) {
        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("title", "Panel Administracyjny");
        return "administration/administration-dashboard"; // zmiana tutaj
    }
}


