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
@Table(name = "warriors")
@HeroComponent(name = "warrior")
public class Warrior extends Hero {

    private int strength;

    @Override
    public int getExtra() { return strength; }

    @Override
    public void setExtra(int extra) { this.strength = extra; }

    @Callable(name = "Удар мечом")
    public String attack() { return "Воин бьёт мечом"; }

    @Callable(name = "Поднять щит")
    public String defend() { return "Воин поднимает щит"; }
}