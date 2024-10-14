package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(conflict = {
        @Condition("cullclouds"),
        @Condition("extended-clouds"),
        @Condition("extendedclouds")
})
@Mixin(CloudRenderer.class)
public abstract class CloudRenderer_cloudsMixin {
    @Inject(
            method = "buildMesh(Lnet/minecraft/client/renderer/CloudRenderer$RelativeCameraPos;" +
                    "Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIZ)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/CloudRenderer;" +
                            "texture:Lnet/minecraft/client/renderer/CloudRenderer$TextureData;",
                    ordinal = 3,
                    shift = At.Shift.AFTER
            )
    )
    private void moreculling$renderClouds(CloudRenderer.RelativeCameraPos relativeCameraPos,
                                          BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n,
                                          boolean bl, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.cloudCulling) {
            return;
        }
        RenderSystem.enableCull();
    }
}
