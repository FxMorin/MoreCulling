package ca.fxco.moreculling.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.CloudRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*@Restriction(conflict = { TODO adapt this to 1.21.6
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
}*/
