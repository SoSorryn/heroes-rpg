package com.example.heroes.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "heroes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int health;

    public abstract int getExtra();

    public abstract void setExtra(int extra);
}