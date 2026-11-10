package com.example.heroes.repository;

import com.example.heroes.model.Warrior;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarriorRepository extends JpaRepository<Warrior, Long> {
}