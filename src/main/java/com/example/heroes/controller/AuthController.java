package com.example.heroes.controller;

import com.example.heroes.model.User;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/register")
    public String registerForm() { return "auth/register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String displayName,
                           @RequestParam String password,
                           @RequestParam String passwordConfirm,
                           Model model) {
        String u = username == null ? "" : username.trim();
        String dn = displayName == null ? "" : displayName.trim();
        String p = password == null ? "" : password;

        if (u.length() < 3 || u.length() > 50) {
            model.addAttribute("error", "Логин должен быть от 3 до 50 символов");
            return "auth/register";
        }
        if (!u.matches("^[a-zA-Z0-9_.-]+$")) {
            model.addAttribute("error", "Логин может содержать только буквы, цифры и _ . -");
            return "auth/register";
        }
        if (!p.equals(passwordConfirm)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "auth/register";
        }
        if (p.length() < 6) {
            model.addAttribute("error", "Пароль должен быть не короче 6 символов");
            return "auth/register";
        }
        if (dn.length() > 50) {
            model.addAttribute("error", "Отображаемое имя не длиннее 50 символов");
            return "auth/register";
        }
        if (userRepository.existsByUsername(u)) {
            model.addAttribute("error", "Такой логин уже занят");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(u);
        user.setDisplayName(dn.isBlank() ? u : dn);
        user.setPassword(passwordEncoder.encode(p));
        user.setAdmin(false);
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}