package ru.nedan.functional;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.awt.*;

/**
 * Класс для рендера всех путей
 */

public class Rendering {

    private static final Minecraft mc = Minecraft.getInstance();

    /**
     * Функция для рендера всех путей
     * @param e - рендер в 2д для видимости всех путей
     */

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            MatrixStack matrixStack = e.getMatrixStack();

            WaypointsMap.forEach(entry -> {

                // Подготовка переменных для рендера пути
                String name = "Точка " + TextFormatting.GOLD + TextFormatting.ITALIC + entry.getKey();
                Waypoint waypoint = entry.getValue();
                Vector3d vector3d = waypoint.wayEntity.position();
                double distance = mc.player.distanceTo(waypoint.wayEntity);
                String distanceStr = String.format("%.1f", distance).replaceAll(",", ".") + "m";

                String max = mc.font.width(name) > mc.font.width(distanceStr) ? name : distanceStr;
                float maxWidth = mc.font.width(max) + 12;

                // Получение координат на экране для рендера в 2Д
                Vector2f vector2f = Projection.project(vector3d.x, vector3d.y, vector3d.z);

                float x = (vector2f.x - maxWidth / 2f);
                float y = vector2f.y;

                // Рендер пути
                Rounded.drawRound(x, y, maxWidth, 22, 4, Color.BLACK);
                mc.font.draw(matrixStack, name, x + (maxWidth - mc.font.width(name)) / 2f, y + 2, -1);
                mc.font.draw(matrixStack, distanceStr, x + (maxWidth - mc.font.width(distanceStr)) / 2f, y + mc.font.lineHeight + 3, -1);
            });
        }
    }
}
