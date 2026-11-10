package com.example.heroes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String displayName;

    @Column(nullable = false)
    private boolean admin = false;

    @Enumerated(EnumType.STRING)
    private HeroClass heroClass;

    @Enumerated(EnumType.STRING)
    private Biome currentBiome;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_biome")
    private Biome lastBiome;

    private LocalDateTime questEndTime;

    @Column(nullable = false)
    private boolean questActive = false;

    @Column(nullable = false)
    private int gold = 80;

    @Column(nullable = false)
    private int skillPoints = 0;

    private LocalDateTime lastDailyReward;

    @Column(nullable = false)
    private int wins = 0;

    @Column(nullable = false)
    private int losses = 0;

    @Column(nullable = false)
    private int storyProgress = 0;

    @Column(nullable = false)
    private int duelWins = 0;

    @Column(nullable = false)
    private int duelLosses = 0;

    @Column(nullable = false)
    private int skillStrBonus = 0;

    @Column(nullable = false)
    private int skillMagBonus = 0;

    @Column(nullable = false)
    private int skillAgiBonus = 0;

    // === Поля для боя ===
    private String enemyName;
    private String enemyImage;
    private int enemyHp;
    private int enemyMaxHp;
    private int enemyStr;
    private int enemyMag;
    private int enemyAgi;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_inventory",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private Set<Item> inventory = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "weapon_id")
    private Item weapon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "helmet_id")
    private Item helmet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chest_id")
    private Item chest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ring_id")
    private Item ring;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "amulet_id")
    private Item amulet; // НОВОЕ ПОЛЕ

    public boolean isEquipped(Item item) {
        return equippedSlot(item) != null;
    }

    public String equippedSlot(Item item) {
        if (item == null || item.getId() == null) return null;
        Long id = item.getId();
        if (weapon != null && id.equals(weapon.getId())) return "WEAPON";
        if (helmet != null && id.equals(helmet.getId())) return "HELMET";
        if (chest  != null && id.equals(chest.getId()))  return "CHEST";
        if (ring   != null && id.equals(ring.getId()))   return "RING";
        if (amulet != null && id.equals(amulet.getId())) return "AMULET";
        return null;
    }

    public void setBySlot(String slot, Item item) {
        if (slot == null) return;
        switch (slot) {
            case "WEAPON" -> weapon = item;
            case "HELMET" -> helmet = item;
            case "CHEST"  -> chest  = item;
            case "RING"   -> ring   = item;
            case "AMULET" -> amulet = item;
        }
    }
}