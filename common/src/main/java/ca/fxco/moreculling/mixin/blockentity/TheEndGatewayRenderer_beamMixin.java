package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.api.renderers.ExtendedLevelRenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.client.renderer.blockentity.state.EndGatewayRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TheEndGatewayRenderer.class)
public class TheEndGatewayRenderer_beamMixin {

    @WrapWithCondition(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/EndGatewayRenderState;" +
                    "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                    "Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;" +
                            "submitBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
                            "Lnet/minecraft/resources/Identifier;FFIIIFF)V"
            )
    )
    private boolean moreculling$onRenderBeam(PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
                                             Identifier resourceLocation, float tickDelta, float heightScale,
                                             int yOffset, int maxY, int color, float innerRadius, float outerRadius,
                                             EndGatewayRenderState endGatewayRenderState) {
        if (!MoreCulling.CONFIG.beaconBeamCulling) {
            return true;
        }
        BlockPos pos = endGatewayRenderState.blockPos;
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        return renderer == null || ((ExtendedLevelRenderer) renderer).moreculling$getFrustum().isVisible(new AABB(
                pos.getX() - 1, pos.getY() + yOffset - 1, pos.getZ() - 1,
                pos.getX() + 1, Math.min(pos.getY() + yOffset + maxY, 1024) + 1, pos.getZ() + 1
        ));
    }
}
