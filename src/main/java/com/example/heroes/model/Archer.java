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
@Table(name = "archers")
@HeroComponent(name = "archer")
public class Archer extends Hero {

    private int arrows;

    @Override
    public int getExtra() { return arrows; }

    @Override
    public void setExtra(int extra) { this.arrows = extra; }

    @Callable(name = "Выстрел")
    public String shoot() { return "Лучник стреляет из лука"; }

    @Callable(name = "Ловушка")
    public String setTrap() { return "Лучник ставит ловушку"; }
}