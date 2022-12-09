package ca.fxco.moreculling.mixin;

import ca.fxco.moreculling.MoreCulling;
import com.mojang.blaze3d.systems.RenderSystem;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(conflict = @Condition("cullclouds"))
@Mixin(WorldRenderer.class)
public class WorldRenderer_cloudsMixin {

    @Shadow @Nullable private CloudRenderMode lastCloudRenderMode;

    @Inject(
            method = "renderClouds(Lnet/minecraft/client/render/BufferBuilder;" +
                    "DDDLnet/minecraft/util/math/Vec3d;)Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void renderClouds(BufferBuilder builder, double x, double y, double z, Vec3d color, CallbackInfoReturnable<BufferBuilder.BuiltBuffer> cir) {
        if (!MoreCulling.CONFIG.cloudCulling) return;
        float k = (float) MathHelper.floor(x) * 0.00390625F;
        float l = (float)MathHelper.floor(z) * 0.00390625F;
        float m = (float)color.x;
        float n = (float)color.y;
        float o = (float)color.z;
        float p = m * 0.9F;
        float q = n * 0.9F;
        float r = o * 0.9F;
        float s = m * 0.7F;
        float t = n * 0.7F;
        float u = o * 0.7F;
        float v = m * 0.8F;
        float w = n * 0.8F;
        float aa = o * 0.8F;
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
        builder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);
        float ab = (float)Math.floor(y / 4.0) * 4.0F;
        if (this.lastCloudRenderMode == CloudRenderMode.FANCY) {
            RenderSystem.enableCull(); // Enable culling
            for(int ac = -3; ac <= 4; ++ac) {
                for(int ad = -3; ad <= 4; ++ad) {
                    float ae = (float)(ac * 8);
                    float af = (float)(ad * 8);
                    if (ab > -5.0F) { // bottom [-y] - Reversed Winding Order
                        builder.vertex(ae + 0.0F, ab + 0.0F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F, ab + 0.0F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F, ab + 0.0F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                        builder.vertex(ae + 0.0F, ab + 0.0F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(s, t, u, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    }
                    if (ab <= 5.0F) { // top [+y]
                        builder.vertex(ae + 0.0F, ab + 4.0F - 9.765625E-4F, af + 8.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F, ab + 4.0F - 9.765625E-4F, af + 8.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex(ae + 8.0F, ab + 4.0F - 9.765625E-4F, af + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                        builder.vertex(ae + 0.0F, ab + 4.0F - 9.765625E-4F, af + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, 1.0F, 0.0F).next();
                    }
                    int ag;
                    if (ac > -1) { // west [-x]
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float) ag + 0.0F, ab + 0.0F, af + 8.0F).texture((ae + (float) ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float) ag + 0.0F, ab + 4.0F, af + 8.0F).texture((ae + (float) ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float) ag + 0.0F, ab + 4.0F, af + 0.0F).texture((ae + (float) ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float) ag + 0.0F, ab + 0.0F, af + 0.0F).texture((ae + (float) ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(-1.0F, 0.0F, 0.0F).next();
                        }
                    }
                    if (ac <= 1) { // east [+x] - Reversed Winding Order
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + (float)ag + 1.0F - 9.765625E-4F, ab + 0.0F, af + 0.0F).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)ag + 1.0F - 9.765625E-4F, ab + 4.0F, af + 0.0F).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 0.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)ag + 1.0F - 9.765625E-4F, ab + 4.0F, af + 8.0F).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                            builder.vertex(ae + (float)ag + 1.0F - 9.765625E-4F, ab + 0.0F, af + 8.0F).texture((ae + (float)ag + 0.5F) * 0.00390625F + k, (af + 8.0F) * 0.00390625F + l).color(p, q, r, 0.8F).normal(1.0F, 0.0F, 0.0F).next();
                        }
                    }
                    if (ad > -1) { // north [-z]
                        for (ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + 0.0F, ab + 4.0F, af + (float) ag + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float) ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex(ae + 8.0F, ab + 4.0F, af + (float) ag + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float) ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex(ae + 8.0F, ab + 0.0F, af + (float) ag + 0.0F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float) ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                            builder.vertex(ae + 0.0F, ab + 0.0F, af + (float) ag + 0.0F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float) ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, -1.0F).next();
                        }
                    }
                    if (ad <= 1) { // south [+z] - Reversed Winding Order
                        for(ag = 0; ag < 8; ++ag) {
                            builder.vertex(ae + 0.0F, ab + 0.0F, af + (float)ag + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex(ae + 8.0F, ab + 0.0F, af + (float)ag + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex(ae + 8.0F, ab + 4.0F, af + (float)ag + 1.0F - 9.765625E-4F).texture((ae + 8.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                            builder.vertex(ae + 0.0F, ab + 4.0F, af + (float)ag + 1.0F - 9.765625E-4F).texture((ae + 0.0F) * 0.00390625F + k, (af + (float)ag + 0.5F) * 0.00390625F + l).color(v, w, aa, 0.8F).normal(0.0F, 0.0F, 1.0F).next();
                        }
                    }
                }
            }
        } else {
            for(int ah = -32; ah < 32; ah += 32) {
                for(int ai = -32; ai < 32; ai += 32) {
                    builder.vertex(ah, ab, ai + 32).texture((float)(ah) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ah + 32, ab, ai + 32).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai + 32) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ah + 32, ab, ai).texture((float)(ah + 32) * 0.00390625F + k, (float)(ai) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                    builder.vertex(ah, ab, ai).texture((float)(ah) * 0.00390625F + k, (float)(ai) * 0.00390625F + l).color(m, n, o, 0.8F).normal(0.0F, -1.0F, 0.0F).next();
                }
            }
        }
        cir.setReturnValue(builder.end());
    }
}
