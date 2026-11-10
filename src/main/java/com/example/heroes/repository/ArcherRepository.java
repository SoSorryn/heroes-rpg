package com.example.heroes.repository;

import com.example.heroes.model.Archer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArcherRepository extends JpaRepository<Archer, Long> {
}