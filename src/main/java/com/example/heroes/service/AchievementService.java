package com.example.heroes.service;

import com.example.heroes.model.Achievement;
import com.example.heroes.model.User;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final UserRepository userRepository;

    public static final List<Achievement> ALL = List.of(
            new Achievement("first_quest", "🎯", "Первый шаг",       "Завершите первый квест"),
            new Achievement("wins_10",     "🏆", "Ветеран",          "10 успешных квестов"),
            new Achievement("wins_50",     "👑", "Легенда",          "50 успешных квестов"),
            new Achievement("gold_500",    "💰", "Богач",            "500 золота"),
            new Achievement("gold_2000",   "💎", "Магнат",           "2000 золота"),
            new Achievement("legendary",   "⭐", "Коллекционер",     "Найдите предмет 5-й редкости"),
            new Achievement("all_biomes",  "🗺️", "Путешественник",   "Посетите все 7 биомов"),
            new Achievement("duel_first",  "⚔️", "Дуэлянт",          "Выиграйте первую дуэль"),
            new Achievement("duel_10",     "🥇", "Гладиатор",        "Выиграйте 10 дуэлей"),
            new Achievement("full_equip",  "🛡️", "Экипирован",       "Наденьте 4 предмета"),
            new Achievement("story_3",     "📖", "Сказитель",        "Пройдите 3 главы"),
            new Achievement("story_end",   "🏆", "Спаситель мира",   "Завершите сюжет")
    );

    public List<AchievementView> forUser(User user) {
        Set<String> unlocked = userRepository.findUnlockedAchievements(user.getId());
        return ALL.stream()
                .map(a -> new AchievementView(a, unlocked.contains(a.getCode())))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Achievement> checkAndUnlock(User user, QuestStats s) {
        Set<String> already = userRepository.findUnlockedAchievements(user.getId());
        List<Achievement> newly = new ArrayList<>();
        for (Achievement a : ALL) {
            if (already.contains(a.getCode())) continue;
            if (isEligible(a.getCode(), user, s)) {
                userRepository.unlockAchievement(user.getId(), a.getCode());
                newly.add(a);
            }
        }
        return newly;
    }

    private boolean isEligible(String code, User user, QuestStats s) {
        return switch (code) {
            case "first_quest" -> s.totalQuests() >= 1;
            case "wins_10"     -> s.wins() >= 10;
            case "wins_50"     -> s.wins() >= 50;
            case "gold_500"    -> user.getGold() >= 500;
            case "gold_2000"   -> user.getGold() >= 2000;
            case "legendary"   -> user.getInventory().stream().anyMatch(i -> i.getRarity() >= 5);
            case "all_biomes"  -> s.visitedBiomes() >= 7;
            case "duel_first"  -> s.duelWins() >= 1;
            case "duel_10"     -> s.duelWins() >= 10;
            case "full_equip"  -> user.getWeapon() != null && user.getHelmet() != null
                    && user.getChest() != null && user.getRing() != null;
            case "story_3"     -> user.getStoryProgress() >= 3;
            case "story_end"   -> user.getStoryProgress() >= 7;
            default -> false;
        };
    }

    public record AchievementView(Achievement achievement, boolean unlocked) {}
    public record QuestStats(int totalQuests, int wins, int visitedBiomes, int duelWins) {}
}