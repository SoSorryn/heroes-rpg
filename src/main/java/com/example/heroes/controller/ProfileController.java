package com.example.heroes.controller;

import com.example.heroes.model.HeroClass;
import com.example.heroes.model.Item;
import com.example.heroes.model.User;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import com.example.heroes.service.AchievementService;
import com.example.heroes.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final GameService gameService;
    private final AchievementService achievementService;

    private User current(Authentication auth) {
        // Используем стандартный метод findByUsername вместо findByUsernameWithInventory
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    @GetMapping("/")
    public String index(Authentication auth, Model model) {
        User user = current(auth);
        model.addAttribute("user", user);
        if (user.getHeroClass() != null) {
            model.addAttribute("totalStr", gameService.getTotalStr(user));
            model.addAttribute("totalMag", gameService.getTotalMag(user));
            model.addAttribute("totalAgi", gameService.getTotalAgi(user));
        }
        model.addAttribute("achievements", achievementService.forUser(user));
        model.addAttribute("dailyAvailable",
                user.getLastDailyReward() == null ||
                        user.getLastDailyReward().isBefore(LocalDateTime.now().minusDays(1)));
        model.addAttribute("storyProgress", user.getStoryProgress());
        return "index";
    }

    @PostMapping("/profile/select-class")
    public String selectClass(@RequestParam String heroClass, Authentication auth, RedirectAttributes ra) {
        User u = current(auth);
        ra.addFlashAttribute("message", gameService.selectClass(u, HeroClass.valueOf(heroClass)));
        return "redirect:/";
    }

    @GetMapping("/inventory")
    public String inventory(Authentication auth, Model model) {
        User u = current(auth);
        model.addAttribute("user", u);
        model.addAttribute("totalStr", u.getHeroClass() != null ? gameService.getTotalStr(u) : 0);
        model.addAttribute("totalMag", u.getHeroClass() != null ? gameService.getTotalMag(u) : 0);
        model.addAttribute("totalAgi", u.getHeroClass() != null ? gameService.getTotalAgi(u) : 0);

        List<Item> visibleInventory = u.getInventory().stream()
                .filter(it -> !u.isEquipped(it))
                .collect(Collectors.toList());

        model.addAttribute("visibleInventory", visibleInventory);
        model.addAttribute("visibleCount", visibleInventory.size());

        return "inventory";
    }

    @PostMapping("/inventory/equip/{id}")
    public String equipItem(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User u = current(auth);
        itemRepository.findById(id).ifPresent(item -> {
            if (!"any".equals(item.getForClass()) && (u.getHeroClass() == null || !u.getHeroClass().getCode().equals(item.getForClass()))) {
                ra.addFlashAttribute("message", "❌ Этот предмет не для вашего класса!");
                return;
            }
            switch (item.getSlot()) {
                case "WEAPON" -> u.setWeapon(item);
                case "HELMET" -> u.setHelmet(item);
                case "CHEST" -> u.setChest(item);
                case "RING" -> u.setRing(item);
            }
            userRepository.save(u);
            ra.addFlashAttribute("message", "✅ Надет предмет: " + item.getName());
        });
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/unequip")
    public String unequipItem(@RequestParam String slot, Authentication auth, RedirectAttributes ra) {
        User u = current(auth);
        switch (slot) {
            case "WEAPON" -> { if (u.getWeapon() != null) { u.setWeapon(null); ra.addFlashAttribute("message", "✅ Снято оружие"); } }
            case "HELMET" -> { if (u.getHelmet() != null) { u.setHelmet(null); ra.addFlashAttribute("message", "✅ Снят шлем"); } }
            case "CHEST"  -> { if (u.getChest()  != null) { u.setChest(null);  ra.addFlashAttribute("message", "✅ Снята броня"); } }
            case "RING"   -> { if (u.getRing()   != null) { u.setRing(null);   ra.addFlashAttribute("message", "✅ Снято кольцо"); } }
        }
        userRepository.save(u);
        return "redirect:/inventory";
    }
}