package com.example.heroes.controller;

import com.example.heroes.model.Item;
import com.example.heroes.model.User;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/images/item_icons/uploads/";

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", itemRepository.findAll());
        return "admin/items";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Item item = new Item();
        item.setIcon("🎁");
        item.setRarity(1);
        item.setDropChance(50);
        item.setPrice(50);
        item.setForClass("any");
        item.setSlot("WEAPON");
        item.setType("other");
        model.addAttribute("item", item);
        return "admin/item-form";
    }

    @Transactional
    @PostMapping("/new")
    public String create(@ModelAttribute Item form,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         RedirectAttributes ra) {
        Item it = new Item();
        copyFields(it, form);

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            if (fileName != null) it.setImageName("uploads/" + fileName);
        }

        itemRepository.save(it);
        ra.addFlashAttribute("message", "✅ Предмет создан: " + it.getName());
        return "redirect:/admin/items";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id).orElseThrow();
        model.addAttribute("item", item);
        return "admin/item-form";
    }

    @Transactional
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute Item form,
                       @RequestParam(value = "file", required = false) MultipartFile file,
                       RedirectAttributes ra) {
        Item it = itemRepository.findById(id).orElseThrow();
        copyFields(it, form);

        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            if (fileName != null) it.setImageName("uploads/" + fileName);
        }

        itemRepository.save(it);
        ra.addFlashAttribute("message", "💾 Предмет сохранён: " + it.getName());
        return "redirect:/admin/items";
    }

    @Transactional
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        Item it = itemRepository.findById(id).orElse(null);
        if (it == null) {
            ra.addFlashAttribute("message", "❌ Предмет не найден.");
            return "redirect:/admin/items";
        }
        for (User u : userRepository.findAll()) {
            boolean changed = false;
            if (u.isEquipped(it)) {
                String slot = u.equippedSlot(it);
                if (slot != null) { u.setBySlot(slot, null); changed = true; }
            }
            if (u.getInventory().remove(it)) changed = true;
            if (changed) userRepository.save(u);
        }
        itemRepository.delete(it);
        ra.addFlashAttribute("message", "🗑 Предмет удалён.");
        return "redirect:/admin/items";
    }

    // Вспомогательный метод для копирования полей
    private void copyFields(Item target, Item source) {
        target.setName(source.getName());
        target.setIcon(source.getIcon());
        target.setForClass(source.getForClass());
        target.setType(source.getType());
        target.setSlot(source.getSlot());
        target.setPrice(Math.max(0, source.getPrice()));
        target.setRarity(Math.max(1, Math.min(5, source.getRarity())));
        target.setBonusStr(Math.max(0, source.getBonusStr()));
        target.setBonusMag(Math.max(0, source.getBonusMag()));
        target.setBonusAgi(Math.max(0, source.getBonusAgi()));
        target.setDropChance(Math.max(0, Math.min(100, source.getDropChance())));
    }

    // Сохранение файла на диск
    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String newName = UUID.randomUUID().toString() + ext;
            Path filePath = uploadPath.resolve(newName);
            file.transferTo(filePath.toFile());
            return newName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}