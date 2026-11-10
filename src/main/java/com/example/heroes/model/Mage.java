package com.example.heroes.model;

import com.example.heroes.annotation.Callable;
import com.example.heroes.annotation.HeroComponent;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mages")
@HeroComponent(name = "mage")
public class Mage extends Hero {

    private int mana;

    @Override
    public int getExtra() { return mana; }

    @Override
    public void setExtra(int extra) { this.mana = extra; }

    @Callable(name = "Огненный шар")
    public String fireball() { return "Маг запускает огненный шар"; }

    @Callable(name = "Лечение")
    public String heal() { return "Маг лечит союзника"; }
}