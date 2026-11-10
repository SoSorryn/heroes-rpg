package com.example.heroes.controller;

import com.example.heroes.model.Item;
import com.example.heroes.model.User;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/market")
@RequiredArgsConstructor
public class MarketController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User current(Authentication auth) {
        // Стандартный метод без WithInventory
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    private long discountItemId() {
        List<Item> all = itemRepository.findAll();
        if (all.isEmpty()) return -1;
        int dayOfYear = LocalDate.now().getDayOfYear();
        return all.get(dayOfYear % all.size()).getId();
    }

    @GetMapping
    public String market(@RequestParam(required = false) String sort,
                         @RequestParam(required = false) String slot,
                         @RequestParam(required = false) String type,
                         Authentication auth, Model model) {
        User user = current(auth);
        model.addAttribute("user", user);

        List<Item> items = itemRepository.findAll();

        if (slot != null && !slot.isEmpty()) {
            items = items.stream().filter(i -> slot.equals(i.getSlot())).collect(Collectors.toList());
        }
        if (type != null && !type.isEmpty()) {
            items = items.stream().filter(i -> type.equals(i.getType())).collect(Collectors.toList());
        }

        if ("price_asc".equals(sort)) {
            items.sort(Comparator.comparingInt(Item::getPrice));
        } else if ("price_desc".equals(sort)) {
            items.sort((i1, i2) -> Integer.compare(i2.getPrice(), i1.getPrice()));
        } else if ("rarity".equals(sort)) {
            items.sort((i1, i2) -> Integer.compare(i2.getRarity(), i1.getRarity()));
        } else if ("drop".equals(sort)) {
            items.sort((i1, i2) -> Integer.compare(i2.getDropChance(), i1.getDropChance()));
        } else {
            items.sort(Comparator.comparing(Item::getName));
        }

        List<Item> mySellable = user.getInventory().stream()
                .filter(i -> !user.isEquipped(i))
                .sorted(Comparator.comparing(Item::getName))
                .collect(Collectors.toList());

        model.addAttribute("allItems", items);
        model.addAttribute("mySellable", mySellable);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentSlot", slot);
        model.addAttribute("currentType", type);
        model.addAttribute("discountItemId", discountItemId());
        return "market";
    }

    @PostMapping("/buy/{id}")
    public String buyItem(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User user = current(auth);
        long discounted = discountItemId();
        itemRepository.findById(id).ifPresent(item -> {
            int price = item.getPrice();
            boolean isDiscount = item.getId() != null && item.getId() == discounted;
            if (isDiscount) price = (int) Math.round(price * 0.7);

            if (user.getGold() >= price) {
                user.setGold(user.getGold() - price);
                user.getInventory().add(item);
                userRepository.save(user);
                String suffix = isDiscount ? " (со скидкой −30%!)" : "";
                ra.addFlashAttribute("message", "✅ Куплен предмет: " + item.getName() + suffix + " за " + price);
            } else {
                ra.addFlashAttribute("message", "❌ Недостаточно золота! Нужно " + price);
            }
        });
        return "redirect:/market";
    }

    @PostMapping("/sell/{id}")
    public String sellItem(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        User user = current(auth);
        user.getInventory().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .ifPresent(item -> {
                    if (user.isEquipped(item)) {
                        ra.addFlashAttribute("message", "❌ Нельзя продать надетый предмет. Сначала снимите его.");
                        return;
                    }
                    int sellPrice = item.getPrice() / 2;
                    user.getInventory().remove(item);
                    user.setGold(user.getGold() + sellPrice);
                    userRepository.save(user);
                    ra.addFlashAttribute("message", "💰 Продан предмет: " + item.getName() + " за " + sellPrice);
                });
        return "redirect:/market";
    }
}