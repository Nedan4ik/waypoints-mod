package ru.nedan.functional;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import ru.nedan.mixin.GameRendererAccessor;

public class Projection {
    private static final Minecraft mc = Minecraft.getInstance();

    public static Vector2f project(double x, double y, double z) {
        Vector3d camera_pos = mc.getEntityRenderDispatcher().camera.getPosition();
        Quaternion cameraRotation = mc.getEntityRenderDispatcher().cameraOrientation().copy();
        cameraRotation.conj();

        Vector3f result3f = new Vector3f((float) (camera_pos.x - x), (float) (camera_pos.y - y), (float) (camera_pos.z - z));
        result3f.transform(cameraRotation);

        if (mc.options.bobView) {
            Entity renderViewEntity = mc.getCameraEntity();
            if (renderViewEntity instanceof PlayerEntity) {
                calculateViewBobbing((PlayerEntity) renderViewEntity, result3f);
            }
        }

        double fov = ((GameRendererAccessor)mc.gameRenderer).invokeGetFov(mc.getEntityRenderDispatcher().camera, mc.getDeltaFrameTime(), true);

        return calculateScreenPosition(result3f, fov);
    }

    private static void calculateViewBobbing(PlayerEntity playerentity, Vector3f result3f) {
        float walked = playerentity.walkDist;
        float f = walked - playerentity.walkDistO;
        float f1 = -(walked + f * mc.getDeltaFrameTime());
        float f2 = MathHelper.lerp(mc.getDeltaFrameTime(), playerentity.oBob, playerentity.bob);

        Quaternion quaternion = new Quaternion(Vector3f.XP, Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F, true);
        quaternion.conj();
        result3f.transform(quaternion);

        Quaternion quaternion1 = new Quaternion(Vector3f.ZP, MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F, true);
        quaternion1.conj();
        result3f.transform(quaternion1);

        Vector3f bobTranslation = new Vector3f((MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F), (-Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2)), 0.0f);
        bobTranslation.set(bobTranslation.x(), -bobTranslation.y(), bobTranslation.z());
        result3f.add(bobTranslation);
    }

    private static Vector2f calculateScreenPosition(Vector3f result3f, double fov) {
        float halfHeight = mc.getWindow().getGuiScaledHeight() / 2.0F;
        float scaleFactor = halfHeight / (result3f.z() * (float) Math.tan(Math.toRadians(fov / 2.0F)));
        if (result3f.z() < 0.0F) {
            return new Vector2f(-result3f.x() * scaleFactor + mc.getWindow().getGuiScaledWidth() / 2.0F, mc.getWindow().getGuiScaledHeight() / 2.0F - result3f.y() * scaleFactor);
        }
        return new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    }

}
