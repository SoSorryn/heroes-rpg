package com.example.heroes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(length = 20)
    private String icon;

    /** Имя файла картинки в /images/items/, например "iron_sword.png". */
    @Column(length = 120)
    private String imageName;

    @Column(length = 20)
    private String forClass;

    @Column(nullable = false)
    private int price = 10;

    @Column(nullable = false)
    private int rarity = 1;

    @Column(length = 30)
    private String type;

    @Column(length = 30)
    private String slot;

    @Column(nullable = false) private int bonusStr = 0;
    @Column(nullable = false) private int bonusMag = 0;
    @Column(nullable = false) private int bonusAgi = 0;

    @Column(nullable = false)
    private int dropChance = 50;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}