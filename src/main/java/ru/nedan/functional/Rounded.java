package ru.nedan.functional;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * Класс для рендера круглых ректов
 */

public class Rounded {
    private static final Minecraft mc = Minecraft.getInstance();
    private final static Shader roundedShader = new Shader("rounded");

    public static void drawRound(float x, float y, float width, float height, float radius, Color color) {
        drawRound(x, y, width, height, radius, false, color);
    }

    private static void drawRound(float x, float y, float width, float height, float radius, boolean blur, Color color) {
        GlStateManager._enableBlend();
        GlStateManager._blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        roundedShader.init();

        setupRoundedRectUniforms(x, y, width, height, radius, roundedShader);
        roundedShader.setUniformf("blur", blur ? 1 : 0);
        roundedShader.setUniformf("color", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

        Shader.drawQuads(x - 1, y - 1, width + 2, height + 2);
        roundedShader.unload();
        GlStateManager._disableBlend();
    }

    private static void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, Shader roundedTexturedShader) {
        MainWindow sr = mc.getWindow();
        roundedTexturedShader.setUniformf("location", (float) (x * sr.getGuiScale()),
                (float) ((mc.getWindow().getHeight() - (height * sr.getGuiScale())) - (y * sr.getGuiScale())));
        roundedTexturedShader.setUniformf("rectSize", (float) (width * sr.getGuiScale()), (float) (height * sr.getGuiScale()));
        roundedTexturedShader.setUniformf("radius", (float) (radius * sr.getGuiScale()));
    }
}
