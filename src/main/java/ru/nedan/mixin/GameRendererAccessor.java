package ru.nedan.mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Invoker("getFov")
    double invokeGetFov(ActiveRenderInfo p_215311_1_, float p_215311_2_, boolean p_215311_3_);

}
