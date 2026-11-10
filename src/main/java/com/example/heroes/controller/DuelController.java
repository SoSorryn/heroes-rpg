package com.example.heroes.controller;

import com.example.heroes.model.User;
import com.example.heroes.repository.UserRepository;
import com.example.heroes.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DuelController {

    private final UserRepository userRepository;
    private final GameService gameService;
    private final Random random = new Random();

    @Transactional(readOnly = true)
    @GetMapping("/duel/list")
    public List<Map<String, Object>> list(Authentication auth,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        User me = userRepository.findByUsername(auth.getName()).orElseThrow();
        int safeSize = Math.max(1, Math.min(size, 50));

        return userRepository.findAll(PageRequest.of(Math.max(0, page), safeSize)).stream()
                .filter(u -> !Objects.equals(u.getId(), me.getId()))
                .filter(u -> u.getHeroClass() != null)
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "name", u.getDisplayName(),
                        "class", u.getHeroClass().getTitle(),
                        "gold", u.getGold(),
                        "power", totalPower(u)
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    @PostMapping("/duel/{id}")
    public Map<String, String> fight(@PathVariable Long id, Authentication auth) {
        User me = userRepository.findByUsername(auth.getName()).orElseThrow();
        User enemy = userRepository.findById(id).orElse(null);

        if (enemy == null) return Map.of("message", "Соперник не найден.");
        if (Objects.equals(me.getId(), enemy.getId())) return Map.of("message", "Нельзя дуэлиться с собой.");
        if (me.getHeroClass() == null || enemy.getHeroClass() == null)
            return Map.of("message", "Оба должны выбрать класс.");
        if (me.getGold() < 20) return Map.of("message", "Нужно минимум 20 золота для дуэли.");

        int myPower  = totalPower(me) + random.nextInt(20);
        int hisPower = totalPower(enemy) + random.nextInt(20);

        int stake = Math.max(10, Math.min(100, (Math.min(me.getGold(), enemy.getGold()) * 20) / 100));
        if (enemy.getGold() < stake) return Map.of("message", "У соперника не хватает золота для ставки.");

        if (myPower >= hisPower) {
            me.setGold(me.getGold() + stake);
            enemy.setGold(enemy.getGold() - stake);
            me.setDuelWins(me.getDuelWins() + 1);
            enemy.setDuelLosses(enemy.getDuelLosses() + 1);
            userRepository.save(me);
            userRepository.save(enemy);
            return Map.of("message", "🏆 Победа! +" + stake + " 💰.");
        } else {
            me.setGold(me.getGold() - stake);
            enemy.setGold(enemy.getGold() + stake);
            me.setDuelLosses(me.getDuelLosses() + 1);
            enemy.setDuelWins(enemy.getDuelWins() + 1);
            userRepository.save(me);
            userRepository.save(enemy);
            return Map.of("message", "💀 Поражение. −" + stake + " 💰.");
        }
    }

    private int totalPower(User u) {
        return Math.max(0, gameService.getTotalStr(u))
                + Math.max(0, gameService.getTotalMag(u))
                + Math.max(0, gameService.getTotalAgi(u));
    }
}