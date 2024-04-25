package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.mixin.accessors.WorldRendererAccessor;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.EndGatewayBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EndGatewayBlockEntityRenderer.class)
public class EndGatewayBlockEntityRenderer_beamMixin {

    @WrapWithCondition(
            method = "render(Lnet/minecraft/block/entity/EndGatewayBlockEntity;" +
                    "FLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/entity/BeaconBlockEntityRenderer;" +
                            "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;" +
                            "Lnet/minecraft/client/render/VertexConsumerProvider;" +
                            "Lnet/minecraft/util/Identifier;FFJII[FFF)V"
            )
    )
    private boolean moreculling$onRenderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                             Identifier textureId, float tickDelta, float heightScale,
                                             long worldTime, int yOffset, int maxY, float[] color,
                                             float innerRadius, float outerRadius,
                                             EndGatewayBlockEntity endGatewayBlockEntity) {
        if (!MoreCulling.CONFIG.beaconBeamCulling) {
            return true;
        }
        BlockPos pos = endGatewayBlockEntity.getPos();
        WorldRenderer renderer = MinecraftClient.getInstance().worldRenderer;
        return renderer == null || ((WorldRendererAccessor) renderer).getFrustum().isVisible(new Box(
                pos.getX() - 1, pos.getY() + yOffset - 1, pos.getZ() - 1,
                pos.getX() + 1, Math.min(pos.getY() + yOffset + maxY, 1024) + 1, pos.getZ() + 1
        ));
    }
}
