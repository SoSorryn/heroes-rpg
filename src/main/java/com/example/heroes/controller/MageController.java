package com.example.heroes.controller;

import com.example.heroes.model.Mage;
import com.example.heroes.repository.MageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/mage")
@RequiredArgsConstructor
public class MageController {

    private final MageRepository repository;

    private void meta(Model model) {
        model.addAttribute("title", "Маги");
        model.addAttribute("icon", "🔮");
        model.addAttribute("cssClass", "mage");
        model.addAttribute("base", "/mage");
        model.addAttribute("extraLabel", "Мана");
        model.addAttribute("extraIcon", "💧");
    }

    private Mage find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public String list(Model model) {
        meta(model);
        model.addAttribute("heroes", repository.findAll());
        return "hero/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        meta(model);
        model.addAttribute("hero", new Mage());
        model.addAttribute("heading", "Новый маг");
        model.addAttribute("action", "/mage/add");
        return "hero/form";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("hero") Mage hero) {
        hero.setId(null);
        repository.save(hero);
        return "redirect:/mage";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        meta(model);
        model.addAttribute("hero", find(id));
        model.addAttribute("heading", "Редактирование: маг");
        model.addAttribute("action", "/mage/" + id + "/edit");
        return "hero/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute("hero") Mage hero) {
        find(id);
        hero.setId(id);
        repository.save(hero);
        return "redirect:/mage";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/mage";
    }
}