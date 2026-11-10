package com.example.heroes.model;

import lombok.Getter;

@Getter
public enum Biome {
    FOREST("Лес Глубокой Тени", "🌲", 14, 14, 14, 1.0, "Леший",          "forest.png"),
    SWAMP("Топи Забвения",      "🌿", 16, 14, 18, 1.2, "Темный волк",     "swamp.png"),
    RUINS("Священные Руины",    "🏛️", 18, 22, 16, 1.6, "Костяной страж",  "ruins.png"),
    SNOW("Снежные Вершины",     "❄️", 18, 18, 16, 1.4, "Ледяной великан", "snow.png"),
    VOLCANO("Пепельные Высоты", "🌋", 24, 24, 16, 1.9, "Огненный рыцарь", "volcano.png"),
    PLAINS("Равнины Палачей",   "💀", 24, 20, 22, 2.2, "Темный палач",    "plains.png"),
    DRAGON("Остров Драконов",   "🐉", 30, 30, 30, 3.0, "Древний дракон",  "dragon.png");

    private final String name;
    private final String icon;
    private final int reqStrength;
    private final int reqMagic;
    private final int reqAgility;
    private final double goldMultiplier;
    private final String monsterName;
    private final String imageName;

    Biome(String name, String icon, int rs, int rm, int ra, double gold, String monsterName, String imageName) {
        this.name = name;
        this.icon = icon;
        this.reqStrength = rs;
        this.reqMagic = rm;
        this.reqAgility = ra;
        this.goldMultiplier = gold;
        this.monsterName = monsterName;
        this.imageName = imageName;
    }
}