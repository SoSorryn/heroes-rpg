package com.example.heroes.controller;

import com.example.heroes.model.User;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SkillController {

    private final UserRepository userRepository;

    @Transactional
    @PostMapping("/skill/up")
    public Map<String, String> up(@RequestParam String stat, Authentication auth) {
        User u = userRepository.findByUsername(auth.getName()).orElseThrow();
        if (u.getSkillPoints() <= 0) return Map.of("message", "Нет очков навыков.");
        if (!stat.matches("str|mag|agi")) return Map.of("message", "Неизвестный стат.");

        switch (stat) {
            case "str" -> u.setSkillStrBonus(u.getSkillStrBonus() + 1);
            case "mag" -> u.setSkillMagBonus(u.getSkillMagBonus() + 1);
            case "agi" -> u.setSkillAgiBonus(u.getSkillAgiBonus() + 1);
        }
        u.setSkillPoints(u.getSkillPoints() - 1);
        userRepository.save(u);
        return Map.of("message", "✅ +1 " + stat.toUpperCase());
    }
}