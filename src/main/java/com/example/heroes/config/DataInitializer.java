package com.example.heroes.config;

import com.example.heroes.model.Item;
import com.example.heroes.model.User;
import com.example.heroes.repository.ItemRepository;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.password:changeme}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        log.info("=== DataInitializer START ===");

        if (userRepository.count() == 0 || !userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setDisplayName("Администратор");
            admin.setAdmin(true);
            admin.setGold(9999);
            userRepository.save(admin);
            log.info("Admin created (password from app.admin.password)");
        }

        if (itemRepository.count() == 0) {
            initItems();
            log.info("Items initialized: " + itemRepository.count());
        } else {
            log.info("Items in DB: " + itemRepository.count());
        }

        log.info("=== DataInitializer END ===");
    }

    private void initItems() {
        List<Item> items = List.of(
                // ================= ОРУЖИЕ (WEAPON) =================
                // Редкость 1: Цена ~ 50, Дроп ~ 60%
                item("Ржавый меч",          "🗡️", "warrior-wp/weapons/rusty_sword.png",     "any",     "WEAPON", "weapon",  50, 1, 15,  0,  0, 60),
                item("Посох ученика",       "🪄", "mage-wp/weapons/apprentice_staff.png",   "mage",    "WEAPON", "weapon",  50, 1,  0, 15,  0, 60),
                item("Простой лук",         "🏹", "archer-wp/weapons/simple_bow.png",       "archer",  "WEAPON", "weapon",  50, 1,  0,  0, 15, 60),

                // Редкость 2: Цена ~ 150-180, Дроп ~ 35-40%
                item("Стальной клинок",     "⚔️", "warrior-wp/weapons/steel_blade.png",     "warrior", "WEAPON", "weapon", 150, 2, 25,  0,  0, 40),
                item("Серебряный меч",      "⚔️", "warrior-wp/weapons/silver_sword.png",    "warrior", "WEAPON", "weapon", 180, 2, 28,  0,  0, 35),
                item("Посох огня",          "🔥", "mage-wp/weapons/fire_staff.png",         "mage",    "WEAPON", "weapon", 150, 2,  0, 25,  0, 40),
                item("Посох света",         "✨", "mage-wp/weapons/holy_staff.png",         "mage",    "WEAPON", "weapon", 180, 2,  0, 28,  0, 35),
                item("Еловый лук",          "🏹", "archer-wp/weapons/spruce_bow.png",       "archer",  "WEAPON", "weapon", 150, 2,  0,  0, 25, 40),
                item("Костяной лук",        "🦴", "archer-wp/weapons/bone_bow.png",         "archer",  "WEAPON", "weapon", 180, 2,  0,  0, 28, 35),

                // Редкость 3: Цена ~ 380-420, Дроп ~ 18-20%
                item("Ледяной клинок",      "❄️", "warrior-wp/weapons/frost_blade.png",     "warrior", "WEAPON", "weapon", 380, 3, 40,  0,  0, 20),
                item("Обсидиановый клинок", "🗡️", "warrior-wp/weapons/obsidian_blade.png",  "warrior", "WEAPON", "weapon", 420, 3, 42,  0,  5, 18),
                item("Ледяной посох",       "❄️", "mage-wp/weapons/frost_staff.png",        "mage",    "WEAPON", "weapon", 380, 3,  0, 40,  0, 20),
                item("Посох углей",         "🔥", "mage-wp/weapons/ember_staff.png",        "mage",    "WEAPON", "weapon", 420, 3,  5, 42,  0, 18),
                item("Ледяной лук",         "❄️", "archer-wp/weapons/frost_bow.png",        "archer",  "WEAPON", "weapon", 380, 3,  0,  0, 40, 20),
                item("Лук охотника",        "🏹", "archer-wp/weapons/hunter_bow.png",       "archer",  "WEAPON", "weapon", 420, 3,  5,  0, 42, 18),

                // Редкость 4: Цена ~ 950-1050, Дроп ~ 7-8%
                item("Клинок Зари",         "🌅", "warrior-wp/weapons/dawn_blade.png",      "warrior", "WEAPON", "weapon", 950, 4, 60,  0, 10, 8),
                item("Изумрудный клинок",   "💚", "warrior-wp/weapons/emerald_blade.png",   "warrior", "WEAPON", "weapon",1000, 4, 58,  0,  8, 8),
                item("Лунный клинок",       "🌙", "warrior-wp/weapons/moonlight_blade.png", "warrior", "WEAPON", "weapon",1050, 4, 65,  0,  0, 7),
                item("Рубиновый жезл",      "🔴", "mage-wp/weapons/ruby_rod.png",           "mage",    "WEAPON", "weapon", 950, 4,  0, 60,  0, 8),
                item("Жезл бури",           "🌩️", "mage-wp/weapons/storm_rod.png",          "mage",    "WEAPON", "weapon",1000, 4,  0, 58, 10, 8),
                item("Кристальный посох",   "💎", "mage-wp/weapons/crystal_staff.png",      "mage",    "WEAPON", "weapon",1050, 4,  0, 65,  0, 7),
                item("Багровый лук",        "🔴", "archer-wp/weapons/crimson_bow.png",      "archer",  "WEAPON", "weapon", 950, 4,  0,  0, 60, 8),
                item("Дыхание леса",        "🌿", "archer-wp/weapons/forest_breath.png",    "archer",  "WEAPON", "weapon",1000, 4,  8,  0, 58, 8),
                item("Лунный лук",          "🌙", "archer-wp/weapons/moonlight_bow.png",    "archer",  "WEAPON", "weapon",1050, 4,  0,  0, 65, 7),

                // Редкость 5: Цена ~ 3000, Дроп ~ 2%
                item("Солнечный клинок",    "☀️", "warrior-wp/weapons/sun_blade.png",       "warrior", "WEAPON", "weapon",3000, 5, 100, 0, 15, 2),
                item("Жезл Акатоша",        "🔮", "mage-wp/weapons/akatosh_rod.png",        "mage",    "WEAPON", "weapon",3000, 5,  0,100, 15, 2),
                item("Солнечный лук",       "☀️", "archer-wp/weapons/sun_bow.png",          "archer",  "WEAPON", "weapon",3000, 5, 15,  0,100, 2),

                // ================= ШЛЕМЫ (HELMET) =================
                item("Кожаный капюшон",     "🪖", "warrior-wp/helmets/leather_hood.png",    "any",     "HELMET", "armor",   50, 1,  0,  0,  5, 60),

                item("Стальной шлем",       "🪖", "warrior-wp/helmets/steel_helmet.png",    "warrior", "HELMET", "armor",  130, 2, 18,  0,  0, 40),
                item("Серебряный шлем",     "🪖", "warrior-wp/helmets/silver_helmet.png",   "warrior", "HELMET", "armor",  150, 2, 20,  0,  0, 35),

                item("Ледяной шлем",        "❄️", "warrior-wp/helmets/frost_helmet.png",    "warrior", "HELMET", "armor",  320, 3, 30,  0,  0, 20),
                item("Чёрный шлем",         "🖤", "warrior-wp/helmets/black_helmet.png",    "warrior", "HELMET", "armor",  350, 3, 32,  0,  5, 18),
                item("Капюшон тени",        "🕶️", "archer-wp/helmets/shadow_hood.png",      "archer",  "HELMET", "armor",  320, 3,  0,  0, 30, 20),

                item("Багровый шлем",       "🔴", "warrior-wp/helmets/crimson_helmet.png",  "warrior", "HELMET", "armor",  800, 4, 45,  0,  0, 8),
                item("Изумрудный шлем",     "💚", "warrior-wp/helmets/emerald_helmet.png",  "warrior", "HELMET", "armor",  850, 4, 45,  0,  8, 8),

                item("Корона мудреца",      "👑", "mage-wp/helmets/sage_crown.png",         "mage",    "HELMET", "armor", 3000, 5,  0, 80, 20, 2),

                // ================= БРОНЯ (CHEST) =================
                item("Стёганый доспех",     "🛡️", "warrior-wp/chests/padded_armor.png",    "warrior", "CHEST",  "armor",   50, 1,  8,  0,  0, 60),
                item("Тканевая роба",       "🥼", "mage-wp/chests/cloth_robe.png",         "mage",    "CHEST",  "armor",   50, 1,  0,  8,  0, 60),
                item("Кожаная жилетка",     "🧥", "archer-wp/chests/leather_vest.png",     "archer",  "CHEST",  "armor",   50, 1,  0,  0,  8, 60),

                item("Кольчуга",            "🛡️", "warrior-wp/chests/chainmail.png",       "warrior", "CHEST",  "armor",  130, 2, 22,  0,  0, 40),
                item("Серебряная кираса",   "🛡️", "warrior-wp/chests/silver_cuirass.png",  "warrior", "CHEST",  "armor",  150, 2, 24,  0,  0, 35),
                item("Мантия ученика",      "🧙", "mage-wp/chests/apprentice_robe.png",    "mage",    "CHEST",  "armor",  130, 2,  0, 22,  0, 40),
                item("Роба целителя",       "✨", "mage-wp/chests/healer_robe.png",        "mage",    "CHEST",  "armor",  150, 2,  0, 24,  0, 35),
                item("Куртка охотника",     "🧥", "archer-wp/chests/hunter_jacket.png",    "archer",  "CHEST",  "armor",  130, 2,  0,  0, 22, 40),
                item("Куртка разведчика",   "🧥", "archer-wp/chests/scout_jacket.png",     "archer",  "CHEST",  "armor",  150, 2,  0,  0, 24, 35),

                item("Ледяная кираса",      "❄️", "warrior-wp/chests/frost_cuirass.png",   "warrior", "CHEST",  "armor",  320, 3, 35,  0,  0, 20),
                item("Чёрный доспех",       "🖤", "warrior-wp/chests/black_armor.png",     "warrior", "CHEST",  "armor",  350, 3, 35,  0,  5, 18),
                item("Ледяная мантия",      "❄️", "mage-wp/chests/frost_robe.png",         "mage",    "CHEST",  "armor",  320, 3,  0, 35,  0, 20),
                item("Мантия тёмного мага", "🖤", "mage-wp/chests/dark_mage_robe.png",     "mage",    "CHEST",  "armor",  350, 3,  0, 35,  5, 18),
                item("Ледяной жилет",       "❄️", "archer-wp/chests/frost_vest.png",        "archer",  "CHEST",  "armor",  320, 3,  0,  0, 35, 20),
                item("Плащ следопыта",      "🧥", "archer-wp/chests/pathfinder_cloak.png", "archer",  "CHEST",  "armor",  350, 3,  0,  0, 35, 18),

                item("Кровавая кираса",     "🩸", "warrior-wp/chests/blood_cuirass.png",   "warrior", "CHEST",  "armor",  800, 4, 50,  0,  0, 8),
                item("Изумрудная кираса",   "💚", "warrior-wp/chests/emerald_cuirass.png", "warrior", "CHEST",  "armor",  850, 4, 48,  0,  8, 8),
                item("Золотая кираса",      "🛡️", "warrior-wp/chests/golden_cuirass.png",  "warrior", "CHEST",  "armor",  900, 4, 55,  0,  0, 7),
                item("Багровая мантия",     "🔴", "mage-wp/chests/crimson_robe.png",       "mage",    "CHEST",  "armor",  800, 4,  0, 50,  0, 8),
                item("Мантия архимага",     "🧙", "mage-wp/chests/archmage_robe.png",      "mage",    "CHEST",  "armor",  850, 4,  0, 48,  8, 8),
                item("Золотая мантия",      "🌟", "mage-wp/chests/golden_robe.png",        "mage",    "CHEST",  "armor",  900, 4,  0, 55,  0, 7),
                item("Багровая куртка",     "🔴", "archer-wp/chests/crimson_vest.png",     "archer",  "CHEST",  "armor",  800, 4,  0,  0, 50, 8),
                item("Лесная куртка",       "🌿", "archer-wp/chests/forest_vest.png",      "archer",  "CHEST",  "armor",  850, 4,  8,  0, 48, 8),
                item("Золотой жилет",       "🌟", "archer-wp/chests/golden_vest.png",      "archer",  "CHEST",  "armor",  900, 4,  0,  0, 55, 7),

                item("Доспех дракона",      "🐉", "warrior-wp/chests/dragon_armor.png",    "any",     "CHEST",  "armor", 3500, 5, 60, 60, 60, 2),

                // ================= КОЛЬЦА (RING) =================
                item("Медное кольцо",       "💍", "warrior-wp/rings/copper_ring.png",      "any",     "RING",   "jewelry", 50, 1,  3,  3,  3, 60),

                item("Кольцо силы",         "💍", "warrior-wp/rings/ring_of_strength.png", "any",     "RING",   "jewelry",120, 2, 15,  0,  0, 40),
                item("Кольцо мудрости",     "💍", "warrior-wp/rings/ring_of_wisdom.png",   "any",     "RING",   "jewelry",120, 2,  0, 15,  0, 40),
                item("Кольцо ловкости",     "💍", "warrior-wp/rings/ring_of_agility.png",  "any",     "RING",   "jewelry",120, 2,  0,  0, 15, 40),

                item("Ледяное кольцо",      "❄️", "warrior-wp/rings/frost_ring.png",       "any",     "RING",   "jewelry",300, 3,  0, 25,  0, 20),
                item("Кольцо углей",        "🔥", "warrior-wp/rings/ember_ring.png",       "any",     "RING",   "jewelry",300, 3, 25,  0,  0, 20),

                item("Рубиновое кольцо",    "🔴", "warrior-wp/rings/ruby_ring.png",        "any",     "RING",   "jewelry",750, 4, 20, 20,  0, 8),
                item("Кольцо солнечного камня","🌟","warrior-wp/rings/sunstone_ring.png",  "any",     "RING",   "jewelry",750, 4,  0, 20, 20, 8),

                item("Перстень Акатоша",    "💎", "warrior-wp/rings/akatosh_signet.png",   "any",     "RING",   "jewelry",2500, 5, 30, 30, 30, 2),

                // ================= АМУЛЕТЫ (AMULET) =================
                item("Лунный амулет",       "🌙", "warrior-wp/amulets/moon_amulet.png",    "any",     "AMULET", "jewelry",400, 3,  0, 30,  5, 15)
        );
        itemRepository.saveAll(items);
    }

    private Item item(String name, String icon, String imageName,
                      String forClass, String slot, String type,
                      int price, int rarity,
                      int str, int mag, int agi, int drop) {
        Item it = new Item();
        it.setName(name);
        it.setIcon(icon);
        it.setImageName(imageName);
        it.setForClass(forClass);
        it.setSlot(slot);
        it.setType(type);
        it.setPrice(price);
        it.setRarity(rarity);
        it.setBonusStr(str);
        it.setBonusMag(mag);
        it.setBonusAgi(agi);
        it.setDropChance(drop);
        return it;
    }
}