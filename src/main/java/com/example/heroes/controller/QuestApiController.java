package com.example.heroes.controller;

import com.example.heroes.model.Biome;
import com.example.heroes.model.User;
import com.example.heroes.repository.UserRepository;
import com.example.heroes.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuestApiController {

    private final UserRepository userRepository;
    private final GameService gameService;

    private User current(Authentication auth) {
        return userRepository.findByUsername(auth.getName()).orElseThrow();
    }

    @PostMapping("/quest/start")
    public Map<String, String> startQuest(@RequestParam String biome, Authentication auth) {
        User user = current(auth);
        String msg = gameService.startQuest(user, Biome.valueOf(biome));
        return Map.of("message", msg);
    }

    /**
     * @param playerHp Текущее HP игрока в браузере. Если <= 0 — поражение.
     */
    @PostMapping("/quest/finish")
    public Map<String, String> finishQuest(@RequestParam(defaultValue = "100") int playerHp,
                                           Authentication auth) {
        User user = current(auth);
        boolean isAdmin = user.isAdmin();

        // Если HP игрока <= 0, это гарантированное поражение (кроме админа)
        boolean forceLose = playerHp <= 0 && !isAdmin;

        System.out.println("========================================");
        System.out.println("=== FINISH QUEST ===");
        System.out.println("playerHp = " + playerHp);
        System.out.println("isAdmin = " + isAdmin);
        System.out.println("forceLose = " + forceLose);
        System.out.println("user.questActive = " + user.isQuestActive());
        System.out.println("========================================");

        String msg = gameService.finishQuest(user, isAdmin, forceLose);

        System.out.println("RESULT MESSAGE: " + msg);
        System.out.println("========================================");

        return Map.of("message", msg);
    }

    @GetMapping("/quest/combat-status")
    public Map<String, Object> combatStatus(Authentication auth) {
        User user = current(auth);
        if (!user.isQuestActive()) return Map.of("error", "not active");

        int pAtk = gameService.getTotalStr(user) + gameService.getTotalMag(user) + gameService.getTotalAgi(user);
        int pDef = gameService.getTotalAgi(user);
        int pHp = 100 + gameService.getTotalStr(user) * 10;

        int eAtk = user.getEnemyStr() + user.getEnemyMag() + user.getEnemyAgi();
        int eDef = user.getEnemyAgi();
        int eHp = user.getEnemyHp();

        int playerDamagePerSec = Math.max(1, pAtk - eDef / 2);
        int enemyDamagePerSec = Math.max(1, eAtk - pDef / 2);

        String enemyImage = user.getEnemyImage() != null && !user.getEnemyImage().isEmpty()
                ? "/images/" + user.getEnemyImage()
                : "";

        return Map.of(
                "playerHp", pHp,
                "playerMaxHp", pHp,
                "enemyHp", eHp,
                "enemyMaxHp", user.getEnemyMaxHp(),
                "enemyName", user.getEnemyName(),
                "enemyImage", enemyImage,
                "playerDmg", playerDamagePerSec,
                "enemyDmg", enemyDamagePerSec
        );
    }
}