package com.example.heroes.service;

import com.example.heroes.model.User;
import com.example.heroes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MinigameService {

    private final UserRepository userRepository;

    /** Античит: последний вызов (userId -> [timestamp, count]). */
    private final Map<Long, long[]> lastCalls = new ConcurrentHashMap<>();

    @Transactional
    public String speedup(User user, int seconds) {
        if (!user.isQuestActive() || user.getQuestEndTime() == null)
            return "Вы не в путешествии.";

        long now = System.currentTimeMillis();
        long[] state = lastCalls.computeIfAbsent(user.getId(), k -> new long[]{0, 0});
        if (now - state[0] < 500) return "Слишком быстро.";
        state[0] = now;

        int capped = Math.max(0, Math.min(seconds, 30));
        LocalDateTime newEnd = user.getQuestEndTime().minusSeconds(capped);
        if (newEnd.isBefore(LocalDateTime.now())) newEnd = LocalDateTime.now();
        user.setQuestEndTime(newEnd);
        userRepository.save(user);
        return "⏩ Квест ускорен на " + capped + " сек.";
    }
}