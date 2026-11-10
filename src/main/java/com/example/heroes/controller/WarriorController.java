package com.example.heroes.controller;

import com.example.heroes.model.Warrior;
import com.example.heroes.repository.WarriorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/warrior")
@RequiredArgsConstructor
public class WarriorController {

    private final WarriorRepository repository;

    private void meta(Model model) {
        model.addAttribute("title", "Воины");
        model.addAttribute("icon", "⚔️");
        model.addAttribute("cssClass", "warrior");
        model.addAttribute("base", "/warrior");
        model.addAttribute("extraLabel", "Сила");
        model.addAttribute("extraIcon", "💪");
    }

    private Warrior find(Long id) {
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
        model.addAttribute("hero", new Warrior());
        model.addAttribute("heading", "Новый воин");
        model.addAttribute("action", "/warrior/add");
        return "hero/form";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("hero") Warrior hero) {
        hero.setId(null);
        repository.save(hero);
        return "redirect:/warrior";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        meta(model);
        model.addAttribute("hero", find(id));
        model.addAttribute("heading", "Редактирование: воин");
        model.addAttribute("action", "/warrior/" + id + "/edit");
        return "hero/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute("hero") Warrior hero) {
        find(id);
        hero.setId(id);
        repository.save(hero);
        return "redirect:/warrior";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/warrior";
    }
}