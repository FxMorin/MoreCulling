package ca.fxco.moreculling.mixin.blockentity;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.mixin.accessors.LevelRendererAccessor;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconRenderer.class)
public class BeaconRenderer_frustumMixin<T extends BlockEntity & BeaconBeamOwner> {

    @WrapWithCondition(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;" +
                            "renderBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;" +
                            "Lnet/minecraft/client/renderer/MultiBufferSource;FFJIII)V"
            )
    )
    private boolean moreculling$onRenderBeam(PoseStack p_112185_, MultiBufferSource p_112186_,
                                             float i, float tickDelta, long worldTime, int yOffset,
                                             int maxY, int color, T blockEntity) {
        if (!MoreCulling.CONFIG.beaconBeamCulling) {
            return true;
        }
        BlockPos pos = blockEntity.getBlockPos();
        LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
        return renderer == null || ((LevelRendererAccessor) renderer).getFrustum().isVisible(new AABB(
                pos.getX() - 1, pos.getY() + yOffset - 1, pos.getZ() - 1,
                pos.getX() + 1, Math.min(pos.getY() + yOffset + maxY, 1024) + 1, pos.getZ() + 1
        ));
    }
}
