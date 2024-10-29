package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.CullingUtils;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.vulkanmod.render.chunk.build.frapi.render.AbstractBlockRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Vulkan Support
 */
@Restriction(require = @Condition("vulkanmod"))
@Mixin(AbstractBlockRenderContext.class)
public class AbstractBlockRenderContext_vulkanModMixin {
    @Shadow protected BlockAndTintGetter renderRegion;

    @Shadow protected BlockPos blockPos;

    @Inject(
            method = "faceNotOccluded",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/BlockGetter;getBlockState(" +
                            "Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void moreculling$useMoreCulling(BlockState blockState, Direction face,
                                            CallbackInfoReturnable<Boolean> cir,
                                            @Local BlockPos adjPos) {
        if (MoreCulling.CONFIG.useBlockStateCulling) {
            cir.setReturnValue(CullingUtils.shouldDrawSideCulling(blockState, renderRegion, blockPos, face, adjPos));
        }
    }
}
