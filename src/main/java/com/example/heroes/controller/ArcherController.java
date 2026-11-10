package com.example.heroes.controller;

import com.example.heroes.model.Archer;
import com.example.heroes.repository.ArcherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/archer")
@RequiredArgsConstructor
public class ArcherController {

    private final ArcherRepository repository;

    private void meta(Model model) {
        model.addAttribute("title", "Лучники");
        model.addAttribute("icon", "🏹");
        model.addAttribute("cssClass", "archer");
        model.addAttribute("base", "/archer");
        model.addAttribute("extraLabel", "Стрелы");
        model.addAttribute("extraIcon", "🎯");
    }

    private Archer find(Long id) {
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
        model.addAttribute("hero", new Archer());
        model.addAttribute("heading", "Новый лучник");
        model.addAttribute("action", "/archer/add");
        return "hero/form";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("hero") Archer hero) {
        hero.setId(null);
        repository.save(hero);
        return "redirect:/archer";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        meta(model);
        model.addAttribute("hero", find(id));
        model.addAttribute("heading", "Редактирование: лучник");
        model.addAttribute("action", "/archer/" + id + "/edit");
        return "hero/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute("hero") Archer hero) {
        find(id);
        hero.setId(id);
        repository.save(hero);
        return "redirect:/archer";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        repository.deleteById(id);
        return "redirect:/archer";
    }
}