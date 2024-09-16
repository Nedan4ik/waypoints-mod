package ru.nedan.functional;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Класс для пути
 */

public class Waypoint {
    public PlayerEntity wayEntity;

    public Waypoint(PlayerEntity wayEntity) {
        this.wayEntity = wayEntity;
    }
}
