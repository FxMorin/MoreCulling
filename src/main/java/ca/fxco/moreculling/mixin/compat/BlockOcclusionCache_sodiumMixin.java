package ca.fxco.moreculling.mixin.compat;

import ca.fxco.moreculling.mixin.accessors.AbstractBlockAccessor;
import ca.fxco.moreculling.patches.BakedTransparency;
import ca.fxco.moreculling.utils.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static ca.fxco.moreculling.MoreCulling.blockRenderManager;

@Pseudo
@Mixin(targets = "me/jellysquid/mods/sodium/client/render/occlusion/BlockOcclusionCache", remap = false)
public class BlockOcclusionCache_sodiumMixin {

    /*
     * Sodium support
     */


    @Inject(
            method = "shouldDrawSide",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)" +
                            "Lnet/minecraft/block/BlockState;",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void useMoreCulling(BlockState selfState, BlockView view, BlockPos pos,
                                Direction facing, CallbackInfoReturnable<Boolean> cir, BlockPos.Mutable adjPos) {
        cir.setReturnValue(BlockUtils.shouldDrawSideTransparency(
                selfState,
                view,
                pos,
                facing,
                adjPos,
                ((BakedTransparency)blockRenderManager.getModel(selfState)).hasTransparency()
        ));
    }


    /*@Redirect(
            method = "shouldDrawSide",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOpaque()Z"
            )
    )
    private boolean isOpaqueOrNotTransparent(BlockState blockState, BlockState selfState) {
        return blockState.isOpaque() ||
                (((AbstractBlockAccessor)selfState.getBlock()).getCollidable() &&
                        !((BakedTransparency)blockRenderManager.getModel(blockState)).hasTransparency() &&
                        !((BakedTransparency)blockRenderManager.getModel(selfState)).hasTransparency());
    }*/
}
