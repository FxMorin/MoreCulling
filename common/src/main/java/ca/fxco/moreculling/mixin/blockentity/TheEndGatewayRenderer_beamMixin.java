package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.mixin.accessors.LevelRendererAccessor;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.TheEndGatewayRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TheEndGatewayRenderer.class)
public class TheEndGatewayRenderer_beamMixin {

    @WrapWithCondition(
            method = "render(Lnet/minecraft/world/level/block/entity/TheEndGatewayBlockEntity;" +
                    "FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;renderBeaconBeam(" +
                            "Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;" +
                            "Lnet/minecraft/resources/ResourceLocation;FFJIIIFF)V"
            )
    )
    private boolean moreculling$onRenderBeam(PoseStack poseStack, MultiBufferSource multiBufferSource,
                                             ResourceLocation textureId, float tickDelta, float heightScale,
                                             long worldTime, int yOffset, int maxY, int color,
                                             float innerRadius, float outerRadius,
                                             TheEndGatewayBlockEntity endGatewayBlockEntity) {
        if (!MoreCulling.CONFIG.beaconBeamCulling) {
            return true;
        }
        BlockPos pos = endGatewayBlockEntity.getBlockPos();
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        return renderer == null || ((LevelRendererAccessor) renderer).getFrustum().isVisible(new AABB(
                pos.getX() - 1, pos.getY() + yOffset - 1, pos.getZ() - 1,
                pos.getX() + 1, Math.min(pos.getY() + yOffset + maxY, 1024) + 1, pos.getZ() + 1
        ));
    }
}
