package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.utils.CullingUtils;
import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * Sodium Support
 */
@Restriction(require = @Condition("sodium"))
@Pseudo
@Mixin(AbstractBlockRenderContext.class)
public class BlockOcclusionCache_sodiumMixin {

    @Shadow
    protected BlockState state;

    @Shadow
    protected BlockAndTintGetter level;

    @Shadow
    protected BlockPos pos;

    @Inject(
            method = "shouldDrawSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockAndTintGetter;" +
                            "getBlockState(Lnet/minecraft/core/BlockPos;" +
                            ")Lnet/minecraft/world/level/block/state/BlockState;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void moreculling$useMoreCulling(Direction facing, CallbackInfoReturnable<Boolean> cir, @Local BlockPos.MutableBlockPos adjPos) {
        if (MoreCulling.CONFIG.useBlockStateCulling) {
            cir.setReturnValue(CullingUtils.shouldDrawSideCulling(state, level.getBlockState(adjPos), level, pos, facing, adjPos));
        }
    }
}
