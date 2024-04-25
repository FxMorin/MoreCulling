package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(conflict = {
        @Condition("cullclouds"),
        @Condition("extended-clouds"),
        @Condition("extendedclouds")
})
@Mixin(LevelRenderer.class)
public class WorldRenderer_cloudsMixin {

    @Unique
    private static final float SCALE = 0.00390625F;

    @Shadow
    @Nullable
    private CloudStatus prevCloudsType;

    @Inject(
            method = "buildClouds",
            at = @At("HEAD"),
            cancellable = true
    )
    private void moreculling$renderClouds(BufferBuilder builder, double x, double y, double z, Vec3 color,
                                          CallbackInfoReturnable<BufferBuilder.RenderedBuffer> cir) {
        if (!MoreCulling.CONFIG.cloudCulling) {
            return;
        }
        float k = (float) Mth.floor(x) * SCALE;
        float l = (float) Mth.floor(z) * SCALE;
        float m = (float) color.x;
        float n = (float) color.y;
        float o = (float) color.z;
        float p = m * 0.9F;
        float q = n * 0.9F;
        float r = o * 0.9F;
        float s = m * 0.7F;
        float t = n * 0.7F;
        float u = o * 0.7F;
        float v = m * 0.8F;
        float w = n * 0.8F;
        float aa = o * 0.8F;
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        float ab = (float) Math.floor(y / 4.0) * 4.0F;
        if (this.prevCloudsType == CloudStatus.FANCY) {
            RenderSystem.enableCull(); // Enable culling
            for (int ac = -3; ac <= 4; ++ac) {
                for (int ad = -3; ad <= 4; ++ad) {
                    float ae = (float) (ac * 8);
                    float af = (float) (ad * 8);
                    if (ab > -5.0F) { // bottom [-y] - Reversed Winding Order
                        builder.vertex(ae + 0.0F, ab + 0.0F, af + 0.0F).uv((ae + 0.0F) * SCALE + k, (af + 0.0F) * SCALE + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        builder.vertex(ae + 8.0F, ab + 0.0F, af + 0.0F).uv((ae + 8.0F) * SCALE + k, (af + 0.0F) * SCALE + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        builder.vertex(ae + 8.0F, ab + 0.0F, af + 8.0F).uv((ae + 8.0F) * SCALE + k, (af + 8.0F) * SCALE + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                        builder.vertex(ae + 0.0F, ab + 0.0F, af + 8.0F).uv((ae + 0.0F) * SCALE + k, (af + 8.0F) * SCALE + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    }
                    if (ab <= 5.0F) { // top [+y]
                        builder.vertex(ae + 0.0F, ab + 4.0F - 9.765625E-4F, af + 8.0F).uv((ae + 0.0F) * SCALE + k, (af + 8.0F) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(ae + 8.0F, ab + 4.0F - 9.765625E-4F, af + 8.0F).uv((ae + 8.0F) * SCALE + k, (af + 8.0F) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(ae + 8.0F, ab + 4.0F - 9.765625E-4F, af + 0.0F).uv((ae + 8.0F) * SCALE + k, (af + 0.0F) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                        builder.vertex(ae + 0.0F, ab + 4.0F - 9.765625E-4F, af + 0.0F).uv((ae + 0.0F) * SCALE + k, (af + 0.0F) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).endVertex();
                    }
                    int ag;
                    if (ac > -1) { // west [-x]
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float) ag + 0.0F, ab + 0.0F, af + 8.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 8.0F) * SCALE + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            builder.vertex(ae + (float) ag + 0.0F, ab + 4.0F, af + 8.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 8.0F) * SCALE + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            builder.vertex(ae + (float) ag + 0.0F, ab + 4.0F, af + 0.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 0.0F) * SCALE + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                            builder.vertex(ae + (float) ag + 0.0F, ab + 0.0F, af + 0.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 0.0F) * SCALE + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }
                    if (ac <= 1) { // east [+x] - Reversed Winding Order
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float) ag + 1.0F - 9.765625E-4F, ab + 0.0F, af + 0.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 0.0F) * SCALE + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            builder.vertex(ae + (float) ag + 1.0F - 9.765625E-4F, ab + 4.0F, af + 0.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 0.0F) * SCALE + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            builder.vertex(ae + (float) ag + 1.0F - 9.765625E-4F, ab + 4.0F, af + 8.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 8.0F) * SCALE + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                            builder.vertex(ae + (float) ag + 1.0F - 9.765625E-4F, ab + 0.0F, af + 8.0F).uv((ae + (float) ag + 0.5F) * SCALE + k, (af + 8.0F) * SCALE + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).endVertex();
                        }
                    }
                    if (ad > -1) { // north [-z]
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + 0.0F, ab + 4.0F, af + (float) ag + 0.0F).uv((ae + 0.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            builder.vertex(ae + 8.0F, ab + 4.0F, af + (float) ag + 0.0F).uv((ae + 8.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            builder.vertex(ae + 8.0F, ab + 0.0F, af + (float) ag + 0.0F).uv((ae + 8.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                            builder.vertex(ae + 0.0F, ab + 0.0F, af + (float) ag + 0.0F).uv((ae + 0.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).endVertex();
                        }
                    }
                    if (ad <= 1) { // south [+z] - Reversed Winding Order
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + 0.0F, ab + 0.0F, af + (float) ag + 1.0F - 9.765625E-4F).uv((ae + 0.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            builder.vertex(ae + 8.0F, ab + 0.0F, af + (float) ag + 1.0F - 9.765625E-4F).uv((ae + 8.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            builder.vertex(ae + 8.0F, ab + 4.0F, af + (float) ag + 1.0F - 9.765625E-4F).uv((ae + 8.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                            builder.vertex(ae + 0.0F, ab + 4.0F, af + (float) ag + 1.0F - 9.765625E-4F).uv((ae + 0.0F) * SCALE + k, (af + (float) ag + 0.5F) * SCALE + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).endVertex();
                        }
                    }
                }
            }
        } else {
            for (int ah = -32; ah < 32; ah += 32) {
                for (int ai = -32; ai < 32; ai += 32) {
                    builder.vertex(ah, ab, ai + 32).uv((float) (ah) * SCALE + k, (float) (ai + 32) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    builder.vertex(ah + 32, ab, ai + 32).uv((float) (ah + 32) * SCALE + k, (float) (ai + 32) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    builder.vertex(ah + 32, ab, ai).uv((float) (ah + 32) * SCALE + k, (float) (ai) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                    builder.vertex(ah, ab, ai).uv((float) (ah) * SCALE + k, (float) (ai) * SCALE + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).endVertex();
                }
            }
        }
        cir.setReturnValue(builder.end());
    }
}
