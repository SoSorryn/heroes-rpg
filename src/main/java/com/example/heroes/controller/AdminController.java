package com.example.heroes.controller;

import com.example.heroes.model.Item;
import com.example.heroes.model.User;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import com.example.heroes.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameService gameService;

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("items", itemRepository.findAll());
        return "admin/users";
    }

    @PostMapping("/users/{id}/force-finish")
    public String forceFinish(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        if (u.isQuestActive()) {
            // forceLose = false: админ принудительно завершает квест
            gameService.finishQuest(u, true, false);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-admin")
    public String toggleAdmin(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        if (!"admin".equals(u.getUsername())) {
            u.setAdmin(!u.isAdmin());
            userRepository.save(u);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        User u = userRepository.findById(id).orElseThrow();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow();
        if (!"admin".equals(u.getUsername())) {
            userRepository.delete(u);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/skip-story")
    public String skipStory(@PathVariable Long id, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        if (u.getStoryProgress() < 7) {
            u.setStoryProgress(u.getStoryProgress() + 1);
            userRepository.save(u);
            ra.addFlashAttribute("message", "📜 Глава пропущена. Прогресс: " + u.getStoryProgress() + "/7");
        } else {
            ra.addFlashAttribute("message", "📜 Сюжет уже полностью пройден.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unlock-all")
    public String unlockAll(@PathVariable Long id, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setStoryProgress(7);
        userRepository.save(u);
        ra.addFlashAttribute("message", "🗺️ Все локации открыты!");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/cancel-quest")
    public String cancelQuest(@PathVariable Long id, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        if (u.isQuestActive()) {
            u.setQuestActive(false);
            u.setCurrentBiome(null);
            u.setQuestEndTime(null);
            userRepository.save(u);
            ra.addFlashAttribute("message", "❌ Квест отменён без награды.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/add-gold")
    public String addGold(@PathVariable Long id, @RequestParam int amount, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setGold(u.getGold() + amount);
        userRepository.save(u);
        ra.addFlashAttribute("message", "💰 Начислено " + amount + " золота.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/give-item")
    public String giveItem(@PathVariable Long id, @RequestParam Long itemId, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        u.getInventory().add(item);
        userRepository.save(u);
        ra.addFlashAttribute("message", "🎁 Выдан предмет: " + item.getName());
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-story")
    public String resetStory(@PathVariable Long id, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setStoryProgress(0);
        userRepository.save(u);
        ra.addFlashAttribute("message", "🔄 Сюжет сброшен. Глава 1.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/clear-achievements")
    public String clearAchievements(@PathVariable Long id, RedirectAttributes ra) {
        userRepository.clearAchievements(id);
        ra.addFlashAttribute("message", "🏅 Достижения очищены.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/add-skill-point")
    public String addSkillPoint(@PathVariable Long id, @RequestParam int amount, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setSkillPoints(u.getSkillPoints() + amount);
        userRepository.save(u);
        ra.addFlashAttribute("message", "⚡ Начислено " + amount + " очков навыков.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/set-class")
    public String setClass(@PathVariable Long id, @RequestParam String heroClass, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setHeroClass(com.example.heroes.model.HeroClass.valueOf(heroClass));
        userRepository.save(u);
        ra.addFlashAttribute("message", "🎭 Класс изменён на " + heroClass);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-class")
    public String resetClass(@PathVariable Long id, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setHeroClass(null);
        u.setWeapon(null);
        u.setHelmet(null);
        u.setChest(null);
        u.setRing(null);
        u.setSkillStrBonus(0);
        u.setSkillMagBonus(0);
        u.setSkillAgiBonus(0);
        u.setSkillPoints(0);
        userRepository.save(u);
        ra.addFlashAttribute("message", "🔄 Класс и экипировка сброшены.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-gold")
    public String resetGold(@PathVariable Long id, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        u.setGold(0);
        userRepository.save(u);
        ra.addFlashAttribute("message", "💰 Баланс сброшен в 0.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/add-stat")
    public String addStat(@PathVariable Long id, @RequestParam String stat, @RequestParam int amount, RedirectAttributes ra) {
        User u = userRepository.findById(id).orElseThrow();
        switch (stat) {
            case "str" -> u.setSkillStrBonus(u.getSkillStrBonus() + amount);
            case "mag" -> u.setSkillMagBonus(u.getSkillMagBonus() + amount);
            case "agi" -> u.setSkillAgiBonus(u.getSkillAgiBonus() + amount);
        }
        userRepository.save(u);
        ra.addFlashAttribute("message", "📈 " + stat.toUpperCase() + " увеличено на " + amount + ".");
        return "redirect:/admin/users";
    }
}