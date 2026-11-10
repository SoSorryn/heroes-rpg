package com.example.heroes.service;

import com.example.heroes.model.*;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final AchievementService achievementService;
    private final Random random = new Random();

    @Transactional
    public String selectClass(User user, HeroClass heroClass) {
        if (user.getHeroClass() != null) return "Класс уже выбран!";
        user.setHeroClass(heroClass);
        userRepository.save(user);
        return "Вы выбрали класс: " + heroClass.getTitle();
    }

    @Transactional
    public String startQuest(User user, Biome biome) {
        if (user.getHeroClass() == null) return "Сначала выберите класс!";
        if (user.isQuestActive()) return "Вы уже в бою!";

        user.setCurrentBiome(biome);
        user.setQuestActive(true);
        user.setEnemyName(biome.getMonsterName());
        user.setEnemyImage(biome.getImageName());

        int reqStr = biome.getReqStrength();
        int reqMag = biome.getReqMagic();
        int reqAgi = biome.getReqAgility();

        int baseHp = 100 + (reqStr + reqMag + reqAgi) * 3;
        user.setEnemyMaxHp(baseHp + random.nextInt(50));
        user.setEnemyHp(user.getEnemyMaxHp());
        user.setEnemyStr(Math.max(1, reqStr + (random.nextInt(9) - 4)));
        user.setEnemyMag(Math.max(1, reqMag + (random.nextInt(9) - 4)));
        user.setEnemyAgi(Math.max(1, reqAgi + (random.nextInt(9) - 4)));

        userRepository.save(user);
        return "Вы вступили в бой с " + user.getEnemyName() + "!";
    }

    @Transactional
    public String finishQuest(User user, boolean isAdmin, boolean forceLose) {
        if (!user.isQuestActive()) {
            System.out.println(">>> finishQuest: user.questActive = false, выходим");
            return "Вы не в бою.";
        }

        Biome biome = user.getCurrentBiome();
        int chance = successChance(user, biome);
        boolean success = isAdmin || (!forceLose && random.nextInt(100) < chance);

        System.out.println(">>> finishQuest: chance=" + chance
                + ", forceLose=" + forceLose
                + ", isAdmin=" + isAdmin
                + ", success=" + success);

        String message;
        if (success) {
            int baseGold = (int) Math.round(25 * biome.getGoldMultiplier());
            int goldFound = baseGold + random.nextInt(10);
            user.setGold(user.getGold() + goldFound);

            user.setWins(user.getWins() + 1);
            if (user.getWins() % 3 == 0) {
                user.setSkillPoints(user.getSkillPoints() + 1);
                message = "✅ Победа! Вы победили " + user.getEnemyName() + ". Найдено " + goldFound + " золота. 🌟 +1 очко навыка!";
            } else {
                message = "✅ Победа! Вы победили " + user.getEnemyName() + " и нашли " + goldFound + " золота!";
            }

            if (isAdmin || random.nextInt(100) < 45) {
                List<Item> allItems = itemRepository.findAll();
                if (!allItems.isEmpty()) {
                    int totalWeight = allItems.stream().mapToInt(Item::getDropChance).sum();
                    Item found = null;
                    if (totalWeight > 0) {
                        int roll = random.nextInt(totalWeight);
                        int current = 0;
                        for (Item it : allItems) {
                            current += it.getDropChance();
                            if (roll < current) { found = it; break; }
                        }
                    }
                    if (found != null) {
                        user.getInventory().add(found);
                        message += " Найден: " + found.getIcon() + " " + found.getName();
                    }
                }
            }

            String storyMsg = advanceStory(user, biome);
            if (storyMsg != null) message += " " + storyMsg;

        } else {
            user.setLosses(user.getLosses() + 1);
            String[] failMessages = {
                    "❌ Поражение! " + user.getEnemyName() + " оказался слишком силён.",
                    "💀 Вы пали в бою. " + user.getEnemyName() + " не оставил шансов.",
                    "🩸 Вас одолел " + user.getEnemyName() + ". Нужно больше силы.",
                    "⚰️ " + user.getEnemyName() + " разорвал вас на части."
            };
            message = failMessages[random.nextInt(failMessages.length)];
        }

        user.setLastBiome(biome);
        user.setQuestActive(false);
        user.setCurrentBiome(null);
        user.setEnemyName(null);
        user.setEnemyImage(null);
        userRepository.save(user);

        AchievementService.QuestStats stats = new AchievementService.QuestStats(
                user.getWins(), user.getWins(), 7, user.getWins()
        );
        achievementService.checkAndUnlock(user, stats);

        return message;
    }

    private String advanceStory(User user, Biome biome) {
        int p = user.getStoryProgress();
        if (p == 0 && biome == Biome.FOREST) { user.setStoryProgress(1); return "📜 Вы нашли Амулет Теней!"; }
        if (p == 1 && biome == Biome.SWAMP) { user.setStoryProgress(2); return "📜 В топях вы узнали имя Дракона."; }
        if (p == 2 && biome == Biome.RUINS) { user.setStoryProgress(3); return "📜 Пророчество гласит: нужен клинок."; }
        if (p == 3 && biome == Biome.SNOW) { user.setStoryProgress(4); return "📜 Ледяные стражи закалили клинок."; }
        if (p == 4 && biome == Biome.VOLCANO) { user.setStoryProgress(5); return "📜 Клинок пылает."; }
        if (p == 5 && biome == Biome.PLAINS) { user.setStoryProgress(6); return "📜 Равнины пали."; }
        if (p == 6 && biome == Biome.DRAGON) { user.setStoryProgress(7); return "🏆 Вы победили Дракона!"; }
        return null;
    }

    private int successChance(User user, Biome biome) {
        double rStr = ratio(getTotalStr(user), biome.getReqStrength());
        double rMag = ratio(getTotalMag(user), biome.getReqMagic());
        double rAgi = ratio(getTotalAgi(user), biome.getReqAgility());
        double minRatio = Math.min(Math.min(rStr, rMag), rAgi);
        if (minRatio >= 1.0) return 100;
        double chance = 100.0 * Math.pow(minRatio, 3);
        int c = (int) Math.round(chance);
        if (c < 1) c = 1;
        if (c > 99) c = 99;
        return c;
    }

    private double ratio(int have, int need) {
        if (need <= 0) return 1.0;
        return Math.min(1.0, (double) have / need);
    }

    public int getTotalStr(User user) {
        int total = user.getHeroClass().getStrength() + user.getSkillStrBonus();
        if (user.getWeapon() != null) total += user.getWeapon().getBonusStr();
        if (user.getHelmet() != null) total += user.getHelmet().getBonusStr();
        if (user.getChest() != null) total += user.getChest().getBonusStr();
        if (user.getRing() != null) total += user.getRing().getBonusStr();
        return total;
    }

    public int getTotalMag(User user) {
        int total = user.getHeroClass().getMagic() + user.getSkillMagBonus();
        if (user.getWeapon() != null) total += user.getWeapon().getBonusMag();
        if (user.getHelmet() != null) total += user.getHelmet().getBonusMag();
        if (user.getChest() != null) total += user.getChest().getBonusMag();
        if (user.getRing() != null) total += user.getRing().getBonusMag();
        return total;
    }

    public int getTotalAgi(User user) {
        int total = user.getHeroClass().getAgility() + user.getSkillAgiBonus();
        if (user.getWeapon() != null) total += user.getWeapon().getBonusAgi();
        if (user.getHelmet() != null) total += user.getHelmet().getBonusAgi();
        if (user.getChest() != null) total += user.getChest().getBonusAgi();
        if (user.getRing() != null) total += user.getRing().getBonusAgi();
        return total;
    }
}