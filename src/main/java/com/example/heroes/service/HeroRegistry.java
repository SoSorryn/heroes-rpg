package com.example.heroes.service;

import com.example.heroes.annotation.Callable;
import com.example.heroes.annotation.HeroComponent;
import com.example.heroes.model.Archer;
import com.example.heroes.model.Mage;
import com.example.heroes.model.Warrior;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class HeroRegistry {

    private static final List<Class<?>> CANDIDATES = List.of(Warrior.class, Mage.class, Archer.class);

    private final Map<String, Object> instances = new LinkedHashMap<>();
    private final Map<String, Map<String, Method>> callableCache = new LinkedHashMap<>();

    @PostConstruct
    void init() {
        for (Class<?> clazz : CANDIDATES) {
            if (!clazz.isAnnotationPresent(HeroComponent.class)) continue;
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                String name = clazz.getAnnotation(HeroComponent.class).name();
                if (name.isEmpty()) name = clazz.getSimpleName();
                instances.put(name, instance);
            } catch (ReflectiveOperationException e) {
                log.error("Не удалось создать экземпляр {}", clazz.getSimpleName(), e);
            }
        }

        for (Map.Entry<String, Object> e : instances.entrySet()) {
            Map<String, Method> methods = new LinkedHashMap<>();
            for (Method m : e.getValue().getClass().getDeclaredMethods()) {
                if (m.isAnnotationPresent(Callable.class)) {
                    String n = m.getAnnotation(Callable.class).name();
                    methods.put(n.isEmpty() ? m.getName() : n, m);
                }
            }
            callableCache.put(e.getKey(), methods);
        }

        if (instances.isEmpty()) log.warn("Не найдено компонентов");
        else log.info("HeroRegistry: загружено {} героев, {} callable-методов",
                instances.size(), callableCache.values().stream().mapToInt(Map::size).sum());
    }

    public Set<String> getNames() {
        return instances.keySet();
    }

    public Map<String, Method> getCallableMethods(String heroName) {
        return callableCache.getOrDefault(heroName, Map.of());
    }

    public Object invoke(String heroName, String methodName) throws ReflectiveOperationException {
        Object instance = instances.get(heroName);
        if (instance == null) throw new NoSuchMethodException("Нет героя: " + heroName);
        Method method = getCallableMethods(heroName).get(methodName);
        if (method == null) throw new NoSuchMethodException(methodName);
        method.setAccessible(true);
        return method.invoke(instance);
    }
}