package com.example.demo.controller.view;

import com.example.demo.domain.dto.RegisterDTO;
import com.example.demo.domain.exceptions.UserAlreadyExistsException;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthViewController {
    final private UserService userService;

    public AuthViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") RegisterDTO req, BindingResult result) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.register(req);
            return "redirect:/login?registered";
        } catch (UserAlreadyExistsException e) {
            result.rejectValue("username", "error.user", "Username already exists");
            return "register";
        }
    }
}
