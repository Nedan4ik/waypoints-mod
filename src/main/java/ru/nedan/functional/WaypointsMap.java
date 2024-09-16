package ru.nedan.functional;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import ru.nedan.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Склад всех вейпоинтов
 */

public class WaypointsMap {
    private static final Minecraft mc = Minecraft.getInstance();
    private static HashMap<String, Waypoint> waypoints = new HashMap<>();

    /**
     * Функция для добавления путей
     * @param name - название пути
     * @param x - координата по оси X
     * @param y - координата по оси Y
     * @param z - координата по оси Z
     * @return Pair - дуо из булеана и стринга для получения отчёта
     */

    public static Pair<Boolean, String> addWay(String name, double x, double y, double z) {
        try {
            if (waypoints.containsKey(name)) {
                return new Pair<>(false, String.format("Путь с названием \"%s\" уже существует!", name));
            }

            BlockPos blockPos = new BlockPos(x, y, z);

            PlayerEntity entity = new PlayerEntity(mc.level, blockPos, 90, new GameProfile(UUID.randomUUID(), name)) {
                @Override
                public boolean isSpectator() {
                    return true;
                }

                @Override
                public boolean isCreative() {
                    return true;
                }
            };

            Waypoint waypoint = new Waypoint(entity);

            waypoints.put(name, waypoint);
            return new Pair<>(true, String.format("Успешно добавил новый путь с названием: %s", name));
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return new Pair<>(false, "Произошла ошибка");
        }
    }

    /**
     * Функция удаления пути
     * @param name - Названия пути для удаления
     * @return Pair - дуо из булеана и стринга для получения отчёта
     */

    public static Pair<Boolean, String> removeWay(String name) {
        if (!waypoints.containsKey(name)) {
            return new Pair<>(false, String.format("Пути с названием \"%s\" нету в списке", name));
        }

        waypoints.remove(name);

        return new Pair<>(true, "Успешно удалил путь с названием: " + name);
    }

    public static void forEach(Consumer<Map.Entry<String, Waypoint>> consumer) {
        waypoints.entrySet().forEach(consumer);
    }

}
