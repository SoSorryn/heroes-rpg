package com.example.heroes.controller;

import com.example.heroes.model.Item;
import com.example.heroes.model.User;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DailyRewardController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Random random = new Random();

    @PostMapping("/daily-reward")
    public Map<String, String> claim(Authentication auth) {
        // ИСПРАВЛЕНО: используем findByUsername вместо findByUsernameWithInventory
        User u = userRepository.findByUsername(auth.getName()).orElseThrow();

        LocalDateTime last = u.getLastDailyReward();
        if (last != null && last.isAfter(LocalDateTime.now().minusDays(1))) {
            return Map.of("message", "Уже забрано сегодня. Возвращайтесь завтра.");
        }
        u.setLastDailyReward(LocalDateTime.now());

        int roll = random.nextInt(100);
        if (roll < 50) {
            int gold = 30 + random.nextInt(40);
            u.setGold(u.getGold() + gold);
            userRepository.save(u);
            return Map.of("message", "🎁 Ежедневная награда: " + gold + " золота!");
        } else if (roll < 85) {
            List<Item> all = itemRepository.findAll();
            if (!all.isEmpty()) {
                Item it = all.get(random.nextInt(all.size()));
                u.getInventory().add(it);
                userRepository.save(u);
                return Map.of("message", "🎁 Ежедневная награда: " + it.getIcon() + " " + it.getName() + "!");
            }
            u.setGold(u.getGold() + 50);
            userRepository.save(u);
            return Map.of("message", "🎁 Награда: 50 золота!");
        } else {
            u.setGold(u.getGold() + 120);
            userRepository.save(u);
            return Map.of("message", "🎁 Джекпот! +120 золота!");
        }
    }
}