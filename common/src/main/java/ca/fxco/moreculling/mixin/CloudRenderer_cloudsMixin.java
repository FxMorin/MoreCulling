package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.renderer.CloudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
    @Shadow
    private static boolean isNorthEmpty(long l) {
        return false;
    }

    @Shadow
    private static boolean isSouthEmpty(long l) {
        return false;
    }

    @Shadow
    private static boolean isWestEmpty(long l) {
        return false;
    }

    @Shadow
    private static boolean isEastEmpty(long l) {
        return false;
    }

    @Inject(
            method = "buildMesh(Lnet/minecraft/client/renderer/CloudRenderer$RelativeCameraPos;Lcom/mojang/blaze3d/vertex/BufferBuilder;IIIIIIZ)V",
            at = @At(value = "HEAD")
    )
    private void moreculling$enableCull(CloudRenderer.RelativeCameraPos p_363221_, BufferBuilder p_364486_,
                                        int p_361006_, int p_362674_, int p_362100_, int p_360889_, int p_360776_,
                                        int p_365003_, boolean p_362207_, CallbackInfo ci) {
        if (p_362207_ && MoreCulling.CONFIG.cloudCulling)
            RenderSystem.enableCull();
    }

    @Inject(
            method = "buildExtrudedCell",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void moreculling$renderClouds(
            CloudRenderer.RelativeCameraPos relativeCameraPos,
            BufferBuilder bufferBuilder,
            int i,
            int j,
            int k,
            int l,
            int m,
            int n,
            long o, CallbackInfo ci) {
        if (!MoreCulling.CONFIG.cloudCulling) {
            return;
        }
        RenderSystem.enableCull();
        float f = (float)m * 12.0F;
        float g = f + 12.0F;
        float h = 0.0F;
        float p = 4.0F;
        float q = (float)n * 12.0F;
        float r = q + 12.0F;
        if (relativeCameraPos != CloudRenderer.RelativeCameraPos.BELOW_CLOUDS) {
            bufferBuilder.addVertex(g, 4.0F, r).setColor(j);
            bufferBuilder.addVertex(g, 4.0F, q).setColor(j);
            bufferBuilder.addVertex(f, 4.0F, q).setColor(j);
            bufferBuilder.addVertex(f, 4.0F, r).setColor(j);
        }

        if (relativeCameraPos != CloudRenderer.RelativeCameraPos.ABOVE_CLOUDS) {
            bufferBuilder.addVertex(g, 0.0F, q).setColor(i);
            bufferBuilder.addVertex(g, 0.0F, r).setColor(i);
            bufferBuilder.addVertex(f, 0.0F, r).setColor(i);
            bufferBuilder.addVertex(f, 0.0F, q).setColor(i);
        }

        if (isNorthEmpty(o) && n > 0) {
            bufferBuilder.addVertex(f, 0.0F, q).setColor(l);
            bufferBuilder.addVertex(f, 4.0F, q).setColor(l);
            bufferBuilder.addVertex(g, 4.0F, q).setColor(l);
            bufferBuilder.addVertex(g, 0.0F, q).setColor(l);
        }

        if (isSouthEmpty(o) && n < 0) {
            bufferBuilder.addVertex(f, 4.0F, r).setColor(l);
            bufferBuilder.addVertex(f, 0.0F, r).setColor(l);
            bufferBuilder.addVertex(g, 0.0F, r).setColor(l);
            bufferBuilder.addVertex(g, 4.0F, r).setColor(l);
        }

        if (isWestEmpty(o) && m > 0) {
            bufferBuilder.addVertex(f, 0.0F, r).setColor(k);
            bufferBuilder.addVertex(f, 4.0F, r).setColor(k);
            bufferBuilder.addVertex(f, 4.0F, q).setColor(k);
            bufferBuilder.addVertex(f, 0.0F, q).setColor(k);
        }

        if (isEastEmpty(o) && m < 0) {
            bufferBuilder.addVertex(g, 4.0F, r).setColor(k);
            bufferBuilder.addVertex(g, 0.0F, r).setColor(k);
            bufferBuilder.addVertex(g, 0.0F, q).setColor(k);
            bufferBuilder.addVertex(g, 4.0F, q).setColor(k);
        }

        boolean bl = Math.abs(m) <= 1 && Math.abs(n) <= 1;
        if (bl) {
            bufferBuilder.addVertex(g, 4.0F, q).setColor(j);
            bufferBuilder.addVertex(g, 4.0F, r).setColor(j);
            bufferBuilder.addVertex(f, 4.0F, r).setColor(j);
            bufferBuilder.addVertex(f, 4.0F, q).setColor(j);
            bufferBuilder.addVertex(f, 0.0F, q).setColor(i);
            bufferBuilder.addVertex(f, 0.0F, r).setColor(i);
            bufferBuilder.addVertex(g, 0.0F, r).setColor(i);
            bufferBuilder.addVertex(g, 0.0F, q).setColor(i);
            bufferBuilder.addVertex(g, 0.0F, q).setColor(l);
            bufferBuilder.addVertex(g, 4.0F, q).setColor(l);
            bufferBuilder.addVertex(f, 4.0F, q).setColor(l);
            bufferBuilder.addVertex(f, 0.0F, q).setColor(l);
            bufferBuilder.addVertex(f, 0.0F, r).setColor(l);
            bufferBuilder.addVertex(f, 4.0F, r).setColor(l);
            bufferBuilder.addVertex(g, 4.0F, r).setColor(l);
            bufferBuilder.addVertex(g, 0.0F, r).setColor(l);
            bufferBuilder.addVertex(f, 0.0F, q).setColor(k);
            bufferBuilder.addVertex(f, 4.0F, q).setColor(k);
            bufferBuilder.addVertex(f, 4.0F, r).setColor(k);
            bufferBuilder.addVertex(f, 0.0F, r).setColor(k);
            bufferBuilder.addVertex(g, 0.0F, r).setColor(k);
            bufferBuilder.addVertex(g, 4.0F, r).setColor(k);
            bufferBuilder.addVertex(g, 4.0F, q).setColor(k);
            bufferBuilder.addVertex(g, 0.0F, q).setColor(k);
        }
        ci.cancel();
    }
}
