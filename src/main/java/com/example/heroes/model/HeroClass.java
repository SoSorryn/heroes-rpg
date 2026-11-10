package com.example.heroes.model;

import lombok.Getter;

@Getter
public enum HeroClass {
    WARRIOR("warrior", "Воин", "⚔️", "Мастер ближнего боя. Высокая сила, слабая магия.", 16, 4, 8),
    MAGE("mage", "Маг", "🔮", "Повелитель стихий. Высокая магия, слабая сила.", 4, 16, 8),
    ARCHER("archer", "Лучник", "🏹", "Меткий стрелок. Ловкость и баланс.", 8, 8, 16);

    private final String code;
    private final String title;
    private final String icon;
    private final String description;
    private final int strength;
    private final int magic;
    private final int agility;

    HeroClass(String code, String title, String icon, String description, int strength, int magic, int agility) {
        this.code = code;
        this.title = title;
        this.icon = icon;
        this.description = description;
        this.strength = strength;
        this.magic = magic;
        this.agility = agility;
    }
}