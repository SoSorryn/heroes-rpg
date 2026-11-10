package com.example.heroes.repository;

import com.example.heroes.model.Mage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MageRepository extends JpaRepository<Mage, Long> {
}