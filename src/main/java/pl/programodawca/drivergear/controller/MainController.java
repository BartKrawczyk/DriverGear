package pl.programodawca.drivergear.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.programodawca.drivergear.service.UserService_Old;

@Controller
public class MainController {

    private final UserService_Old userService;

    public MainController(UserService_Old userService) {
        this.userService = userService;
    }

//    @GetMapping("/")
//    public String home() {
//        return "home";
//    }

    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "login";
    }

}


