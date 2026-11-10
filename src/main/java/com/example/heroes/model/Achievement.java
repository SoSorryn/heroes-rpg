package com.example.heroes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Achievement {
    private String code;
    private String icon;
    private String name;
    private String description;
}