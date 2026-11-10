package com.example.heroes.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HeroComponent {
    String name() default "";
}