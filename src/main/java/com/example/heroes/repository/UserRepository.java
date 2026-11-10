package com.example.heroes.repository;

import com.example.heroes.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query(value = "SELECT achievement_code FROM user_achievements WHERE user_id = :userId", nativeQuery = true)
    Set<String> findUnlockedAchievements(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_achievements (user_id, achievement_code) VALUES (:userId, :code) ON CONFLICT DO NOTHING", nativeQuery = true)
    void unlockAchievement(@Param("userId") Long userId, @Param("code") String code);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_achievements WHERE user_id = :userId", nativeQuery = true)
    void clearAchievements(@Param("userId") Long userId);
}